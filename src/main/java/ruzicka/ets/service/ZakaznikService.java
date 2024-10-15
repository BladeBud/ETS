package ruzicka.ets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ruzicka.ets.db.Zakaznik;
import ruzicka.ets.repository.ZakaznikRepository;

import java.util.Optional;

/**
 * @author czech
 * @since 2024-10-06
 */
/**
 * The {@code ZakaznikService} class provides methods to manage customers, including operations such as finding and saving
 */
@Service
public class ZakaznikService {
//----------------------------------------------------------------------------------------------------------------------
    @Autowired
    private ZakaznikRepository zakaznikRepository;

    public Optional<Zakaznik> findZakaznikByMail(String mail) {
        return zakaznikRepository.findByMail(mail);
    }

    public Zakaznik save(Zakaznik zakaznik) {
        return zakaznikRepository.save(zakaznik);
    }
}
