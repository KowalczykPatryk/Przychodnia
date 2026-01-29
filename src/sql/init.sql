CREATE SCHEMA IF NOT EXISTS przychodnia;
SET search_path TO przychodnia;

CREATE TABLE IF NOT EXISTS lekarz (
    id SERIAL PRIMARY KEY,
    imie VARCHAR(50) NOT NULL,
    nazwisko VARCHAR(50) NOT NULL,
    pesel VARCHAR(11) UNIQUE NOT NULL,
    telefon VARCHAR(15),
    email VARCHAR(100) UNIQUE,
    haslo VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS specjalizacja (
    id SERIAL PRIMARY KEY,
    nazwa VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS lekarz_specjalizacja (
    lekarz_id INT REFERENCES lekarz(id) ON DELETE CASCADE,
    specjalizacja_id INT REFERENCES specjalizacja(id) ON DELETE CASCADE,
    opis TEXT,
    czas_trwania INT,
    PRIMARY KEY (lekarz_id, specjalizacja_id)
);

CREATE TABLE IF NOT EXISTS pacjent (
    id SERIAL PRIMARY KEY,
    imie VARCHAR(50) NOT NULL,
    nazwisko VARCHAR(50) NOT NULL,
    pesel VARCHAR(11) UNIQUE NOT NULL,
    telefon VARCHAR(15),
    email VARCHAR(100) UNIQUE,
    haslo VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS lekarz_pacjent (
    lekarz_id INT REFERENCES lekarz(id) ON DELETE CASCADE,
    pacjent_id INT REFERENCES pacjent(id) ON DELETE CASCADE,
    PRIMARY KEY (lekarz_id, pacjent_id)
);

CREATE TABLE IF NOT EXISTS lek (
    id SERIAL PRIMARY KEY,
    nazwa VARCHAR(100) UNIQUE NOT NULL,
    substancja_czynna VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS pacjent_lek (
    pacjent_id INT REFERENCES pacjent(id) ON DELETE CASCADE,
    lek_id INT REFERENCES lek(id) ON DELETE CASCADE,
    PRIMARY KEY (pacjent_id, lek_id)
);

CREATE TABLE IF NOT EXISTS recepta (
    id SERIAL PRIMARY KEY,
    numer_recepty VARCHAR(20) UNIQUE NOT NULL,
    pacjent_id INT REFERENCES pacjent(id) ON DELETE CASCADE,
    lekarz_id INT REFERENCES lekarz(id) ON DELETE CASCADE,
    data_wystawienia DATE NOT NULL,
    data_ważnosci DATE NOT NULL,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS recepta_lek (
    recepta_id INT REFERENCES recepta(id) ON DELETE CASCADE,
    lek_id INT REFERENCES lek(id) ON DELETE CASCADE,
    PRIMARY KEY (recepta_id, lek_id)
);

CREATE TABLE IF NOT EXISTS wizyta (
    id SERIAL PRIMARY KEY,
    lekarz_id INT REFERENCES lekarz(id) ON DELETE CASCADE,
    pacjent_id INT REFERENCES pacjent(id) ON DELETE CASCADE,
    data DATE NOT NULL,
    godzina_rozpoczecia TIME NOT NULL,
    godzina_zakonczenia TIME,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS badanie (
    id SERIAL PRIMARY KEY,
    nazwa VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS wizyta_badanie (
    wizyta_id INT REFERENCES wizyta(id) ON DELETE CASCADE,
    badanie_id INT REFERENCES badanie(id) ON DELETE CASCADE,
    opis TEXT,
    PRIMARY KEY (wizyta_id, badanie_id)
);

CREATE TABLE IF NOT EXISTS wizyta_recepta (
    wizyta_id INT REFERENCES wizyta(id) ON DELETE CASCADE,
    recepta_id INT REFERENCES recepta(id) ON DELETE CASCADE,
    PRIMARY KEY (wizyta_id, recepta_id)
);

CREATE TABLE IF NOT EXISTS alergia (
    id SERIAL PRIMARY KEY,
    nazwa VARCHAR(100) UNIQUE NOT NULL,
    opis TEXT
);

CREATE TABLE IF NOT EXISTS pacjent_alergia (
    pacjent_id INT REFERENCES pacjent(id) ON DELETE CASCADE,
    alergia_id INT REFERENCES alergia(id) ON DELETE CASCADE,
    PRIMARY KEY (pacjent_id, alergia_id)
);

CREATE TABLE IF NOT EXISTS choroba (
    id SERIAL PRIMARY KEY,
    nazwa VARCHAR(100) UNIQUE NOT NULL,
    opis TEXT,
    czy_przewlekla BOOLEAN NOT NULL DEFAULT FALSE,
    czy_zakazna BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS pacjent_choroba (
    pacjent_id INT REFERENCES pacjent(id) ON DELETE CASCADE,
    choroba_id INT REFERENCES choroba(id) ON DELETE CASCADE,
    PRIMARY KEY (pacjent_id, choroba_id)
);

CREATE TABLE IF NOT EXISTS symptom (
    id SERIAL PRIMARY KEY,
    nazwa VARCHAR(100) UNIQUE NOT NULL,
    opis TEXT
);

CREATE TABLE IF NOT EXISTS choroba_symptom (
    choroba_id INT REFERENCES choroba(id) ON DELETE CASCADE,
    symptom_id INT REFERENCES symptom(id) ON DELETE CASCADE,
    PRIMARY KEY (choroba_id, symptom_id)
);

