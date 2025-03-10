-- FUNCIÓN PARA EL LOGIN
CREATE OR REPLACE FUNCTION login(
    p_username VARCHAR(18),
    p_password VARCHAR(100),
    OUT p_message VARCHAR(255),
    OUT error_code INT,
    OUT p_rol VARCHAR(255)
)
LANGUAGE plpgsql
AS $$
DECLARE
    hashed_password TEXT; -- Cambiar a TEXT
    user_found INT;
BEGIN
    -- Verificar si el usuario existe
    SELECT COUNT(*)
    INTO user_found
    FROM usuario
    WHERE usuario = p_username;

    IF user_found = 0 THEN
        -- Usuario no encontrado
        error_code := -2;
        p_message := 'Usuario o contraseña incorrectos.';
        p_rol := NULL;

    ELSE
        -- Obtener la contraseña encriptada y el rol
        SELECT u.password::TEXT, t.tipo
        INTO hashed_password, p_rol
        FROM usuario u
        INNER JOIN tipousuario t ON u.tipou = t.idtu
        WHERE u.usuario = p_username;

        -- Comparar contraseñas usando crypt()
        IF crypt(p_password::TEXT, hashed_password::TEXT) = hashed_password THEN
            -- Contraseña correcta
            error_code := 0;
            p_message := 'Inicio de sesión exitoso.';
        ELSE
            -- Contraseña incorrecta
            error_code := -1;
            p_message := 'Usuario o contraseña incorrectos.';
            p_rol := NULL;
        END IF;
    END IF;

    RETURN;
END;
$$;

-- FUNCIÓN PARA EL LISTADO DE ETS
CREATE OR REPLACE FUNCTION ListInscripcionesETS(
    boletaC VARCHAR(18)
)
RETURNS TABLE(
    idets INTEGER,
    periodo VARCHAR,
    turno_nombre VARCHAR,
    fecha DATE,
    unidad_aprendizaje_nombre VARCHAR,
	inscrito Boolean
)
LANGUAGE plpgsql
AS $$
BEGIN
	RETURN QUERY

	SELECT 
	    ets.idets,
	    periodoets.periodo, 
	    turno.nombre AS turno, 
	    ets.fecha,
	    unidadaprendizaje.nombre AS unidad_aprendizaje,
	    EXISTS (
            SELECT 1 
            FROM inscripcionets i 
            WHERE i.idets = ets.idets 
            AND i.boleta = boletaC
        ) AS inscrito
	FROM ets
	INNER JOIN periodoets ON ets.id_periodo = periodoets.id_periodo 
	INNER JOIN turno ON turno.id_turno = ets.turno
	INNER JOIN unidadaprendizaje ON unidadaprendizaje.idua = ets.idua;
    
END;
$$;

-- FUNCIÓN PARA EL LISTADO DE ETS
CREATE OR REPLACE FUNCTION ListAplica(
    boletaC VARCHAR(18)
)
RETURNS TABLE(
    idets INTEGER,
    periodo VARCHAR,
    turno_nombre VARCHAR,
    fecha DATE,
    unidad_aprendizaje_nombre VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
	RETURN QUERY

    SELECT aplica.idets, periodoets.periodo, turno.nombre as turno, ets.fecha, unidadaprendizaje.nombre FROM aplica
	INNER JOIN ets ON aplica.idets = ets.idets
	INNER JOIN periodoets ON ets.id_periodo = periodoets.id_periodo 
	INNER JOIN turno ON turno.id_turno = ets.turno
	INNER JOIN unidadaprendizaje ON unidadaprendizaje.idua = ets.idua WHERE aplica.docente_rfc = boletaC;
	
    
END;
$$;

select *from login('2022630467', '123');
SELECT * FROM usuario;
SELECT crypt('123', gen_salt('bf'));

INSERT INTO alumno
VALUES ('20230001', 'a@a.com', 'a', '0', 'IIA-2024' );

CREATE OR REPLACE FUNCTION ObtenerAsistenciaDetalles(
    etsprueba INTEGER
)
RETURNS TABLE(
    idETS INTEGER,
    Boleta VARCHAR(18),
    CURP VARCHAR(18),
    NombreA VARCHAR,
    ApellidoP VARCHAR,
    ApellidoM VARCHAR,
    Sexo VARCHAR,  -- Ahora se obtiene el nombre del sexo
    Correo VARCHAR,
    Carrera VARCHAR,
    Aceptado BOOLEAN
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        ai.inscripcionets_idets AS idETS,  -- ID del ETS
        ai.inscripcionets_boleta AS Boleta, -- Boleta del alumno
        a.curp AS CURP, -- CURP del alumno
        p.nombre AS NombreA, -- Nombre del alumno
        p.apellido_p AS ApellidoP, -- Apellido paterno
        p.apellido_m AS ApellidoM, -- Apellido materno
        s.nombre AS Sexo, -- Se une con la tabla 'sexo' para obtener el nombre
        a.correoi AS Correo, -- Correo institucional
        a.idpa::VARCHAR AS Carrera, -- ID de la carrera convertido a texto
        ai.aceptado AS Aceptado -- Estado de aceptación
    FROM asistenciainscripcion ai
    INNER JOIN alumno a ON ai.inscripcionets_boleta = a.boleta
    INNER JOIN persona p ON a.curp = p.curp
    INNER JOIN sexo s ON p.sexo = s.id_sexo -- JOIN con la tabla 'sexo' para obtener el nombre
    WHERE ai.inscripcionets_idets = etsprueba;
END;
$$;



DROP FUNCTION obtenerasistenciadetalles(integer)

select * from programaacademico;
select * from unidadaprendizaje;

SELECT * FROM ObtenerAsistenciaDetalles(52);

CREATE OR REPLACE FUNCTION obtenerpersona(
    boletaC VARCHAR(18)
) 
RETURNS TABLE(
    nombre VARCHAR,
    apellido_p VARCHAR, 
    apellido_m VARCHAR 
    
) 
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        p.nombre::VARCHAR,
        p.apellido_p::VARCHAR, 
        p.apellido_m::VARCHAR
        
    FROM usuario u
    INNER JOIN persona p ON u.curp = p.curp
    WHERE u.usuario = boletaC;
END;
$$;

-- ======================= TRIGGERS ===========================
-- PARA VALIDAR UN PROGRAMA ACADÉMICO
CREATE OR REPLACE FUNCTION validar_programa_academico()
RETURNS TRIGGER AS $$
DECLARE
    escuela_alumno INT;
    escuela_programa INT;
BEGIN
    -- Obtener la unidad académica del alumno
    SELECT id_escuela INTO escuela_alumno 
    FROM persona 
    WHERE curp = NEW.curp;

    -- Obtener la unidad académica del programa académico
    SELECT id_escuela INTO escuela_programa 
    FROM escuelaprograma 
    WHERE idpa = NEW.idpa;

    -- Validar si coinciden
    IF escuela_alumno IS NULL OR escuela_programa IS NULL THEN
        RAISE EXCEPTION 'Error: No se encontró la unidad académica del alumno o del programa académico.';
    END IF;

    IF escuela_alumno != escuela_programa THEN
        RAISE EXCEPTION 'Error: El programa académico no pertenece a la unidad académica del alumno.';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- TRIGGER QUE EJECUTA LA FUNCIÓN PARA VALIDAR UN PROGRAMA
CREATE TRIGGER trigger_validar_programa
BEFORE INSERT OR UPDATE ON alumno
FOR EACH ROW
EXECUTE FUNCTION validar_programa_academico();

-- FUNCION PARA CREAR EL USUARIO DE UN ALUMNO
CREATE OR REPLACE FUNCTION crear_usuarioA()
RETURNS TRIGGER AS $$
BEGIN
    BEGIN

        INSERT INTO usuario
        VALUES (NEW.boleta, crypt(NEW.boleta, gen_salt('bf')), NEW.curp, (SELECT idtu FROM tipousuario WHERE tipo = 'Alumno'));
    
    EXCEPTION WHEN OTHERS THEN
        -- Si hay un error, cancelar la transacción y lanzar excepción
        RAISE EXCEPTION 'Error al crear usuario, operación cancelada: %', SQLERRM;
        RETURN NULL; -- Evita que se inserte el alumno si hay un error
    END;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


-- TRIGGER QUE EJECUTA LA FUNCIÓN PARA CREAR UN NUEVO USUARIO DE ALUMNO
CREATE OR REPLACE TRIGGER create_user 
AFTER INSERT ON alumno
FOR EACH ROW
EXECUTE FUNCTION crear_usuarioA();

-- FUNCIÓN PARA CREAR EL USUARIO DE UN PERSONAL ACADÉMICO
CREATE OR REPLACE FUNCTION crear_usuarioPA()
RETURNS TRIGGER AS $$
BEGIN
    BEGIN
	
        INSERT INTO usuario
        VALUES (NEW.rfc, crypt(NEW.rfc, gen_salt('bf')), NEW.curp, (SELECT idtu FROM tipousuario WHERE tipo = 'Personal Academico'));
    
    EXCEPTION WHEN OTHERS THEN
        -- Si hay un error, cancelar la transacción y lanzar excepción
        RAISE EXCEPTION 'Error al crear usuario, operación cancelada: %', SQLERRM;
        RETURN NULL; -- Evita que se inserte el personal academico si hay un error
    END;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- TRIGGER PARA EJECUTAR LA FUNCIÓN DE CREAR USUARIO DE PERSONAL ACADÉMICO
CREATE OR REPLACE TRIGGER create_userPA
AFTER INSERT ON personalacademico
FOR EACH ROW
EXECUTE FUNCTION crear_usuarioPA();

-- FUNCIÓN PARA CREAR UN USUARIO DE PERSONAL DE SEGURIDAD
CREATE OR REPLACE FUNCTION crear_usuariopPS()
RETURNS TRIGGER AS $$

BEGIN

	BEGIN

		INSERT INTO usuario
		VALUES (NEW.rfc, crypt(NEW.rfc, gen_salt('bf')), NEW.curp,
		(SELECT idtu FROM tipousuario WHERE tipo = 'Personal Seguridad'));

	EXCEPTION WHEN OTHERS THEN
        -- Si hay un error, cancelar la transacción y lanzar excepción
        RAISE EXCEPTION 'Error al crear usuario, operación cancelada: %', SQLERRM;
        RETURN NULL; -- Evita que se inserte el personal academico si hay un error
    END;

	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- TRIGGER QUE EJECUTA LA FUNCIÓN PARA CREAR EL USUARIO DE PERSONAL DE SEGURIDAD
CREATE OR REPLACE TRIGGER create_userPS
AFTER INSERT ON personalseguridad
FOR EACH ROW
EXECUTE FUNCTION crear_usuariopPS();

select*from persona;


-- ON UPDATE CASCADE ON DELETE CASCADE 
ALTER TABLE public.personalacademico
DROP CONSTRAINT fkoannh428gwaa99dj2ghpfnek7;

ALTER TABLE public.personalacademico 
ADD CONSTRAINT fkoannh428gwaa99dj2ghpfnek7 
FOREIGN KEY (curp) 
REFERENCES public.persona(curp) 
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE public.cargodocente
DROP CONSTRAINT fk3mtxsq6vvg52q7ok4or8cbs3o;

ALTER TABLE public.cargodocente 
ADD CONSTRAINT fk3mtxsq6vvg52q7ok4or8cbs3o 
FOREIGN KEY (rfc) 
REFERENCES public.personalacademico(rfc) 
ON DELETE CASCADE
ON UPDATE CASCADE;


ALTER TABLE public.personalseguridad
DROP CONSTRAINT fkaqfhf3tuec5c7e0787a3dc21i;

ALTER TABLE public.personalseguridad 
ADD CONSTRAINT fkaqfhf3tuec5c7e0787a3dc21i 
FOREIGN KEY (curp) 
REFERENCES public.persona(curp) 
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE public.alumno 
DROP CONSTRAINT fkop2ux5hhbenpakvtwjeb1iswo;

ALTER TABLE public.alumno 
ADD CONSTRAINT fkop2ux5hhbenpakvtwjeb1iswo 
FOREIGN KEY (curp) 
REFERENCES public.persona(curp) 
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE public.usuario 
DROP CONSTRAINT fk7k5mbe2uawbnfhr2d7h8jxlo0;

ALTER TABLE public.usuario 
ADD CONSTRAINT fk7k5mbe2uawbnfhr2d7h8jxlo0 
FOREIGN KEY (curp) 
REFERENCES public.persona(curp) 
ON DELETE CASCADE
ON UPDATE CASCADE;