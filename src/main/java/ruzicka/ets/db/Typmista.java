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
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "idtypmista", nullable = false)
    private Integer id;

    @Column(name = "typmista", nullable = false, length = 3)
    private String typmista;

    @Column(name = "cena", nullable = false)
    private Integer cena;

    @OneToMany(mappedBy = "idtypmista")
    private Set<misto> mistos = new LinkedHashSet<>();

    public Set<misto> getMistos() {
        return mistos;
    }

    public void setMistos(Set<misto> mistos) {
        this.mistos = mistos;
    }

    public Integer getCena() {
        return cena;
    }

    public void setCena(Integer cena) {
        this.cena = cena;
    }

    public String getTypmista() {
        return typmista;
    }

    public void setTypmista(String typmista) {
        this.typmista = typmista;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}