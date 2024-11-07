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
public class Misto {
//----------------------------------------------------------------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "idmisto", nullable = false)
    private Integer idmisto;

    @Column(name = "poradi", nullable = false)
    private String poradi;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idstul", nullable = false)
    private Stul stul;

    @Column(name = "status", nullable = false)
    private String status;


//----------------------------------------------------------------------------------------------------------------------
    // Getters and Setters

    public Integer getIdmisto() {
        return idmisto;
    }

    public Misto setIdmisto(Integer idmisto) {
        this.idmisto = idmisto;
        return this;
    }

    public String getPoradi() {
        return poradi;
    }

    public Misto setPoradi(String poradi) {
        this.poradi = poradi;
        return this;
    }

    public Stul getStul() {
        return stul;
    }

    public Misto setStul(Stul stul) {
        this.stul = stul;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public Misto setStatus(String status) {
        this.status = status;
        return this;
    }

    public enum Status {
        A, R
    }
}