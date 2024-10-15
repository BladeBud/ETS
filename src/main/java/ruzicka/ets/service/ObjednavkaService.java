package ruzicka.ets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ruzicka.ets.db.Objednavka;
import ruzicka.ets.db.Zakaznik;
import ruzicka.ets.db.misto;
import ruzicka.ets.dto.OrderRequestDTO;
import ruzicka.ets.repository.MistoRepository;
import ruzicka.ets.repository.ObjednavkaRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * The {@code ObjednavkaService} class provides methods to manage orders, including operations such as finding,
 * creating, and reserving orders, as well as releasing expired reservations.
 */
@Service
public class ObjednavkaService {
//----------------------------------------------------------------------------------------------------------------------
    @Autowired
    private ObjednavkaRepository objednavkaRepository;
    @Autowired
    private MistoRepository mistoRepository;

//----------------------------------------------------------------------------------------------------------------------
    public List<Objednavka> findOrdersByZakaznikId(Integer zakaznikId) {
        return objednavkaRepository.findByIdzakaznik_Idzakaznik(zakaznikId);
    }

    public Objednavka findOrderById(Integer orderId) {
        return objednavkaRepository.findById(orderId).orElse(null);
    }
    public Objednavka save(Objednavka objednavka) {
        return objednavkaRepository.save(objednavka);
    }
//----------------------------------------------------------------------------------------------------------------------
    /**
     * Reserves the given order.
     * @param objednavka
     * @return save objednavka
     */
    public Objednavka reserveOrder(Objednavka objednavka) {
        objednavka.setDatumcas(Instant.now());
        objednavka.setStatus("R"); // R for Reserved
        return objednavkaRepository.save(objednavka);
    }
//----------------------------------------------------------------------------------------------------------------------
    /**
     * Releases expired reservations.
     */
    public void releaseExpiredReservations() {
        Instant tenMinutesAgo = Instant.now().minus(10, ChronoUnit.MINUTES);
        List<Objednavka> expiredReservations = objednavkaRepository.findByDatumcasBeforeAndStatus(tenMinutesAgo, "R");
        for (Objednavka objednavka : expiredReservations) {
            objednavka.setStatus("E"); // E for Expired
            objednavkaRepository.save(objednavka);
        }
    }
//----------------------------------------------------------------------------------------------------------------------
    /**
     * Creates an order for the given {@code OrderRequestDTO} object.
     * @param orderRequest
     * @return objednavka
     */
    public Objednavka createOrder(OrderRequestDTO orderRequest) {
        // Check if the address is already reserved
        if (isAddressReserved(orderRequest.getAdresa())) {
            return null;
        }

        // Check if the quantity is available
        List<misto> availableMisto = mistoRepository.findByAdresaAndAvailableQuantity(orderRequest.getAdresa(), orderRequest.getQuantity());
        if (availableMisto.isEmpty()) {
            return null;
        }

        // Create the order
        Objednavka objednavka = new Objednavka();
        Zakaznik zakaznik = new Zakaznik();
        objednavka.setIdzakaznik(zakaznik);
        objednavka.setIdmisto(availableMisto.get(0));
        objednavka.setQuantity(orderRequest.getQuantity());
        objednavka.setDatumcas(Instant.now());
        objednavka.setStatus("R"); // R for Reserved

        objednavkaRepository.save(objednavka);

        return objednavka;
    }
//----------------------------------------------------------------------------------------------------------------------
    /**
     * Checks if the given address is already reserved.
     * @param adresa
     * @return true if the address is reserved, false otherwise
     */
    private boolean isAddressReserved(Integer adresa) {
        List<Objednavka> reservedOrders = objednavkaRepository.findByIdmisto_AdresaAndStatus(adresa, "R");
        return !reservedOrders.isEmpty();
    }
}