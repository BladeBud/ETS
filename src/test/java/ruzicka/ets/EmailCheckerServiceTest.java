//package ruzicka.ets;
//
//import jakarta.mail.MessagingException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import ruzicka.ets.db.Objednavka;
//import ruzicka.ets.repository.ObjednavkaRepository;
//import ruzicka.ets.service.EmailCheckerService;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.Mockito.when;
//
///**
// * @author czech
// * @since 2024-10-06
// */
//class EmailCheckerServiceTest {
//
//    @Mock
//    private ObjednavkaRepository objednavkaRepository;
//
//    @InjectMocks
//    private EmailCheckerService emailCheckerService;
//
//    @BeforeEach
//    void setUp() {
//        // Mock your IMAP connection setup if necessary here
//    }
//
//    @Test
//    void testProcessValidPaymentEmail() throws MessagingException {
//        // Mock email content
//        String emailContent = "Payment Confirmation\nVariable Symbol: 12345\nAmount: 1000 CZK";
//
//        // Create a mock order that should match the email content
//        Objednavka mockOrder = new Objednavka();
//        mockOrder.setId(12345);
//        mockOrder.setCena(1000);
//
//        // Mock repository call to find the order by symbol and amount
//        when(objednavkaRepository.findByStatusAndId("12345", 1)).thenReturn(List.of(mockOrder));
//
//        // Call the method
//        boolean result = emailCheckerService.validateAndProcessPayment("12345", 1000);
//
//        // Assert the result
//        assertTrue(result);
//
//        // Verify that the order was updated and saved
//        Mockito.verify(objednavkaRepository).save(mockOrder);
//    }
//
//    @Test
//    void testProcessInvalidPaymentEmail() throws MessagingException {
//        // Mock email content
//        String emailContent = "Payment Confirmation\nVariable Symbol: 12345\nAmount: 1000 CZK";
//
//        // No matching order in the repository
//        when(objednavkaRepository.findByStatusAndId("12345", 1)).thenReturn(new ArrayList<>());
//
//        // Call the method
//        boolean result = emailCheckerService.validateAndProcessPayment("12345", 1000);
//
//        // Assert the result
//        assertFalse(result);
//    }
//
//    @Test
//    void testExtractVariableSymbol() {
//        String emailContent = "Payment Confirmation\nVariable Symbol: 12345\nAmount: 1000 CZK";
//        String variableSymbol = emailCheckerService.extractVariableSymbol(emailContent);
//        assertEquals("12345", variableSymbol);
//    }
//
//    @Test
//    void testExtractAmount() {
//        String emailContent = "Payment Confirmation\nVariable Symbol: 12345\nAmount: 1000 CZK";
//        int amount = emailCheckerService.extractAmount(emailContent);
//        assertEquals(1000, amount);
//    }
//}