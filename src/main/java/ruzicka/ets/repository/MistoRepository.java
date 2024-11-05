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

//    @Query("SELECT m FROM misto m WHERE m.adresa = :adresa AND m.availableQuantity >= :availableQuantity")
//    List<misto> findByAdresaAndAvailableQuantity(
//            @Param("adresa") Integer adresa,
//            @Param("availableQuantity") Integer availableQuantity
//    );

    //TODO:select for update
    Misto findByAdresa(String adresa);

    List<Misto> findByStulAndStatus(Stul stul, Misto.Status status);
}
