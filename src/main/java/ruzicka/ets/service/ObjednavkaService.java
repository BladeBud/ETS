package ruzicka.ets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ruzicka.ets.db.Objednavka;
import ruzicka.ets.db.Zakaznik;
import ruzicka.ets.db.misto;
import ruzicka.ets.dto.OrderRequestDTO;
import ruzicka.ets.repository.MistoRepository;
import ruzicka.ets.repository.ObjednavkaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ObjednavkaService {

    private static final Logger log = LoggerFactory.getLogger(ObjednavkaService.class);

    @Autowired
    private ObjednavkaRepository objednavkaRepository;

    @Autowired
    private MistoRepository mistoRepository;

    // Find orders by zakaznikId
    public List<Objednavka> findOrdersByZakaznikId(Integer zakaznikId) {
        return objednavkaRepository.findByIdzakaznik_Idzakaznik(zakaznikId);
    }

    // Find order by orderId
    public Objednavka findOrderById(Integer orderId) {
        return objednavkaRepository.findById(orderId).orElse(null);
    }

    // Save an order
    public Objednavka save(Objednavka objednavka) {
        return objednavkaRepository.save(objednavka);
    }

    // Reserve an order
    public Objednavka reserveOrder(Objednavka objednavka) {
        log.info("Reserving order: {}", objednavka);
        objednavka.setDatumcas(Instant.now());
        objednavka.setStatus("R");
        return objednavkaRepository.save(objednavka);
    }

    // Release expired reservations
    public void releaseExpiredReservations() {
        Instant tenMinutesAgo = Instant.now().minus(10, ChronoUnit.MINUTES);
        List<Objednavka> expiredReservations = objednavkaRepository.findByDatumcasBeforeAndStatus(tenMinutesAgo, "R");
        for (Objednavka objednavka : expiredReservations) {
            log.info("Releasing expired order: {}", objednavka);
            objednavka.setStatus("E");
            objednavkaRepository.save(objednavka);
        }
    }

    // Create order
    public Objednavka createOrder(OrderRequestDTO orderRequest) {
        log.info("Attempting to create order for address: {} and quantity: {}", orderRequest.getAdresa(), orderRequest.getQuantity());

        // Check if the address is already reserved
        if (isAddressReserved(orderRequest.getAdresa())) {
            log.warn("Address {} is already reserved.", orderRequest.getAdresa());
            return null;
        }

        // Check if the requested quantity is available
        List<misto> availableMisto = mistoRepository.findByAdresaAndAvailableQuantity(orderRequest.getAdresa(), orderRequest.getQuantity());
        if (availableMisto.isEmpty()) {
            log.warn("No available quantity for address: {} with requested quantity: {}", orderRequest.getAdresa(), orderRequest.getQuantity());
            return null;
        }

        // Create and save the order
        Objednavka objednavka = new Objednavka();
        Zakaznik zakaznik = new Zakaznik();
        zakaznik.setIdzakaznik(orderRequest.getZakaznikId());
        objednavka.setIdzakaznik(zakaznik);
        objednavka.setIdmisto(availableMisto.get(0));
        objednavka.setQuantity(orderRequest.getQuantity());
        objednavka.setDatumcas(Instant.now());
        objednavka.setStatus("R");

        Objednavka savedOrder = objednavkaRepository.save(objednavka);
        log.info("Order successfully saved with ID: {}", savedOrder.getId());

        return savedOrder;
    }

    // Check if the address is already reserved
    private boolean isAddressReserved(Integer adresa) {
        List<Objednavka> reservedOrders = objednavkaRepository.findByIdmisto_AdresaAndStatus(adresa, "R");
        return !reservedOrders.isEmpty();
    }
}
