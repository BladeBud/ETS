package ruzicka.ets.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author czech
 * @since 2024-11-05
 */

@Entity
@Table(name = "misto_objednavka")
public class MistoObjednavka {
    //----------------------------------------------------------------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "idmistoobjednavka", nullable = false)
    private Integer idmistoobjednavka;

    @Column(name = "idmisto", nullable = false)
    private Integer idmisto;

    @Column(name = "idobjednavka", nullable = false)
    private Integer idobjednavka;

    //----------------------------------------------------------------------------------------------------------------------

    public Integer getIdmistoobjednavka() {
        return idmistoobjednavka;
    }

    public MistoObjednavka setIdmistoobjednavka(Integer idmistoobjednavka) {
        this.idmistoobjednavka = idmistoobjednavka;
        return this;
    }

    public Integer getIdmisto() {
        return idmisto;
    }

    public MistoObjednavka setIdmisto(Integer idmisto) {
        this.idmisto = idmisto;
        return this;
    }

    public Integer getIdobjednavka() {
        return idobjednavka;
    }

    public MistoObjednavka setIdobjednavka(Integer idobjednavka) {
        this.idobjednavka = idobjednavka;
        return this;
    }
}


