package ruzicka.ets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ruzicka.ets.db.Zakaznik;
import ruzicka.ets.repository.ZakaznikRepository;

import java.sql.Timestamp;
import java.util.Optional;

/**
 * Service to handle sending verification emails.
 */

@Service
public class EmailVerificationService {
  //--------------------------------------------------------------------------------------------------------------------
    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private ZakaznikRepository zakaznikRepository;

    private final String HOST = "imap.seznam.cz";
    @Value("${email.username}")
    private String EMAIL;

    @Value("${email.password}")
    private String PASSWORD;
  //--------------------------------------------------------------------------------------------------------------------

    public void sendVerificationEmail(Zakaznik zakaznik, String subject, String messageContent) {
        Integer zakaznikId = zakaznik.getIdzakaznik();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(zakaznik.getMail());
        message.setSubject(subject);
        message.setText(messageContent + "\n\n" +
                "Prosím klikněte na tento odkaz pro potvrzení mailu: " + //TODO: změnit znění mailu
                "http://localhost:8080/api/tickets/verify-email?email=" + zakaznik.getMail() + "&id=" + zakaznikId); //TODO: Change to production URL

        emailSender.send(message);
        System.out.println("Verification email sent to " + zakaznik.getMail());
    }

    public boolean verifyEmail(String email, Integer id) {
        Optional<Zakaznik> zakaznikOptional = zakaznikRepository.findByMail(email);
        if (zakaznikOptional.isPresent()) {
            Zakaznik zakaznik = zakaznikOptional.get();
            if (zakaznik.getIdzakaznik().equals(id)) {
                zakaznik.setStatus("V");
                zakaznik.setCaspotvrzeni(new Timestamp(System.currentTimeMillis()));
                zakaznikRepository.save(zakaznik);
                System.out.println("Email " + email + " has been verified.");
                return true;
            }
        }
        return false;
    }
}
