package ruzicka.ets.service;

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
import org.springframework.stereotype.Service;
import ruzicka.ets.repository.ObjednavkaRepository;
/**

 * @author czech
 * @since 2023-10-03
 */


import org.springframework.mail.javamail.JavaMailSender;
import ruzicka.ets.db.Objednavka;
import ruzicka.ets.repository.ZakaznikRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;



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

    // This method is called to start the email checking service
    public void startEmailCheckingService() {
        new Thread(this::checkEmailLoop).start();
    }

    // Method to loop and check for emails
    private void checkEmailLoop() {
        while (true) {
            try {
                checkForNewEmails();
                Thread.sleep(60000); // Check every 60 seconds
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Connect to the email server and check for new emails
    private void checkForNewEmails() throws MessagingException, IOException {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        Session session = Session.getDefaultInstance(properties, null);

        Store store = session.getStore("imaps");
        store.connect(HOST, username, password);

        Folder inbox = store.getFolder("inbox");
        inbox.open(Folder.READ_WRITE);

        // Fetch unread messages
        Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

        for (Message message : messages) {
            if (isBankEmail(message)) {
                String content = getTextFromMessage(message);

                // Extract variable symbol and amount from email content
                String variableSymbol = extractVariableSymbol(content);
                int amount = extractAmount(content);

                // Validate payment and update order status
                if (variableSymbol != null && amount > 0 && validateAndProcessPayment(variableSymbol, amount)) {
                    System.out.println("Payment verified for order with symbol: " + variableSymbol);
                }
            }
        }

        inbox.close(false);
        store.close();
    }

    // Check if the email is from the bank
    private boolean isBankEmail(Message message) throws MessagingException {
        return message.getFrom()[0].toString().contains("bank@example.com"); // Change to your bank's email
    }

    // Extract variable symbol from email content
    private String extractVariableSymbol(String content) {
        // More robust parsing logic (example):
        int index = content.indexOf("Variable Symbol:");
        if (index != -1) {
            String symbol = content.substring(index + 16).split("\\s+")[0]; // Extract the symbol
            return symbol.trim();
        }
        return null;
    }

    // Extract amount from email content
    private int extractAmount(String content) {
        int index = content.indexOf("Amount:");
        if (index != -1) {
            String amountStr = content.substring(index + 7).split("\\s+")[0]; // Extract the amount
            try {
                return Integer.parseInt(amountStr.trim());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    // Validate payment and update order status
    private boolean validateAndProcessPayment(String variableSymbol, int amount) {
        try {
            // Retrieve the order using the variable symbol
            Optional<Objednavka> orderOpt = objednavkaRepository.findById(Integer.parseInt(variableSymbol));

            if (orderOpt.isPresent()) {
                Objednavka order = orderOpt.get();

                // Match the amount and update order status
                if (order.getCena().equals(amount)) {
                    order.setStatus("PAID");
                    objednavkaRepository.save(order);

                    // Trigger ticket sending process (you can enhance this method as needed)
                    sendTicketEmail(order);

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

    // Extract text content from the email
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

    // Dummy email sending logic, replace this with actual email sending code
    private void sendTicketEmail(Objednavka order) {
        System.out.println("Sending tickets to the user for order ID: " + order.getId());
        // Implement ticket email sending logic using JavaMailSender if needed
    }
}