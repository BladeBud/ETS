package ruzicka.ets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ruzicka.ets.db.Objednavka;

import java.time.Instant;
import java.util.List;

/**
 * @author czech
 * @since 2024-09-30
 */
@Repository
public interface ObjednavkaRepository extends JpaRepository<Objednavka, Integer> {
    List<Objednavka> findByIdzakaznik_Idzakaznik(Integer idzakaznik);

    List<Objednavka> findByStatusAndId(String variableSymbol,Integer idzakaznik);

    List<Objednavka> findByDatumcasBeforeAndStatus(Instant tenMinutesAgo, String res);

    List<Objednavka> findByAdresaAndStatus(Integer adresa, String r);

    //  List<Objednavka> findByZakaznikIdzakaznik(Integer zakaznikId);
}
