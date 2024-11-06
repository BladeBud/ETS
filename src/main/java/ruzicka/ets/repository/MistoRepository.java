package ruzicka.ets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ruzicka.ets.db.Misto;
import ruzicka.ets.db.Stul;

import java.util.List;

/**
 * @author czech
 * @since 2024-09-30
 */
@Repository
public interface MistoRepository extends JpaRepository<Misto, Integer> {
//----------------------------------------------------------------------------------------------------------------------
    List<Misto> findByStulAndStatus(Stul stul, String status);
}
