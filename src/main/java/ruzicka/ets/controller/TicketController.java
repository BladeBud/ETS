package ruzicka.ets.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ruzicka.ets.db.Zakaznik;
import ruzicka.ets.db.Objednavka;
import ruzicka.ets.repository.ZakaznikRepository;
import ruzicka.ets.repository.ObjednavkaRepository;
import ruzicka.ets.service.EmailService;

import java.sql.Timestamp;
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
    private EmailService emailService;

    @PostMapping("/login-register")
    public ResponseEntity<?> loginOrRegister(@RequestBody String email) {
        Optional<Zakaznik> existingUser = zakaznikRepository.findByMail(email);

        if (existingUser.isPresent()) {
            Zakaznik user = existingUser.get();
            if ("V".equals(user.getStatus())) { //verified
                List<Objednavka> orders = objednavkaRepository.findByIdzakaznik_Idzakaznik(user.getIdzakaznik());
                return ResponseEntity.ok(orders);
            } else {
                return ResponseEntity.status(403).body("Please verify your email.");
            }
        } else {
            Zakaznik newUser = new Zakaznik();
            newUser.setMail(email);
            newUser.setStatus("P"); //pending
            newUser.setCaspotvrzeni(null);
            zakaznikRepository.save(newUser);
            emailService.sendVerificationEmail(email, "Verify your email", "Click this link to verify.");
            return ResponseEntity.ok("Verification email sent.");
        }
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String email) {
        Optional<Zakaznik> user = zakaznikRepository.findByMail(email);
        if (user.isPresent()) {
            Zakaznik zakaznik = user.get();
            zakaznik.setStatus("VERIFIED");
            zakaznik.setCaspotvrzeni(new Timestamp(System.currentTimeMillis()));
            zakaznikRepository.save(zakaznik);
            return ResponseEntity.ok("Email verified.");
        } else {
            return ResponseEntity.status(404).body("User not found.");
        }
    }
}

