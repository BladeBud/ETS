package ruzicka.ets.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ruzicka.ets.db.Zakaznik;
import ruzicka.ets.db.Objednavka;
import ruzicka.ets.repository.ZakaznikRepository;
import ruzicka.ets.repository.ObjednavkaRepository;
import ruzicka.ets.service.EmailVerificationService;

import java.util.List;
import java.util.Optional;

/**
 * @author czech
 * @since 2023-10-03
 */

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private ZakaznikRepository zakaznikRepository;

    @Autowired
    private ObjednavkaRepository objednavkaRepository;

    @Autowired
    private EmailVerificationService emailService;

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
            emailService.sendVerificationEmail(newUser, "Verify your email", "Please verify your email address.");

            return ResponseEntity.ok("Verification email sent.");
        }
    }

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

