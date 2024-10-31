package ruzicka.ets.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import ruzicka.ets.db.Objednavka;
import ruzicka.ets.db.Zakaznik;
import ruzicka.ets.repository.ObjednavkaRepository;
import ruzicka.ets.repository.ZakaznikRepository;
import ruzicka.ets.service.EmailVerificationService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TicketControllerTest {

    @Mock
    private ZakaznikRepository zakaznikRepository;

    @Mock
    private ObjednavkaRepository objednavkaRepository;

    @Mock
    private EmailVerificationService emailService;

    @InjectMocks
    private TicketController ticketController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLoginOrRegister_userVerified() {
        String email = "verified@example.com";
        Zakaznik user = new Zakaznik();
        user.setMail(email);
        user.setStatus("V");
        user.setIdzakaznik(1);

        List<Objednavka> orders = Arrays.asList(new Objednavka());

        when(zakaznikRepository.findByMail(email)).thenReturn(Optional.of(user));
        when(objednavkaRepository.findByIdzakaznik_Idzakaznik(user.getIdzakaznik())).thenReturn(orders);

        ResponseEntity<?> response = ticketController.loginOrRegister(email);

        verify(zakaznikRepository, times(1)).findByMail(email);
        verify(objednavkaRepository, times(1)).findByIdzakaznik_Idzakaznik(user.getIdzakaznik());
        assertEquals(ResponseEntity.ok(orders), response);
    }

    @Test
    public void testLoginOrRegister_userNotVerified() {
        String email = "unverified@example.com";
        Zakaznik user = new Zakaznik();
        user.setMail(email);
        user.setStatus("P");

        when(zakaznikRepository.findByMail(email)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = ticketController.loginOrRegister(email);

        verify(zakaznikRepository, times(1)).findByMail(email);
        verify(objednavkaRepository, never()).findByIdzakaznik_Idzakaznik(anyInt());
        assertEquals(ResponseEntity.status(403).body("Please verify your email."), response);
    }

    @Test
    public void testLoginOrRegister_newUser() {
        String email = "newuser@example.com";
        Zakaznik newUser = new Zakaznik();
        newUser.setMail(email);
        newUser.setStatus("P");

        when(zakaznikRepository.findByMail(email)).thenReturn(Optional.empty());
        when(zakaznikRepository.save(any(Zakaznik.class))).thenReturn(newUser);

        ResponseEntity<?> response = ticketController.loginOrRegister(email);

        verify(zakaznikRepository, times(1)).findByMail(email);
        verify(zakaznikRepository, times(1)).save(any(Zakaznik.class));
        verify(emailService, times(1)).sendVerificationEmail(any(Zakaznik.class), eq("Ověření mailu Ples"), eq("Prosím ověřte svůj mail."));
        assertEquals(ResponseEntity.ok("Verification email sent."), response);
    }

    @Test
    public void testVerifyEmail_success() {
        String email = "verify@example.com";
        Integer id = 1;

        when(emailService.verifyEmail(email, id)).thenReturn(true);

        ResponseEntity<String> response = ticketController.verifyEmail(email, id);

        verify(emailService, times(1)).verifyEmail(email, id);
        assertEquals(ResponseEntity.ok("Email verified successfully."), response);
    }

    @Test
    public void testVerifyEmail_fail() {
        String email = "verify@example.com";
        Integer id = 1;

        when(emailService.verifyEmail(email, id)).thenReturn(false);

        ResponseEntity<String> response = ticketController.verifyEmail(email, id);

        verify(emailService, times(1)).verifyEmail(email, id);
        assertEquals(ResponseEntity.status(404).body("Email verification failed. Invalid ID or email."), response);
    }
}