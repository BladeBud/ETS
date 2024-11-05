-- CREATE SEQUENCE public.misto_idmisto_seq
--     AS integer
--     START WITH 1
--     INCREMENT BY 1
--     NO MINVALUE
--     NO MAXVALUE
--     CACHE 1;
--
-- CREATE SEQUENCE public.misto_seq
--     START WITH 1
--     INCREMENT BY 50
--     NO MINVALUE
--     NO MAXVALUE
--     CACHE 1;
--
-- CREATE SEQUENCE public.objednavka_idobjednavaka_seq
--     AS integer
--     START WITH 1
--     INCREMENT BY 1
--     NO MINVALUE
--     NO MAXVALUE
--     CACHE 1;
--
-- CREATE SEQUENCE public.objednavka_seq
--     START WITH 1
--     INCREMENT BY 50
--     NO MINVALUE
--     NO MAXVALUE
--     CACHE 1;
--
-- CREATE SEQUENCE public.typmista_idtypmista_seq
--     AS integer
--     START WITH 1
--     INCREMENT BY 1
--     NO MINVALUE
--     NO MAXVALUE
--     CACHE 1;
--
-- CREATE SEQUENCE public.typmista_seq
--     START WITH 1
--     INCREMENT BY 50
--     NO MINVALUE
--     NO MAXVALUE
--     CACHE 1;
--
-- CREATE SEQUENCE public.zakaznik_idzakaznik_seq
--     AS integer
--     START WITH 1
--     INCREMENT BY 1
--     NO MINVALUE
--     NO MAXVALUE
--     CACHE 1;
--
-- CREATE SEQUENCE public.zakaznik_seq
--     START WITH 1
--     INCREMENT BY 50
--     NO MINVALUE
--     NO MAXVALUE
--     CACHE 1;
--
-- CREATE SEQUENCE public.misto_objednavka_seq
--     START WITH 1
--     INCREMENT BY 50
--     NO MINVALUE
--     NO MAXVALUE
--     CACHE 1;


create table typmista
(
    idtypmista serial
        primary key,
    typmista   varchar(3) not null,
    cena       integer    not null
);

create table stul
(
    idstul   serial
        primary key,
    nazev    varchar(255) not null unique ,
    avaiablequantity integer not null,
    quantitysum      integer not null,
    idtypmista       integer not null
        references typmista
);

create table misto
(
    idmisto          serial
        primary key,
    poradi           integer not null unique,
    idstul           integer
        references stul,
    status          varchar(3) not null
);

create table zakaznik
(
    idzakaznik   serial
        primary key,
    jmeno        varchar(255) ,
    prijmeni     varchar(255) ,
    mail         varchar(255) not null,
    status       varchar(20),
    caspotvrzeni timestamp
);

create table objednavka
(
    idobjednavka serial
        primary key,
    idzakaznik   integer                                                           not null
        references zakaznik,
    cena         integer                                                           not null,
    datumcas     timestamp                                                         not null,
    status       varchar(20)                                                       not null
);

create table misto_objednavka
(
    idmistoobjednavka serial
        primary key,
    idmisto      integer not null
        references misto,
    idobjednavka integer not null
        references objednavka
);
