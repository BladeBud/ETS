package ruzicka.ets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ruzicka.ets.db.Objednavka;
import ruzicka.ets.repository.ObjednavkaRepository;

import java.util.List;

/**
 * @author czech
 * @since 2024-10-05
 */
@Service
public class ObjednavkaService {

    @Autowired
    private ObjednavkaRepository objednavkaRepository;

    public List<Objednavka> findOrdersByZakaznikId(Integer zakaznikId) {
        return objednavkaRepository.findByIdzakaznik_Idzakaznik(zakaznikId);
    }

    public Objednavka findOrderById(Integer orderId) {
        return objednavkaRepository.findById(orderId).orElse(null);
    }
    public Objednavka save(Objednavka objednavka) {
        return objednavkaRepository.save(objednavka);
    }
}