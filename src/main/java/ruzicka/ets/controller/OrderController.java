package ruzicka.ets.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ruzicka.ets.db.Objednavka;
import ruzicka.ets.db.misto;
import ruzicka.ets.dto.EventInfoDTO;
import ruzicka.ets.dto.OrderRequestDTO;
import ruzicka.ets.repository.MistoRepository;
import ruzicka.ets.service.ObjednavkaService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author czech
 * @since 2024-09-30
 */

@RestController
public class OrderController {
//----------------------------------------------------------------------------------------------------------------------

    @Autowired
    private MistoRepository mistoRepository;

    @Autowired
    private ObjednavkaService objednavkaService;

//----------------------------------------------------------------------------------------------------------------------
    @GetMapping("/misto")
    public List<EventInfoDTO> getEventInfo(@RequestParam Integer adresa, @RequestParam Integer avaiableQuantity) {
        List<misto> mistoList = mistoRepository.findByAdresaAndAvailableQuantity(adresa, avaiableQuantity);
        return mistoList.stream().map(misto -> {
            EventInfoDTO response = new EventInfoDTO();
            response.setAdresa(misto.getAdresa());
            response.setAvaiablequantity(misto.getAvailableQuantity());
            //response.setIdzakaznik(objednavka.getIdzakaznik());
            response.setCena(misto.getIdtypmista().getCena());
            return response;
        }).collect(Collectors.toList());
    }
 //----------------------------------------------------------------------------------------------------------------------
    @PostMapping("/reserve")
    public Objednavka reserveOrder(@RequestBody Objednavka objednavka) {
        return objednavkaService.reserveOrder(objednavka);
    }

    @Scheduled(fixedRate = 600000) // TODO: Run every 10 minutes (domluvit se na nacasovani)
    public void cleanupExpiredReservations() {
        objednavkaService.releaseExpiredReservations();
    }

    @PostMapping("/order")
    public ResponseEntity<String> createOrder(@RequestBody OrderRequestDTO orderRequestDTO) {
        boolean isCreated = objednavkaService.createOrder(orderRequestDTO);
        if (isCreated) {
            return ResponseEntity.ok("Objednávka proběhla úspěšně");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Místo je už zabrané");
        }
    }
}
