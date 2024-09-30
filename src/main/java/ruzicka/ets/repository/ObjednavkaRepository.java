package ruzicka.ets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ruzicka.ets.db.Objednavka;

/**
 * @author czech
 * @since 2024-09-30
 */

public interface ObjednavkaRepository extends JpaRepository<Objednavka, Integer> {

}
