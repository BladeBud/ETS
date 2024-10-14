//package ruzicka.ets.service;
//
//import jakarta.mail.internet.InternetAddress;
//import jakarta.mail.search.FlagTerm;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.ArgumentCaptor;
//import org.springframework.mail.javamail.JavaMailSender;
//import ruzicka.ets.db.Objednavka;
//import ruzicka.ets.db.Zakaznik;
//import ruzicka.ets.repository.ObjednavkaRepository;
//import ruzicka.ets.repository.ZakaznikRepository;
//
//import jakarta.mail.*;
//import jakarta.mail.internet.MimeMessage;
//
//import java.io.IOException;
//import java.util.Optional;
//import java.util.Properties;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class EmailCheckerServiceTest {
//
//    @InjectMocks
//    private EmailCheckerService emailCheckerService;
//
//    @Mock
//    private ObjednavkaRepository objednavkaRepository;
//
//    @Mock
//    private ZakaznikRepository zakaznikRepository;
//
//    @Mock
//    private JavaMailSender emailSender;
//
//    @Mock
//    private Store store;
//
//    @Mock
//    private Folder folder;
//
//    @Mock
//    private Session session;
//
//    @Mock
//    private Message message;
//
//    @Mock
//    private Objednavka objednavka;
//
//    @Mock
//    private Zakaznik zakaznik;
//
//    @BeforeEach
//    void setUp() throws MessagingException {
//        MockitoAnnotations.openMocks(this);
//
//        // Mock session and store
//        when(session.getStore("imaps")).thenReturn(store);
//        when(store.getFolder("inbox")).thenReturn(folder);
//    }
//
//    @Test
//    void testCheckForNewEmails_withValidBankEmailAndPaymentProcessed() throws Exception {
//        // Arrange
//        String mockContent = "Variable Symbol: 12345\nAmount: 100";
//        when(folder.search(any(FlagTerm.class))).thenReturn(new Message[]{message});
//        when(message.getFrom()).thenReturn(new Address[]{new InternetAddress("adam.ruzicka@email.cz")});
//        when(emailCheckerService.getTextFromMessage(message)).thenReturn(mockContent);
//
//        // Mock order and customer retrieval
//        when(objednavkaRepository.findById(12345)).thenReturn(Optional.of(objednavka));
//        when(objednavka.getCena()).thenReturn(100);
//        when(zakaznikRepository.findById(anyInt())).thenReturn(Optional.of(zakaznik));
//        when(zakaznik.getMail()).thenReturn("customer@email.com");
//
//        // Act
//        emailCheckerService.checkForNewEmails();
//
//        // Assert
//        verify(objednavka).setStatus("P");
//        verify(objednavkaRepository).save(objednavka);
//        verify(emailSender).send(any(MimeMessage.class));
//    }
//
//    @Test
//    void testCheckForNewEmails_noBankEmailFound() throws Exception {
//        // Arrange
//        when(folder.search(any(FlagTerm.class))).thenReturn(new Message[]{});
//
//        // Act
//        emailCheckerService.checkForNewEmails();
//
//        // Assert
//        verify(objednavkaRepository, never()).findById(anyInt());
//        verify(emailSender, never()).send(any(MimeMessage.class));
//    }
//
//    @Test
//    void testExtractVariableSymbol() {
//        // Arrange
//        String content = "Variable Symbol: 12345\nAmount: 100";
//
//        // Act
//        String result = emailCheckerService.extractVariableSymbol(content);
//
//        // Assert
//        assertEquals("12345", result);
//    }
//
//    @Test
//    void testExtractAmount() {
//        // Arrange
//        String content = "Variable Symbol:12345\nAmount:100";
//
//        // Act
//        int result = emailCheckerService.extractAmount(content);
//
//        // Assert
//        assertEquals(100, result);
//    }
//
//    @Test
//    void testValidateAndProcessPayment_withMatchingOrder() {
//        // Arrange
//        when(objednavkaRepository.findById(12345)).thenReturn(Optional.of(objednavka));
//        when(objednavka.getCena()).thenReturn(100);
//
//        // Act
//        boolean result = emailCheckerService.validateAndProcessPayment("12345", 100);
//
//        // Assert
//        assertTrue(result);
//        verify(objednavka).setStatus("P");
//        verify(objednavkaRepository).save(objednavka);
//    }
//
//    @Test
//    void testValidateAndProcessPayment_withNonMatchingOrder() {
//        // Arrange
//        when(objednavkaRepository.findById(12345)).thenReturn(Optional.of(objednavka));
//        when(objednavka.getCena()).thenReturn(200);  // Amount mismatch
//
//        // Act
//        boolean result = emailCheckerService.validateAndProcessPayment("12345", 100);
//
//        // Assert
//        assertFalse(result);
//        verify(objednavka, never()).setStatus(anyString());
//        verify(objednavkaRepository, never()).save(objednavka);
//    }
//
//    @Test
//    void testSendTicketEmail() throws Exception {
//        // Arrange
//        when(zakaznikRepository.findById(anyInt())).thenReturn(Optional.of(zakaznik));
//        when(zakaznik.getMail()).thenReturn("customer@email.com");
//
//        // Capture the email being sent
//        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
//
//        // Act
//        emailCheckerService.sendTicketEmail(objednavka);
//
//        // Assert
//        verify(emailSender).send(captor.capture());
//        MimeMessage sentMessage = captor.getValue();
//        assertNotNull(sentMessage);
//        assertEquals("customer@email.com", sentMessage.getAllRecipients()[0].toString());
//    }
//
//}
