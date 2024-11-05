CREATE SEQUENCE public.misto_idmisto_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE public.misto_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE public.objednavka_idobjednavaka_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE public.objednavka_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE public.typmista_idtypmista_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE public.typmista_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE public.zakaznik_idzakaznik_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE public.zakaznik_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


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
    status  varchar(20) not null,
    avaiablequantity integer not null,
    quantitysum      integer not null
);

create table misto
(
    idmisto          serial
        primary key,
    adresa           varchar(100) not null unique,
    idstul           integer
        references stul,
    idtypmista       integer not null
        references typmista
);



create table zakaznik
(
    idzakaznik   serial
        primary key,
    jmeno        varchar(255) not null,
    prijmeni     varchar(255) not null,
    mail         varchar(255) not null,
    status       varchar(20),
    caspotvrzeni timestamp
);


create table objednavka
(
    idobjednavka integer default nextval('objednavka_idobjednavaka_seq'::regclass) not null
        primary key,
    idmisto      integer                                                           not null
        references misto,
    idzakaznik   integer                                                           not null
        references zakaznik,
    cena         integer                                                           not null,
    quantity     integer                                                           not null,
    datumcas     timestamp                                                         not null,
    status       varchar(3)                                                        not null
);

