create database if not exists rftt;
use rftt;
select*from website_usuario;

-- drop database rftt;
-- Insertar profesores
INSERT INTO website_Usuario (boleta, nombre, apellido, password, is_admin, is_active, last_login) 
VALUES ('P002', 'Ana', 'Lopez', 'pbkdf2_sha256$870000$79S8Gxd0u3o3H0S2jr95HM$ELCy5b9m4e38Ok07ZodXrFuXDu8wwz1WyIJFkr66zcs=', TRUE, TRUE, '2024-09-22 00:40:12.944317');

-- Insertar alumnos
INSERT INTO website_Usuario (boleta, nombre, apellido, password, is_admin, is_active, last_login) 
VALUES ('A001', 'Maria', 'Gomez', 'pbkdf2_sha256$870000$ivBE71mlU2kR49DviSEvIr$wB5ZjHOTNNpH0l3jAWptcMCNw5U3DW6ZxBl4YVYUq98=', FALSE, TRUE, '2024-09-22 00:40:12.944317');

INSERT INTO website_Usuario (boleta, nombre, apellido, password, is_admin, is_active, last_login) 
VALUES ('A002', 'Carlos', 'Sanchez', 'pbkdf2_sha256$870000$79S8Gxd0u3o3H0S2jr95HM$ELCy5b9m4e38Ok07ZodXrFuXDu8wwz1WyIJFkr66zcs=', FALSE, TRUE, '2024-09-22 00:40:12.944317');

INSERT INTO website_Usuario (boleta, nombre, apellido, password, is_admin, is_active, last_login) 
VALUES ('A003', 'Laura', 'Martinez', 'pbkdf2_sha256$870000$W7VohoSdi1nmmK8cSmzbc9$yTCdKvG8ekcEB+y4JujVraw9eHy6eKm30tY9xFIhwqQ=', FALSE, TRUE, '2024-09-22 00:40:12.944317');

-- Insertar Materias
insert into website_materias values
('M1', 'Matemáticas I', 'Programación'),
('B1', 'Biología I', 'Programación'),
('S1', 'Sistemas I', 'Programación'),
('RV1', 'Reconocimiento de Voz I', 'Programación');

-- Insertar ETS
INSERT INTO website_ETS (cod_ets, turno, periodo_escolar, cod_mat_id) 
VALUES ('EM1', 'Matutino', '2022-1', 'M1');

INSERT INTO website_ETS (cod_ets, turno, periodo_escolar, cod_mat_id) 
VALUES ('ES1', 'Vespertino', '2022-1', 'S1');

INSERT INTO website_ETS (cod_ets, turno, periodo_escolar, cod_mat_id) 
VALUES ('ERV1', 'Vespertino', '2022-1', 'RV1');

-- Asignar alumnos a ETS con profesores
INSERT INTO website_Alumno_ETS (cod_profe_id, boletaAlm_id, cod_ETS_id) 
VALUES ('P001', 'A001', 'EM1');  -- Juan Perez supervisa a Maria en Matemáticas I

INSERT INTO website_Alumno_ETS (cod_profe_id, boletaAlm_id, cod_ETS_id) 
VALUES ('P002', 'A002', 'EM1');  -- Ana Lopez supervisa a Carlos en Física II

INSERT INTO website_Alumno_ETS (cod_profe_id, boletaAlm_id, cod_ETS_id) 
VALUES ('P001', 'A003', 'ERV1');  -- Juan Perez supervisa a Laura en Química Orgánica

select*from website_Alumno_ETS;
select*from website_ets;
select*from website_materias;
select*from website_usuario;

DELIMITER |
CREATE PROCEDURE Agregar_ETS(
    IN CMAT VARCHAR(10), 
    IN Mat VARCHAR(100),
    IN Carr VARCHAR(100),
    IN CETS VARCHAR(10),
    IN TURN VARCHAR(3),
    IN PE VARCHAR(10))
BEGIN

	DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
    END;

	START TRANSACTION;
		INSERT INTO website_materias VALUES(
			CMAT, Mat, Carr
        );
        
        INSERT INTO website_ets values(
			CETS, TURN, PE, CMAT
        );
    COMMIT;
END |

DELIMITER ;

DELIMITER |
CREATE PROCEDURE Inscribir_ETS(
    IN BALM VARCHAR(10), 
    IN BPROF VARCHAR(10),
    IN CETS VARCHAR(10))
BEGIN

	DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
    END;

	START TRANSACTION;
		INSERT INTO website_alumno_ets VALUES(
			BALM, BPROF, CETS
        );
    COMMIT;
END |

DELIMITER ;

DELIMITER |
CREATE PROCEDURE Inscribir_User(
	IN BOLET VARCHAR(10),
	IN NM VARCHAR(100),
    IN APE VARCHAR(100))
BEGIN

	DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
    END;

	START TRANSACTION;
		INSERT INTO website_Usuario (boleta, nombre, apellido, password, is_admin, is_active, last_login) 
		VALUES (
			BOLET, NM, APE,
            'pbkdf2_sha256$870000$W7VohoSdi1nmmK8cSmzbc9$yTCdKvG8ekcEB+y4JujVraw9eHy6eKm30tY9xFIhwqQ=', 
			FALSE, TRUE, '2024-09-22 00:40:12.944317'
        );	
    COMMIT;
END |

DELIMITER ;

CALL Inscribir_User('123456', 'Rodrigo', 'Pacheco');
CALL Agregar_ETS('DSD1', 'Diseño de Sistemas Digitales', 'Inteligencia Artificial', 'ETSDSD1', 'MAT', '2024-1');
CALL Inscribir_ETS('123456', '2020202020', 'DSD1');