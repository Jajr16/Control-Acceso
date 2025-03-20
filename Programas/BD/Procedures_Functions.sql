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
    Sexo VARCHAR,
    Correo VARCHAR,
    Carrera VARCHAR,
    Aceptado INTEGER
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        ai.idets AS idETS,  
        ai.boleta AS Boleta, 
        a.curp AS CURP, 
        p.nombre AS NombreA, 
        p.apellido_p AS ApellidoP, 
        p.apellido_m AS ApellidoM, 
        s.nombre AS Sexo, 
        a.correoi AS Correo, 
        a.idpa::VARCHAR AS Carrera, 
        COALESCE(ax.estado, 0) AS Aceptado
    FROM inscripcionets ai
    INNER JOIN alumno a ON ai.boleta = a.boleta
    INNER JOIN persona p ON a.curp = p.curp
    INNER JOIN sexo s ON p.sexo = s.id_sexo
    LEFT JOIN ingreso_salon ax ON ai.boleta = ax.boleta
    WHERE ai.idets = etsprueba;
END;
$$;


DROP FUNCTION ObtenerAsistenciaDetalles


SELECT * FROM ObtenerAsistenciaDetalles(1);

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

-- FUNCIÓN PARA EL TRIGGER DE ASISTENCIAINCRIPCION

CREATE OR REPLACE FUNCTION insertar_asistenciainscripcion()
RETURNS TRIGGER AS $$
BEGIN
    -- Insertar en asistenciainscripcion usando los valores de la nueva fila en inscripcionets
    INSERT INTO asistenciainscripcion (fecha_asistencia, inscripcionets_boleta, inscripcionets_idets, aceptado, asistio, resultado_rn)
    SELECT e.fecha, NEW.boleta, NEW.idets, false, false, false
    FROM ets e
    WHERE e.idets = NEW.idets
    AND NOT EXISTS (
        SELECT 1
        FROM asistenciainscripcion fa
        WHERE fa.inscripcionets_boleta = NEW.boleta
          AND fa.inscripcionets_idets = NEW.idets
    );

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- TRIGGER PARA ASISTENCIAINCRIPCION

CREATE TRIGGER trigger_insertar_asistenciainscripcion
AFTER INSERT ON inscripcionets
FOR EACH ROW
EXECUTE FUNCTION insertar_asistenciainscripcion();

-- PRUEBA DE LA LOGICA DEL TRIGGER DE ASISTENCIAINCRIPCION

INSERT INTO asistenciainscripcion (fecha_asistencia, inscripcionets_boleta, inscripcionets_idets, aceptado, asistio, resultado_rn)
SELECT e.fecha, i.boleta, i.idets, false, false, false
FROM inscripcionets i
JOIN ets e ON i.idets = e.idets
WHERE NOT EXISTS (
    SELECT 1
    FROM asistenciainscripcion fa
    WHERE fa.inscripcionets_boleta = i.boleta
      AND fa.inscripcionets_idets = i.idets
);

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