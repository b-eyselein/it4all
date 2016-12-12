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

CREATE TABLE publishers (
  id INT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  contact_person VARCHAR(100) NOT NULL,
  address VARCHAR(100) NOT NULL,
  phone VARCHAR(100) DEFAULT NULL
);
INSERT INTO publishers VALUES
	(1, 'Wolters Kluwer Deutschland', 'Hadwig Olschewski', '41747 Viersen - Nelkenstraße 98', '+49 798 / 515024'),
	(2, 'Haufe-Gruppe', 'Folker Schlüter', '67592 Flörsheim-Dalsheim - Waisenstieg 6', '+49 8592 / 724244'),
	(3, 'Mair-Dumont', 'Wilhardt Linden', '79599 Wittlingen - Trobischstraße 23c', '+49 10370 / 898113'),
	(4, 'Random House', 'Germo Priebe', '67310 Hettenleidelheim - Grüner Markt 55', '+49 8032 / 532845'),
	(5, 'Westermann Verlagsgruppe', 'Leyla Wilk', '54346 Mehring - Lina-Meyer-Straße 61', '+49 1619 / 309661'),
	(6, 'Franz Cornelsen Bildungsgruppe', 'Winfriede Goldmann', '24582 Bordesholm - Moorende 24', '+49 222 / 555735'),
	(7, 'Carlsen', 'Walter Andres', '53842 Troisdorf - Tacitusstraße 7c', '+49 4604 / 60062'),
	(8, 'Goldmann', 'Liesbeth Stingl', '74259 Widdern - Seelenberger Straße 6', '+49 5625 / 702537'),
	(9, 'Penguin Books', 'Danuta Rosemann', '25576 Brokdorf - Konrad-Duden-Weg 28', '+49 3316 / 11384'),
	(10, 'Klett-Cotta', 'Caterina Göhring', '07751 Sulza - Polizeimeister-Kaspar-Straße 86', '+49 2402 / 806341'),
	(11, 'Rauch', 'Natalija Buschmann', '18334 Eixen - Gustav-Voigt-Straße 34a', '+49 1901 / 656275'),
	(12, 'Springer Science', 'Gerthold Morgenroth', '17348 Mildenitz - Hertogestraße 67a', '+49 8281 / 682569'),
  (13, 'Heyne', 'Berthold Tunda', '54786 Morga - Hauptstrasse 3', '+49 3856 / 827932');

-- Table "authors"

CREATE TABLE authors (
  id INT PRIMARY KEY,
  first_name VARCHAR(100) NOT NULL,
  family_name VARCHAR(100) NOT NULL,
  birthday DATE NOT NULL
);
INSERT INTO authors VALUES
  (1, 'Joanne K.', 'Rowling', '1965-07-31'),
  (2, 'George', 'Orwell', '1903-06-25'),
  (3, 'John Ronald Reuel', 'Tolkien', '1892-01-03'),
  (4, 'Antoine', 'de Saint-Exupéry', '1900-06-29'),
  (5, 'Robert', 'Ludlum', '1927-05-25');

-- Table "books"

CREATE TABLE books (
  id INT PRIMARY KEY,
  title VARCHAR(100) NOT NULL,
  author_id INT NOT NULL,
  year int(4) DEFAULT NULL,
  publisher_id INT NOT NULL,
  isbn VARCHAR(14) NOT NULL,
  stock INT NOT NULL,
  price FLOAT(5,2) DEFAULT '0.00',
  FOREIGN KEY(author_id) REFERENCES authors(id),
  FOREIGN KEY(publisher_id) REFERENCES publishers(id)
);
INSERT INTO books VALUES
	(1, 'Harry Potter und der Stein der Weisen', 1, 1998, 7, '978-3551354013', 75848, 8.99),
	(2, 'Harry Potter und die Kammer des Schreckens', 1, 1998, 7, '978-3551354020', 71458, 8.99),
	(3, 'Harry Potter und der Gefangene von Askaban', 1, 1999, 7, '978-3551354037', 63419, 10.99),
	(4, 'Harry Potter und der Feuerkelch', 1, 2000, 7, '978-3551354044', 32888, 12.90),
	(5, 'Harry Potter und der Orden des Phönix', 1, 2003, 7, '978-3551354051', 95686, 14.99),
	(6, 'Harry Potter und der Halbblutprinz', 1, 2005, 7, '978-3551354068', 87923, 11.99),
	(7, 'Harry Potter und die Heiligtümer des Todes', 1, 2007, 7, '978-3551354075', 93632, 12.99),
	(8, '1984', 2, 1949, 9,'978-3548234106', 33623, 9.95),
	(9, 'Animal Farm', 2, 1945, 9, '978-3257201185', 60893, 9.00),
	(10, 'Der kleine Hobbit', 3, 1997, 10, '978-3423715669', 89143, 8.95),
	(11, 'Herr der Ringe: Die Gefährten', 3, 2001, 10, '978-3608939811', 30542, 14.95),
	(12, 'Herr der Ringe: Die zwei Türme', 3, 2001, 10, '978-3608939828', 6728, 14.95),
	(13, 'Herr der Ringe: Die Rückkehr des Königs', 3, 2001, 10, '978-3608939835', 56632, 14.95),
	(14, 'Der kleine Prinz', 4, 1943, 11, '978-3730602294', 79562, 3.95),
	(15, 'Die Bourne Identität', 5, 1980, 13, '978-3453438583', 87453, 9.99),
	(16, 'Das Bourne Imperium', 5, 1986, 13, '978-3453438590', 97523, 9.99),
	(17, 'Das Bourne Ultimatum', 5, 1990, 13, '978-3453438606', 54385, 9.99),
  (18, 'Die Stadt in der Wüste', 4, 1956, 11, '978-3792000373', 75483, 5.95);

-- Table "customers"

CREATE TABLE customers (
  id INT PRIMARY KEY,
  first_name VARCHAR(100) NOT NULL,
  family_name VARCHAR(100) NOT NULL,
  birthday date NOT NULL,
  email VARCHAR(100) NOT NULL,
  password VARCHAR(100) DEFAULT NULL,
  address VARCHAR(100) DEFAULT NULL
);
INSERT INTO customers VALUES
	(1, 'Ilrich', 'Horn', '1982-11-04', 'ilrich_1570@aol.com', 'f50d5fb612cb24441aa8f6edf02ba483', '06779 Marke/Rudolfstraße 37c'),
	(2, 'Hilma', 'Woll', '1991-03-10', 'hilma_1411@web.de', '778b1afceb88d9960bad369e55c28442', '01683 Nossen/Amselsteg 40'),
	(3, 'Hilda', 'Endres', '1999-07-08', 'hilda_1181@aol.com', '6f6d73c247980acb23bf668249247279', '27412 Tarmstedt/Johnsbacher Weg 17'),
	(4, 'Sieghard', 'Jung', '1990-04-02', 'sieghard_1474@yahoo.com', 'a7ebd00b89d15d0dd01e819e98068f24', '07570 Wünschendorf/Eichelseeweg 95b'),
	(5, 'Mechthilde', 'Donath', '1978-11-30', 'mechthilde_1442@uni.de', 'a52612d8df6aa92874c257b1de408952', '84032 Altdorf/Arnsberger Straße 39'),
	(6, 'Thomas', 'Schober', '1991-03-02', 'thomas_849@aol.com', 'c110f9a6c8682d61fc1b68dcf992371b', '25551 Silzen/Dopplerstraße 15'),
	(7, 'Paulina', 'Rohr', '1974-09-24', 'xiong@yahoo.com', '02fca1052c878ae92ca83d425bb3a8b3', '15838 Kummersdorf-Gut/Mannheimer Straße 86'),
	(8, 'Amalie', 'Krah', '2000-08-19', 'amalie_1011@yahoo.com', 'a74b7ab833f25efcd07fae00a2805908', '17168 Jördenstorf/Rubinsteinstraße 65b'),
	(9, 'Janet', 'Senger', '1994-01-21', 'janet_1157@uni.de', 'fcd5bc7a142980163bc44ddbdbfc9f0d', '91341 Röttenbach/Heckenweg 63'),
	(10, 'Jost', 'Reiter', '1999-05-21', 'jost_645@gmx.de', '234ec6baa6a8fb37105775a2cc501d49', '99869 Bufleben/Gilbrechtstraße 86'),
	(11, 'Welf', 'Günter', '1986-07-22', 'xiong@yahoo.com', '69e63af6270379a0fa841dbb45f945f8', '49086 Osnabrück/Lochnerstraße 51'),
	(12, 'Wilhard', 'Herold', '1985-01-07', 'wilhard_1041@web.de', '07be9f0e365320bb64f711f0c021a204', '86859 Igling/Wilthener Straße 67'),
	(13, 'Hansl', 'Appel', '1993-02-26', 'hansl_1408@uni.de', '11b317ebf0ac60767af1e878bd054b5d', '73460 Hüttlingen/Ackerweg 52c'),
	(14, 'Sylvelin', 'Hackbarth', '1981-02-13', 'sylvelin_28@aol.com', '8d5058e254dfaffb21de732fcc76854e', '06449 Wilsleben/Postgang 53'),
	(15, 'Pius', 'Nitsche', '1994-07-04', 'pius_609@uni.de', '11676e2af44c99393a74d289253aba2e', '24634 Padenstedt/Gillestraße 43'),
	(16, 'Irlanda', 'Giesen', '1985-09-30', 'irlanda_1047@yahoo.com', '974cdaa3a4ebcb56506b06c01ecb1d23', '39319 Nielebock/Hegelstraße 92'),
	(17, 'Liborius', 'Steffens', '1995-05-17', 'liborius_1545@gmail.com', 'a870d738ba3a144d6ce98096f1ad823a', '06295 Schmalzerode/Helma-Steinbach-Weg 62'),
	(18, 'Christl', 'Seeliger', '1974-03-25', 'christl_478@uni.de', '53c7c3a279d7f169cf5f000a24840c15', '57583 Mörlen/Heideweg 29a'),
	(19, 'Dorchen', 'Rombach', '2001-01-04', 'dorchen_1682@uni.de', 'c4584090e31b5cf80a754c010956f184', '97791 Obersinn/Sechslingspforte 65'),
	(20, 'Arne', 'Dyck', '1976-02-10', 'arne_670@uni.de', 'fe68d57c6679634170c047f57d83e4a0', '54668 Prümzurlay/Dunantring 30'),
	(21, 'Roman', 'Henrich', '1981-06-10', 'roman_757@aol.com', '709c9b8a725206dda51c89ac2809ea53', '23936 Roggenstorf/Hinter dem Zwinger 53a'),
	(22, 'Antonietta', 'Stenger', '1981-09-12', 'xiong@yahoo.com', '3e45af4ca27ea2b03fc6183af40ea112', '21514 Langenlehsten/Wallauer Straße 71'),
	(23, 'Zacharias', 'Thaler', '1971-10-26', 'zacharias_1273@gmail.com', '3e45af4ca27ea2b03fc6183af40ea112', '03226 Koßwig/Norderhof 56'),
	(24, 'Jutta', 'Fuß', '1974-07-20', 'jutta_1892@aol.com', 'beae72071a5cec21564e7a6f960464dd', '24220 Techelsdorf/Josef-Eicher-Straße 93c'),
	(25, 'Eveline', 'Schurr', '1979-07-11', 'eveline_1639@gmx.de', '5317d89be9d1ca2b3ffb37ee2fd6ed03', '91587 Adelshofen/Siget 48b'),
	(26, 'Gusti', 'Orlowski', '1994-02-04', 'gusti_1816@aol.com', 'c1f5a9e158eae934b007193ca3d5aaac', '04736 Waldheim/Herkulesstraße 42'),
	(27, 'Burghild', 'Flaig', '1973-09-17', 'burghild_1528@gmx.de', '90a01c9f5ede5355dc48c80ee0a65db3', '45699 Herten/Ostritzer Straße 9'),
	(28, 'Xaver', 'Kretschmann', '1994-12-05', 'xaver_1562@aol.com', 'c25bd66bde33cf5c28c2ed268be8f288', '55444 Eckenroth/Henry-Budge-Straße 20'),
	(29, 'Janin', 'Smith', '1972-07-06', 'xaver_1562@aol.com', '1cccca9a471b08bf5f52bf04edb334dd', '70794 Filderstadt/Dubliner Straße 91'),
	(30, 'Holk', 'Hack', '1972-11-24', 'holk_901@uni.de', 'f341201275a479515850d78a761ba983', '95448 Bayreuth/Oberer Kreutberg 38');


-- Table "ratings"

CREATE TABLE ratings (
  id INT PRIMARY KEY,
  book_id INT NOT NULL,
  customer_id INT NOT NULL,
  rating tinyint(4) NOT NULL,
  FOREIGN KEY(book_id) REFERENCES books(id),
  FOREIGN KEY(customer_id) REFERENCES customers(id)
);
INSERT INTO ratings VALUES
	(1, 6, 1, 2), (2, 12, 1, 1), (3, 18, 1, 2), (4, 6, 2, 2), (5, 18, 2, 1),
	(6, 6, 3, 4), (7, 12, 3, 5), (8, 18, 3, 4), (9, 12, 5, 4), (10, 6, 6, 4),
	(11, 18, 6, 1), (12, 12, 7, 5), (13, 18, 7, 3), (14, 6, 8, 4), (15, 12, 8, 3),
	(16, 18, 8, 4), (17, 18, 9, 2), (18, 12, 10, 1), (19, 18, 10, 4), (20, 18, 11, 5),
	(21, 6, 12, 5), (22, 18, 12, 5), (23, 6, 13, 3), (24, 12, 13, 4), (25, 6, 14, 3),
	(26, 18, 15, 2), (27, 12, 16, 3), (28, 6, 17, 3), (29, 12, 17, 3), (30, 18, 17, 3),
	(31, 6, 18, 3), (32, 18, 18, 5), (33, 18, 20, 2);

-- Table "wishlists"

CREATE TABLE wishlists (
  customer_id INT NOT NULL,
  book_id INT NOT NULL,
  date date NOT NULL,
  PRIMARY KEY(customer_id, book_id),
  FOREIGN KEY(customer_id) REFERENCES customers(id),
  FOREIGN KEY(book_id) REFERENCES books(id)
);
INSERT INTO wishlists VALUES
	(5, 2, '2012-09-06'), (4, 14, '2012-09-01'), (6, 17, '2012-09-10'), (12, 12, '2013-04-11'),
	(6, 15, '2012-11-14'), (10, 15, '2012-10-24'), (12, 15, '2013-01-23'), (13, 14, '2012-10-04'), (6, 8, '2012-12-16'), (5, 5, '2012-08-15'),
	(7, 15, '2012-09-16'), (7, 2, '2013-04-03'), (11, 18, '2012-07-22'), (3, 10, '2013-04-01'), (1, 18, '2013-01-19'),
	(11, 4, '2013-02-05'), (5, 18, '2013-01-02'), (7, 8, '2012-07-21'), (12, 14, '2012-05-17'), (13, 17, '2012-08-11'),
	(15, 3, '2012-08-31'), (6, 16, '2012-12-10'), (13, 4, '2012-05-21');

-- Table "orders"

CREATE TABLE orders (
  id INT PRIMARY KEY,
  customer_id INT NOT NULL,
  date date NOT NULL,
  paid tinyint(1) NOT NULL DEFAULT '0',
  FOREIGN KEY(customer_id) REFERENCES customers(id)
);
INSERT INTO orders VALUES
	(1,13,'2013-02-16',1), (2,16,'2013-02-23',1), (3,17,'2013-04-11',0), (4,6,'2013-03-06',1),
	(5,12,'2013-02-27',1), (6,18,'2013-03-20',1), (7,13,'2013-03-11',1), (8,3,'2013-04-02',1), (9,16,'2013-04-07',1), (10,7,'2013-02-17',1),
	(11,13,'2013-04-10',1), (12,5,'2013-03-22',0), (13,18,'2013-04-09',1), (14,14,'2013-02-13',1), (15,3,'2013-04-19',1),
	(16,4,'2013-03-11',1), (17,13,'2013-03-27',1), (18,7,'2013-03-07',1), (19,19,'2013-03-19',1), (20,4,'2013-03-13',1),
	(21,11,'2013-04-08',1), (22,4,'2013-04-13',1);

-- Table "order_positions"

CREATE TABLE order_positions (
  id INT PRIMARY KEY,
  order_id INT NOT NULL,
  book_id INT NOT NULL,
  amount INT NOT NULL,
  price FLOAT(5,2) NOT NULL,
  FOREIGN KEY(order_id) REFERENCES orders(ID),
  FOREIGN KEY(book_id) REFERENCES books(id)
);
INSERT INTO order_positions VALUES
	(1, 1, 8, 4, 8.50), (2, 1, 17, 1, 22.99), (3, 1, 18, 2, 8.50), (4, 1, 12, 3, 12.10), (5, 1, 17, 3, 19.99),
	(6, 2, 13, 4, 10.99), (7, 2, 16, 4, 14.60), (8, 2, 11, 3, 12.10), (9, 2, 2, 4, 14.60), (10, 2, 17, 4, 10.99),
	(11, 3, 9, 2, 19.99), (12, 3, 12, 3, 8.50), (13, 4, 3, 3, 9.99), (14, 4, 6, 2, 7.50), (15, 4, 13, 2, 9.50),
	(16, 4, 9, 1, 12.10), (17, 4, 15, 1, 22.99), (18, 6, 16, 1, 12.50), (19, 6, 8, 4, 22.99), (20, 6, 2, 3, 14.60),
	(21, 7, 10, 2, 10.99), (22, 7, 3, 4, 4.99), (23, 7, 12, 4, 19.99), (24, 8, 7, 3, 6.50), (25, 9, 9, 2, 1.99),
	(26, 9, 16, 2, 9.50), (27, 9, 5, 3, 34.50), (28, 9, 12, 2, 7.50), (29, 9, 10, 2, 14.60), (30, 11, 2, 4, 19.99),
	(31, 11, 3, 2, 9.50), (32, 12, 15, 2, 9.99), (33, 12, 18, 3, 19.99), (34, 13, 13, 4, 10.99), (35, 13, 9, 3, 8.50),
	(36, 14, 11, 2, 10.99), (37, 14, 1, 3, 4.99), (38, 15, 4, 2, 22.99), (39, 15, 2, 2, 6.50), (40, 15, 4, 4, 8.50),
	(41, 15, 13, 4, 9.99), (42, 16, 3, 4, 10.99), (43, 16, 7, 3, 7.50), (44, 16, 15, 3, 9.50), (45, 16, 12, 4, 8.50),
	(46, 17, 9, 4, 1.99), (47, 17, 12, 2, 12.10), (48, 17, 10, 4, 7.50), (49, 18, 9, 4, 19.99), (50, 19, 9, 1, 9.50),
	(51, 19, 17, 1, 10.99), (52, 20, 13, 3, 34.50), (53, 20, 13, 1, 12.50), (54, 18, 5, 4, 10.99), (55, 13, 5, 4, 10.99),
	(56, 20, 9, 4, 4.99), (57, 21, 10, 2, 34.50), (58, 21, 12, 2, 1.99), (59, 21, 16, 4, 7.50), (60, 21, 11, 3, 8.50),
	(61, 22, 9, 2, 7.50), (62, 22, 13, 4, 12.50), (63, 22, 9, 1, 12.10), (64, 22, 18, 3, 8.50), (65, 22, 10, 4, 22.99);
