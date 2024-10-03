package ruzicka.ets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ruzicka.ets.db.misto;

import java.util.List;

/**
 * @author czech
 * @since 2024-09-30
 */
@Repository
public interface MistoRepository extends JpaRepository<misto, Integer> {
    List<misto> findByAdresaAndAvailableQuantity(Integer adresa, Integer availableQuantity);
}