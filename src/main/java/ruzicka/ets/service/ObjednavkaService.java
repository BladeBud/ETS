package ruzicka.ets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ruzicka.ets.db.MistoObjednavka;
import ruzicka.ets.db.Objednavka;
import ruzicka.ets.db.Stul;
import ruzicka.ets.db.Zakaznik;
import ruzicka.ets.db.Misto;
import ruzicka.ets.dto.OrderRequestDTO;
import ruzicka.ets.repository.MistoObjednavkaRepository;
import ruzicka.ets.repository.MistoRepository;
import ruzicka.ets.repository.ObjednavkaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ruzicka.ets.repository.StulRepository;
import ruzicka.ets.repository.ZakaznikRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ObjednavkaService {

    private static final Logger log = LoggerFactory.getLogger(ObjednavkaService.class);

    @Autowired
    private ObjednavkaRepository objednavkaRepository;

    @Autowired
    private MistoRepository mistoRepository;

    @Autowired
    private StulRepository stulRepository;
    @Autowired
    private MistoObjednavkaRepository mistoObjednavkaRepository;
    @Autowired
    private ZakaznikRepository zakaznikRepository;

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
        List<Objednavka> expiredOrders = objednavkaRepository.findByDatumcasBeforeAndStatus(tenMinutesAgo, "R");

        for (Objednavka objednavka : expiredOrders) {
            Misto relatedMisto = objednavka.getIdmisto();

            // Mark the order as expired
            objednavka.setStatus("E");
            objednavkaRepository.save(objednavka);

            // Restore the available quantity
            int quantityToRestore = objednavka.getQuantity();
            relatedMisto.setAvailableQuantity(relatedMisto.getAvailableQuantity() + quantityToRestore);
            mistoRepository.save(relatedMisto);

            log.info("Order with ID {} set to expired and quantity {} restored to misto", objednavka.getId(), quantityToRestore);
        }
    }

    // Create order with mixed 'misto' types and update available quantity

    public synchronized Objednavka createOrder(OrderRequestDTO orderRequest) {
        log.info("Attempting to create order for address: {} and quantity: {}", orderRequest.getAdresa(), orderRequest.getQuantity());

        Optional<Zakaznik> zakaznik = zakaznikRepository.findByMail(orderRequest.getMail());
        if (zakaznik.isEmpty()) {
            log.warn("Zakaznik with mail {} does not exist.", orderRequest.getMail());
            return null;
        }

        Stul stul = stulRepository.findByNazev(orderRequest.getAdresa());
        if (stul == null) {
            log.warn("Address {} does not exist.", orderRequest.getAdresa());
            return null;
        }

        if (stul.getAvailableQuantity() < orderRequest.getQuantity()) {
            log.warn("Not enough available quantity for address: {}. Requested: {}, Available: {}", orderRequest.getAdresa(), orderRequest.getQuantity(), stul.getAvailableQuantity());
            return null;
        }

        // Fetch available 'misto' for the provided address
        List<Misto> availableMistoList = mistoRepository.findByStulAndStatus(stul, Misto.Status.A);
        if (availableMistoList.isEmpty()) {
            log.warn("No available quantity for address: {} with requested quantity: {}", orderRequest.getAdresa(), orderRequest.getQuantity());
            return null;
        }
        if (availableMistoList.size() >= orderRequest.getQuantity()) {
            log.warn("not enough available for address: {}. Requested: {}, Available: {}", orderRequest.getAdresa(), orderRequest.getQuantity(), availableMistoList.get(0).getStul().getAvailableQuantity());
        }

        // Create and save the order
        Objednavka objednavka = new Objednavka();
        objednavka.setIdzakaznik(zakaznik.get());
        objednavka.setCena(0);
        objednavka.setDatumcas(Instant.now());
        objednavka.setStatus("R");

        objednavka = objednavkaRepository.save(objednavka);
        log.info("Order successfully saved with ID: {}", objednavka.getId());

        stul.setAvailableQuantity(stul.getAvailableQuantity() - orderRequest.getQuantity());
        stulRepository.save(stul);

        int totalOrderedQuantity = orderRequest.getQuantity();
        int totalPrice = 0;

        for (int i = 0; i < orderRequest.getQuantity(); i++) {
            Misto availableMisto = availableMistoList.get(i);
            int unitPrice = calculatePriceByType(String.valueOf(availableMisto.getStul().getIdtypmista()));


            // Calculate the price for this portion and add it to the total price
            totalPrice += unitPrice;


            // Update the available quantity for this 'misto'
            availableMisto.setStatus(Misto.Status.R);

            mistoRepository.save(availableMisto);

            mistoObjednavkaRepository.save(new MistoObjednavka().setIdmisto(availableMisto.getIdmisto()).setIdobjednavka(objednavka.getId()));
        }
        objednavka.setCena(totalPrice);
        objednavkaRepository.save(objednavka);

        return objednavka;
    }

    // Price calculation based on the 'typmista'
    //TODO: should not be hardcoded, should be fetched from a configuration or database
    private int calculatePriceByType(String typmista) {
        switch (typmista) {
            case "L":
                return 700;  // Price for 'L' type
            case "B":
                return 500;  // Price for 'B' type
            case "V":
                return 600;  // Price for 'V' type
            case "S":
                return 300;   // Price for 'S' type
            default:
                log.warn("Unknown 'typmista': {}. Defaulting to 0.", typmista);
                return 0;  // Default to 0 in case of unknown type
        }
    }
}
