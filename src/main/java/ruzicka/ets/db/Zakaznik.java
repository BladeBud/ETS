package ruzicka.ets.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.sql.Timestamp;

/**
 * Represents a customer in the system.
 * Each instance of this class corresponds to a record in the "zakaznik" table.
 */
@Entity
@Table(name = "zakaznik")
public class Zakaznik {
    //----------------------------------------------------------------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "zakaznik_idzakaznik_seq")
    @SequenceGenerator(name = "zakaznik_idzakaznik_seq", sequenceName = "zakaznik_idzakaznik_seq", allocationSize = 1)
    @Column(name = "idzakaznik", nullable = false)
    private Integer idzakaznik;

    @Column(name = "jmeno", nullable = true, length = 255)
    private String jmeno;

    @Column(name = "prijmeni", nullable = true, length = 255)
    private String prijmeni;

    @Column(name = "mail", nullable = false, length = 255)
    private String mail;

    @Column(name = "status", nullable = false, length = 20)
    private String status;  // Used for tracking email verification: "PENDING", "VERIFIED", etc.

    @Column(name = "caspotvrzeni")
    private Timestamp caspotvrzeni;
    //----------------------------------------------------------------------------------------------------------------------
    // Getters and Setters
    public Integer getIdzakaznik() {
        return idzakaznik;
    }

    public void setIdzakaznik(Integer idzakaznik) {
        this.idzakaznik = idzakaznik;
    }

    public String getJmeno() {
        return jmeno;
    }

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    public String getPrijmeni() {
        return prijmeni;
    }

    public void setPrijmeni(String prijmeni) {
        this.prijmeni = prijmeni;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCaspotvrzeni() {
        return caspotvrzeni;
    }

    public void setCaspotvrzeni(Timestamp caspotvrzeni) {
        this.caspotvrzeni = caspotvrzeni;
    }
}