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

    /**
     * Sends a verification email to a specified customer.
     *
     * @param zakaznik the customer to whom the verification email will be sent
     * @param subject the subject of the verification email
     * @param messageContent the body content of the verification email
     */
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
//--------------------------------------------------------------------------------------------------------------------

    /**
     * Verifies the email address of a customer by updating their status and confirmation timestamp.
     *
     * @param email The email address to be verified.
     * @param id The unique ID of the customer.
     * @return {@code true} if the email was successfully verified; {@code false} otherwise.
     */
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
