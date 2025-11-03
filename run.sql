SET FEEDBACK ON
SET HEADING ON
SET PAGESIZE 200
SET LINESIZE 200
SPOOL evidencia_conexion_bd/ddl_y_inserts_ok.txt

@db/script_oracle.sql

PROMPT ====== RECUENTO TABLAS ======
SELECT 'USUARIOS' tabla, COUNT(*) total FROM usuarios
UNION ALL SELECT 'LABORATORIOS', COUNT(*) FROM laboratorios
UNION ALL SELECT 'RESERVAS', COUNT(*) FROM reservas;

PROMPT ====== JOIN RESERVAS-DETALLE ======
SELECT r.id_reserva, TO_CHAR(r.fecha,'YYYY-MM-DD') AS fecha,
       r.hora_inicio, r.hora_fin,
       l.nombre AS lab, u.nombre AS usuario
FROM reservas r
JOIN laboratorios l ON l.id_lab = r.id_lab
JOIN usuarios u     ON u.id     = r.id_usuario
ORDER BY r.id_reserva;

SPOOL OFF
EXIT
