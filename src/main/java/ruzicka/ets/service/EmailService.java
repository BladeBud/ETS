package ruzicka.ets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ruzicka.ets.db.Objednavka;
import ruzicka.ets.db.Zakaznik;
import ruzicka.ets.repository.ObjednavkaRepository;
import ruzicka.ets.repository.ZakaznikRepository;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.Properties;

/**
 * Service to handle sending verification emails and checking for payment confirmation emails from the bank.
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private ZakaznikRepository zakaznikRepository;

    @Autowired
    private ObjednavkaRepository objednavkaRepository;

    private final String HOST = "imap.seznam.cz";
    @Value("${email.username}")
    private String EMAIL;

    @Value("${email.password}")
    private String PASSWORD;

    public void sendVerificationEmail(Zakaznik zakaznik, String subject, String messageContent) {
        Integer zakaznikId = zakaznik.getIdzakaznik();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(zakaznik.getMail());
        message.setSubject(subject);
        message.setText(messageContent + "\n\n" +
                "Click this link to verify your email: " +
                "http://localhost:8080/api/tickets/verify-email?email=" + zakaznik.getMail() + "&id=" + zakaznikId);

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

    @Scheduled(fixedRate = 60000)
    public void checkEmails() {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");

        try {
            Session session = Session.getInstance(properties);
            Store store = session.getStore("imaps");
            store.connect(HOST, EMAIL, PASSWORD);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            boolean bankEmailFound = false;

            while (!bankEmailFound) {
                Message[] messages = inbox.getMessages();
                for (Message message : messages) {
                    try {
                        if (message instanceof MimeMessage) {
                            MimeMessage mimeMessage = (MimeMessage) message;
                            if (isBankEmail(mimeMessage)) {
                                bankEmailFound = true;
                                processBankEmail(mimeMessage);
                            }
                        }
                    } catch (MessagingException | IOException e) {
                        e.printStackTrace();
                    }
                }

                if (!bankEmailFound) {
                    System.out.println("No bank email found in this iteration.");
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            inbox.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isBankEmail(MimeMessage message) throws MessagingException {
        return message.getFrom()[0].toString().contains("mbankl@example.com"); //TODO: Change to bank's email
    }

    private void processBankEmail(MimeMessage message) throws MessagingException, IOException {
        String subject = message.getSubject();
        String content = message.getContent().toString();

        if (subject.contains("Payment Confirmation")) {
            String[] parts = subject.split(": ");
            String variableSymbol = parts[1].trim();

            String paymentAmountStr = parsePaymentAmount(content);
            int paymentAmount = Integer.parseInt(paymentAmountStr);

            validateOrder(variableSymbol, paymentAmount);
        }
    }

    private String parsePaymentAmount(String content) {
        int amountStart = content.indexOf("payment of ") + 11;
        int amountEnd = content.indexOf(" CZK", amountStart);
        return content.substring(amountStart, amountEnd);
    }

    private void validateOrder(String variableSymbol, int paymentAmount) {
        Optional<Objednavka> orderOpt = objednavkaRepository.findById(Integer.parseInt(variableSymbol));

        if (orderOpt.isPresent()) {
            Objednavka order = orderOpt.get();
            if (order.getCena() == paymentAmount) {
                order.setStatus("PAID");
                objednavkaRepository.save(order);
                System.out.println("Order " + variableSymbol + " confirmed and tickets sent.");
            } else {
                System.out.println("Payment amount mismatch for order ID " + variableSymbol);
            }
        } else {
            System.out.println("Order not found for ID " + variableSymbol);
        }
    }
}
