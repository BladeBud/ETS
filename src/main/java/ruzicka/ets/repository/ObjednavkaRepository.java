package ruzicka.ets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ruzicka.ets.db.Objednavka;

import java.util.List;

/**
 * @author czech
 * @since 2024-09-30
 */

public interface ObjednavkaRepository extends JpaRepository<Objednavka, Integer> {
    List<Objednavka> findByIdzakaznik_Idzakaznik(Integer idzakaznik);
}
