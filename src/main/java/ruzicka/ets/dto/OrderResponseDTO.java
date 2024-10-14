package ruzicka.ets.dto;

public class OrderResponseDTO {
    private Integer idzak;
    private Integer cena;
    private String cisloBankovnihoUctu;

    // Constructor
    public OrderResponseDTO(Integer idzak, Integer cena, String cisloBankovnihoUctu) {
        this.idzak = idzak;
        this.cena = cena;
        this.cisloBankovnihoUctu = cisloBankovnihoUctu;
    }

    // Getters
    public Integer getIdzak() {
        return idzak;
    }

    public Integer getCena() {
        return cena;
    }

    public String getCisloBankovnihoUctu() {
        return cisloBankovnihoUctu;
    }
}