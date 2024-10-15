package ruzicka.ets.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "typmista")
public class Typmista {
    //----------------------------------------------------------------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "idtypmista", nullable = false)
    private Integer idtypmista;

    @Column(name = "typmista", nullable = false, length = 3)
    private String typMista;

    @Column(name = "cena", nullable = false)
    private Integer cena;
    //----------------------------------------------------------------------------------------------------------------------
    // Getters and Setters
    public Integer getIdtypmista() {
        return idtypmista;
    }

    public void setIdtypmista(Integer idtypmista) {
        this.idtypmista = idtypmista;
    }

    public String getTypMista() {
        return typMista;
    }

    public void setTypMista(String typMista) {
        this.typMista = typMista;
    }

    public Integer getCena() {
        return cena;
    }

    public void setCena(Integer cena) {
        this.cena = cena;
    }
}