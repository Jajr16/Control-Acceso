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
    unidad_aprendizaje_nombre VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
	RETURN QUERY

    SELECT inscripcionets.idets, periodoets.periodo, turno.nombre as turno, ets.fecha, unidadaprendizaje.nombre FROM inscripcionets
	INNER JOIN ets ON inscripcionets.idets = ets.idets
	INNER JOIN periodoets ON ets.id_periodo = periodoets.id_periodo 
	INNER JOIN turno ON turno.id_turno = ets.turno
	INNER JOIN unidadaprendizaje ON unidadaprendizaje.idua = ets.idua WHERE inscripcionets.boleta = boletaC;
	
    
END;
$$;

select *from login('2022630467', '123');
SELECT * FROM usuario;
SELECT crypt('123', gen_salt('bf'));
