//// src/test/java/ruzicka/ets/service/EmailCheckerServiceTest.java
//package ruzicka.ets;
//
//import jakarta.mail.*;
//import jakarta.mail.internet.InternetAddress;
//import jakarta.mail.search.FlagTerm;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.test.util.ReflectionTestUtils;
//import ruzicka.ets.db.Objednavka;
//import ruzicka.ets.repository.ObjednavkaRepository;
//import ruzicka.ets.repository.ZakaznikRepository;
//import ruzicka.ets.service.EmailCheckerService;
//
//import java.io.IOException;
//import java.util.Optional;
//import java.util.Properties;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class EmailCheckerServiceTest {
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
//    private Session session;
//
//    @Mock
//    private Store store;
//
//    @Mock
//    private Folder inbox;
//
//    @Mock
//    private Message message;
//
//    @Value("${email.username}")
//    private String username;
//
//    @Value("${email.password}")
//    private String password;
//
//    @BeforeEach
//    public void setUp() throws Exception {
//        ReflectionTestUtils.setField(emailCheckerService, "username", "testuser");
//        ReflectionTestUtils.setField(emailCheckerService, "password", "testpass");
//
//        when(session.getStore("imaps")).thenReturn(store);
//        when(store.getFolder("inbox")).thenReturn(inbox);
//    }
//
//    @Test
//    public void testStartEmailCheckingService() {
//        emailCheckerService.startEmailCheckingService();
//        // Verify that a new thread is started
//        verify(emailCheckerService, times(1)).startEmailCheckingService();
//    }
//
//    @Test
//    public void testCheckForNewEmails() throws MessagingException, IOException {
//        when(inbox.search(any(FlagTerm.class))).thenReturn(new Message[]{message});
//        when(message.getFrom()).thenReturn(new Address[]{new InternetAddress("adam.ruzicka@email.cz")});
//        when(message.getContent()).thenReturn("Variable Symbol: 12345\nAmount: 100");
//
//        emailCheckerService.checkForNewEmails();
//
//        verify(inbox, times(1)).open(Folder.READ_WRITE);
//        verify(inbox, times(1)).close(false);
//        verify(store, times(1)).close();
//    }
//
//    @Test
//    public void testExtractVariableSymbol() {
//        String content = "Variable Symbol: 12345\nAmount: 100";
//        String variableSymbol = emailCheckerService.extractVariableSymbol(content);
//        assertEquals("12345", variableSymbol);
//    }
//
//    @Test
//    public void testExtractAmount() {
//        String content = "Variable Symbol: 12345\nAmount: 100";
//        int amount = emailCheckerService.extractAmount(content);
//        assertEquals(100, amount);
//    }
//
//    @Test
//    public void testValidateAndProcessPayment() {
//        Objednavka order = new Objednavka();
//        order.setId(12345);
//        order.setCena(100);
//
//        when(objednavkaRepository.findById(12345)).thenReturn(Optional.of(order));
//
//        boolean result = emailCheckerService.validateAndProcessPayment("12345", 100);
//        assertTrue(result);
//        verify(objednavkaRepository, times(1)).save(order);
//    }
//}