package ruzicka.ets.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ruzicka.ets.db.Typmista;
import ruzicka.ets.db.misto;
import ruzicka.ets.repository.MistoRepository;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MistoRepository mistoRepository;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    public void testGetEventInfo() throws Exception {
        Typmista typMista = new Typmista();
        typMista.setCena(2000);

        misto mistoEntity = new misto();
        mistoEntity.setAdresa(1);
        mistoEntity.setAvailableQuantity(10);
        mistoEntity.setIdtypmista(typMista); // Set the TypMista object

        List<misto> mistoList = List.of(mistoEntity);

        // Mock repository behavior
        when(mistoRepository.findAll())

                .thenReturn(mistoList);

        // Perform the GET request and validate the response
        mockMvc.perform(get("/misto")
                        .param("adresa", "1")
                       )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].adresa").value(1))
                .andExpect(jsonPath("$[0].avaiablequantity").value(10))
                .andExpect(jsonPath("$[0].cena").value(2000));
    }
}