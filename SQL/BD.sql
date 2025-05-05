PRAGMA foreign_keys = ON;

DROP TABLE IF EXISTS Detalii_Tranzactii;
DROP TABLE IF EXISTS Tranzactii;
DROP TABLE IF EXISTS Produse;
DROP TABLE IF EXISTS Clienti;


CREATE TABLE IF NOT EXISTS Clienti (
    ID_Client INTEGER PRIMARY KEY AUTOINCREMENT,
    Nume VARCHAR(50) NOT NULL,
    Prenume VARCHAR(50) NOT NULL,
    Email VARCHAR(100),
    Telefon VARCHAR(15),
    Data_Nasterii DATE,
    Adresa TEXT,
    Oras VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS Produse (
     ID_Produs INTEGER PRIMARY KEY AUTOINCREMENT,
    Nume_Produs VARCHAR(100) NOT NULL,
    Categorie VARCHAR(50),
    Pret DECIMAL(10, 2) NOT NULL CHECK (Pret > 0)
);


CREATE TABLE IF NOT EXISTS Tranzactii (
    ID_Tranzactie INTEGER PRIMARY KEY AUTOINCREMENT,
    ID_Client INTEGER NOT NULL,
    Data_Tranzactie DATETIME NOT NULL,
    Valoare_Totala DECIMAL(10, 2),
    FOREIGN KEY (ID_Client) REFERENCES Clienti(ID_Client) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Detalii_Tranzactii (
    ID_Tranzactie INTEGER NOT NULL,
    ID_Produs INTEGER NOT NULL,
    Cantitate INT NOT NULL,
    Pret_Total DECIMAL(10, 2) NOT NULL CHECK (Pret_Total > 0),
    PRIMARY KEY (ID_Tranzactie, ID_Produs),
    FOREIGN KEY (ID_Tranzactie) REFERENCES Tranzactii(ID_Tranzactie) ON DELETE CASCADE,
    FOREIGN KEY (ID_Produs) REFERENCES Produse(ID_Produs) ON DELETE CASCADE
);


SELECT name, type FROM sqlite_master;

SELECT name 
FROM sqlite_master 
WHERE type = 'table';

PRAGMA table_info(Clienti);
PRAGMA table_info(Produse);
PRAGMA table_info(Tranzactii);
PRAGMA table_info(Detalii_Tranzactii);


PRAGMA foreign_key_list(Tranzactii);
PRAGMA foreign_key_list(Detalii_Tranzactii);
PRAGMA foreign_key_list(Produse);
PRAGMA foreign_key_list(Clienti);

DROP INDEX IF EXISTS idx_client_nume_prenume;
DROP INDEX IF EXISTS idx_Nume_Prod;
CREATE INDEX IF NOT EXISTS idx_client_nume_prenume ON Clienti(ID_Client,Nume, Prenume);
CREATE INDEX IF NOT EXISTS idx_Nume_Prod ON Produse(ID_Produs,Nume_Produs);

SELECT name FROM sqlite_master WHERE type = 'index';


INSERT INTO Clienti (Nume, Prenume, Email, Telefon, Data_Nasterii, Adresa, Oras)
VALUES 
    ('Popescu', 'Ion', 'ion.popescu@gmail.com', '0741234567', '1985-06-15', 'Strada Florilor, Nr. 10', 'Bucuresti'),
    ('Ionescu', 'Maria', 'maria.ionescu@yahoo.com', '0729876543', '1990-03-22', 'Strada Libertatii, Nr. 5', 'Cluj-Napoca'),
    ('Georgescu', 'Andrei', 'andrei.georgescu@gmail.com', '0734567890', '1978-11-03', 'Strada Mihai Viteazu, Nr. 15', 'Timisoara'),
    ('Dumitru', 'Elena', 'elena.dumitru@yahoo.com', '0765432109', '1988-09-12', 'Strada Unirii, Nr. 8', 'Constanta'),
    ('Vasilescu', 'Ioana', 'ioana.vasilescu@gmail.com', '0756789012', '1995-01-18', 'Strada Eminescu, Nr. 20', 'Iasi'),
    ('Stoica', 'Daniel', 'daniel.stoica@yahoo.com', '0723456789', '1980-05-25', 'Strada Primaverii, Nr. 30', 'Craiova'),
    ('Marinescu', 'Cristina', 'cristina.marinescu@gmail.com', '0767890123', '1992-10-10', 'Strada Vasile Alecsandri, Nr. 22', 'Brasov'),
    ('Tudor', 'Radu', 'radu.tudor@yahoo.com', '0732109876', '1986-12-20', 'Strada Republicii, Nr. 14', 'Ploiesti'),
    ('Grigore', 'Ana', 'ana.grigore@gmail.com', '0745678901', '1993-08-30', 'Strada Independentei, Nr. 9', 'Oradea'),
    ('Iliescu', 'Mihai', 'mihai.iliescu@yahoo.com', '0754321098', '1987-04-05', 'Strada Ion Creanga, Nr. 18', 'Arad'),
    ('Petrescu', 'Sofia', 'sofia.petrescu@gmail.com', '0721567890', '1998-11-11', 'Strada Tudor Vladimirescu, Nr. 7', 'Galati'),
    ('Radulescu', 'Diana', 'diana.radulescu@yahoo.com', '0736543210', '1991-02-17', 'Strada Stefan cel Mare, Nr. 3', 'Baia Mare'),
    ('Tanase', 'Victor', 'victor.tanase@gmail.com', '0763456781', '1983-09-23', 'Strada Garii, Nr. 25', 'Sibiu'),
    ('Moldovan', 'Bianca', 'bianca.moldovan@yahoo.com', '0742345678', '1996-06-06', 'Strada Rozelor, Nr. 12', 'Pitesti'),
    ('Barbu', 'George', 'george.barbu@gmail.com', '0729988776', '1989-03-14', 'Strada Aviatorilor, Nr. 6', 'Suceava'),
    ('Avram', 'Delia', 'delia.avram@yahoo.com', '0745671234', '1992-07-18', 'Strada Universitatii, Nr. 5', 'Bucuresti'),
    ('Serban', 'Marius', 'marius.serban@gmail.com', '0737890456', '1981-02-28', 'Strada Zorilor, Nr. 18', 'Cluj-Napoca'),
    ('Neagu', 'Alina', 'alina.neagu@yahoo.com', '0729087654', '1994-10-12', 'Strada Salcamilor, Nr. 3', 'Timisoara'),
    ('Toma', 'Razvan', 'razvan.toma@gmail.com', '0751203456', '1989-08-09', 'Strada Bucuresti, Nr. 45', 'Constanta'),
    ('Rusu', 'Gabriela', 'gabriela.rusu@yahoo.com', '0760987123', '1991-03-25', 'Strada Alba Iulia, Nr. 11', 'Iasi'),
    ('Zamfir', 'Paul', 'paul.zamfir@gmail.com', '0744532768', '1982-12-01', 'Strada Crinului, Nr. 7', 'Craiova'),
    ('Lungu', 'Elisabeta', 'elisabeta.lungu@yahoo.com', '0722345678', '1997-01-05', 'Strada Magnoliei, Nr. 15', 'Brasov'),
    ('Enache', 'Florin', 'florin.enache@gmail.com', '0739876541', '1985-06-23', 'Strada Doina, Nr. 30', 'Ploiesti'),
    ('Dragan', 'Claudia', 'claudia.dragan@yahoo.com', '0765437890', '1993-11-17', 'Strada Pescarilor, Nr. 8', 'Oradea'),
    ('Matei', 'Bogdan', 'bogdan.matei@gmail.com', '0758765432', '1990-09-06', 'Strada Bucegi, Nr. 12', 'Arad'),
    ('Stan', 'Monica', 'monica.stan@yahoo.com', '0721564321', '1987-04-15', 'Strada Padurii, Nr. 18', 'Galati'),
    ('Ciobanu', 'Victor', 'victor.ciobanu@gmail.com', '0733210987', '1984-10-10', 'Strada Cerbului, Nr. 25', 'Baia Mare'),
    ('Oprea', 'Larisa', 'larisa.oprea@yahoo.com', '0768901234', '1992-03-04', 'Strada Narciselor, Nr. 4', 'Sibiu'),
    ('Grigorescu', 'Stefan', 'stefan.grigorescu@gmail.com', '0743210456', '1988-02-11', 'Strada Dealului, Nr. 6', 'Pitesti'),
    ('Antonescu', 'Irina', 'irina.antonescu@yahoo.com', '0728765432', '1995-05-21', 'Strada Campului, Nr. 9', 'Suceava'),
    ('Ciuca', 'Vlad', 'vlad.ciuca@gmail.com', '0745567890', '1990-06-15', 'Strada Alunului, Nr. 7', 'Bucuresti'),
    ('Panait', 'Liliana', 'liliana.panait@yahoo.com', '0729084321', '1983-09-09', 'Strada Primaverii, Nr. 3', 'Cluj-Napoca'),
    ('Istrate', 'Adrian', 'adrian.istrate@gmail.com', '0736547890', '1985-01-20', 'Strada Trandafirilor, Nr. 6', 'Timisoara'),
    ('Munteanu', 'Carmen', 'carmen.munteanu@yahoo.com', '0762345678', '1993-10-30', 'Strada Mihai Eminescu, Nr. 12', 'Constanta'),
    ('Patrascu', 'Dorin', 'dorin.patrascu@gmail.com', '0743210987', '1988-04-17', 'Strada Stejarului, Nr. 14', 'Iasi'),
    ('Chirila', 'Denisa', 'denisa.chirila@yahoo.com', '0725432109', '1994-11-23', 'Strada Zambilelor, Nr. 20', 'Craiova'),
    ('Pavel', 'Lucian', 'lucian.pavel@gmail.com', '0756781234', '1987-02-14', 'Strada Carol I, Nr. 10', 'Brasov'),
    ('Carp', 'Oana', 'oana.carp@yahoo.com', '0739872109', '1992-08-04', 'Strada Grivitei, Nr. 4', 'Ploiesti'),
    ('Iacob', 'Teodora', 'teodora.iacob@gmail.com', '0767890456', '1995-12-12', 'Strada Horea, Nr. 9', 'Oradea'),
    ('Costache', 'Emil', 'emil.costache@yahoo.com', '0741098765', '1986-07-07', 'Strada Libertatii, Nr. 6', 'Arad'),
    ('Baciu', 'Daniela', 'daniela.baciu@gmail.com', '0724321678', '1989-03-08', 'Strada Ciprian Porumbescu, Nr. 8', 'Galati'),
    ('Avramescu', 'Cristian', 'cristian.avramescu@yahoo.com', '0747654321', '1980-11-22', 'Strada Verde, Nr. 2', 'Bucuresti'),
    ('Badea', 'Simona', 'simona.badea@gmail.com', '0738765432', '1991-05-10', 'Strada Albastra, Nr. 19', 'Cluj-Napoca'),
    ('Filip', 'Razvan', 'razvan.filip@yahoo.com', '0723456788', '1984-02-15', 'Strada Soarelui, Nr. 8', 'Timisoara'),
    ('Voinea', 'Andreea', 'andreea.voinea@gmail.com', '0765432123', '1995-09-30', 'Strada Dorobantilor, Nr. 12', 'Constanta'),
    ('Cojocaru', 'Bogdan', 'bogdan.cojocaru@yahoo.com', '0756789000', '1987-03-17', 'Strada Centrala, Nr. 16', 'Iasi'),
    ('Dima', 'Florentina', 'florentina.dima@gmail.com', '0738765123', '1992-11-04', 'Strada Vointei, Nr. 4', 'Craiova'),
    ('Popa', 'Catalina', 'catalina.popa@yahoo.com', '0742345679', '1990-01-13', 'Strada Principala, Nr. 8', 'Brasov'),
    ('Stanciu', 'Mihai', 'mihai.stanciu@gmail.com', '0728765901', '1985-07-07', 'Strada Marasesti, Nr. 14', 'Ploiesti'),
    ('Gavrila', 'Oana', 'oana.gavrila@yahoo.com', '0735467890', '1988-10-25', 'Strada Tineretului, Nr. 6', 'Oradea'),
    ('Tudose', 'Emanuel', 'emanuel.tudose@gmail.com', '0769876543', '1991-06-12', 'Strada Mare, Nr. 10', 'Arad'),
    ('Rotaru', 'Alexandra', 'alexandra.rotaru@yahoo.com', '0748976541', '1993-09-03', 'Strada Noua, Nr. 15', 'Galati'),
    ('Gheorghe', 'Roxana', 'roxana.gheorghe@gmail.com', '0765678923', '1982-08-18', 'Strada Unirii, Nr. 23', 'Buzau'),
    ('Lazar', 'Adrian', 'adrian.lazar@yahoo.com', '0756782934', '1990-12-02', 'Strada Vulturului, Nr. 4', 'Piatra Neamt'),
    ('Petru', 'Irina', 'irina.petru@gmail.com', '0732123435', '1991-05-29', 'Strada Oltului, Nr. 18', 'Alba Iulia'),
    ('Dinu', 'Gabriel', 'gabriel.dinu@yahoo.com', '0724536789', '1987-09-21', 'Strada Dunarii, Nr. 19', 'Slatina'),
    ('Enescu', 'Daniela', 'daniela.enescu@gmail.com', '0761239876', '1993-03-14', 'Strada Teilor, Nr. 7', 'Deva'),
    ('Costea', 'Paul', 'paul.costea@yahoo.com', '0759087612', '1985-01-23', 'Strada Siretului, Nr. 15', 'Bacau'),
    ('Constantinescu', 'Oana', 'oana.constantinescu@gmail.com', '0742167890', '1994-07-30', 'Strada Ciresului, Nr. 11', 'Targu Mures'),
    ('Anghel', 'Florin', 'florin.anghel@yahoo.com', '0723456543', '1988-10-12', 'Strada Frasinului, Nr. 9', 'Resita'),
    ('Grosu', 'Andreea', 'andreea.grosu@gmail.com', '0739872165', '1992-12-25', 'Strada Lupului, Nr. 14', 'Bistrita'),
    ('Ionita', 'Emanuel', 'emanuel.ionita@yahoo.com', '0765432198', '1991-06-19', 'Strada Plevnei, Nr. 2', 'Focsani'),
    ('Balan', 'Adela', 'adela.balan@gmail.com', '0756798123', '1989-02-10', 'Strada Berzei, Nr. 8', 'Tulcea'),
    ('Carbunaru', 'Mihai', 'mihai.carbunaru@yahoo.com', '0745467892', '1990-08-30', 'Strada Taberei, Nr. 6', 'Hunedoara'),
    ('Rosu', 'Cristina', 'cristina.rosu@gmail.com', '0724567810', '1987-05-17', 'Strada Soimului, Nr. 13', 'Roman');
    
UPDATE Clienti
SET Nume = 'Popescu', Prenume = 'Maria'
WHERE Nume = 'Ionescu' AND Prenume = 'Maria';

INSERT INTO Produse (Nume_Produs, Categorie, Pret)
VALUES
    ('Laptop ASUS', 'Electronice', 4500.50),
    ('Smartphone Samsung Galaxy S22', 'Electronice', 3500.00),
    ('Televizor LG', 'Electronice', 3000.00),
    ('Cameră Foto Canon', 'Fotografie', 2500.75),
    ('Frigider Arctic', 'Electro Casnice', 2000.00),
    ('Masina de spalat Whirlpool', 'Electro Casnice', 1500.00),
    ('Casti Apple AirPods', 'Electronice', 800.00),
    ('Mixer Bosch', 'Electro Casnice', 300.00),
    ('Joc PS5', 'Gaming', 350.00),
    ('Cărți - Pachet educativ', 'Carti', 150.00);
    
INSERT INTO Tranzactii (ID_Client, Data_Tranzactie, Valoare_Totala)
VALUES
    (1, '2023-12-02 15:00:00', 3000.00),
    (2, '2023-12-03 10:45:00', 350.00),
    (3, '2023-12-04 12:30:00', 1250.00),
    (4, '2023-12-05 16:00:00', 7500.00),
    (5, '2023-12-06 10:15:00', 2750.00),
    (6, '2023-12-07 14:45:00', 1800.00),
    (7, '2023-12-08 09:15:00', 700.00),
    (8, '2023-12-09 17:30:00', 450.00),
    (9, '2023-12-10 11:45:00', 1250.00),
    (10, '2023-12-11 20:10:00', 8300.50),
    (1, '2023-12-12 08:30:00', 600.00),
    (2, '2023-12-13 13:20:00', 480.00),
    (3, '2023-12-14 10:30:00', 3600.00),
    (4, '2023-12-15 14:50:00', 4100.00),
    (5, '2023-12-16 09:45:00', 500.00),
    (6, '2023-12-17 16:20:00', 780.00),
    (7, '2023-12-18 18:30:00', 1250.00),
    (8, '2023-12-19 10:40:00', 2600.00),
    (9, '2023-12-20 11:15:00', 900.00),
    (10, '2023-12-21 20:20:00', 850.00),
    (2, '2024-01-17 09:16:06', 8501.00),
    (14, '2022-03-27 02:22:24', 6000.75),
    (47, '2022-02-12 14:17:31', 12500.50),
    (21, '2022-05-22 00:27:01', 7350.75),
    (28, '2024-08-12 07:49:44', 3300.00),
    (23, '2022-03-06 22:02:27', 9800.50),
    (25, '2023-06-24 22:05:46', 12500.00),
    (13, '2022-09-14 01:20:40', 7100.50),
    (5, '2022-10-10 04:17:09', 5000.00),
    (44, '2024-10-10 09:57:12', 2500.75),
    (47, '2023-10-06 11:40:41', 24552.5),
    (5, '2023-03-14 09:48:37', 750.0),
    (18, '2023-12-26 22:51:33', 16503.75),
    (18, '2024-09-26 17:55:14', 16050.0),
    (1, '2024-08-01 02:40:39', 450.0),
    (28, '2024-04-04 18:43:14', 1500.0),
    (32, '2022-12-20 00:15:10', 9001.0),
    (50, '2024-12-19 11:39:18', 1500.0),
    (14, '2023-02-21 14:36:34', 13951.5),
    (3, '2023-02-16 20:23:00', 21000.0);

INSERT INTO Detalii_Tranzactii (ID_Tranzactie, ID_Produs, Cantitate, Pret_Total)
VALUES
    (1, 6, 2, 3000.00),
    (2, 9, 1, 350.00),
    (3, 10, 3, 450.00),
    (4, 4, 1, 2500.75),
    (5, 7, 2, 1600.00),
    (6, 8, 3, 900.00),
    (7, 9, 2, 700.00),
    (8, 10, 3, 450.00),
    (9, 6, 1, 1500.00),
    (10, 7, 1, 800.00),
    (11, 5, 1, 2000.00),
    (12, 9, 1, 350.00),
    (13, 3, 1, 3000.00),
    (14, 4, 1, 2500.75),
    (15, 10, 2, 300.00),
    (16, 6, 1, 1500.00),
    (17, 8, 1, 300.00),
    (17, 9, 2, 700.00),
    (18, 1, 1, 4500.50),
    (19, 2, 2, 7000.00),
    (20, 3, 1, 3000.00),
    (21, 1, 2, 4500.50), 
    (21, 2, 1, 3500.00), 
    (22, 3, 2, 6000.75), 
    (23, 5, 3, 6000.00), 
    (23, 4, 1, 2500.75),
    (24, 7, 5, 4000.00),
    (25, 6, 2, 3000.00), 
    (26, 9, 3, 1050.00),
    (27, 8, 3, 900.00),
    (28, 10, 4, 600.00),
    (29, 2, 1, 3500.00), 
    (30, 3, 2, 6000.75), 
    (31, 1, 5, 22502.5),
    (31, 7, 2, 1600.0),
    (31, 10, 3, 450.0),
    (32, 10, 5, 750.0),
    (33, 7, 5, 4000.0),
    (33, 4, 5, 12503.75),
    (34, 10, 2, 300.0),
    (34, 2, 4, 14000.0),
    (34, 9, 5, 1750.0),
    (35, 10, 3, 450.0),
    (36, 6, 1, 1500.0),
    (37, 1, 2, 9001.0),
    (38, 7, 1, 800.0),
    (38, 9, 2, 700.0),
    (39, 1, 3, 13501.5),
    (39, 10, 3, 450.0),
    (40, 5, 1, 2000.0),
    (40, 2, 2, 7000.0),
    (40, 3, 4, 12000.0);
    
DELETE FROM Detalii_Tranzactii
WHERE ID_Tranzactie = 4;

DROP VIEW IF EXISTS Total_Achizitii_Clienti;
DROP VIEW IF EXISTS Top_5_Clienti;
DROP VIEW IF EXISTS Numar_Tranzactii_Clienti;
DROP VIEW IF EXISTS Clienti_Fara_Tranzactii;
DROP VIEW IF EXISTS Clienti_Dupa_Oras;
DROP VIEW IF EXISTS Produse_Populare;
DROP VIEW IF EXISTS Vanzari_Pe_Categorie;
DROP VIEW IF EXISTS Media_Cheltuieli_Clienti;
DROP VIEW IF EXISTS Clienti_Cheltuitori;
DROP VIEW IF EXISTS Clienti_Activi_Lunar;
DROP VIEW IF EXISTS Clienti_Produs_Specific;
DROP VIEW IF EXISTS Clienti_Sezoniere;
DROP VIEW IF EXISTS Zile_Populare;
DROP VIEW IF EXISTS Clienti_Dupa_Varsta;
DROP VIEW IF EXISTS Raport;
DROP VIEW IF EXISTS Vanzari_Medii_Zilnice;
DROP VIEW IF EXISTS Clienti_Oraș_Specific;
SELECT name FROM sqlite_master WHERE type = 'view';

CREATE VIEW IF NOT EXISTS Clienti_Oraș_Specific AS
SELECT 
    c.Oras,
    CONCAT(c.Prenume, ' ', c.Nume) AS Nume_Complet,
    SUM(t.Valoare_Totala) AS Total_Achizitii
FROM 
    Clienti c
LEFT JOIN 
    Tranzactii t ON c.ID_Client = t.ID_Client
WHERE 
    c.Oras = 'Bucuresti'
GROUP BY 
    c.Oras, c.Nume
ORDER BY 
    Total_Achizitii DESC;

CREATE VIEW IF NOT EXISTS Vanzari_Medii_Zilnice AS
SELECT 
    strftime('%Y-%m-%d', t.Data_Tranzactie) AS Ziua,
    AVG(t.Valoare_Totala) AS Valoare_Medie_Zilnica
FROM 
    Tranzactii t
GROUP BY 
    Ziua
ORDER BY 
    Ziua DESC;

CREATE VIEW IF NOT EXISTS Raport AS
SELECT 
    c.ID_Client,
    CONCAT(c.Prenume, ' ', c.Nume) AS Nume_Complet,
    SUM(CASE WHEN strftime('%m', t.Data_Tranzactie) = '01' THEN t.Valoare_Totala ELSE 0 END) AS Ianuarie,
    SUM(CASE WHEN strftime('%m', t.Data_Tranzactie) = '02' THEN t.Valoare_Totala ELSE 0 END) AS Februarie,
    SUM(CASE WHEN strftime('%m', t.Data_Tranzactie) = '03' THEN t.Valoare_Totala ELSE 0 END) AS Martie,
    SUM(CASE WHEN strftime('%m', t.Data_Tranzactie) = '04' THEN t.Valoare_Totala ELSE 0 END) AS Aprilie,
    SUM(CASE WHEN strftime('%m', t.Data_Tranzactie) = '05' THEN t.Valoare_Totala ELSE 0 END) AS Mai,
    SUM(CASE WHEN strftime('%m', t.Data_Tranzactie) = '06' THEN t.Valoare_Totala ELSE 0 END) AS Iunie,
    SUM(CASE WHEN strftime('%m', t.Data_Tranzactie) = '07' THEN t.Valoare_Totala ELSE 0 END) AS Iulie,
    SUM(CASE WHEN strftime('%m', t.Data_Tranzactie) = '08' THEN t.Valoare_Totala ELSE 0 END) AS August,
    SUM(CASE WHEN strftime('%m', t.Data_Tranzactie) = '09' THEN t.Valoare_Totala ELSE 0 END) AS Septembrie,
    SUM(CASE WHEN strftime('%m', t.Data_Tranzactie) = '10' THEN t.Valoare_Totala ELSE 0 END) AS Octombrie,
    SUM(CASE WHEN strftime('%m', t.Data_Tranzactie) = '11' THEN t.Valoare_Totala ELSE 0 END) AS Noiembrie,
    SUM(CASE WHEN strftime('%m', t.Data_Tranzactie) = '12' THEN t.Valoare_Totala ELSE 0 END) AS Decembrie,
    COUNT(t.ID_Tranzactie) AS Total_Tranzactii, 
    SUM(t.Valoare_Totala) AS Total_Achizitii
FROM 
    Clienti c
LEFT JOIN 
    Tranzactii t ON c.ID_Client = t.ID_Client
GROUP BY 
    c.ID_Client, c.Prenume, c.Nume;

CREATE VIEW IF NOT EXISTS Clienti_Dupa_Varsta AS
SELECT 
    CASE 
        WHEN strftime('%Y', 'now') - strftime('%Y', c.Data_Nasterii) < 25 THEN 'Sub 25 de ani'
        WHEN strftime('%Y', 'now') - strftime('%Y', c.Data_Nasterii) BETWEEN 25 AND 40 THEN '25-40 de ani'
        WHEN strftime('%Y', 'now') - strftime('%Y', c.Data_Nasterii) BETWEEN 41 AND 60 THEN '41-60 de ani'
        ELSE 'Peste 60 de ani'
    END AS Grup_Varsta,
    COUNT(c.ID_Client) AS Numar_Tranzactii,
    SUM(t.Valoare_Totala) AS Total_Achizitii
FROM 
    Clienti c
LEFT JOIN 
    Tranzactii t ON c.ID_Client = t.ID_Client
GROUP BY 
    Grup_Varsta
ORDER BY 
    Total_Achizitii DESC;


CREATE VIEW IF NOT EXISTS Zile_Populare AS
SELECT 
    strftime('%w', t.Data_Tranzactie) AS Ziua_Saptamanii,
    CASE 
        WHEN strftime('%w', t.Data_Tranzactie) = '0' THEN 'Duminică'
        WHEN strftime('%w', t.Data_Tranzactie) = '1' THEN 'Luni'
        WHEN strftime('%w', t.Data_Tranzactie) = '2' THEN 'Marți'
        WHEN strftime('%w', t.Data_Tranzactie) = '3' THEN 'Miercuri'
        WHEN strftime('%w', t.Data_Tranzactie) = '4' THEN 'Joi'
        WHEN strftime('%w', t.Data_Tranzactie) = '5' THEN 'Vineri'
        WHEN strftime('%w', t.Data_Tranzactie) = '6' THEN 'Sâmbătă'
    END AS Ziua,
    COUNT(t.ID_Tranzactie) AS Numar_Tranzactii
FROM 
    Tranzactii t
GROUP BY 
    Ziua_Saptamanii, Ziua
ORDER BY 
    Numar_Tranzactii DESC;

CREATE VIEW IF NOT EXISTS Clienti_Sezoniere AS
SELECT 
    c.ID_Client,
    CONCAT(c.Prenume, ' ', c.Nume) AS Nume_Complet,
    CASE 
        WHEN strftime('%m', t.Data_Tranzactie) IN ('12', '01', '02') THEN 'Iarna'
        WHEN strftime('%m', t.Data_Tranzactie) IN ('03', '04', '05') THEN 'Primăvară'
        WHEN strftime('%m', t.Data_Tranzactie) IN ('06', '07', '08') THEN 'Vară'
        WHEN strftime('%m', t.Data_Tranzactie) IN ('09', '10', '11') THEN 'Toamnă'
    END AS Sezon,
    COUNT(t.ID_Tranzactie) AS Numar_Tranzactii
FROM 
    Clienti c
JOIN 
    Tranzactii t ON c.ID_Client = t.ID_Client
GROUP BY 
    c.ID_Client, c.Prenume, c.Nume, Sezon
ORDER BY 
    Numar_Tranzactii DESC;

CREATE VIEW IF NOT EXISTS Clienti_Activi_Lunar AS
SELECT 
    strftime('%Y-%m', t.Data_Tranzactie) AS Luna,
    COUNT(DISTINCT t.ID_Client) AS Numar_Clienti_Activi
FROM 
    Tranzactii t
GROUP BY 
    Luna
ORDER BY 
    Luna DESC;
    
CREATE VIEW IF NOT EXISTS Clienti_Produs_Specific AS
SELECT 
    DISTINCT c.ID_Client,
    CONCAT(c.Prenume, ' ', c.Nume) AS Nume_Complet,
    p.Nume_Produs
FROM 
    Clienti c
JOIN 
    Tranzactii t ON c.ID_Client = t.ID_Client
JOIN 
    Detalii_Tranzactii dt ON t.ID_Tranzactie = dt.ID_Tranzactie
JOIN 
    Produse p ON dt.ID_Produs = p.ID_Produs
WHERE 
    p.Nume_Produs = 'Laptop ASUS';

CREATE VIEW IF NOT EXISTS Clienti_Cheltuitori AS
SELECT 
    c.ID_Client,
    CONCAT(c.Prenume, ' ', c.Nume) AS Nume_Complet,
    SUM(t.Valoare_Totala) AS Total_Achizitii
FROM 
    Clienti c
JOIN 
    Tranzactii t ON c.ID_Client = t.ID_Client
GROUP BY 
    c.ID_Client, c.Prenume, c.Nume
HAVING 
    SUM(t.Valoare_Totala) > 5000
ORDER BY 
    Total_Achizitii DESC;

CREATE VIEW IF NOT EXISTS Total_Achizitii_Clienti AS
SELECT 
    c.ID_Client,
    CONCAT(c.Prenume, ' ', c.Nume) AS Nume_Complet,
    SUM(t.Valoare_Totala) AS Total_Achizitii
FROM 
    Clienti c
LEFT JOIN 
    Tranzactii t ON c.ID_Client = t.ID_Client
GROUP BY 
    c.ID_Client, c.Prenume, c.Nume;

CREATE VIEW IF NOT EXISTS Top_5_Clienti AS
SELECT 
    c.ID_Client,
    CONCAT(c.Prenume, ' ', c.Nume) AS Nume_Complet,
    SUM(t.Valoare_Totala) AS Total_Achizitii
FROM 
    Clienti c
LEFT JOIN 
    Tranzactii t ON c.ID_Client = t.ID_Client
GROUP BY 
    c.ID_Client, c.Prenume, c.Nume
ORDER BY 
    Total_Achizitii DESC
LIMIT 5;

CREATE VIEW IF NOT EXISTS Numar_Tranzactii_Clienti AS
SELECT 
    c.ID_Client,
    CONCAT(c.Prenume, ' ', c.Nume) AS Nume_Complet,
    COUNT(t.ID_Tranzactie) AS Numar_Tranzactii
FROM 
    Clienti c
LEFT JOIN 
    Tranzactii t ON c.ID_Client = t.ID_Client
GROUP BY 
    c.ID_Client, c.Prenume, c.Nume;

CREATE VIEW IF NOT EXISTS Clienti_Fara_Tranzactii AS
SELECT 
    c.ID_Client,
    CONCAT(c.Prenume, ' ', c.Nume) AS Nume_Complet,
    c.Email,
    c.Telefon
FROM 
    Clienti c
LEFT JOIN 
    Tranzactii t ON c.ID_Client = t.ID_Client
WHERE 
    t.ID_Tranzactie IS NULL;

CREATE VIEW IF NOT EXISTS Clienti_Dupa_Oras AS
SELECT 
    Oras,
    COUNT(ID_Client) AS Numar_Clienti
FROM 
    Clienti
GROUP BY 
    Oras
ORDER BY 
    Numar_Clienti DESC;

CREATE VIEW IF NOT EXISTS Produse_Populare AS
SELECT 
    p.ID_Produs,
    p.Nume_Produs,
    SUM(dt.Cantitate) AS Cantitate_Totala
FROM 
    Produse p
LEFT JOIN 
    Detalii_Tranzactii dt ON p.ID_Produs = dt.ID_Produs
GROUP BY 
    p.ID_Produs, p.Nume_Produs
ORDER BY 
    Cantitate_Totala DESC;

CREATE VIEW IF NOT EXISTS Vanzari_Pe_Categorie AS
SELECT 
    p.Categorie,
    SUM(dt.Pret_Total) AS Vanzari_Totale
FROM 
    Produse p
LEFT JOIN 
    Detalii_Tranzactii dt ON p.ID_Produs = dt.ID_Produs
GROUP BY 
    p.Categorie
ORDER BY 
    Vanzari_Totale DESC;

CREATE VIEW IF NOT EXISTS Media_Cheltuieli_Clienti AS
SELECT 
    c.ID_Client,
    CONCAT(c.Prenume, ' ', c.Nume) AS Nume_Complet,
    AVG(t.Valoare_Totala) AS Media_Cheltuieli
FROM 
    Clienti c
LEFT JOIN 
    Tranzactii t ON c.ID_Client = t.ID_Client
GROUP BY 
    c.ID_Client, c.Prenume, c.Nume;
    
SELECT name FROM sqlite_master WHERE type = 'view';