COPY public.typmista (idtypmista, typmista, cena) FROM stdin;
1	S	300
4	L	700
2	V	600
3	B	500
\.

INSERT INTO public.stul (idstul, nazev, avaiablequantity, quantitysum, idtypmista) VALUES (1,'vpravodole-1', 6,  6 ,  1);
INSERT INTO public.stul (idstul, nazev, avaiablequantity, quantitysum, idtypmista) VALUES (2,'vpravodole-2', 6,  6 ,  1);

INSERT INTO public.misto (idmisto, poradi, idstul, status) VALUES (1, '1', 1, 'A');
INSERT INTO public.misto (idmisto, poradi, idstul, status) VALUES (2, '2', 1, 'A');
INSERT INTO public.misto (idmisto, poradi, idstul, status) VALUES (3, '3', 1, 'A');
INSERT INTO public.misto (idmisto, poradi, idstul, status) VALUES (4, '4', 1, 'A');
INSERT INTO public.misto (idmisto, poradi, idstul, status) VALUES (5, '5', 1, 'A');

INSERT INTO public.zakaznik (idzakaznik, jmeno, prijmeni, mail, status, caspotvrzeni) VALUES (1, null, null, 'adam.ruzicka@email.cz', 'V', null);



