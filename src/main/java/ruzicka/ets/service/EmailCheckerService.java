package ruzicka.ets.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import jakarta.annotation.PostConstruct;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.mail.search.FlagTerm;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ruzicka.ets.db.Zakaznik;
import ruzicka.ets.db.Objednavka;
import ruzicka.ets.repository.ObjednavkaRepository;
import ruzicka.ets.repository.ZakaznikRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmailCheckerService {
//----------------------------------------------------------------------------------
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
//-----------------------------------------------------------------------------------
    @PostConstruct
    public void init() {
        startEmailCheckingService();
    }

    public void startEmailCheckingService() {
        new Thread(this::checkEmailLoop).start();
    }
//-----------------------------------------------------------------------------------
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
//-----------------------------------------------------------------------------------
    private void checkForNewEmails() throws MessagingException, IOException {
        System.out.println("Connecting to the email server...");

        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        Session session = Session.getDefaultInstance(properties, null);

        Store store = session.getStore("imaps");
        store.connect(HOST, username, password);

        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);

        Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
        System.out.println("Found " + messages.length + " unread emails.");

        for (Message message : messages) {
            System.out.println("Processing email from: " + message.getFrom()[0].toString());

            if (isBankEmail(message)) {
                System.out.println("Email is from the bank. Extracting content...");

                String content = getTextFromMessage(message);
                System.out.println("Email content extracted: " + content);

                String variableSymbol = extractVariableSymbol(content);
                double amount = extractAmount(content);

                System.out.println("Extracted variable symbol: " + variableSymbol);
                System.out.println("Extracted amount: " + amount);

                if (variableSymbol != null && amount > 0 && validateAndProcessPayment(variableSymbol, amount)) {
                    System.out.println("Payment verified for order with symbol: " + variableSymbol);
                } else {
                    System.out.println("Payment validation failed for email.");
                }

                message.setFlag(Flags.Flag.SEEN, true);
                System.out.println("Email marked as read.");
            } else {
                System.out.println("Email is not from the bank. Skipping.");
            }
        }

        inbox.close(false);
        store.close();
        System.out.println("Disconnected from the email server.");
    }
//-----------------------------------------------------------------------------------
    private boolean isBankEmail(Message message) throws MessagingException {
        boolean isFromBank = message.getFrom()[0].toString().contains("automat@fio.cz");
        System.out.println("Checking if email is from the bank: " + isFromBank);
        return isFromBank;
    }

    private String extractVariableSymbol(String content) {

        Pattern pattern = Pattern.compile("VS:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return matcher.group(1).trim(); // Vrátí nalezený VS
        }
        return null; // Pokud VS nenajde, vrátíme null
    }

    private double extractAmount(String content) {
        try {
            // Použije regulární výraz k nalezení částky ve formátu X,XX
            Pattern pattern = Pattern.compile("Částka:\\s*([\\d,]+)");
            Matcher matcher = pattern.matcher(content);

            if (matcher.find()) {
                String amountStr = matcher.group(1).replace(",", "."); // Nahrazení čárky tečkou pro parsování
                return Double.parseDouble(amountStr.trim());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0.0; // Pokud se nepodaří částku najít nebo převést, vrát 0
    }
//-----------------------------------------------------------------------------------
    private boolean validateAndProcessPayment(String variableSymbol, double amount) {
        try {
            Optional<Objednavka> orderOpt = objednavkaRepository.findById(Integer.parseInt(variableSymbol));

            if (orderOpt.isPresent()) {
                Objednavka order = orderOpt.get();

                if (order.getCena() == amount) {
                    order.setStatus("P"); // Payment processed
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

//-----------------------------------------------------------------------------------
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

            // Create the MimeMessage
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Set the email details
            helper.setFrom(username); // Use your email address
            helper.setTo(userEmail);
            helper.setSubject(subject);
            helper.setText(bodyText);

            // Generate the ticket file
            File ticketFile = generateTicketFile(order);
            FileSystemResource file = new FileSystemResource(ticketFile);
            helper.addAttachment("Ticket_" + order.getId() + ".pdf", file);

            // Send the email
            emailSender.send(message);
            System.out.println("Tickets sent to " + userEmail + " for order ID: " + order.getId());
        } else {
            System.out.println("Zakaznik not found for order ID: " + order.getId());
        }
    } catch (MessagingException e) {
        e.printStackTrace();
        System.out.println("Failed to send ticket email for order ID: " + order.getId() + " due to messaging error: " + e.getMessage());
    } catch (IOException e) {
        e.printStackTrace();
        System.out.println("Failed to generate or send ticket for order ID: " + order.getId() + " due to IO error: " + e.getMessage());
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("An unexpected error occurred while sending ticket email for order ID: " + order.getId());
    }
}
//-----------------------------------------------------------------------------------
    public File generateTicketFile(Objednavka order) throws IOException {
        File tempFile = File.createTempFile("Ticket_" + order.getId(), ".pdf");

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Customer Name: " + order.getIdzakaznik().getJmeno());
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(50, 730);
                contentStream.showText("Address: " + order.getIdmisto().getAdresa());
                contentStream.endText();

                String qrCodeData = "Order ID: " + order.getId() + ", Customer Name: " + order.getIdzakaznik().getJmeno();
                String qrCodeFilePath = generateQRCodeImage(qrCodeData, 150, 150);

                BufferedImage qrImage = ImageIO.read(new File(qrCodeFilePath));
                PDImageXObject pdImage = PDImageXObject.createFromFile(qrCodeFilePath, document);
                contentStream.drawImage(pdImage, 50, 550);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            document.save(tempFile);
        }

        return tempFile;
    }

    private String generateQRCodeImage(String text, int width, int height) throws Exception {
        String filePath = UUID.randomUUID() + "_QRCode.png";
        Path path = FileSystems.getDefault().getPath(filePath);

        BitMatrix matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height);
        MatrixToImageWriter.writeToPath(matrix, "PNG", path);

        return filePath;
    }
//-----------------------------------------------------------------------------------
    private String getTextFromMessage(Message message) throws IOException, MessagingException {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            return getTextFromMimeMultipart(mimeMultipart);
        }
        return "";
    }

    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws IOException, MessagingException {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent());
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result.append(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }
}