package ruzicka.ets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ruzicka.ets.db.misto;

import java.util.List;

/**
 * @author czech
 * @since 2024-09-30
 */
@Repository
public interface MistoRepository extends JpaRepository<misto, Integer> {

    @Query("SELECT m FROM misto m WHERE m.adresa = :adresa AND m.availableQuantity >= :availableQuantity")
    List<misto> findByAdresaAndAvailableQuantity(
            @Param("adresa") Integer adresa,
            @Param("availableQuantity") Integer availableQuantity
    );

    //TODO:select for update
    misto findByAdresa(String adresa);

}
