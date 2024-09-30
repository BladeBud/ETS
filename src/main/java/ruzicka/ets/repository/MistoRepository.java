package ruzicka.ets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ruzicka.ets.db.misto;

import java.util.List;

/**
 * @author czech
 * @since 2024-09-30
 */
public interface MistoRepository extends JpaRepository<misto, Integer> {
    List<misto> findByAdresaAndAvaiablequantity(Integer adresa, Integer quantityavaiable);
}
