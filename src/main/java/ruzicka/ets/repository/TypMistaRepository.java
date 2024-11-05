package ruzicka.ets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ruzicka.ets.db.Typmista;

/**
 * @author czech
 * @since 2024-11-05
 */
@Repository
public interface TypMistaRepository extends JpaRepository<Typmista, Integer> {
    Typmista findByTypMista(String typMista);
}