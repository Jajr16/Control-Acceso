-- FUNCIÓN PARA EL LOGIN
CREATE OR REPLACE FUNCTION login(
    p_username VARCHAR(18),
    p_password VARCHAR(100)
)
RETURNS TABLE (
    p_message VARCHAR(255),
    error_code INT,
    p_rol VARCHAR(255),
    cargos TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    hashed_password TEXT;
    user_found INT;
    rol_base TEXT;
    cargo_base TEXT;
BEGIN
    -- Verificar si el usuario existe
    SELECT COUNT(*) INTO user_found
    FROM usuario
    WHERE usuario = p_username;

    IF user_found = 0 THEN
        RETURN QUERY SELECT 
            'Usuario o contraseña incorrectos.'::VARCHAR(255), 
            -2::INT, 
            NULL::VARCHAR(255), 
            NULL::TEXT;
    ELSE
        -- Obtener contraseña y tipo de usuario
        SELECT u.password::TEXT, t.tipo
        INTO hashed_password, rol_base
        FROM usuario u
        INNER JOIN tipousuario t ON u.tipou = t.idtu
        WHERE u.usuario = p_username;

        -- Verificar contraseña
        IF crypt(p_password, hashed_password) = hashed_password THEN

            IF rol_base = 'Personal Academico' THEN
                -- Ver si es docente
                SELECT tp.cargo
                INTO cargo_base
                FROM tipopersonal tp
                INNER JOIN personalacademico pa ON pa.tipopa = tp.tipopa
                INNER JOIN usuario u ON pa.rfc = u.usuario
                WHERE u.usuario = p_username;

                IF cargo_base = 'Docente' THEN
                    RETURN QUERY 
                    SELECT 
                        'Inicio de sesión exitoso.'::VARCHAR(255), 
                        0::INT, 
                        cargo_base::VARCHAR(255), 
                        (
                            SELECT STRING_AGG(c.cargo, ', ')::TEXT
                            FROM cargodocente cd
							 INNER JOIN cargo c ON c.id_cargo = cd.id_cargo
                            INNER JOIN usuario u ON cd.rfc = u.usuario
                            WHERE u.usuario = p_username
                        );
                ELSE
                    RETURN QUERY SELECT 
                        'Inicio de sesión exitoso.'::VARCHAR(255), 
                        0::INT, 
                        cargo_base::VARCHAR(255), 
                        NULL::TEXT;
                END IF;
            ELSE
                RETURN QUERY SELECT 
                    'Inicio de sesión exitoso.'::VARCHAR(255), 
                    0::INT, 
                    rol_base::VARCHAR(255), 
                    NULL::TEXT;
            END IF;

        ELSE
            RETURN QUERY SELECT 
                'Usuario o contraseña incorrectos.'::VARCHAR(255), 
                -1::INT, 
                NULL::VARCHAR(255), 
                NULL::TEXT;
        END IF;
    END IF;
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

-- Función para buscar datos especificos de un alumno

CREATE OR REPLACE FUNCTION buscardatosestudiante(boleta_input VARCHAR)
RETURNS TABLE (
    boleta VARCHAR,
    curp VARCHAR,
    apellido_p VARCHAR,
    apellido_m VARCHAR,
    nombre VARCHAR,
    unidadAcademica VARCHAR
) AS $$
BEGIN
    -- Retornar los datos deseados
    RETURN QUERY
    SELECT 
        e.boleta,
        e.curp,
        p.apellido_p,
        p.apellido_m,
        p.nombre,
        pa.nombre AS unidadAcademica
    FROM alumno e
    INNER JOIN persona p ON e.curp = p.curp
    INNER JOIN programaacademico pa ON e.idpa = pa.idpa
    WHERE e.boleta = boleta_input;
END;
$$ LANGUAGE plpgsql;

-- select *from login('2022630467', '123');
-- SELECT * FROM usuario;
-- SELECT crypt('123', gen_salt('bf'));

-- INSERT INTO alumno
-- VALUES ('20230001', 'a@a.com', 'a', '0', 'IIA-2024' );

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
    LEFT JOIN ingreso_salon ax ON ai.boleta = ax.boleta AND ax.idets = etsprueba 
    WHERE ai.idets = etsprueba;
END;
$$;


-- DROP FUNCTION ObtenerAsistenciaDetalles


-- SELECT * FROM ObtenerAsistenciaDetalles(1);

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

CREATE OR REPLACE FUNCTION obtener_datos_reporte(p_idets INTEGER, p_boleta VARCHAR)
RETURNS TABLE (
    curp VARCHAR,
    nombre VARCHAR,
    apellido_p VARCHAR,
    apellido_m VARCHAR,
    escuela VARCHAR,
    carrera VARCHAR,
    periodo VARCHAR,
    tipo VARCHAR,
    turno VARCHAR,
    materia VARCHAR,
    fecha_ingreso DATE,
    hora_ingreso TIME,
    nombre_docente VARCHAR,
    tipo_estado VARCHAR,
    presicion REAL,
    motivo VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        p.curp,
        p.nombre,
        p.apellido_p,
        p.apellido_m,
        ua.nombre AS escuela,
        pa.nombre AS carrera,
        pe.periodo,
        pe.tipo::VARCHAR,
        t.nombre AS turno,
        ua2.nombre AS materia,
        isalon.fecha AS fecha_ingreso,
        isalon.hora AS hora_ingreso,
        isalon.docente AS nombre_docente,
        te.tipo AS tipo_estado,
        COALESCE(rn.precision, 0.0) AS presicion, 
        COALESCE(mr.motivo, 'No Motivo') AS motivo 
    FROM
        alumno a
    INNER JOIN
        persona p ON a.curp = p.curp
    INNER JOIN
        unidadacademica ua ON p.id_escuela = ua.id_escuela
    INNER JOIN
        programaacademico pa ON a.idpa = pa.idpa
    INNER JOIN
        ets e ON e.idets = p_idets
    INNER JOIN
        periodoets pe ON e.id_periodo = pe.id_periodo
    INNER JOIN
        turno t ON e.turno = t.id_turno
    INNER JOIN
        unidadaprendizaje ua2 ON e.idua = ua2.idua
    INNER JOIN
        ingreso_salon isalon ON e.idets = isalon.idets AND a.boleta = isalon.boleta
    INNER JOIN
        tipo_estado te ON isalon.estado = te.idtipo
    LEFT JOIN
        resultadorn rn ON e.idets = rn.idets AND a.boleta = rn.boleta 
    LEFT JOIN
        motivo_rechazo mr ON a.boleta = mr.boleta AND e.idets = mr.idets -
    WHERE
        a.boleta = p_boleta AND e.idets = p_idets;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION obtener_imagen_alumno(p_idets INTEGER, p_boleta VARCHAR)
RETURNS VARCHAR AS $$
DECLARE
    ruta_imagen VARCHAR;
BEGIN
    SELECT rn.imagen_alumno INTO ruta_imagen
    FROM resultadorn rn
    INNER JOIN ets e ON e.idets = rn.idets
    INNER JOIN alumno a ON a.boleta = rn.boleta
    WHERE e.idets = p_idets AND a.boleta = p_boleta;

    RETURN ruta_imagen;
END;
$$ LANGUAGE plpgsql;

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

CREATE OR REPLACE FUNCTION verificar_ingreso_salon (p_boleta VARCHAR, p_idets INTEGER)
RETURNS BOOLEAN
AS $$
BEGIN
    RETURN EXISTS (SELECT 1 FROM ingreso_salon WHERE boleta = p_boleta AND idets = p_idets);
END;
$$ LANGUAGE plpgsql;




-- select * from verificar_ingreso_salon('2022630738',1)

CREATE OR REPLACE FUNCTION eliminar_reporte_alumno(p_idets INTEGER, p_boleta VARCHAR)
RETURNS BOOLEAN AS $$
DECLARE
    registros_eliminados INTEGER := 0;
BEGIN
    
    DELETE FROM resultadorn
    WHERE idets = p_idets AND boleta = p_boleta;
    IF FOUND THEN
        registros_eliminados := registros_eliminados + 1;
    END IF;

    
    DELETE FROM motivo_rechazo
    WHERE idets = p_idets AND boleta = p_boleta;
    IF FOUND THEN
        registros_eliminados := registros_eliminados + 1;
    END IF;

    
    DELETE FROM ingreso_salon
    WHERE idets = p_idets AND boleta = p_boleta;
    IF FOUND THEN
        registros_eliminados := registros_eliminados + 1;
    END IF;

    
    IF registros_eliminados > 0 THEN
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;
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


CREATE OR REPLACE FUNCTION obtener_docente_rfc(p_idets INTEGER) RETURNS VARCHAR AS $$
DECLARE
    v_docente_rfc VARCHAR;
BEGIN
    -- Obtener docente_rfc de la tabla aplica
    SELECT docente_rfc INTO v_docente_rfc
    FROM aplica
    WHERE idets = p_idets;

    -- Verificar si se encontró el docente_rfc
    IF v_docente_rfc IS NULL THEN
        RETURN 'No se encontró docente RFC para idets ' || p_idets;
    ELSE
        RETURN v_docente_rfc;
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 'Error al obtener docente RFC: ' || SQLERRM;
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