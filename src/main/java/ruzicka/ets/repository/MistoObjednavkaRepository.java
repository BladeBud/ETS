package ruzicka.ets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ruzicka.ets.db.MistoObjednavka;

import java.util.List;

/**
 * @author czech
 * @since 2024-11-05
 */

@Repository
public interface MistoObjednavkaRepository extends JpaRepository<MistoObjednavka, Integer> {
    int countByIdobjednavka(Integer id);

    List<MistoObjednavka> findByIdobjednavka(Integer id);
}
