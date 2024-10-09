package ruzicka.ets;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import ruzicka.ets.controller.TicketController;
import ruzicka.ets.db.Zakaznik;
import ruzicka.ets.repository.ZakaznikRepository;
import ruzicka.ets.repository.ObjednavkaRepository;
import ruzicka.ets.service.EmailService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TicketControllerTest {

    @Mock
    private ZakaznikRepository zakaznikRepository;

    @Mock
    private ObjednavkaRepository objednavkaRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private TicketController ticketController;

    @Test
    void testLoginOrRegister_NewUser() {
        // Mocking a scenario where the user doesn't exist in the repository
        when(zakaznikRepository.findByMail(any(String.class))).thenReturn(Optional.empty());

        // Execute the method
        ResponseEntity<?> response = ticketController.loginOrRegister("newuser@example.com");

        // Verify
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Verification email sent"));

        // Verify that the email service is triggered for new users
        Mockito.verify(emailService).sendVerificationEmail(any(Zakaznik.class), any(String.class), any(String.class));
    }

    @Test
    void testLoginOrRegister_ExistingVerifiedUser() {
        // Create a mock verified user
        Zakaznik mockUser = new Zakaznik();
        mockUser.setStatus("V"); // Verified

        // Mock the repository response
        when(zakaznikRepository.findByMail("verifieduser@example.com")).thenReturn(Optional.of(mockUser));

        // Execute the method
        ResponseEntity<?> response = ticketController.loginOrRegister("verifieduser@example.com");

        // Verify
        assertEquals(200, response.getStatusCodeValue());
        // You can also check if the orders were retrieved (in case you're returning them)
    }

    @Test
    void testLoginOrRegister_ExistingUnverifiedUser() {
        // Create a mock unverified user
        Zakaznik mockUser = new Zakaznik();
        mockUser.setStatus("P"); // Pending

        // Mock the repository response
        when(zakaznikRepository.findByMail("unverifieduser@example.com")).thenReturn(Optional.of(mockUser));

        // Execute the method
        ResponseEntity<?> response = ticketController.loginOrRegister("unverifieduser@example.com");

        // Verify
        assertEquals(403, response.getStatusCodeValue());
        assertEquals("Please verify your email.", response.getBody());
    }

    @Test
    void testVerifyEmail_Success() {
        // Create a mock unverified user
        Zakaznik mockUser = new Zakaznik();
        mockUser.setStatus("P"); // Pending

        // Mock the repository response
        when(zakaznikRepository.findByMail("user@example.com")).thenReturn(Optional.of(mockUser));

        // Execute the method
        ResponseEntity<String> response = ticketController.verifyEmail("user@example.com", 1);

        // Verify
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Email verified successfully.", response.getBody());

        // Ensure the user status was updated
        Mockito.verify(zakaznikRepository).save(mockUser);
    }

    @Test
    void testVerifyEmail_Failure() {
        // Mock the repository response with an empty optional (user not found)
        when(zakaznikRepository.findByMail("invaliduser@example.com")).thenReturn(Optional.empty());

        // Execute the method
        ResponseEntity<String> response = ticketController.verifyEmail("invaliduser@example.com", 1);

        // Verify
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Email verification failed. Invalid token or email.", response.getBody());
    }
}
