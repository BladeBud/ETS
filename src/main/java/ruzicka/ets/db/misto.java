package ruzicka.ets.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author czech
 * @since 2024-09-30
 */
@Entity
@Table(name = "misto")
public class misto {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "idmisto", nullable = false)
    private Integer id;

    @Column(name = "adresa", nullable = false)
    private Integer adresa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idtypmista", nullable = false)
    private Typmista idtypmista;

    @Column(name = "avaiablequantity", nullable = false)
    private Integer avaiablequantity;

    @Column(name = "quantitysum", nullable = false)
    private Integer quantitysum;

    @OneToMany(mappedBy = "idmisto")
    private Set<Objednavka> objednavkas = new LinkedHashSet<>();

    public Set<Objednavka> getObjednavkas() {
        return objednavkas;
    }

    public void setObjednavkas(Set<Objednavka> objednavkas) {
        this.objednavkas = objednavkas;
    }

    public Integer getQuantitysum() {
        return quantitysum;
    }

    public void setQuantitysum(Integer quantitysum) {
        this.quantitysum = quantitysum;
    }

    public Integer getAvaiablequantity() {
        return avaiablequantity;
    }

    public void setAvaiablequantity(Integer avaiablequantity) {
        this.avaiablequantity = avaiablequantity;
    }

    public Typmista getIdtypmista() {
        return idtypmista;
    }

    public void setIdtypmista(Typmista idtypmista) {
        this.idtypmista = idtypmista;
    }

    public Integer getAdresa() {
        return adresa;
    }

    public void setAdresa(Integer adresa) {
        this.adresa = adresa;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
