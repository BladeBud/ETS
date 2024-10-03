package ruzicka.ets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ruzicka.ets.controller.OrderController;
import ruzicka.ets.db.Typmista;
import ruzicka.ets.db.misto;
import ruzicka.ets.repository.MistoRepository;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MistoRepository mistoRepository;

    @Test
    public void testGetEventInfo() throws Exception {
        Typmista typmista = new Typmista();
        typmista.setCena(100);

        misto misto1 = new misto();
        misto1.setAdresa(123);
        misto1.setAvailableQuantity(50);
        misto1.setIdtypmista(typmista);

        List<misto> mistoList = Arrays.asList(misto1);

        when(mistoRepository.findByAdresaAndAvailableQuantity(123, 50)).thenReturn(mistoList);

        mockMvc.perform(get("/misto")
                        .param("adresa", "123")
                        .param("quantityavaiable", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].adresa").value(123))
                .andExpect(jsonPath("$[0].avaiableQuantity").value(50))
                .andExpect(jsonPath("$[0].cena").value(100));
    }
}