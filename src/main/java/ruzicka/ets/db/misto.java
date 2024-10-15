package ruzicka.ets.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * @author czech
 * @since 2024-09-30
 */

/**
 * Represents a 'misto' entity with details such as address, type, availability, and quantity information.
 */
@Entity
@Table(name = "misto")
public class misto {
//----------------------------------------------------------------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "idmisto", nullable = false)
    private Integer idmisto;

    @Column(name = "adresa", nullable = false)
    private Integer adresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtypmista", nullable = false)
    private Typmista idtypmista;

    @Column(name = "avaiablequantity", nullable = false)
    private Integer availableQuantity;

    @Column(name = "quantitysum", nullable = false)
    private Integer quantitySum;
//----------------------------------------------------------------------------------------------------------------------
    // Getters and Setters
    public Integer getIdmisto() {
        return idmisto;
    }

    public void setIdmisto(Integer idmisto) {
        this.idmisto = idmisto;
    }

    public Integer getAdresa() {
        return adresa;
    }

    public void setAdresa(Integer adresa) {
        this.adresa = adresa;
    }

    public Typmista getIdtypmista() {
        return idtypmista;
    }

    public void setIdtypmista(Typmista idtypmista) {
        this.idtypmista = idtypmista;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Integer getQuantitySum() {
        return quantitySum;
    }

    public void setQuantitySum(Integer quantitySum) {
        this.quantitySum = quantitySum;
    }
}