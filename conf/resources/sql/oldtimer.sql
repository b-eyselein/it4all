CREATE TABLE IF NOT EXISTS marke (
  markenOID INT PRIMARY KEY,
  name VARCHAR(20) NOT NULL,
  gruendung YEAR
);

INSERT INTO marke (`markenOID`, `name`, `gruendung`) VALUES
  (1, 'Audi', 1909),
  (2, 'BMW', 1916),
  (3, 'Ford', 1903),
  (4, 'Mercedes', 1926),
  (5, 'Porsche', 1931);
