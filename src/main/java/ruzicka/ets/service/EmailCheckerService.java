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
/**

 * @author czech
 * @since 2023-10-03
 */


import org.springframework.mail.javamail.JavaMailSender;
import ruzicka.ets.db.Objednavka;
import ruzicka.ets.repository.ZakaznikRepository;

import java.io.File;
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
        while (true) {
            try {
                checkForNewEmails();
                Thread.sleep(60000); //TODO: domluvit se na to kolik casu to ma byt momentalne 1min
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkForNewEmails() throws MessagingException, IOException {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        Session session = Session.getDefaultInstance(properties, null);

        Store store = session.getStore("imaps");
        store.connect(HOST, username, password);

        Folder inbox = store.getFolder("inbox");
        inbox.open(Folder.READ_WRITE);

        boolean bankEmailFound = false;

        while (!bankEmailFound) {
            Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

            for (Message message : messages) {
                if (isBankEmail(message)) {
                    bankEmailFound = true;
                    String content = getTextFromMessage(message);

                    String variableSymbol = extractVariableSymbol(content);
                    int amount = extractAmount(content);

                    if (variableSymbol != null && amount > 0 && validateAndProcessPayment(variableSymbol, amount)) {
                        System.out.println("Payment verified for order with symbol: " + variableSymbol);
                    }
                }
            }

            if (!bankEmailFound) {
                System.out.println("No bank email found. Sleeping for 60 seconds.");
                try {
                    Thread.sleep(60000); //TODO: taky zmenit
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        inbox.close(false);
        store.close();
    }

    private boolean isBankEmail(Message message) throws MessagingException {
        return message.getFrom()[0].toString().contains("adam.ruzicka@email.cz"); //TODO: Change to bank's email
    }

    private String extractVariableSymbol(String content) {
        int index = content.indexOf("Variable Symbol:");
        if (index != -1) {
            String symbol = content.substring(index + 16).split("\\s+")[0];
            return symbol.trim();
        }
        return null;
    }

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

    private boolean validateAndProcessPayment(String variableSymbol, int amount) {
        try {
            Optional<Objednavka> orderOpt = objednavkaRepository.findById(Integer.parseInt(variableSymbol));

            if (orderOpt.isPresent()) {
                Objednavka order = orderOpt.get();

                if (order.getCena().equals(amount)) {
                    order.setStatus("P");
                    objednavkaRepository.save(order);

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