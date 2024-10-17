package ruzicka.ets.service;

import jakarta.annotation.PostConstruct;
import jakarta.mail.BodyPart;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.search.FlagTerm;
import jakarta.mail.internet.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ruzicka.ets.db.Zakaznik;
import ruzicka.ets.repository.ObjednavkaRepository;
import org.springframework.mail.javamail.JavaMailSender;
import ruzicka.ets.db.Objednavka;
import ruzicka.ets.repository.ZakaznikRepository;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

/**

 * @author czech
 * @since 2023-10-03
 */

/**
 * Service responsible for checking and processing emails for order payments.
 *
 * The service continuously monitors an email inbox for new emails from a specific bank,
 * extracts payment information, validates it, and processes corresponding orders.
 */
@Service
public class EmailCheckerService {

    private static final String HOST = "imap.seznam.cz";

    @Value("${email.username}")
    private String username;

    @Value("${email.password}")
    private String password;

    @Autowired
    private ObjednavkaRepository objednavkaRepository;

    @Autowired
    private ZakaznikRepository zakaznikRepository;

    @Autowired
    private JavaMailSender emailSender;

    @PostConstruct
    public void init() {
        startEmailCheckingService();
    }

    public void startEmailCheckingService() {
        new Thread(this::checkEmailLoop).start();
    }

    private void checkEmailLoop() {
        System.out.println("Starting email checking loop...");
        while (true) {
            try {
                System.out.println("Checking for new emails...");
                checkForNewEmails();
                System.out.println("Sleeping for 60 seconds before next check...");
                Thread.sleep(60000); // Check every 60 seconds
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Continuously checks for new unread emails from the configured email account.
     * If a bank email is found, the method processes all such emails.
     *
     * @throws MessagingException If there is an error in the messaging operations.
     * @throws IOException If there is an error in reading the email content.
     */
    private void checkForNewEmails() throws MessagingException, IOException {
        System.out.println("Connecting to the email server...");

        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        Session session = Session.getDefaultInstance(properties, null);

        Store store = session.getStore("imaps");
        store.connect(HOST, username, password);

        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);

        // Search for all unread emails
        Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
        System.out.println("Found " + messages.length + " unread emails.");

        // Process each unread message
        for (Message message : messages) {
            System.out.println("Processing email from: " + message.getFrom()[0].toString());

            // Check if the email is from the specific sender (e.g., bank)
            if (isBankEmail(message)) {
                System.out.println("Email is from the bank. Extracting content...");

                String content = getTextFromMessage(message);
                System.out.println("Email content extracted: " + content);

                String variableSymbol = extractVariableSymbol(content);
                int amount = extractAmount(content);

                System.out.println("Extracted variable symbol: " + variableSymbol);
                System.out.println("Extracted amount: " + amount);

                // Validate and process payment
                if (variableSymbol != null && amount > 0 && validateAndProcessPayment(variableSymbol, amount)) {
                    System.out.println("Payment verified for order with symbol: " + variableSymbol);
                } else {
                    System.out.println("Payment validation failed for email.");
                }

                // Mark the email as read after processing
                message.setFlag(Flags.Flag.SEEN, true);
                System.out.println("Email marked as read.");
            } else {
                System.out.println("Email is not from the bank. Skipping.");
            }
        }

        inbox.close(false); // Close folder without expunging
        store.close();
        System.out.println("Disconnected from the email server.");
    }

    /**
     * Checks if the sender of the specified email message matches the bank's email address.
     *
     * @param message The email message to be checked.
     * @return true if the email is from the bank; false otherwise.
     * @throws MessagingException If there is an error while retrieving the sender's address.
     */
    private boolean isBankEmail(Message message) throws MessagingException {
        boolean isFromBank = message.getFrom()[0].toString().contains("adam.ruzicka@email.cz"); // Change to actual bank email
        System.out.println("Checking if email is from the bank: " + isFromBank);
        return isFromBank;
    }


    /**
     * Extracts the variable symbol from the email content.
     *
     * @param content The input string that contains the email content.
     * @return The extracted variable symbol.
     */
    private String extractVariableSymbol(String content) {
        int index = content.indexOf("Variable Symbol:");
        if (index != -1) {
            String symbol = content.substring(index + 16).split("\\s+")[0];
            return symbol.trim();
        }
        return null;
    }

    /**
     * Extracts the amount value from the email content.
     *
     * @param content The input string that contains the email content.
     * @return The extracted amount.
     */
    private int extractAmount(String content) {
        int index = content.indexOf("Amount:");
        if (index != -1) {
            String amountStr = content.substring(index + 7).split("\\s+")[0];
            try {
                return Integer.parseInt(amountStr.trim());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * Validates the provided variable symbol and amount and processes the payment if valid.
     *
     * @param variableSymbol The variable symbol of the order to be validated and processed.
     * @param amount The amount to be validated against the order's expected amount.
     * @return true if the payment is successfully validated and processed; false otherwise.
     */
    private boolean validateAndProcessPayment(String variableSymbol, int amount) {
        try {
            Optional<Objednavka> orderOpt = objednavkaRepository.findById(Integer.parseInt(variableSymbol));

            if (orderOpt.isPresent()) {
                Objednavka order = orderOpt.get();

                if (order.getCena().equals(amount)) {
                    order.setStatus("P"); // Payment processed
                    objednavkaRepository.save(order);

                    sendTicketEmail(order); // Send ticket after payment confirmation
                    return true;
                } else {
                    System.out.println("Amount mismatch for order: " + variableSymbol);
                }
            } else {
                System.out.println("No matching order found for symbol: " + variableSymbol);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Extracts the plain text content from an email message. This method handles
     * both "text/plain" and "multipart/*" MIME types.
     *
     * @param message The email message from which to extract the plain text content.
     * @return The extracted plain text content.
     * @throws MessagingException If there is an error in the messaging operations.
     * @throws IOException If there is an error in reading the email content.
     */
    private String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    /**
     * Extracts the plain text content from a MimeMultipart object. This method
     * iterates through the parts of the MimeMultipart to retrieve text/plain content.
     *
     * @param mimeMultipart The MimeMultipart object from which to extract the plain text content.
     * @return The extracted plain text content.
     * @throws MessagingException If there is an error in accessing the MimeMultipart parts.
     * @throws IOException If there is an error in reading the content of the MimeMultipart parts.
     */
    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent());
            }
        }
        return result.toString();
    }
//----------------------------------------------------------------------------------------------------------------------

    /**
     * Sends a ticket email to the customer associated with the given order.
     * The email includes the customer's name, order details, and an attachment containing the tickets.
     *
     * @param order The order for which the tickets are to be sent. The order must contain valid customer information.
     */
    private void sendTicketEmail(Objednavka order) {
        try {
            Optional<Zakaznik> zakaznikOpt = zakaznikRepository.findById(order.getIdzakaznik().getIdzakaznik());

            if (zakaznikOpt.isPresent()) {
                Zakaznik zakaznik = zakaznikOpt.get();
                String userEmail = zakaznik.getMail();
                String subject = "Lístky pro objednávku: " + order.getId();
                String bodyText = "Dobrý den " + zakaznik.getJmeno() + ",\n\n"
                        + "Děkujeme za zakoupení lístků. Lístky můžete najít v příloze.\n\n"
                        + "Naviděnou,\n"
                        + "Vaše Oktávy";

                // Create a MimeMessage
                MimeMessage message = emailSender.createMimeMessage();

                // Use MimeMessageHelper to attach files
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setFrom(username);
                helper.setTo(userEmail);      // Recipient email
                helper.setSubject(subject);
                helper.setText(bodyText);

                // Add ticket as an attachment (assuming tickets are generated as PDFs)
                File ticketFile = generateTicketFile(order);  //TODO: Implement ticket generation logic
                FileSystemResource file = new FileSystemResource(ticketFile);
                helper.addAttachment("Ticket_" + order.getId() + ".pdf", file);

                // Send the email
                emailSender.send(message);
                System.out.println("Tickets sent to " + userEmail + " for order ID: " + order.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to send ticket email for order ID: " + order.getId());
        }
    }
//----------------------------------------------------------------------------------------------------------------------
    /**
     * Generates a ticket file for the given order.
     */
    private File generateTicketFile(Objednavka order) throws IOException {
        // TODO: Implement ticket generation logic (e.g., create PDF, image, etc.)
        File tempFile = File.createTempFile("Ticket_" + order.getId(), ".pdf");
        // Write ticket content to tempFile
        return tempFile;
    }

}