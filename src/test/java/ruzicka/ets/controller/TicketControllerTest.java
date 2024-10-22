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

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TicketControllerTest {

    @Mock
    private ZakaznikRepository zakaznikRepository;

    @Mock
    private ObjednavkaRepository objednavkaRepository;

    @Mock
    private EmailVerificationService emailService;

    @InjectMocks
    private TicketController ticketController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginOrRegister_ExistingVerifiedUser() {
        String email = "verified@example.com";
        Zakaznik user = new Zakaznik();
        user.setMail(email);
        user.setStatus("V");
        when(zakaznikRepository.findByMail(email)).thenReturn(Optional.of(user));
        when(objednavkaRepository.findByIdzakaznik_Idzakaznik(user.getIdzakaznik()))
                .thenReturn(Collections.singletonList(new Objednavka()));

        ResponseEntity<?> response = ticketController.loginOrRegister(email);

        assertEquals(200, response.getStatusCodeValue());
        verify(zakaznikRepository, times(1)).findByMail(email);
        verify(objednavkaRepository, times(1)).findByIdzakaznik_Idzakaznik(user.getIdzakaznik());
    }

    @Test
    void testLoginOrRegister_ExistingUnverifiedUser() {
        String email = "pending@example.com";
        Zakaznik user = new Zakaznik();
        user.setMail(email);
        user.setStatus("P");
        when(zakaznikRepository.findByMail(email)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = ticketController.loginOrRegister(email);

        assertEquals(403, response.getStatusCodeValue());
        verify(zakaznikRepository, times(1)).findByMail(email);
    }

    @Test
    void testLoginOrRegister_NewUser() {
        String email = "newuser@example.com";
        when(zakaznikRepository.findByMail(email)).thenReturn(Optional.empty());

        ResponseEntity<?> response = ticketController.loginOrRegister(email);

        assertEquals(200, response.getStatusCodeValue());
        verify(zakaznikRepository, times(1)).findByMail(email);
        verify(zakaznikRepository, times(1)).save(any(Zakaznik.class));
        verify(emailService, times(1)).sendVerificationEmail(any(Zakaznik.class), eq("Verify your email"), eq("Please verify your email address."));
    }
}