package ruzicka.ets;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import ruzicka.ets.controller.TicketController;
import ruzicka.ets.repository.ZakaznikRepository;
import ruzicka.ets.service.EmailService;
import ruzicka.ets.db.Zakaznik;

import java.util.Optional;

public class TicketControllerTest {

    @Mock
    private ZakaznikRepository zakaznikRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private TicketController ticketController;

    public TicketControllerTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLoginOrRegisterNewUser() {
        // Arrange
        String email = "test@example.com";
        when(zakaznikRepository.findByMail(email)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = ticketController.loginOrRegister(email);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(zakaznikRepository, times(1)).save(any(Zakaznik.class));
        verify(emailService, times(1)).sendVerificationEmail(eq(email), anyString(), anyString());
    }

    @Test
    public void testLoginOrRegisterExistingVerifiedUser() {
        // Arrange
        String email = "test@example.com";
        Zakaznik zakaznik = new Zakaznik();
        zakaznik.setMail(email);
        zakaznik.setStatus("V");

        when(zakaznikRepository.findByMail(email)).thenReturn(Optional.of(zakaznik));

        // Act
        ResponseEntity<?> response = ticketController.loginOrRegister(email);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        // Add additional asserts depending on the response content
    }

    @Test
    public void testLoginOrRegisterExistingUnverifiedUser() {
        // Arrange
        String email = "test@example.com";
        Zakaznik zakaznik = new Zakaznik();
        zakaznik.setMail(email);
        zakaznik.setStatus("PENDING");

        when(zakaznikRepository.findByMail(email)).thenReturn(Optional.of(zakaznik));

        // Act
        ResponseEntity<?> response = ticketController.loginOrRegister(email);

        // Assert
        assertEquals(403, response.getStatusCodeValue());
        assertEquals("Please verify your email.", response.getBody());
    }
}
