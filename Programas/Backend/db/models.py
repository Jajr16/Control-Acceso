from sqlalchemy import *
from sqlalchemy.orm import relationship
from db.session import Base

class UnidadAcademica(Base):
    __tablename__ = "unidadacademica"

    id_escuela = Column(Integer, primary_key=True, index=True, autoincrement=True)
    nombre = Column(String(200), index=True, nullable=False)

    # Relación con EscuelaPrograma
    escuelaPrograma = relationship("EscuelaPrograma", back_populates="unidadAcademica", lazy="joined")
    # Relación con Persona
    personas = relationship("Persona", back_populates="unidadAcademica", lazy="joined")

class ProgramaAcademico(Base):
    __tablename__ = "programaacademico"

    idpa = Column(String(20), primary_key=True, index=True)
    nombre = Column(String(150), nullable=False, index=True)
    descripcion = Column(String(200), nullable=False)

    # Relación con EscuelaPrograma
    escuelaPrograma = relationship("EscuelaPrograma", back_populates="programaAcademico", lazy="joined")
    alumnoCarrera = relationship("Alumno", back_populates="carrera", lazy="joined")
    nombrePrograma = relationship("UnidadAprendizaje", back_populates="programaAcademico", lazy="joined")

class EscuelaPrograma(Base):
    __tablename__ = "escuelaprograma"

    id_escuela = Column(
        Integer,
        ForeignKey("unidadacademica.id_escuela", ondelete="CASCADE", onupdate="CASCADE"),
        primary_key=True,
        index=True,
    )
    idpa = Column(
        String(20),
        ForeignKey("programaacademico.idpa", ondelete="CASCADE", onupdate="CASCADE"),
        primary_key=True,
        index=True,
    )

    # Relación inversa con UnidadAcademica
    unidadAcademica = relationship("UnidadAcademica", back_populates="escuelaPrograma", lazy="joined")
    # Relación inversa con ProgramaAcademico
    programaAcademico = relationship("ProgramaAcademico", back_populates="escuelaPrograma", lazy="joined")


class Sexo(Base):
    __tablename__ = "sexo"

    id_sexo = Column(Integer, primary_key=True, index=True)
    nombre = Column(String(9), index=True, nullable=False)

    # Relación con Persona
    personas = relationship("Persona", back_populates="id_sexo", lazy="joined")


class Persona(Base):
    __tablename__ = "persona"

    curp = Column(String(18), primary_key=True, index=True)
    nombre = Column(String(100), nullable=False, index=True)
    apellido_p = Column(String(150), nullable=False, index=True)
    apellido_m = Column(String(150), nullable=False, index=True)
    sexo = Column(Integer, ForeignKey("sexo.id_sexo"), nullable=False)
    id_escuela = Column(Integer, ForeignKey("unidadacademica.id_escuela"), nullable=False)

    id_sexo = relationship("Sexo", back_populates="personas", lazy="joined")
    unidadAcademica = relationship("UnidadAcademica", back_populates="personas", lazy="joined")
    personalAcademico = relationship("PersonalAcademico", back_populates="persona", lazy="joined")
    personalSeguridad = relationship("PersonalSeguridad", back_populates="persona", lazy="joined")
    Alumno = relationship("Alumno", back_populates="persona", lazy="joined")
    Usuario = relationship("Usuario", back_populates="persona", lazy="joined")
    
class TipoUsuario(Base):
    __tablename__ = "tipousuario"
    
    idtu = Column(Integer, primary_key=True, index=True)
    tipo = Column(String(18), nullable=False, index=True)
    
    tipoNombre = relationship("Usuario", back_populates="tipoUser", lazy="joined")
    
class Usuario(Base):
    __tablename__ = "usuario"
    
    usuario = Column(String(18), primary_key=True, index=True)
    password = Column(String(100), nullable=False)
    tipou = Column(Integer, ForeignKey("tipousuario.idtu"), nullable=False)
    curp = Column(
        String(18),
        ForeignKey("persona.curp", ondelete="CASCADE", onupdate="CASCADE"),
        nullable=False,
        index=True,
    )
    
    tipoUser = relationship("TipoUsuario", back_populates="tipoNombre", lazy="joined")
    persona = relationship("Persona", back_populates="Usuario", lazy="joined")

class TipoPersonal(Base):
    __tablename__ = "tipopersonal"

    tipopa = Column(Integer, primary_key=True, index=True)
    cargo = Column(String(100), nullable=False, index=True)

    # Relación con PersonalAcademico
    personalAcademicos = relationship("PersonalAcademico", back_populates="tipoPersonal", lazy="joined")


class PersonalAcademico(Base):
    __tablename__ = "personalacademico"

    rfc = Column(String(13), primary_key=True, index=True)
    curp = Column(
        String(18),
        ForeignKey("persona.curp", ondelete="CASCADE", onupdate="CASCADE"),
        nullable=False,
        index=True,
    )
    correoi = Column(String(100), nullable=False, index=True)
    tipopa = Column(Integer, ForeignKey("tipopersonal.tipopa"), index=True)

    persona = relationship("Persona", back_populates="personalAcademico", lazy="joined")
    tipoPersonal = relationship("TipoPersonal", back_populates="personalAcademicos", lazy="joined")
    cargoPersonalAcademico = relationship("CargoDocente", back_populates="nombrePersona", lazy="joined")
    ETSaAplicar = relationship("Aplica", back_populates="ETSAplicado", lazy="joined")
    
class Cargo(Base):
    __tablename__ = "cargo"
    
    id_cargo = Column(Integer, primary_key=True, index=True)
    cargo = Column(String(100), nullable=False, index=True)
    
    cargoDocente = relationship("CargoDocente", back_populates="nombreCargo", lazy="joined")
    
class CargoDocente(Base):
    __tablename__ = "cargodocente"
    
    rfc = Column(String(13), ForeignKey("personalacademico.rfc", ondelete="CASCADE", onupdate="CASCADE"), nullable=False, primary_key=True)
    id_cargo = Column(Integer, ForeignKey("cargo.id_cargo"), nullable=False, primary_key=True)
    
    nombrePersona = relationship("PersonalAcademico", back_populates="cargoPersonalAcademico", lazy="joined")
    nombreCargo = relationship("Cargo", back_populates="cargoDocente", lazy="joined")

class Turno(Base):
    __tablename__ = "turno"
    
    id_turno = Column(Integer, primary_key=True, index=True, autoincrement=True)
    nombre = Column(String(10), nullable=False, index=True)
    
    turnoPS = relationship("PersonalSeguridad", back_populates="turnoSeguridad", lazy="joined")
    id_turnoETS = relationship("ETS", back_populates="id_turno", lazy="joined")
    
class CargoPS(Base):
    __tablename__ = "cargops"
    
    id_cargo = Column(Integer, primary_key=True, index=True)
    nombre = Column(String(100), nullable=False, index=True)
    
    cargoPS = relationship("PersonalSeguridad", back_populates="cargoSeguridad", lazy="joined")

class PersonalSeguridad(Base):
    __tablename__ = "personalseguridad"
    
    curp = Column(
        String(18),
        ForeignKey("persona.curp", ondelete="CASCADE", onupdate="CASCADE"),
        primary_key=True,
        nullable=False,
        index=True,
    )
    turno = Column(Integer, ForeignKey("turno.id_turno"), nullable=False)
    cargo = Column(Integer, ForeignKey("cargops.id_cargo"), nullable=False)
    
    persona = relationship("Persona", back_populates="personalSeguridad", lazy="joined")
    turnoSeguridad = relationship("Turno", back_populates="turnoPS", lazy="joined")
    cargoSeguridad = relationship("CargoPS", back_populates="cargoPS", lazy="joined")
    
class Alumno(Base):
    __tablename__ = "alumno"
    
    boleta = Column(String(15), primary_key=True, index=True)
    curp = Column(
        String(18),
        ForeignKey("persona.curp", ondelete="CASCADE", onupdate="CASCADE"),
        nullable=False,
        index=True,
    )
    correoi = Column(String(100), nullable=False, index=True)
    idpa = Column(String(20), ForeignKey("programaacademico.idpa"), nullable=False)
    imagen_credencial = Column(String(200), nullable=False, index=True)
    
    carrera = relationship("ProgramaAcademico", back_populates="alumnoCarrera", lazy="joined")
    persona = relationship("Persona", back_populates="Alumno", lazy="joined")
    nets = relationship("InscripcionETS", back_populates="alumno", lazy="joined")
    
class periodoETS(Base):
    __tablename__ = "periodoets"
    
    id_periodo = Column(Integer, primary_key=True, index=True, autoincrement=True)
    periodo = Column(String(20), nullable=False, index=False)
    tipo = Column(CHAR, nullable=False)
    fecha_inicio = Column(Date, nullable=False)
    fecha_fin = Column(Date, nullable=False)
    
    nombrePeriodo = relationship("ETS", back_populates="periodo", lazy="joined")
    
class UnidadAprendizaje(Base):
    __tablename__ = "unidadaprendizaje"
    
    idua = Column(String(20), primary_key=True, index=True)
    nombre = Column(String(150), index=True, nullable=False)
    descripcion = Column(String(200), nullable=False)
    idpa = Column(String(20), ForeignKey("programaacademico.idpa", ondelete="CASCADE", onupdate="CASCADE"), index=True, nullable=False)
    
    programaAcademico = relationship("ProgramaAcademico", back_populates="nombrePrograma", lazy="joined")
    nombreUA = relationship("ETS", back_populates="UAETS", lazy="joined")

class ETS(Base):
    __tablename__ = "ets"
    
    idets = Column(Integer, primary_key=True, index=True, autoincrement=True)
    id_periodo = Column(Integer, ForeignKey("periodoets.id_periodo", onupdate="CASCADE", ondelete="CASCADE"), nullable=False, index=True)
    turno = Column(Integer, ForeignKey("turno.id_turno"), nullable=False)
    fecha = Column(DateTime, nullable=False, index=True)
    cupo = Column(Integer, nullable=False)
    idua = Column(String(20), ForeignKey("unidadaprendizaje.idua", ondelete="CASCADE", onupdate="CASCADE"), nullable=False, index=True)
    duracion = Column(Integer, nullable=False)
    
    periodo = relationship("periodoETS", back_populates="nombrePeriodo", lazy="joined")
    id_turno = relationship("Turno", back_populates="id_turnoETS", lazy="joined")
    UAETS = relationship("UnidadAprendizaje", back_populates="nombreUA", lazy="joined")
    salon = relationship("SalonETS", back_populates="ets", lazy="joined")
    alumno = relationship("InscripcionETS", back_populates="ets", lazy="joined")
    AplicarETS = relationship("Aplica", back_populates="docenteaplicador", lazy="joined")

class TipoSalon(Base):
    __tablename__ = "tiposalon"
    
    idts = Column(Integer, primary_key=True, index=True)
    tipo = Column(String(11), nullable=False, index=True)
    
    nts = relationship("Salon", back_populates="salonType", lazy="joined")
    
class Salon(Base):
    __tablename__ = "salon"
    
    num_salon = Column(Integer, primary_key=True, index=True)
    edificio = Column(Integer, nullable=False, index=True)
    piso = Column(Integer, nullable=False, index=True)
    tipo_salon = Column(Integer, ForeignKey("tiposalon.idts"), nullable=False, index=True)
    
    salonType = relationship("TipoSalon", back_populates="nts", lazy="joined")
    ets = relationship("SalonETS", back_populates="salon", lazy="joined")
    
class SalonETS(Base):
    __tablename__ = "salonets"
    
    num_salon = Column(Integer, ForeignKey("salon.num_salon", ondelete="CASCADE", onupdate="CASCADE"), primary_key=True, index=True)
    idets = Column(Integer, ForeignKey("ets.idets", ondelete="CASCADE", onupdate="CASCADE"), primary_key=True, index=True)
    
    salon = relationship("Salon", back_populates="ets", lazy="joined")
    ets = relationship("ETS", back_populates="salon", lazy="joined")
    
class InscripcionETS(Base):
    __tablename__ = "inscripcionets"
    
    boleta = Column(String(15), ForeignKey("alumno.boleta", ondelete="CASCADE", onupdate="CASCADE"), primary_key=True, index=True)
    idets = Column(Integer, ForeignKey("ets.idets", ondelete="CASCADE", onupdate="CASCADE"), primary_key=True, index=True)
    
    ets = relationship("ETS", back_populates="alumno", lazy="joined")
    alumno = relationship("Alumno", back_populates="nets", lazy="joined")
    insETSAlumno = relationship(
        "AsistenciaInscripcion", 
        back_populates="alumnoAsistencia", 
        primaryjoin="and_(InscripcionETS.boleta == AsistenciaInscripcion.inscripcionets_boleta, InscripcionETS.idets == AsistenciaInscripcion.inscripcionets_idets)", 
        lazy="joined")
    
class Aplica(Base):
    __tablename__ = "aplica"
    
    idets = Column(Integer, ForeignKey("ets.idets", ondelete="CASCADE", onupdate="CASCADE"), primary_key=True, index=True)
    docente_rfc = Column(String(13), ForeignKey("personalacademico.rfc", ondelete="CASCADE", onupdate="CASCADE"), primary_key=True, index=True)
    titular = Column(Boolean, nullable=False)
    
    docenteaplicador = relationship("ETS", back_populates="AplicarETS", lazy="joined")
    ETSAplicado = relationship("PersonalAcademico", back_populates="ETSaAplicar", lazy="joined")
    
class AsistenciaInscripcion(Base):
    __tablename__ = "asistenciainscripcion"
    
    fecha_asistencia = Column(DateTime, primary_key=True, index=True)
    inscripcionets_boleta = Column(String(15), primary_key=True, index=True)
    inscripcionets_idets = Column(Integer, primary_key=True, index=True)
    asistio = Column(Boolean, nullable=False, default=False)
    resultado_rn = Column(Boolean, nullable=False, default=False)
    aceptado = Column(Boolean, nullable=False, default=False)
    
    __table_args__ = (
        ForeignKeyConstraint(
            ['inscripcionets_boleta', 'inscripcionets_idets'],
            ['inscripcionets.boleta', 'inscripcionets.idets'],
            ondelete="CASCADE",
            onupdate="CASCADE"
        ),
    )
    
    alumnoAsistencia = relationship(
        "InscripcionETS",
        back_populates="insETSAlumno",
        primaryjoin="and_(AsistenciaInscripcion.inscripcionets_boleta == InscripcionETS.boleta, AsistenciaInscripcion.inscripcionets_idets == InscripcionETS.idets)",
        lazy="joined"
    )
    
################# INSERTAR VALORES PREDETERMINADOS ####################
def insertUA(target, connection, **kwargs):
    session = connection
    unidad = session.query(UnidadAcademica).filter_by(id_escuela=1).first()
    if unidad:
        unidad.nombre = "ESCA"  # Actualiza los datos si ya existe
    else:
        session.add(UnidadAcademica(id_escuela=1, nombre="ESCA"))  # Inserta si no existe
    session.commit()

def insertPA(target, connection, **kwargs):
    session = connection
    programas = [
        {"idpa": "ISC-2024", "nombre": "Ingeniería en Sistemas Computacionales", "descripcion": "Caca"},
        {"idpa": "IIA-2024", "nombre": "Ingeniería en Inteligencia Artificial", "descripcion": "Caca"},
    ]
    for programa in programas:
        existente = session.query(ProgramaAcademico).filter_by(idpa=programa["idpa"]).first()
        if existente:
            existente.nombre = programa["nombre"]
            existente.descripcion = programa["descripcion"]
        else:
            session.add(ProgramaAcademico(**programa))
    session.commit()

def insertEscuelaPrograma(target, connection, **kwargs):
    session = connection
    registro = session.query(EscuelaPrograma).filter_by(id_escuela=1, idpa="ISC-2024").first()
    if not registro:
        session.add(EscuelaPrograma(id_escuela=1, idpa="ISC-2024"))
    session.commit()

def insertSexo(target, connection, **kwargs):
    session = connection
    sexos = [
        {"id_sexo": 1, "nombre": "Masculino"},
        {"id_sexo": 2, "nombre": "Femenino"},
    ]
    for sexo in sexos:
        existente = session.query(Sexo).filter_by(id_sexo=sexo["id_sexo"]).first()
        if existente:
            existente.nombre = sexo["nombre"]
        else:
            session.add(Sexo(**sexo))
    session.commit()

def insertPersona(target, connection, **kwargs):
    session = connection
    personas = [
        {"curp": "1", "nombre": "José Alfredo", "apellido_p": "Jiménez", "apellido_m": "Rodríguez", "sexo": 1, "id_escuela": 1},
        {"curp": "2", "nombre": "Alejandra", "apellido_p": "De la cruz", "apellido_m": "De la cruz", "sexo": 2, "id_escuela": 1},
        {"curp": "3", "nombre": "Luis Antonio", "apellido_p": "Flores", "apellido_m": "Esquivel", "sexo": 1, "id_escuela": 1},
        {"curp": "4", "nombre": "Daniel", "apellido_p": "Huertas", "apellido_m": "Ramírez", "sexo": 1, "id_escuela": 1},
    ]
    for persona in personas:
        existente = session.query(Persona).filter_by(curp=persona["curp"]).first()
        if existente:
            existente.nombre = persona["nombre"]
            existente.apellido_p = persona["apellido_p"]
            existente.apellido_m = persona["apellido_m"]
            existente.sexo = persona["sexo"]
            existente.id_escuela = persona["id_escuela"]
        else:
            session.add(Persona(**persona))
    session.commit()

def insertTipoU(target, connection, **kwargs):
    session = connection
    tipos = [
        {"idtu": 1, "tipo": "Alumno"},
        {"idtu": 2, "tipo": "Docente"},
    ]
    for tipo in tipos:
        existente = session.query(TipoUsuario).filter_by(idtu=tipo["idtu"]).first()
        if existente:
            existente.tipo = tipo["tipo"]
        else:
            session.add(TipoUsuario(**tipo))
    session.commit()

def insertUsuario(target, connection, **kwargs):
    session = connection
    usuarios = [
        {"usuario": "Alfredo", "password": "$2b$12$KejElPRgHbWDWF2BmlukSOMb8rqEzhNSVBbgndRPbhU.YqdDxI8US", "tipou": 1, "curp": "1"},
        {"usuario": "2022630467", "password": "$2b$12$KejElPRgHbWDWF2BmlukSOMb8rqEzhNSVBbgndRPbhU.YqdDxI8US", "tipou": 1, "curp": "2"},
    ]
    for usuario in usuarios:
        existente = session.query(Usuario).filter_by(usuario=usuario["usuario"]).first()
        if existente:
            existente.password = usuario["password"]
            existente.tipou = usuario["tipou"]
            existente.curp = usuario["curp"]
        else:
            session.add(Usuario(**usuario))
    session.commit()

def insertAlumno(target, connection, **kwargs):
    session = connection
    alumnos = [
        {"boleta": "2022325410", "curp": "1", "correoi": "1@gmail.com", "idpa": "ISC-2024", "imagen_credencial": "IMG"},
        {"boleta": "2022630467", "curp": "2", "correoi": "2@gmail.com", "idpa": "IIA-2024", "imagen_credencial": "IMG"},
    ]
    for alumno in alumnos:
        existente = session.query(Alumno).filter_by(boleta=alumno["boleta"]).first()
        if existente:
            existente.curp = alumno["curp"]
            existente.correoi = alumno["correoi"]
            existente.idpa = alumno["idpa"]
            existente.imagen_credencial = alumno["imagen_credencial"]
        else:
            session.add(Alumno(**alumno))
    session.commit()

def insertperiodoETS(target, connection, **kwargs):
    session = connection
    periodo = session.query(periodoETS).filter_by(id_periodo=1).first()
    if periodo:
        periodo.periodo = "25/1"
        periodo.tipo = "O"
        periodo.fecha_inicio = "2024-12-10"
        periodo.fecha_fin = "2024-12-10"
    else:
        session.add(periodoETS(id_periodo=1, periodo="25/1", tipo="O", fecha_inicio="2024-12-10", fecha_fin="2024-12-10"))
    session.commit()
    
def insertTurno(target, connection, **kwargs):
    session = connection

    # Verificar si el turno ya existe antes de agregarlo
    if not session.query(Turno).filter_by(id_turno=1).first():
        session.add(Turno(id_turno=1, nombre="Matutino"))
    
    if not session.query(Turno).filter_by(id_turno=2).first():
        session.add(Turno(id_turno=2, nombre="Vespertino"))
    
    session.commit()

def insertUAprendizaje(target, connection, **kwargs):
    session = connection

    # Verificar si las unidades de aprendizaje ya existen
    if not session.query(UnidadAprendizaje).filter_by(idua="PDI-IIA").first():
        session.add(UnidadAprendizaje(idua="PDI-IIA", nombre="Programación Digital de Imágenes", descripcion="Una materia", idpa="IIA-2024"))
    
    if not session.query(UnidadAprendizaje).filter_by(idua="BBD-ISC").first():
        session.add(UnidadAprendizaje(idua="BBD-ISC", nombre="BigData", descripcion="Una materia", idpa="ISC-2024"))
    
    if not session.query(UnidadAprendizaje).filter_by(idua="BD-IIA").first():
        session.add(UnidadAprendizaje(idua="BD-IIA", nombre="Bases de Datos", descripcion="Una materia", idpa="IIA-2024"))
    
    session.commit()

def insertETS(target, connection, **kwargs):
    session = connection

    # Verificar si las ETS ya existen
    if not session.query(ETS).filter_by(idets=1).first():
        session.add(ETS(idets=1, id_periodo=1, turno=1, fecha="2024-10-04", cupo=20, idua="PDI-IIA", duracion=2))

    if not session.query(ETS).filter_by(idets=2).first():
        session.add(ETS(idets=2, id_periodo=1, turno=1, fecha="2024-10-04", cupo=20, idua="BBD-ISC", duracion=2))

    if not session.query(ETS).filter_by(idets=3).first():
        session.add(ETS(idets=3, id_periodo=1, turno=1, fecha="2024-10-04", cupo=20, idua="BD-IIA", duracion=2))
    
    session.commit()

def insertInscripciones(target, connection, **kwargs):
    session = connection
    # Verificar si la inscripción ya existe
    inscripcion = session.query(InscripcionETS).filter_by(boleta="2022630467").first()
    if not inscripcion:
        session.add(InscripcionETS(boleta="2022630467", idets=1))
        session.add(InscripcionETS(boleta="2022630467", idets=2))
        session.add(InscripcionETS(boleta="2022630467", idets=3))
    session.commit()

def insertTipoSalon(target, connection, **kwargs):
    session = connection

    # Verificar si los tipos de salón ya existen
    if not session.query(TipoSalon).filter_by(idts=1).first():
        session.add(TipoSalon(idts=1, tipo="Laboratorio"))
    
    if not session.query(TipoSalon).filter_by(idts=2).first():
        session.add(TipoSalon(idts=2, tipo="Normal"))
    
    session.commit()

def insertSalones(target, connection, **kwargs):
    session = connection

    # Verificar si los salones ya existen
    if not session.query(Salon).filter_by(num_salon=4108).first():
        session.add(Salon(num_salon=4108, edificio=4, piso=1, tipo_salon=1))
    
    if not session.query(Salon).filter_by(num_salon=3210).first():
        session.add(Salon(num_salon=3210, edificio=3, piso=2, tipo_salon=1))
    
    session.commit()

def insertSalonETS(target, connection, **kwargs):
    session = connection

    # Verificar si los salones de ETS ya existen
    if not session.query(SalonETS).filter_by(num_salon=4108, idets=1).first():
        session.add(SalonETS(num_salon=4108, idets=1))
    
    if not session.query(SalonETS).filter_by(num_salon=3210, idets=2).first():
        session.add(SalonETS(num_salon=3210, idets=2))
    
    if not session.query(SalonETS).filter_by(num_salon=4108, idets=2).first():
        session.add(SalonETS(num_salon=4108, idets=2))
    
    session.commit()


def inicializar_base_datos(session):
    insertUA(None, session)
    insertPA(None, session)
    insertEscuelaPrograma(None, session)
    insertSexo(None, session)
    insertPersona(None, session)
    insertTipoU(None, session)
    insertUsuario(None, session)
    insertAlumno(None, session)
    insertperiodoETS(None, session)
    insertTurno(None, session)
    insertUAprendizaje(None, session)
    insertETS(None, session)
    insertInscripciones(None, session)
    insertTipoSalon(None, session)
    insertSalones(None, session)
    insertSalonETS(None, session)