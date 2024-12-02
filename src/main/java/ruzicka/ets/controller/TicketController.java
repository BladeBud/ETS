package ruzicka.ets.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ruzicka.ets.db.Objednavka;
import ruzicka.ets.db.Zakaznik;
import ruzicka.ets.dto.EmailDTO;
import ruzicka.ets.repository.ObjednavkaRepository;
import ruzicka.ets.repository.ZakaznikRepository;
import ruzicka.ets.service.EmailVerificationService;

import java.util.List;
import java.util.Optional;

/**
 * @author czech
 * @since 2023-10-03
 */

/**
 * REST controller for managing customers' ticket-related operations.
 */
@RestController
@RequestMapping("/api/ticket")
public class TicketController {
//----------------------------------------------------------------------------------------------------------------------
    private static final Logger importantLog = LoggerFactory.getLogger("important");
    private static final Logger log = LoggerFactory.getLogger(TicketController.class);

    @Autowired
    private ZakaznikRepository zakaznikRepository;

    @Autowired
    private ObjednavkaRepository objednavkaRepository;

    @Autowired
    private EmailVerificationService emailService;
//----------------------------------------------------------------------------------------------------------------------
    /**
     * Endpoint to log in or register a customer based on the provided email address.
     *
     * @param emailDTO The email address to be logged in or registered.
     * @return A ResponseEntity containing a list of orders if the customer is logged in,
     *         or a message if the customer is registered.
     */

    @PostMapping("/login-register")
    public ResponseEntity<?> loginOrRegister(@RequestBody EmailDTO emailDTO) {
        String email = emailDTO.getEmail();
        Optional<Zakaznik> existingUser = zakaznikRepository.findByMail(email);

        if (existingUser.isPresent()) {
            Zakaznik user = existingUser.get();
            if ("V".equals(user.getStatus())) { // Verified
                List<Objednavka> orders = objednavkaRepository.findByIdzakaznik_Idzakaznik(user.getIdzakaznik());
                log.info("User with email {} logged in successfully.", email);
                return ResponseEntity.ok(orders);
            } else {
                log.warn("User with email {} attempted to log in without verifying email.", email);
                return ResponseEntity.status(403).body("Please verify your email.");
            }
        } else {
            Zakaznik newUser = new Zakaznik();
            newUser.setMail(email);
            newUser.setStatus("P"); // Pending verification
            zakaznikRepository.save(newUser);

            // Send verification email with Zakaznik ID
            emailService.sendVerificationEmail(newUser, "Ověření mailu Ples", "Prosím ověřte svůj mail.");
            importantLog.info("New user with email {} registered and verification email sent.", email);

            return ResponseEntity.ok("Verification email sent.");
        }
    }
//----------------------------------------------------------------------------------------------------------------------

    /**
     * Endpoint to verify a customer's email address based on the provided email and ID.
     *
     * @param email The email address to be verified.
     * @param id The unique ID of the customer.
     * @return A ResponseEntity containing a success message if the email is verified,
     *         or an error message if the verification fails.
     */
    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String email, @RequestParam Integer id) {
        boolean verified = emailService.verifyEmail(email, id);

        if (verified) {
            log.info("Email verified successfully for email: {}", email);
            return ResponseEntity.ok("Email ověřen úspěšně. <a href=\"https://www.plesgymjh.cz/selection.html\">Lístky si objednejte zde</a>");
        } else {
            log.warn("Email verification failed for email: {}", email);
            return ResponseEntity.status(404).body("Email se nepovedlo ověřit.");
        }
    }
}

