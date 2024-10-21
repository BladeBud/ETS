package ruzicka.ets.controller;

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
import ruzicka.ets.db.misto;
import ruzicka.ets.dto.EventInfoDTO;
import ruzicka.ets.dto.OrderRequestDTO;
import ruzicka.ets.dto.OrderResponseDTO;
import ruzicka.ets.repository.MistoRepository;
import ruzicka.ets.service.ObjednavkaService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author czech
 * @since 2024-09-30
 */

/**
 * The {@code OrderController} class is a REST controller responsible for handling order-related operations including
 * retrieving event information, reserving orders, cleaning up expired reservations, and creating orders.
 */
@RestController
public class OrderController {
//----------------------------------------------------------------------------------------------------------------------

    @Autowired
    private MistoRepository mistoRepository;

    @Autowired
    private ObjednavkaService objednavkaService;
    @Value("${banking.details}")
    private String cisloUctu;

//----------------------------------------------------------------------------------------------------------------------

    /**
     * Retrieves a list of event information based on the given address and available quantity.
     *
     * @param adresa           the address identifier to filter the events
     * @param avaiableQuantity the number of available quantities to filter the events
     * @return a list of {@code EventInfoDTO} objects containing the event information
     */
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

    /**
     * Reserves an order by delegating it to the objednavkaService.
     *
     * @param objednavka the order to be reserved
     * @return the reserved order
     */
    @PostMapping("/reserve")
    public Objednavka reserveOrder(@RequestBody Objednavka objednavka) {
        return objednavkaService.reserveOrder(objednavka);
    }
//----------------------------------------------------------------------------------------------------------------------

    /**
     * This method is executed at a fixed interval to clean up expired reservations.
     * It delegates the task of releasing expired reservations to the objednavkaService.
     *
     * The method is scheduled to run every 10 minutes.
     */
    @Scheduled(fixedRate = 600000) // TODO: Run every 10 minutes (domluvit se na nacasovani pokus se bude upravovat uparvit i v releaseexpiredreservations)
    public void cleanupExpiredReservations() {
        objednavkaService.releaseExpiredReservations();
    }
//----------------------------------------------------------------------------------------------------------------------

    /**
     * Creates an order based on the given {@code OrderRequestDTO} object.
     * If the order is successfully created, returns an {@code OrderResponseDTO}.
     * Otherwise, returns a {@code BAD_REQUEST} status.
     *
     * @param orderRequestDTO the data transfer object containing the details of the order to be created
     * @return a {@code ResponseEntity} containing an {@code OrderResponseDTO} if the order was successfully created, or a {@code BAD_REQUEST} status if the order creation failed
     *
     */
    @PostMapping("/order")
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderRequestDTO orderRequestDTO) {
        Objednavka objednavka = objednavkaService.createOrder(orderRequestDTO);
        if (objednavka != null) {
            OrderResponseDTO response = new OrderResponseDTO(
                    objednavka.getIdzakaznik().getIdzakaznik(),
                    objednavka.getIdmisto().getIdtypmista().getCena(),
                    cisloUctu
            );
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
