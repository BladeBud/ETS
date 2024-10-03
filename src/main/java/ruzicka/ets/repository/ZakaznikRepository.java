package ruzicka.ets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ruzicka.ets.db.Zakaznik;

import java.util.Optional;

/**
 * @author czech
 * @since 2024-10-02
 */
@Repository
public interface ZakaznikRepository extends JpaRepository<Zakaznik, Integer> {
    Optional<Zakaznik> findByMail(String mail);
    Optional<Zakaznik> findByMailAndStatus(String mail, String status);
}