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
@Table(name = "Stul")
public class Stul {
//----------------------------------------------------------------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "idstul", nullable = false)
    private Integer idstul;

    @Column(name = "nazev", nullable = false)
    private String nazev;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtypmista", nullable = false)
    private Typmista idtypmista;

    @Column(name = "avaiablequantity", nullable = false)
    private Integer availableQuantity;

    @Column(name = "quantitysum", nullable = false)
    private Integer quantitySum;
//----------------------------------------------------------------------------------------------------------------------
    // Getters and Setters

    public Integer getIdstul() {
        return idstul;
    }

    public Stul setIdstul(Integer idstul) {
        this.idstul = idstul;
        return this;
    }

    public String getNazev() {
        return nazev;
    }

    public Stul setNazev(String nazev) {
        this.nazev = nazev;
        return this;
    }

    public Typmista getIdtypmista() {
        return idtypmista;
    }

    public Stul setIdtypmista(Typmista idtypmista) {
        this.idtypmista = idtypmista;
        return this;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public Stul setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
        return this;
    }

    public Integer getQuantitySum() {
        return quantitySum;
    }

    public Stul setQuantitySum(Integer quantitySum) {
        this.quantitySum = quantitySum;
        return this;
    }
}