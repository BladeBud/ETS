package ruzicka.ets.db;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.Instant;

/**
 * Represents an order in the system.
 * Each instance of this class corresponds to a record in the "objednavka" table.
 */
@Entity
@Table(name = "objednavka")
public class Objednavka {
//----------------------------------------------------------------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "idobjednavka", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "idmisto",referencedColumnName = "idmisto", nullable = false)
    private misto idmisto;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "idzakaznik",referencedColumnName = "idzakaznik", nullable = false)
    private Zakaznik idzakaznik;

    @Column(name = "cena", nullable = false)
    private Integer cena;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "datumcas", nullable = false)
    private Instant datumcas;

    @Column(name = "status", nullable = false, length = 3)
    private String status;
//----------------------------------------------------------------------------------------------------------------------
    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public misto getIdmisto() {
        return idmisto;
    }

    public void setIdmisto(misto idmisto) {
        this.idmisto = idmisto;
    }

    public Zakaznik getIdzakaznik() {
        return idzakaznik;
    }

    public void setIdzakaznik(Zakaznik idzakaznik) {
        this.idzakaznik = idzakaznik;
    }

    public Integer getCena() {
        return cena;
    }

    public void setCena(Integer cena) {
        this.cena = cena;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Instant getDatumcas() {
        return datumcas;
    }

    public void setDatumcas(Instant datumcas) {
        this.datumcas = datumcas;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}