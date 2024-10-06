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

import java.time.Instant;

@Entity
@Table(name = "objednavka")
public class Objednavka {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "idobjednavaka", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idmisto",referencedColumnName = "idmisto", nullable = false)
    private misto idmisto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
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