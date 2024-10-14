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
 * @author czech
 * @since 2024-10-05
 */
@Service
public class ObjednavkaService {

    @Autowired
    private ObjednavkaRepository objednavkaRepository;
    @Autowired
    private MistoRepository mistoRepository;

    public List<Objednavka> findOrdersByZakaznikId(Integer zakaznikId) {
        return objednavkaRepository.findByIdzakaznik_Idzakaznik(zakaznikId);
    }

    public Objednavka findOrderById(Integer orderId) {
        return objednavkaRepository.findById(orderId).orElse(null);
    }
    public Objednavka save(Objednavka objednavka) {
        return objednavkaRepository.save(objednavka);
    }

    public Objednavka reserveOrder(Objednavka objednavka) {
        objednavka.setDatumcas(Instant.now());
        objednavka.setStatus("R"); // R for Reserved
        return objednavkaRepository.save(objednavka);
    }

    public void releaseExpiredReservations() {
        Instant tenMinutesAgo = Instant.now().minus(10, ChronoUnit.MINUTES);
        List<Objednavka> expiredReservations = objednavkaRepository.findByDatumcasBeforeAndStatus(tenMinutesAgo, "R");
        for (Objednavka objednavka : expiredReservations) {
            objednavka.setStatus("E"); // E for Expired
            objednavkaRepository.save(objednavka);
        }
    }

    public boolean isAddressReserved(Integer adresa) {
        List<Objednavka> reservedOrders = objednavkaRepository.findByAdresaAndStatus(adresa, "R");
        return !reservedOrders.isEmpty();
    }

    public boolean createOrder(OrderRequestDTO orderRequest) {
        // Check if the address is already reserved
        if (isAddressReserved(orderRequest.getAdresa())) {
            return false;
        }
        // Check if the quantity is available
        List<misto> availableMisto = mistoRepository.findByAdresaAndAvailableQuantity(orderRequest.getAdresa(), orderRequest.getQuantity());
        if (availableMisto.isEmpty()) {
            return false;
        }

        // Create the order
        Objednavka objednavka = new Objednavka();
        objednavka.setIdzakaznik(new Zakaznik());
        objednavka.setIdmisto(availableMisto.get(0));
        objednavka.setQuantity(orderRequest.getQuantity());
        objednavka.setDatumcas(Instant.now());
        objednavka.setStatus("R"); // R for Reserved
        objednavkaRepository.save(objednavka);

        // Create the reservation
        reserveOrder(objednavka);

        return true;
    }

}