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
@Table(name = "zakaznik")
public class Zakaznik {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "idzakaznik", nullable = false)
    private Integer id;

    @Column(name = "jmeno", nullable = false)
    private String jmeno;

    @Column(name = "prijmeni", nullable = false)
    private String prijmeni;

    @Column(name = "mail", nullable = false)
    private String mail;

    @OneToMany(mappedBy = "idzakaznik")
    private Set<Objednavka> objednavkas = new LinkedHashSet<>();

    @Column(name = "status", length = 3)
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<Objednavka> getObjednavkas() {
        return objednavkas;
    }

    public void setObjednavkas(Set<Objednavka> objednavkas) {
        this.objednavkas = objednavkas;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPrijmeni() {
        return prijmeni;
    }

    public void setPrijmeni(String prijmeni) {
        this.prijmeni = prijmeni;
    }

    public String getJmeno() {
        return jmeno;
    }

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}