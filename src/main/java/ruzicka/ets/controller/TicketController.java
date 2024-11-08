package ruzicka.ets.controller;

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
    @Autowired
    private ZakaznikRepository zakaznikRepository;

    @Autowired
    private ObjednavkaRepository objednavkaRepository;

    @Autowired
    private EmailVerificationService emailService;
//----------------------------------------------------------------------------------------------------------------------
    /**
     * Handles the login or registration process for customers.
     * If the customer exists and is verified, returns their orders.
     * If not verified, asks for email verification.
     * If the customer does not exist, creates a new record and sends a verification email.
     *
     * @param email The email of the customer attempting to log in or register.
     * @return A ResponseEntity containing the status and message or list of orders,
     *         based on the customer's verification status and existence in the system.
     */
    @PostMapping("/login-register")
    public ResponseEntity<?> loginOrRegister(@RequestBody String email) {
        Optional<Zakaznik> existingUser = zakaznikRepository.findByMail(email);

        if (existingUser.isPresent()) {
            Zakaznik user = existingUser.get();
            if ("V".equals(user.getStatus())) { // Verified
                List<Objednavka> orders = objednavkaRepository.findByIdzakaznik_Idzakaznik(user.getIdzakaznik());
                return ResponseEntity.ok(orders);
            } else {
                return ResponseEntity.status(403).body("Please verify your email.");
            }
        } else {
            Zakaznik newUser = new Zakaznik();
            newUser.setMail(email);
            newUser.setStatus("P"); // Pending verification
            zakaznikRepository.save(newUser);

            // Send verification email with Zakaznik ID
            emailService.sendVerificationEmail(newUser, "Ověření mailu Ples", "Prosím ověřte svůj mail.");

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
            return ResponseEntity.ok("Email verified successfully.");
        } else {
            return ResponseEntity.status(404).body("Email verification failed. Invalid ID or email.");
        }
    }
}

