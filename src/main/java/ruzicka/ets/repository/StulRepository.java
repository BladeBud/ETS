package ruzicka.ets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ruzicka.ets.db.Misto;
import ruzicka.ets.db.Stul;

/**
 * @author czech
 * @since 2024-11-05
 */

@Repository
public interface StulRepository extends JpaRepository<Stul, Integer> {
    Stul findByNazev(String adresa);
}
