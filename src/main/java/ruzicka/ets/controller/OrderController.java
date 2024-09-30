package ruzicka.ets.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ruzicka.ets.db.Objednavka;
import ruzicka.ets.db.misto;
import ruzicka.ets.dto.OrderResponseDTO;
import ruzicka.ets.repository.ObjednavkaRepository;
import ruzicka.ets.repository.MistoRepository;
/**
 * @author czech
 * @since 2024-09-30
 */



@RestController
public class OrderController {

    @Autowired
    private ObjednavkaRepository objednavkaRepository;

    @Autowired
    private MistoRepository mistoRepository;

    @GetMapping("/order")
    public OrderResponseDTO getOrderDetails(@RequestParam Integer orderId) {
        Objednavka objednavka = objednavkaRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        misto misto = objednavka.getIdmisto();

        OrderResponseDTO response = new OrderResponseDTO();
        response.setIdzakzanika(objednavka.getId());
        response.setAdresa(misto.getAdresa());
        response.setAvaiablequantity(misto.getAvaiablequantity());
        response.setCena(100.0);

        return response;
    }
}
