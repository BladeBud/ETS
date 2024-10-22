package ruzicka.ets.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import ruzicka.ets.db.Zakaznik;
import ruzicka.ets.repository.ZakaznikRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailVerificationServiceTest {

    @Mock
    private JavaMailSender emailSender;

    @Mock
    private ZakaznikRepository zakaznikRepository;

    @InjectMocks
    private EmailVerificationService emailVerificationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendVerificationEmail() {
        Zakaznik zakaznik = new Zakaznik();
        zakaznik.setIdzakaznik(1);
        zakaznik.setMail("test@example.com");

        emailVerificationService.sendVerificationEmail(zakaznik, "Test Subject", "Test Message");

        verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void testVerifyEmail_Success() {
        Zakaznik zakaznik = new Zakaznik();
        zakaznik.setIdzakaznik(1);
        zakaznik.setMail("test@example.com");

        when(zakaznikRepository.findByMail("test@example.com")).thenReturn(Optional.of(zakaznik));

        boolean result = emailVerificationService.verifyEmail("test@example.com", 1);

        assertTrue(result);
        verify(zakaznikRepository, times(1)).save(any(Zakaznik.class));
    }

    @Test
    public void testVerifyEmail_Failure() {
        when(zakaznikRepository.findByMail("test@example.com")).thenReturn(Optional.empty());

        boolean result = emailVerificationService.verifyEmail("test@example.com", 1);

        assertFalse(result);
        verify(zakaznikRepository, never()).save(any(Zakaznik.class));
    }
}