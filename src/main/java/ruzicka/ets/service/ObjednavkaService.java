package ruzicka.ets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ruzicka.ets.db.MistoObjednavka;
import ruzicka.ets.db.Objednavka;
import ruzicka.ets.db.Stul;
import ruzicka.ets.db.Typmista;
import ruzicka.ets.db.Zakaznik;
import ruzicka.ets.db.Misto;
import ruzicka.ets.dto.OrderRequestDTO;
import ruzicka.ets.repository.MistoObjednavkaRepository;
import ruzicka.ets.repository.MistoRepository;
import ruzicka.ets.repository.ObjednavkaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ruzicka.ets.repository.StulRepository;
import ruzicka.ets.repository.TypMistaRepository;
import ruzicka.ets.repository.ZakaznikRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ObjednavkaService {
//------------------------------------------------------------------------------------------------
    private static final Logger log = LoggerFactory.getLogger(ObjednavkaService.class);
    private static final Logger importantLog = LoggerFactory.getLogger("important");

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

    @Autowired
    private TypMistaRepository typMistaRepository;
//------------------------------------------------------------------------------------------------
    // Find orders by zakaznikId
    public List<Objednavka> findOrdersByZakaznikId(Integer zakaznikId) {
        return objednavkaRepository.findByIdzakaznik_Idzakaznik(zakaznikId);
    }
//------------------------------------------------------------------------------------------------
    // Find order by orderId
    public Objednavka findOrderById(Integer orderId) {
        return objednavkaRepository.findById(orderId).orElse(null);
    }
//------------------------------------------------------------------------------------------------
    // Save an order
    public Objednavka save(Objednavka objednavka) {
        return objednavkaRepository.save(objednavka);
    }

//------------------------------------------------------------------------------------------------
    // Release expired reservations
    public void releaseExpiredReservations() {
        Instant minutesAgo = Instant.now().minus(30, ChronoUnit.MINUTES);
        List<Objednavka> expiredOrders = objednavkaRepository.findByDatumcasBeforeAndStatus(minutesAgo, "R");

        for (Objednavka objednavka : expiredOrders) {
            // Mark the order as expired
            objednavka.setStatus("E");
            objednavkaRepository.save(objednavka);

            // Get all MistoObjednavka records associated with the order
            List<MistoObjednavka> mistoObjednavkaList = mistoObjednavkaRepository.findByIdobjednavka(objednavka.getId());

            for (MistoObjednavka mistoObjednavka : mistoObjednavkaList) {
                // Get the Misto and Stul associated with the MistoObjednavka
                Misto misto = mistoRepository.findById(mistoObjednavka.getIdmisto()).orElse(null);
                if (misto != null) {
                    Stul stul = misto.getStul();

                    // Restore the seat status to available
                    misto.setStatus(Misto.Status.A.name());
                    mistoRepository.save(misto);

                    // Restore the available quantity for the table
                    stul.setAvailableQuantity(stul.getAvailableQuantity() + 1);
                    stulRepository.save(stul);
                }
            }

            log.info("Order with ID {} set to expired and associated seats and tables restored", objednavka.getId());
           // importantLog.info("Order with ID {} set to expired and associated seats and tables restored", objednavka.getId());
        }
    }
//------------------------------------------------------------------------------------------------
    /**
     * Creates an order based on the provided order request.
     *
     * @param orderRequest the details of the order
     * @return the created Objednavka object
     */
    @Transactional
    public synchronized Objednavka createOrder(OrderRequestDTO orderRequest) {
        log.info("Attempting to create order for tables: {} and quantities: {}", orderRequest.getNazvy(), orderRequest.getQuantities());

        // Check if the order contains at least one table
        if (orderRequest.getNazvy() == null || orderRequest.getNazvy().isEmpty()) {
            log.warn("Order request does not contain any tables.");
            return null;
        }

        Optional<Zakaznik> zakaznik = zakaznikRepository.findByMail(orderRequest.getMail());
        if (zakaznik.isEmpty()) {
            log.warn("Zakaznik with mail {} does not exist.", orderRequest.getMail());
            return null;
        }

        if (!"V".equals(zakaznik.get().getStatus())) {
            log.warn("Zakaznik with mail {} is not verified.", orderRequest.getMail());
            return null;
        }


        List<String> tableNames = orderRequest.getNazvy();
        List<Integer> quantities = orderRequest.getQuantities();
        int totalPrice = 0;

        Objednavka objednavka = new Objednavka();
        objednavka.setIdzakaznik(zakaznik.get());
        objednavka.setCena(0);
        objednavka.setDatumcas(Instant.now());
        objednavka.setStatus("R");

        objednavka = objednavkaRepository.save(objednavka);
        log.info("Order successfully saved with ID: {}", objednavka.getId());
        importantLog.info("Order created with ID: {}", objednavka.getId());

        for (int i = 0; i < tableNames.size(); i++) {
            String tableName = tableNames.get(i);
            int quantity = quantities.get(i);

            // Check if the table exists
            Stul stul = stulRepository.findByNazev(tableName.trim());
            if (stul == null) {
                log.warn("Table {} does not exist.", tableName);
                continue;
            }

            // Get all available seats for the table
            List<Misto> availableMistoList = mistoRepository.findByStulAndStatus(stul, Misto.Status.A.name());
            if (availableMistoList.size() < quantity) {
                log.warn("Not enough available seats for table: {}. Requested: {}, Available: {}", tableName, quantity, availableMistoList.size());
                continue;
            }

            // Mark the required seats as reserved and create records in the linking table
            for (int j = 0; j < quantity; j++) {
                Misto availableMisto = availableMistoList.get(j);
                int unitPrice = calculatePriceByType(availableMisto.getStul().getIdtypmista().getTypMista());

                totalPrice += unitPrice;

                availableMisto.setStatus(Misto.Status.R.name());
                mistoRepository.save(availableMisto);

                MistoObjednavka mistoObjednavka = new MistoObjednavka();
                mistoObjednavka.setIdmisto(availableMisto.getIdmisto());
                mistoObjednavka.setIdobjednavka(objednavka.getId());
                mistoObjednavkaRepository.save(mistoObjednavka);
            }

            // Update the available quantity for the table
            stul.setAvailableQuantity(stul.getAvailableQuantity() - quantity);
            stulRepository.save(stul);
        }

        // Save the total price to the order
        objednavka.setCena(totalPrice);
        objednavkaRepository.save(objednavka);

        return objednavka;
    }
//------------------------------------------------------------------------------------------------
    // Price calculation based on the 'typmista'
    private int calculatePriceByType(String typMista) {
        Typmista typMistaEntity = typMistaRepository.findByTypMista(typMista);
        if (typMistaEntity != null) {
            return typMistaEntity.getCena();
        } else {
            log.warn("Unknown 'typMista': {}. Defaulting to 0.", typMista);
            return 0;
        }
    }
}
