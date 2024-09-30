package ruzicka.ets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ruzicka.ets.controller.OrderController;
import ruzicka.ets.db.Objednavka;
import ruzicka.ets.db.misto;
import ruzicka.ets.repository.ObjednavkaRepository;
import ruzicka.ets.repository.MistoRepository;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author czech
 * @since 2023-09-30
 */
@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ObjednavkaRepository objednavkaRepository;

    @MockBean
    private MistoRepository mistoRepository;

    @Test
    public void testGetOrderDetails() throws Exception {
        Objednavka objednavka = new Objednavka();
        objednavka.setId(1);
        misto misto = new misto();
        misto.setAdresa(123);
        misto.setAvaiablequantity(50);
        objednavka.setIdmisto(misto);

        when(objednavkaRepository.findById(1)).thenReturn(Optional.of(objednavka));

        mockMvc.perform(get("/order").param("orderId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idzakzanika").value(1))
                .andExpect(jsonPath("$.adresa").value(123))
                .andExpect(jsonPath("$.avaiablequantity").value(50))
                .andExpect(jsonPath("$.cena").value(100.0));
    }
}
