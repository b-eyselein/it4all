-- DROP all tables
DROP TABLE IF EXISTS order_positions;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS wishlists;
DROP TABLE IF EXISTS ratings;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS authors;
DROP TABLE IF EXISTS publishers;

-- Table "publishers"

CREATE TABLE publishers
(
  id             INT PRIMARY KEY,
  name           VARCHAR(100) NOT NULL,
  contact_person VARCHAR(100) NOT NULL,
  address        VARCHAR(100) NOT NULL,
  phone          VARCHAR(100) DEFAULT NULL
);

INSERT INTO publishers (id, name, contact_person, address, phone)
VALUES (1, 'Wolters Kluwer Deutschland', 'Hadwig Olschewski', '41747 Viersen - Nelkenstraße 98', '+49 798 / 515024'),
       (2, 'Haufe-Gruppe', 'Folker Schlüter', '67592 Flörsheim-Dalsheim - Waisenstieg 6', '+49 8592 / 724244'),
       (3, 'Mair-Dumont', 'Wilhardt Linden', '79599 Wittlingen - Trobischstraße 23c', '+49 10370 / 898113'),
       (4, 'Random House', 'Germo Priebe', '67310 Hettenleidelheim - Grüner Markt 55', '+49 8032 / 532845'),
       (5, 'Westermann Verlagsgruppe', 'Leyla Wilk', '54346 Mehring - Lina-Meyer-Straße 61', '+49 1619 / 309661'),
       (6, 'Franz Cornelsen Bildungsgruppe', 'Winfriede Goldmann', '24582 Bordesholm - Moorende 24',
        '+49 222 / 555735'),
       (7, 'Carlsen', 'Walter Andres', '53842 Troisdorf - Tacitusstraße 7c', '+49 4604 / 60062'),
       (8, 'Goldmann', 'Liesbeth Stingl', '74259 Widdern - Seelenberger Straße 6', '+49 5625 / 702537'),
       (9, 'Penguin Books', 'Danuta Rosemann', '25576 Brokdorf - Konrad-Duden-Weg 28', '+49 3316 / 11384'),
       (10, 'Klett-Cotta', 'Caterina Göhring', '07751 Sulza - Polizeimeister-Kaspar-Straße 86', '+49 2402 / 806341'),
       (11, 'Rauch', 'Natalija Buschmann', '18334 Eixen - Gustav-Voigt-Straße 34a', '+49 1901 / 656275'),
       (12, 'Springer Science', 'Gerthold Morgenroth', '17348 Mildenitz - Hertogestraße 67a', '+49 8281 / 682569'),
       (13, 'Heyne', 'Berthold Tunda', '54786 Morga - Hauptstrasse 3', '+49 3856 / 827932');

-- Table "authors"

CREATE TABLE authors
(
  id          INT PRIMARY KEY,
  first_name  VARCHAR(100) NOT NULL,
  family_name VARCHAR(100) NOT NULL,
  birthday    DATE         NOT NULL
);

INSERT INTO authors (id, first_name, family_name, birthday)
VALUES (1, 'Joanne K.', 'Rowling', '1965-07-31'),
       (2, 'George', 'Orwell', '1903-06-25'),
       (3, 'John Ronald Reuel', 'Tolkien', '1892-01-03'),
       (4, 'Antoine', 'de Saint-Exupéry', '1900-06-29'),
       (5, 'Robert', 'Ludlum', '1927-05-25');

-- Table "books"

CREATE TABLE books
(
  id           INT,
  author_id    INT,
  publisher_id INT          NOT NULL,
  title        VARCHAR(100) NOT NULL,
  year         INT(4)       NOT NULL,
  isbn         VARCHAR(14)  NOT NULL,
  stock        INT          NOT NULL,
  price        FLOAT(5, 2) DEFAULT '0.00',
  PRIMARY KEY (id, author_id),
  FOREIGN KEY (author_id) REFERENCES authors (id) ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (publisher_id) REFERENCES publishers (id) ON UPDATE CASCADE ON DELETE CASCADE
);

INSERT INTO books (id, author_id, publisher_id, title, year, isbn, stock, price)
VALUES (1, 1, 7, 'Harry Potter und der Stein der Weisen', 1998, '978-3551354013', 75848, 8.99),
       (2, 1, 7, 'Harry Potter und die Kammer des Schreckens', 1998, '978-3551354020', 71458, 8.99),
       (3, 1, 7, 'Harry Potter und der Gefangene von Askaban', 1999, '978-3551354037', 63419, 10.99),
       (4, 1, 7, 'Harry Potter und der Feuerkelch', 2000, '978-3551354044', 32888, 12.90),
       (5, 1, 7, 'Harry Potter und der Orden des Phönix', 2003, '978-3551354051', 95686, 14.99),
       (6, 1, 7, 'Harry Potter und der Halbblutprinz', 2005, '978-3551354068', 87923, 11.99),
       (7, 1, 7, 'Harry Potter und die Heiligtümer des Todes', 2007, '978-3551354075', 93632, 12.99),
       (8, 2, 9, '1984', 1949, '978-3548234106', 33623, 9.95),
       (9, 2, 9, 'Animal Farm', 1945, '978-3257201185', 60893, 9.00),
       (10, 3, 10, 'Der kleine Hobbit', 1997, '978-3423715669', 89143, 8.95),
       (11, 3, 10, 'Herr der Ringe: Die Gefährten', 2001, '978-3608939811', 30542, 14.95),
       (12, 3, 10, 'Herr der Ringe: Die zwei Türme', 2001, '978-3608939828', 6728, 14.95),
       (13, 3, 10, 'Herr der Ringe: Die Rückkehr des Königs', 2001, '978-3608939835', 56632, 14.95),
       (14, 4, 11, 'Der kleine Prinz', 1943, '978-3730602294', 79562, 3.95),
       (15, 5, 13, 'Die Bourne Identität', 1980, '978-3453438583', 87453, 9.99),
       (16, 5, 13, 'Das Bourne Imperium', 1986, '978-3453438590', 97523, 9.99),
       (17, 5, 13, 'Das Bourne Ultimatum', 1990, '978-3453438606', 54385, 9.99),
       (18, 4, 11, 'Die Stadt in der Wüste', 1956, '978-3792000373', 75483, 5.95);

-- Table "customers"

CREATE TABLE customers
(
  id          INT PRIMARY KEY,
  first_name  VARCHAR(100) NOT NULL,
  family_name VARCHAR(100) NOT NULL,
  birthday    DATE         NOT NULL,
  email       VARCHAR(100) NOT NULL,
  password    VARCHAR(100) DEFAULT NULL,
  address     VARCHAR(100) DEFAULT NULL
);

INSERT INTO customers (id, first_name, family_name, birthday, email, password, address)
VALUES (1, 'Ilrich', 'Horn', '1982-11-04', 'ilrich_1570@aol.com', 'f50d5fb612cb24441aa8f6edf02ba483',
        '06779 Marke/Rudolfstraße 37c'),
       (2, 'Hilma', 'Woll', '1991-03-10', 'hilma_1411@web.de', '778b1afceb88d9960bad369e55c28442',
        '01683 Nossen/Amselsteg 40'),
       (3, 'Hilda', 'Endres', '1999-07-08', 'hilda_1181@aol.com', '6f6d73c247980acb23bf668249247279',
        '27412 Tarmstedt/Johnsbacher Weg 17'),
       (4, 'Sieghard', 'Jung', '1990-04-02', 'sieghard_1474@yahoo.com', 'a7ebd00b89d15d0dd01e819e98068f24',
        '07570 Wünschendorf/Eichelseeweg 95b'),
       (5, 'Mechthilde', 'Donath', '1978-11-30', 'mechthilde_1442@uni.de', 'a52612d8df6aa92874c257b1de408952',
        '84032 Altdorf/Arnsberger Straße 39'),
       (6, 'Thomas', 'Schober', '1991-03-02', 'thomas_849@aol.com', 'c110f9a6c8682d61fc1b68dcf992371b',
        '25551 Silzen/Dopplerstraße 15'),
       (7, 'Paulina', 'Rohr', '1974-09-24', 'xiong@yahoo.com', '02fca1052c878ae92ca83d425bb3a8b3',
        '15838 Kummersdorf-Gut/Mannheimer Straße 86'),
       (8, 'Amalie', 'Krah', '2000-08-19', 'amalie_1011@yahoo.com', 'a74b7ab833f25efcd07fae00a2805908',
        '17168 Jördenstorf/Rubinsteinstraße 65b'),
       (9, 'Janet', 'Senger', '1994-01-21', 'janet_1157@uni.de', 'fcd5bc7a142980163bc44ddbdbfc9f0d',
        '91341 Röttenbach/Heckenweg 63'),
       (10, 'Jost', 'Reiter', '1999-05-21', 'jost_645@gmx.de', '234ec6baa6a8fb37105775a2cc501d49',
        '99869 Bufleben/Gilbrechtstraße 86'),
       (11, 'Welf', 'Günter', '1986-07-22', 'xiong@yahoo.com', '69e63af6270379a0fa841dbb45f945f8',
        '49086 Osnabrück/Lochnerstraße 51'),
       (12, 'Wilhard', 'Herold', '1985-01-07', 'wilhard_1041@web.de', '07be9f0e365320bb64f711f0c021a204',
        '86859 Igling/Wilthener Straße 67'),
       (13, 'Hansl', 'Appel', '1993-02-26', 'hansl_1408@uni.de', '11b317ebf0ac60767af1e878bd054b5d',
        '73460 Hüttlingen/Ackerweg 52c'),
       (14, 'Sylvelin', 'Hackbarth', '1981-02-13', 'sylvelin_28@aol.com', '8d5058e254dfaffb21de732fcc76854e',
        '06449 Wilsleben/Postgang 53'),
       (15, 'Pius', 'Nitsche', '1994-07-04', 'pius_609@uni.de', '11676e2af44c99393a74d289253aba2e',
        '24634 Padenstedt/Gillestraße 43'),
       (16, 'Irlanda', 'Giesen', '1985-09-30', 'irlanda_1047@yahoo.com', '974cdaa3a4ebcb56506b06c01ecb1d23',
        '39319 Nielebock/Hegelstraße 92'),
       (17, 'Liborius', 'Steffens', '1995-05-17', 'liborius_1545@gmail.com', 'a870d738ba3a144d6ce98096f1ad823a',
        '06295 Schmalzerode/Helma-Steinbach-Weg 62'),
       (18, 'Christl', 'Seeliger', '1974-03-25', 'christl_478@uni.de', '53c7c3a279d7f169cf5f000a24840c15',
        '57583 Mörlen/Heideweg 29a'),
       (19, 'Dorchen', 'Rombach', '2001-01-04', 'dorchen_1682@uni.de', 'c4584090e31b5cf80a754c010956f184',
        '97791 Obersinn/Sechslingspforte 65'),
       (20, 'Arne', 'Dyck', '1976-02-10', 'arne_670@uni.de', 'fe68d57c6679634170c047f57d83e4a0',
        '54668 Prümzurlay/Dunantring 30'),
       (21, 'Roman', 'Henrich', '1981-06-10', 'roman_757@aol.com', '709c9b8a725206dda51c89ac2809ea53',
        '23936 Roggenstorf/Hinter dem Zwinger 53a'),
       (22, 'Antonietta', 'Stenger', '1981-09-12', 'xiong@yahoo.com', '3e45af4ca27ea2b03fc6183af40ea112',
        '21514 Langenlehsten/Wallauer Straße 71'),
       (23, 'Zacharias', 'Thaler', '1971-10-26', 'zacharias_1273@gmail.com', '3e45af4ca27ea2b03fc6183af40ea112',
        '03226 Koßwig/Norderhof 56'),
       (24, 'Jutta', 'Fuß', '1974-07-20', 'jutta_1892@aol.com', 'beae72071a5cec21564e7a6f960464dd',
        '24220 Techelsdorf/Josef-Eicher-Straße 93c'),
       (25, 'Eveline', 'Schurr', '1979-07-11', 'eveline_1639@gmx.de', '5317d89be9d1ca2b3ffb37ee2fd6ed03',
        '91587 Adelshofen/Siget 48b'),
       (26, 'Gusti', 'Orlowski', '1994-02-04', 'gusti_1816@aol.com', 'c1f5a9e158eae934b007193ca3d5aaac',
        '04736 Waldheim/Herkulesstraße 42'),
       (27, 'Burghild', 'Flaig', '1973-09-17', 'burghild_1528@gmx.de', '90a01c9f5ede5355dc48c80ee0a65db3',
        '45699 Herten/Ostritzer Straße 9'),
       (28, 'Xaver', 'Kretschmann', '1994-12-05', 'xaver_1562@aol.com', 'c25bd66bde33cf5c28c2ed268be8f288',
        '55444 Eckenroth/Henry-Budge-Straße 20'),
       (29, 'Janin', 'Smith', '1972-07-06', 'xaver_1562@aol.com', '1cccca9a471b08bf5f52bf04edb334dd',
        '70794 Filderstadt/Dubliner Straße 91'),
       (30, 'Holk', 'Hack', '1972-11-24', 'holk_901@uni.de', 'f341201275a479515850d78a761ba983',
        '95448 Bayreuth/Oberer Kreutberg 38');

-- Table "ratings"

CREATE TABLE ratings
(
  book_id     INT        NOT NULL,
  customer_id INT        NOT NULL,
  rating      TINYINT(4) NOT NULL,
  PRIMARY KEY (book_id, customer_id),
  FOREIGN KEY (book_id) REFERENCES books (id) ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (customer_id) REFERENCES customers (id) ON UPDATE CASCADE ON DELETE CASCADE
);

INSERT INTO ratings (book_id, customer_id, rating)
VALUES (6, 1, 2),
       (6, 2, 2),
       (6, 3, 4),
       (6, 6, 4),
       (6, 8, 4),
       (6, 12, 5),
       (6, 13, 3),
       (6, 14, 3),
       (6, 17, 3),
       (6, 18, 3),
       -- 12
       (12, 1, 1),
       (12, 3, 5),
       (12, 5, 4),
       (12, 7, 5),
       (12, 8, 3),
       (12, 10, 1),
       (12, 13, 4),
       (12, 16, 3),
       (12, 17, 3),
       -- 18
       (18, 1, 2),
       (18, 2, 1),
       (18, 3, 4),
       (18, 6, 1),
       (18, 7, 3),
       (18, 8, 4),
       (18, 9, 2),
       (18, 10, 4),
       (18, 11, 5),
       (18, 12, 5),
       (18, 15, 2),
       (18, 17, 3),
       (18, 18, 5),
       (18, 20, 2);

-- Table "wishlists"

CREATE TABLE wishlists
(
  customer_id INT,
  book_id     INT,
  date        DATE NOT NULL,
  PRIMARY KEY (customer_id, book_id),
  FOREIGN KEY (customer_id) REFERENCES customers (id) ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (book_id) REFERENCES books (id) ON UPDATE CASCADE ON DELETE CASCADE
);

INSERT INTO wishlists (customer_id, book_id, date)
VALUES (5, 2, '2012-09-06'),
       (4, 14, '2012-09-01'),
       (6, 17, '2012-09-10'),
       (12, 12, '2013-04-11'),
       (6, 15, '2012-11-14'),
       (10, 15, '2012-10-24'),
       (12, 15, '2013-01-23'),
       (13, 14, '2012-10-04'),
       (6, 8, '2012-12-16'),
       (5, 5, '2012-08-15'),
       (7, 15, '2012-09-16'),
       (7, 2, '2013-04-03'),
       (11, 18, '2012-07-22'),
       (3, 10, '2013-04-01'),
       (1, 18, '2013-01-19'),
       (11, 4, '2013-02-05'),
       (5, 18, '2013-01-02'),
       (7, 8, '2012-07-21'),
       (12, 14, '2012-05-17'),
       (13, 17, '2012-08-11'),
       (15, 3, '2012-08-31'),
       (6, 16, '2012-12-10'),
       (13, 4, '2012-05-21');

-- Table "orders"

CREATE TABLE orders
(
  id          INT,
  customer_id INT     NOT NULL,
  date        DATE    NOT NULL,
  paid        BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (id, customer_id),
  FOREIGN KEY (customer_id) REFERENCES customers (id) ON UPDATE CASCADE ON DELETE CASCADE
);

INSERT INTO orders (id, customer_id, date, paid)
VALUES (1, 3, '2013-04-02', TRUE),
       (2, 3, '2013-04-19', TRUE),
       (1, 4, '2013-03-11', TRUE),
       (2, 4, '2013-03-13', TRUE),
       (3, 4, '2013-04-13', TRUE),
       (1, 5, '2013-03-22', FALSE),
       (1, 6, '2013-03-06', TRUE),
       (1, 7, '2013-02-17', TRUE),
       (2, 7, '2013-03-07', TRUE),
       (1, 11, '2013-04-08', TRUE),
       (1, 12, '2013-02-27', TRUE),
       (1, 13, '2013-02-16', TRUE),
       (2, 13, '2013-03-11', TRUE),
       (3, 13, '2013-04-10', TRUE),
       (4, 13, '2013-03-27', TRUE),
       (1, 14, '2013-02-13', TRUE),
       (1, 16, '2013-02-23', TRUE),
       (2, 16, '2013-04-07', TRUE),
       (1, 17, '2013-04-11', FALSE),
       (1, 18, '2013-03-20', TRUE),
       (2, 18, '2013-04-09', TRUE),
       (1, 19, '2013-03-19', TRUE);

-- Table "order_positions"

CREATE TABLE order_positions
(
  id          INT,
  order_id    INT,
  customer_id INT,
  book_id     INT,
  amount      INT         NOT NULL,
  price       FLOAT(5, 2) NOT NULL,
  PRIMARY KEY (id, order_id, customer_id, book_id),
  FOREIGN KEY (order_id, customer_id) REFERENCES orders (id, customer_id) ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (book_id) REFERENCES books (id) ON UPDATE CASCADE ON DELETE CASCADE
);

INSERT INTO order_positions (id, order_id, customer_id, book_id, amount, price)
VALUES -- order 1 - cust 3
       (1, 1, 3, 4, 2, 22.99),
       (2, 1, 3, 2, 2, 6.50),
       (3, 1, 3, 4, 4, 8.50),
       (4, 1, 3, 13, 4, 9.99),
       -- order 2 - customer 3
       (1, 2, 3, 7, 3, 6.50),
       -- order 1 - customer 4
       (1, 1, 4, 3, 4, 10.99),
       (2, 1, 4, 7, 3, 7.50),
       (3, 1, 4, 15, 3, 9.50),
       (4, 1, 4, 12, 4, 8.50),
       -- order 2 - customer 4
       (1, 2, 4, 13, 3, 34.50),
       (2, 2, 4, 13, 1, 12.50),
       (3, 2, 4, 9, 4, 4.99),
       -- order 3 - customer 4
       (1, 3, 4, 9, 2, 7.50),
       (2, 3, 4, 13, 4, 12.50),
       (3, 3, 4, 9, 1, 12.10),
       (4, 3, 4, 18, 3, 8.50),
       (5, 3, 4, 10, 4, 22.99),
       -- order 1 - customer 5
       (1, 1, 5, 15, 2, 9.99),
       (2, 1, 5, 18, 3, 19.99),
       -- order 1 - customer 6
       (1, 1, 6, 3, 3, 9.99),
       (2, 1, 6, 6, 2, 7.50),
       (3, 1, 6, 13, 2, 9.50),
       (4, 1, 6, 9, 1, 12.10),
       (5, 1, 6, 15, 1, 22.99),
       -- order 1 - customer 7
       (1, 1, 7, 9, 4, 19.99),
       (1, 1, 7, 5, 4, 10.99),
       -- order 1 - customer 11
       (1, 1, 11, 10, 2, 34.50),
       (2, 1, 11, 12, 2, 1.99),
       (3, 1, 11, 16, 4, 7.50),
       (4, 1, 11, 11, 3, 8.50),
       -- order 1 - customer 13
       (1, 1, 13, 8, 4, 8.50),
       (2, 1, 13, 17, 1, 22.99),
       (3, 1, 13, 18, 2, 8.50),
       (4, 1, 13, 12, 3, 12.10),
       (5, 1, 13, 17, 3, 19.99),
       -- order 2 - customer 13
       (1, 2, 13, 10, 2, 10.99),
       (2, 2, 13, 3, 4, 4.99),
       (3, 2, 13, 12, 4, 19.99),
       -- order 3 - customer 13
       (1, 3, 13, 2, 4, 19.99),
       (2, 3, 13, 3, 2, 9.50),
       -- order 4 - customer 14
       (1, 4, 13, 9, 4, 1.99),
       (2, 4, 13, 12, 2, 12.10),
       (3, 4, 13, 10, 4, 7.50),
       -- order 1 - customer 14
       (1, 1, 14, 11, 2, 10.99),
       (2, 1, 14, 1, 3, 4.99),
       -- order 1 - customer 16
       (1, 1, 16, 13, 4, 10.99),
       (2, 1, 16, 16, 4, 14.60),
       (3, 1, 16, 11, 3, 12.10),
       (4, 1, 16, 2, 4, 14.60),
       (5, 1, 16, 17, 4, 10.99),
       -- order 2 - customer 16
       (1, 2, 16, 9, 2, 1.99),
       (2, 2, 16, 16, 2, 9.50),
       (3, 2, 16, 5, 3, 34.50),
       (4, 2, 16, 12, 2, 7.50),
       (5, 2, 16, 10, 2, 14.60),
       -- order 1 - customer 17
       (1, 1, 17, 9, 2, 19.99),
       (2, 1, 17, 12, 3, 8.50),
       -- order 1 - customer 18
       (1, 1, 18, 16, 1, 12.50),
       (2, 1, 18, 8, 4, 22.99),
       (3, 1, 18, 2, 3, 14.60),
       -- order 2 - customer 18
       (1, 2, 18, 13, 4, 10.99),
       (2, 2, 18, 9, 3, 8.50),
       (3, 2, 18, 5, 4, 10.99),
       -- order 1 - customer 19
       (1, 1, 19, 9, 1, 9.50),
       (2, 1, 19, 17, 1, 10.99);
