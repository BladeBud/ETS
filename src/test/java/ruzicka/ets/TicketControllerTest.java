package ruzicka.ets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ruzicka.ets.controller.TicketController;
import ruzicka.ets.db.Zakaznik;
import ruzicka.ets.repository.ZakaznikRepository;


import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;  // Changed to 'get'
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketController.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketController ticketController;

    @MockBean
    private ZakaznikRepository zakaznikRepository;

    @BeforeEach
    void setUp() {
        // Mock behavior for zakaznikRepository
        when(zakaznikRepository.findByMail(anyString())).thenReturn(Optional.empty());
    }

    @Test
    void testVerifyEmail_Success() throws Exception {
        // Define the behavior of the mock when a specific email is checked
        when(zakaznikRepository.findByMail("test@example.com"))
                .thenReturn(Optional.of(new Zakaznik()));

        // Perform a GET request to the /api/tickets/verify-email endpoint
        mockMvc.perform(get("/api/tickets/verify-email")
                        .param("email", "test@example.com"))  // Using GET method instead of POST
                .andExpect(status().isOk());
    }
}
