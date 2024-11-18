package ruzicka.ets.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import jakarta.annotation.PostConstruct;
import jakarta.mail.BodyPart;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.search.FlagTerm;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ruzicka.ets.db.Misto;
import ruzicka.ets.db.MistoObjednavka;
import ruzicka.ets.db.Objednavka;
import ruzicka.ets.db.Stul;
import ruzicka.ets.db.Zakaznik;
import ruzicka.ets.repository.MistoRepository;
import ruzicka.ets.repository.ObjednavkaRepository;
import ruzicka.ets.repository.StulRepository;
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
    // ----------------------------------------------------------------------------------------------------------------------
    private static final Logger log = LoggerFactory.getLogger(EmailCheckerService.class);
    private static final Logger importantLog = LoggerFactory.getLogger("important");
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

    @Autowired
    private MistoRepository mistoRepository;

    @Autowired
    private StulRepository stulRepository;
//----------------------------------------------------------------------------------------------------------------------
    @PostConstruct
    public void init() {
        startEmailCheckingService();
    }

    public void startEmailCheckingService() {
        new Thread(this::checkEmailLoop).start();
    }

    private void checkEmailLoop() {
       // log.info("Starting email checking loop...");
        while (true) {
            try {
               // log.info("Checking for new emails...");
                checkForNewEmails();
               // log.info("Sleeping for 60 seconds before next check...");
                Thread.sleep(60000); // Check every 60 seconds
            } catch (Exception e) {
                log.error("Error in email checking loop", e);
            }
        }
    }
//----------------------------------------------------------------------------------------------------------------------
    private void checkForNewEmails() throws MessagingException, IOException {
      //  log.info("Connecting to the email server...");

        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        Session session = Session.getDefaultInstance(properties, null);

        Store store = session.getStore("imaps");
        store.connect(HOST, username, password);

        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);

        Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
        log.info("Found {} unread emails.", messages.length);

        for (Message message : messages) {
            log.info("Processing email from: {}", message.getFrom()[0].toString());

            if (isBankEmail(message)) {
                log.info("Email is from the bank. Extracting content...");

                String content = getTextFromMessage(message);
                log.info("Email content extracted: {}", content);

                String variableSymbol = extractVariableSymbol(content);
                double amount = extractAmount(content);

                log.info("Extracted variable symbol: {}", variableSymbol);
                log.info("Extracted amount: {}", amount);

                if (variableSymbol != null && amount > 0 && validateAndProcessPayment(variableSymbol, amount)) {
                    log.info("Payment verified for order with symbol: {}", variableSymbol);
                } else {
                    log.warn("Payment validation failed for email.");
                }

                message.setFlag(Flags.Flag.SEEN, true);
               // log.info("Email marked as read.");
            } else {
                log.info("Email is not from the bank. Skipping.");
            }
        }

        inbox.close(false);
        store.close();
       // log.info("Disconnected from the email server.");
    }
//----------------------------------------------------------------------------------------------------------------------
private boolean isBankEmail(Message message) throws MessagingException {
    String fromEmail = message.getFrom()[0].toString();
    boolean isFromBank = fromEmail.contains("automat@fio.cz") || fromEmail.contains("25heyrovskeho@seznam.cz"); //TODO: change if needed
    log.info("Checking if email is from the bank: {}", isFromBank);
    return isFromBank;
}
//----------------------------------------------------------------------------------------------------------------------
    private String extractVariableSymbol(String content) {
        Pattern pattern = Pattern.compile("VS:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }
//----------------------------------------------------------------------------------------------------------------------
    private double extractAmount(String content) {
        try {
            Pattern pattern = Pattern.compile("Částka:\\s*([\\d\\s,]+)");
            Matcher matcher = pattern.matcher(content);

            if (matcher.find()) {
                String amountStr = matcher.group(1).replace(" ", "").replace(",", ".");
                return Double.parseDouble(amountStr.trim());
            }
        } catch (NumberFormatException e) {
            log.error("Error parsing amount", e);
        }
        return 0.0;
    }
//----------------------------------------------------------------------------------------------------------------------
    private boolean validateAndProcessPayment(String variableSymbol, double amount) {
        try {
            Optional<Objednavka> orderOpt = objednavkaRepository.findById(Integer.parseInt(variableSymbol));

            if (orderOpt.isPresent()) {
                Objednavka order = orderOpt.get();

                if ("E".equals(order.getStatus())) {
                    log.warn("Order with symbol {} is expired. Checking availability of ordered places...", variableSymbol);

                    // Check if all ordered places are still available
                    boolean allPlacesAvailable = order.getMistoObjednavkaList().stream()
                            .allMatch(mistoObjednavka -> {
                                Optional<Misto> mistoOpt = mistoRepository.findById(mistoObjednavka.getIdmisto());
                                return mistoOpt.isPresent() && Misto.Status.A.name().equals(mistoOpt.get().getStatus());
                            });

                    if (!allPlacesAvailable) {
                        log.warn("Not all ordered places are available for expired order: {}", variableSymbol);
                        return false;
                    }

                    // Check if the payment amount matches the order's total price
                    if (order.getCena() != amount) {
                        log.warn("Amount mismatch for expired order: {}", variableSymbol);
                        return false;
                    }

                    // Reserve the places
                    for (MistoObjednavka mistoObjednavka : order.getMistoObjednavkaList()) {
                        Optional<Misto> mistoOpt = mistoRepository.findById(mistoObjednavka.getIdmisto());
                        if (mistoOpt.isPresent()) {
                            Misto misto = mistoOpt.get();
                            misto.setStatus(Misto.Status.R.name());
                            mistoRepository.save(misto);

                            Stul stul = misto.getStul();
                            stul.setAvailableQuantity(stul.getAvailableQuantity() - 1);
                            stulRepository.save(stul);
                        }
                    }

                    order.setStatus("P");
                    objednavkaRepository.save(order);

                    sendTicketEmail(order);

                    importantLog.info("Expired order with symbol {} is now paid and places are reserved.", variableSymbol);
                    return true;
                }


                if (order.getCena() == amount) {
                    order.setStatus("P");
                    objednavkaRepository.save(order);

                    sendTicketEmail(order);
                    importantLog.info("Payment verified and order processed for symbol: {}", variableSymbol);
                    return true;
                } else {
                    log.warn("Amount mismatch for order: {}", variableSymbol);
                }
            } else {
                log.warn("No matching order found for symbol: {}", variableSymbol);
            }
        } catch (Exception e) {
            log.error("Error validating and processing payment", e);
        }
        return false;
    }
//----------------------------------------------------------------------------------------------------------------------
private void sendTicketEmail(Objednavka order) {
    try {
        Optional<Zakaznik> zakaznikOpt = zakaznikRepository.findById(order.getIdzakaznik().getIdzakaznik());

        if (zakaznikOpt.isPresent()) {
            Zakaznik zakaznik = zakaznikOpt.get();
            String userEmail = zakaznik.getMail();
            String subject = "Lístky pro objednávku: " + order.getId();
//            String bodyText = "Dobrý den " + zakaznik.getJmeno() + ",\n\n"
            String bodyText = "Dobrý den " + ",\n\n"
                    + "Děkujeme za zakoupení lístků. Lístky můžete najít v příloze.\n\n"
                    + "Naviděnou,\n"
                    + "Vaše Oktávy";

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(username);
            helper.setTo(userEmail);
            helper.setSubject(subject);
            helper.setText(bodyText);

            for (MistoObjednavka mistoObjednavka : order.getMistoObjednavkaList()) {
                Optional<Misto> mistoOpt = mistoRepository.findById(mistoObjednavka.getIdmisto());
                if (mistoOpt.isPresent()) {
                    Misto misto = mistoOpt.get();
                    String nazev = misto.getStul().getNazev();
                    File ticketFile = generateTicketFile(order, nazev);
                    FileSystemResource file = new FileSystemResource(ticketFile);
                    helper.addAttachment("Ticket_" + order.getId() + "_" + nazev + ".pdf", file);
                }
            }

            emailSender.send(message);
            log.info("Tickets sent to {} for order ID: {}", userEmail, order.getId());
            importantLog.info("Tickets sent to {} for order ID: {}", userEmail, order.getId());

        } else {
            log.warn("Zakaznik not found for order ID: {}", order.getId());
        }
    } catch (MessagingException e) {
        log.error("Failed to send ticket email for order ID: {} due to messaging error: {}", order.getId(), e.getMessage());
    } catch (IOException e) {
        log.error("Failed to generate or send ticket for order ID: {} due to IO error: {}", order.getId(), e.getMessage());
    } catch (Exception e) {
        log.error("An unexpected error occurred while sending ticket email for order ID: {}", order.getId(), e);
    }
}
//----------------------------------------------------------------------------------------------------------------------
public File generateTicketFile(Objednavka order, String seatName) throws IOException {
    File tempFile = File.createTempFile("Ticket_" + order.getId() + "_" + seatName, ".pdf");

    try (PDDocument document = new PDDocument()) {
        PDPage page = new PDPage();
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 750);
            //contentStream.showText("Jméno: " + order.getIdzakaznik().getJmeno());
            contentStream.showText("Maturitní ples GJH 4/12/24 19:00");
            contentStream.endText();


            contentStream.beginText();
            contentStream.newLineAtOffset(50, 730);
            contentStream.showText("Místo: " + seatName);
            contentStream.endText();

            String qrCodeData = "Id objednávky: " + order.getId() + ", Jméno: " + order.getIdzakaznik().getJmeno() + ", Místo: " + seatName;
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
//----------------------------------------------------------------------------------------------------------------------
    private String generateQRCodeImage(String text, int width, int height) throws Exception {
        String filePath = UUID.randomUUID() + "_QRCode.png";
        Path path = FileSystems.getDefault().getPath(filePath);

        BitMatrix matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height);
        MatrixToImageWriter.writeToPath(matrix, "PNG", path);

        return filePath;
    }
//----------------------------------------------------------------------------------------------------------------------
    private String getTextFromMessage(Message message) throws IOException, MessagingException {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            return getTextFromMimeMultipart(mimeMultipart);
        }
        return "";
    }
//----------------------------------------------------------------------------------------------------------------------
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