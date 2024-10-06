package ruzicka.ets.service;

import org.springframework.beans.factory.annotation.Autowired;
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

    // Email credentials (these should be configured in your application properties)
    private final String HOST = "imap.seznam.cz";
    private final String EMAIL = ""; // Update with your email
    private final String PASSWORD = ""; // Update with your password

    // Send the verification email
    public void sendVerificationEmail(String email, String subject, String messageContent) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(messageContent + "\n\n" +
                "Click this link to verify your email: " +
                "http://localhost:8080/api/tickets/verify-email?email=" + email);

        emailSender.send(message);
        System.out.println("Verification email sent to " + email);
    }
    // Verify the user's email
    public boolean verifyEmail(String email) {
        Optional<Zakaznik> zakaznikOptional = zakaznikRepository.findByMail(email);
        if (zakaznikOptional.isPresent()) {
            Zakaznik zakaznik = zakaznikOptional.get();
            zakaznik.setStatus("V");  // Set status to verified
            zakaznik.setCaspotvrzeni(new Timestamp(System.currentTimeMillis())); // Set the confirmation time
            zakaznikRepository.save(zakaznik);
            System.out.println("Email " + email + " has been verified.");
            return true;
        }
        return false;
    }

    // Scheduled task to check for emails from the bank every minute
    @Scheduled(fixedRate = 60000) // 1 minute
    public void checkEmails() {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");

        try {
            // Connect to the email store (IMAP)
            Session session = Session.getInstance(properties);
            Store store = session.getStore("imaps");
            store.connect(HOST, EMAIL, PASSWORD);

            // Open the inbox folder
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            // Fetch the messages from inbox
            Message[] messages = inbox.getMessages();

            // Process each message
            for (Message message : messages) {
                if (message instanceof MimeMessage) {
                    MimeMessage mimeMessage = (MimeMessage) message;
                    processBankEmail(mimeMessage);
                }
            }

            // Close the inbox and store connections
            inbox.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Process the bank emails to check if payment has been received
    private void processBankEmail(MimeMessage message) throws MessagingException, IOException {
        String subject = message.getSubject();
        String content = message.getContent().toString();

        // Assuming the subject contains the order ID (variable symbol) and the email content contains the payment amount
        if (subject.contains("Payment Confirmation")) {
            String[] parts = subject.split(": ");
            String variableSymbol = parts[1].trim(); // Assuming the order ID is the variable symbol

            // Parse the email content to get the payment amount
            String paymentAmountStr = parsePaymentAmount(content);
            int paymentAmount = Integer.parseInt(paymentAmountStr);

            // Validate the order and payment
            validateOrder(variableSymbol, paymentAmount);
        }
    }

    // Parse the email content to extract payment amount (example)
    private String parsePaymentAmount(String content) {
        // Example: "Thank you for your payment of 1000 CZK for order..."
        int amountStart = content.indexOf("payment of ") + 11;
        int amountEnd = content.indexOf(" CZK", amountStart);
        return content.substring(amountStart, amountEnd);
    }

    // Validate the order based on the variable symbol (order ID) and payment amount
    private void validateOrder(String variableSymbol, int paymentAmount) {
        // Find the order by variable symbol (ID of order)
        Optional<Objednavka> orderOpt = objednavkaRepository.findById(Integer.parseInt(variableSymbol));

        if (orderOpt.isPresent()) {
            Objednavka order = orderOpt.get();
            if (order.getCena() == paymentAmount) {
                // Mark the order as PAID and save it
                order.setStatus("PAID");
                objednavkaRepository.save(order);

                // Optionally, send tickets via email
                System.out.println("Order " + variableSymbol + " confirmed and tickets sent.");
            } else {
                System.out.println("Payment amount mismatch for order ID " + variableSymbol);
            }
        } else {
            System.out.println("Order not found for ID " + variableSymbol);
        }
    }
}
