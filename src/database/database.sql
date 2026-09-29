-- ------------------------------------------------------------
-- BASE DE DATOS - SPRINT 0
-- Proyecto de Biometría y Medio Ambiente
-- Autor: Fabián Useche
-- ------------------------------------------------------------

-- ------------------------------------------------------------
-- Tabla: Mediciones
--
-- Diseño:
--
-- Mediciones = (
--     id: N,
--     fecha: Texto,
--     tipo: Texto,
--     valor: R
-- )
--
-- Cada fila representa una medición recibida desde el móvil.
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS Mediciones (

    id INT AUTO_INCREMENT PRIMARY KEY,

    fecha DATETIME NOT NULL,

    tipo VARCHAR(50) NOT NULL,

    valor DOUBLE NOT NULL

);


-- ------------------------------------------------------------
-- DATOS DE PRUEBA
-- ------------------------------------------------------------

INSERT INTO Mediciones (
    fecha,
    tipo,
    valor
)
VALUES (
    '2026-09-28 16:25:00',
    'O3',
    0.032
);


INSERT INTO Mediciones (
    fecha,
    tipo,
    valor
)
VALUES (
    '2026-09-28 16:26:00',
    'O3',
    0.035
);


INSERT INTO Mediciones (
    fecha,
    tipo,
    valor
)
VALUES (
    '2026-09-28 16:27:00',
    'O3',
    0.031
);


-- ------------------------------------------------------------
-- PRUEBA 1
-- Leer todas las mediciones
-- ------------------------------------------------------------

SELECT *
FROM Mediciones;


-- ------------------------------------------------------------
-- PRUEBA 2
-- Leer la última medición almacenada
-- ------------------------------------------------------------

SELECT *
FROM Mediciones
ORDER BY id DESC
LIMIT 1;