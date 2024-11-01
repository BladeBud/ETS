package ruzicka.ets.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
//----------------------------------------------------------------------------------------------------------------------
    private static final Logger log = LoggerFactory.getLogger(EmailVerificationService.class);

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private ZakaznikRepository zakaznikRepository;

    private final String HOST = "imap.seznam.cz";
    @Value("${email.username}")
    private String EMAIL;

    @Value("${email.password}")
    private String PASSWORD;
//----------------------------------------------------------------------------------------------------------------------
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
                "Prosím klikněte na tento odkaz pro potvrzení mailu: " + //TODO: zmeit zneni mailu
                "https://plesgymjh.me/api/tickets/verify-email?email=" + zakaznik.getMail() + "&id=" + zakaznikId); //TODO: Change to production URL

        emailSender.send(message);
        log.info("Verification email sent to {}", zakaznik.getMail());
    }
//----------------------------------------------------------------------------------------------------------------------
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
                log.info("Email {} has been verified.", email);
                return true;
            } else {
                log.warn("Verification failed: ID mismatch for email {}", email);
            }
        } else {
            log.warn("Verification failed: No customer found with email {}", email);
        }
        return false;
    }
}