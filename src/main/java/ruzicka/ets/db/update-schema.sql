ALTER TABLE misto
DROP
CONSTRAINT misto_idtypmista_fkey;

ALTER TABLE objednavka
DROP
CONSTRAINT objednavka_idmisto_fkey;

ALTER TABLE objednavka
DROP
CONSTRAINT objednavka_idzakaznik_fkey;

ALTER TABLE misto
    ADD id_misto INTEGER;

DROP TABLE objednavka CASCADE;

DROP TABLE typmista CASCADE;

DROP TABLE zakaznik CASCADE;

ALTER TABLE misto
DROP
COLUMN idmisto;

ALTER TABLE misto
DROP
COLUMN adresa;

ALTER TABLE misto
DROP
COLUMN avaiablequantity;

ALTER TABLE misto
DROP
COLUMN idtypmista;

ALTER TABLE misto
DROP
COLUMN quantitysum;

ALTER TABLE misto
    ADD CONSTRAINT pk_misto PRIMARY KEY (id_misto);