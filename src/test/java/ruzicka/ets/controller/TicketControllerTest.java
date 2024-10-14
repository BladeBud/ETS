package ruzicka.ets.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ruzicka.ets.controller.TicketController;
import ruzicka.ets.db.Zakaznik;
import ruzicka.ets.db.Objednavka;
import ruzicka.ets.repository.ZakaznikRepository;
import ruzicka.ets.repository.ObjednavkaRepository;
import ruzicka.ets.service.EmailVerificationService;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TicketControllerTest {

    @Mock
    private ZakaznikRepository zakaznikRepository;

    @Mock
    private ObjednavkaRepository objednavkaRepository;

    @Mock
    private EmailVerificationService emailService;

    @InjectMocks
    private TicketController ticketController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(ticketController).build();
    }

    @Test
    public void testLoginOrRegister_ExistingVerifiedUser() throws Exception {
        Zakaznik user = new Zakaznik();
        user.setIdzakaznik(1);
        user.setMail("test@example.com");
        user.setStatus("V");

        when(zakaznikRepository.findByMail("test@example.com")).thenReturn(Optional.of(user));
        when(objednavkaRepository.findByIdzakaznik_Idzakaznik(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/api/tickets/login-register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"test@example.com\""))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    public void testLoginOrRegister_ExistingUnverifiedUser() throws Exception {
        Zakaznik user = new Zakaznik();
        user.setIdzakaznik(1);
        user.setMail("test@example.com");
        user.setStatus("P");

        when(zakaznikRepository.findByMail("test@example.com")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/tickets/login-register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"test@example.com\""))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Please verify your email."));
    }

    @Test
    public void testLoginOrRegister_NewUser() throws Exception {
        when(zakaznikRepository.findByMail("test@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/tickets/login-register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"test@example.com\""))
                .andExpect(status().isOk())
                .andExpect(content().string("Verification email sent."));

        verify(zakaznikRepository, times(1)).save(any(Zakaznik.class));
        verify(emailService, times(1)).sendVerificationEmail(any(Zakaznik.class), eq("Verify your email"), eq("Please verify your email address."));
    }

    @Test
    public void testVerifyEmail_Success() throws Exception {
        when(emailService.verifyEmail("test@example.com", 1)).thenReturn(true);

        mockMvc.perform(get("/api/tickets/verify-email")
                        .param("email", "test@example.com")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Email verified successfully."));
    }

    @Test
    public void testVerifyEmail_Failure() throws Exception {
        when(emailService.verifyEmail("test@example.com", 1)).thenReturn(false);

        mockMvc.perform(get("/api/tickets/verify-email")
                        .param("email", "test@example.com")
                        .param("id", "1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Email verification failed. Invalid ID or email."));
    }
}