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

    // Create order with mixed 'misto' types and update available quantity
    public Objednavka createOrder(OrderRequestDTO orderRequest) {
        log.info("Attempting to create order for address: {} and quantity: {}", orderRequest.getAdresa(), orderRequest.getQuantity());

        // Check if the address is already reserved
        if (isAddressReserved(orderRequest.getAdresa())) {
            log.warn("Address {} is already reserved.", orderRequest.getAdresa());
            return null;
        }

        // Fetch available 'misto' for the provided address
        List<misto> availableMistoList = mistoRepository.findByAdresaAndAvailableQuantity(orderRequest.getAdresa(), orderRequest.getQuantity());
        if (availableMistoList.isEmpty()) {
            log.warn("No available quantity for address: {} with requested quantity: {}", orderRequest.getAdresa(), orderRequest.getQuantity());
            return null;
        }

        int totalOrderedQuantity = orderRequest.getQuantity();
        int totalPrice = 0;
        int remainingQuantityToFulfill = totalOrderedQuantity;

        for (misto availableMisto : availableMistoList) {
            int unitPrice = calculatePriceByType(availableMisto.getIdtypmista().getTypMista());
            int availableQuantity = availableMisto.getAvailableQuantity();

            if (remainingQuantityToFulfill <= 0) {
                break;
            }

            // Calculate the quantity that can be used from this 'misto'
            int quantityToUse = Math.min(availableQuantity, remainingQuantityToFulfill);

            // Calculate the price for this portion and add it to the total price
            totalPrice += unitPrice * quantityToUse;

            // Update the remaining quantity that needs to be fulfilled
            remainingQuantityToFulfill -= quantityToUse;

            // Update the available quantity for this 'misto'
            availableMisto.setAvailableQuantity(availableQuantity - quantityToUse);
            mistoRepository.save(availableMisto);
        }

        // If we haven't fulfilled the total ordered quantity, the order cannot be placed
        if (remainingQuantityToFulfill > 0) {
            log.warn("Not enough available quantity for address: {}. Requested: {}, Available: {}", orderRequest.getAdresa(), totalOrderedQuantity, totalOrderedQuantity - remainingQuantityToFulfill);
            return null;
        }

        // Create and save the order
        Objednavka objednavka = new Objednavka();
        Zakaznik zakaznik = new Zakaznik();
        zakaznik.setIdzakaznik(orderRequest.getZakaznikId());
        objednavka.setIdzakaznik(zakaznik);
        objednavka.setIdmisto(availableMistoList.get(0)); // Assume we assign the first `misto` for now
        objednavka.setQuantity(totalOrderedQuantity);
        objednavka.setCena(totalPrice);  // Set the computed total price
        objednavka.setDatumcas(Instant.now());
        objednavka.setStatus("R");

        Objednavka savedOrder = objednavkaRepository.save(objednavka);
        log.info("Order successfully saved with ID: {}", savedOrder.getId());

        return savedOrder;
    }

    // Price calculation based on the 'typmista'
    private int calculatePriceByType(String typmista) {
        switch (typmista) {
            case "L":
                return 3000;  // Price for 'L' type
            case "B":
                return 1000;  // Price for 'B' type
            case "V":
                return 1500;  // Price for 'V' type
            case "S":
                return 100;   // Price for 'S' type
            default:
                log.warn("Unknown 'typmista': {}. Defaulting to 0.", typmista);
                return 0;  // Default to 0 in case of unknown type
        }
    }

    // Check if the address is already reserved
    private boolean isAddressReserved(Integer adresa) {
        List<Objednavka> reservedOrders = objednavkaRepository.findByIdmisto_AdresaAndStatus(adresa, "R");
        return !reservedOrders.isEmpty();
    }
}
