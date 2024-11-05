package ruzicka.ets.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ruzicka.ets.db.Objednavka;
import ruzicka.ets.db.Misto;
import ruzicka.ets.db.Stul;
import ruzicka.ets.dto.EventInfoDTO;
import ruzicka.ets.dto.OrderRequestDTO;
import ruzicka.ets.dto.OrderResponseDTO;
import ruzicka.ets.repository.MistoRepository;
import ruzicka.ets.repository.StulRepository;
import ruzicka.ets.service.ObjednavkaService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class OrderController {
//----------------------------------------------------------------------------------------------------------------------
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private MistoRepository mistoRepository;

    @Autowired
    private ObjednavkaService objednavkaService;

    @Value("${banking.details}")
    private String cisloUctu;
    @Autowired
    private StulRepository stulRepository;

    //----------------------------------------------------------------------------------------------------------------------
    @GetMapping("/misto")
    public List<EventInfoDTO> getEventInfo(@RequestParam Integer adresa) {
        log.info("Fetching event info for address: {}", adresa);
        List<Stul> stulList = stulRepository.findAll();
        return stulList.stream().map(stul -> {
            EventInfoDTO response = new EventInfoDTO();
            response.setAdresa(stul.getNazev());
            response.setAvaiablequantity(stul.getAvailableQuantity());
            response.setCena(stul.getIdtypmista().getCena());
            return response;
        }).collect(Collectors.toList());
    }

//----------------------------------------------------------------------------------------------------------------------
    @Scheduled(fixedRate = 60000) // TODO: Run every 10 minutes (domluvit se na nacasovani pokus se bude upravovat uparvit i v releaseexpiredreservations)
    public void cleanupExpiredReservations() {
        log.info("Cleaning up expired reservations");
        objednavkaService.releaseExpiredReservations();
    }
//----------------------------------------------------------------------------------------------------------------------
    @PostMapping("/order")
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderRequestDTO orderRequestDTO) {
        log.info("Creating order with request: {}", orderRequestDTO);
        Objednavka objednavka = objednavkaService.createOrder(orderRequestDTO);
        if (objednavka != null) {
            OrderResponseDTO response = new OrderResponseDTO(
                    objednavka.getId(),
                    objednavka.getCena(),
                    cisloUctu
            );
            log.info("Order created successfully: {}", response);
            return ResponseEntity.ok(response);
        } else {
            log.warn("Failed to create order with request: {}", orderRequestDTO);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}