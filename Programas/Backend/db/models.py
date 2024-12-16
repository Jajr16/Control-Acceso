from sqlalchemy import *
from sqlalchemy.orm import relationship
from db.session import Base

class UnidadAcademica(Base):
    __tablename__ = "UnidadAcademica"

    idEscuela = Column(Integer, primary_key=True, index=True, autoincrement=True)
    Nombre = Column(String(200), index=True, nullable=False)

    # Relación con EscuelaPrograma
    escuelaPrograma = relationship("EscuelaPrograma", back_populates="unidadAcademica", lazy="joined")
    # Relación con Persona
    personas = relationship("Persona", back_populates="unidadAcademica", lazy="joined")

class ProgramaAcademico(Base):
    __tablename__ = "ProgramaAcademico"

    idPA = Column(String(20), primary_key=True, index=True)
    Nombre = Column(String(150), nullable=False, index=True)
    Descripcion = Column(String(200), nullable=False)

    # Relación con EscuelaPrograma
    escuelaPrograma = relationship("EscuelaPrograma", back_populates="programaAcademico", lazy="joined")
    alumnoCarrera = relationship("Alumno", back_populates="carrera", lazy="joined")
    nombrePrograma = relationship("UnidadAprendizaje", back_populates="programaAcademico", lazy="joined")

class EscuelaPrograma(Base):
    __tablename__ = "EscuelaPrograma"

    idEscuela = Column(
        Integer,
        ForeignKey("UnidadAcademica.idEscuela", ondelete="CASCADE", onupdate="CASCADE"),
        primary_key=True,
        index=True,
    )
    idPA = Column(
        String(20),
        ForeignKey("ProgramaAcademico.idPA", ondelete="CASCADE", onupdate="CASCADE"),
        primary_key=True,
        index=True,
    )

    # Relación inversa con UnidadAcademica
    unidadAcademica = relationship("UnidadAcademica", back_populates="escuelaPrograma", lazy="joined")
    # Relación inversa con ProgramaAcademico
    programaAcademico = relationship("ProgramaAcademico", back_populates="escuelaPrograma", lazy="joined")


class Sexo(Base):
    __tablename__ = "Sexo"

    idSexo = Column(Integer, primary_key=True, index=True)
    Nombre = Column(String(9), index=True, nullable=False)

    # Relación con Persona
    personas = relationship("Persona", back_populates="sexo", lazy="joined")


class Persona(Base):
    __tablename__ = "Persona"

    CURP = Column(String(18), primary_key=True, index=True)
    Nombre = Column(String(100), nullable=False, index=True)
    ApellidoP = Column(String(150), nullable=False, index=True)
    ApellidoM = Column(String(150), nullable=False, index=True)
    Sexo = Column(Integer, ForeignKey("Sexo.idSexo"), nullable=False)
    idEscuela = Column(Integer, ForeignKey("UnidadAcademica.idEscuela"), nullable=False)

    sexo = relationship("Sexo", back_populates="personas", lazy="joined")
    unidadAcademica = relationship("UnidadAcademica", back_populates="personas", lazy="joined")
    personalAcademico = relationship("PersonalAcademico", back_populates="persona", lazy="joined")
    personalSeguridad = relationship("PersonalSeguridad", back_populates="persona", lazy="joined")
    Alumno = relationship("Alumno", back_populates="persona", lazy="joined")
    Usuario = relationship("Usuario", back_populates="persona", lazy="joined")
    
class TipoUsuario(Base):
    __tablename__ = "TipoUsuario"
    
    idTU = Column(Integer, primary_key=True, index=True)
    Tipo = Column(String(18), nullable=False, index=True)
    
    tipoNombre = relationship("Usuario", back_populates="tipoUser", lazy="joined")
    
class Usuario(Base):
    __tablename__ = "Usuario"
    
    Usuario = Column(String(18), primary_key=True, index=True)
    Password = Column(String(100), nullable=False)
    TipoU = Column(Integer, ForeignKey("TipoUsuario.idTU"), nullable=False)
    CURP = Column(
        String(18),
        ForeignKey("Persona.CURP", ondelete="CASCADE", onupdate="CASCADE"),
        nullable=False,
        index=True,
    )
    
    tipoUser = relationship("TipoUsuario", back_populates="tipoNombre", lazy="joined")
    persona = relationship("Persona", back_populates="Usuario", lazy="joined")

class TipoPersonal(Base):
    __tablename__ = "tipoPersonal"

    tipoPA = Column(Integer, primary_key=True, index=True)
    Cargo = Column(String(100), nullable=False, index=True)

    # Relación con PersonalAcademico
    personalAcademicos = relationship("PersonalAcademico", back_populates="tipoPersonal", lazy="joined")


class PersonalAcademico(Base):
    __tablename__ = "PersonalAcademico"

    RFC = Column(String(13), primary_key=True, index=True)
    CURP = Column(
        String(18),
        ForeignKey("Persona.CURP", ondelete="CASCADE", onupdate="CASCADE"),
        nullable=False,
        index=True,
    )
    CorreoI = Column(String(100), nullable=False, index=True)
    TipoPA = Column(Integer, ForeignKey("tipoPersonal.tipoPA"), index=True)

    persona = relationship("Persona", back_populates="personalAcademico", lazy="joined")
    tipoPersonal = relationship("TipoPersonal", back_populates="personalAcademicos", lazy="joined")
    cargoPersonalAcademico = relationship("CargoDocente", back_populates="nombrePersona", lazy="joined")
    ETSaAplicar = relationship("Aplica", back_populates="ETSAplicado", lazy="joined")
    
class Cargo(Base):
    __tablename__ = "Cargo"
     
    idCargo = Column(Integer, primary_key=True, index=True)
    Cargo = Column(String(100), nullable=False, index=True)
    
    cargoDocente = relationship("CargoDocente", back_populates="nombreCargo", lazy="joined")
       
class CargoDocente(Base):
    __tablename__ = "CargoDocente"
    
    RFC = Column(String(13), ForeignKey("PersonalAcademico.RFC", ondelete="CASCADE", onupdate="CASCADE"), nullable=False, primary_key=True)
    idCargo = Column(Integer, ForeignKey("Cargo.idCargo"), nullable=False, primary_key=True)
    
    nombrePersona = relationship("PersonalAcademico", back_populates="cargoPersonalAcademico", lazy="joined")
    nombreCargo = relationship("Cargo", back_populates="cargoDocente", lazy="joined")

class Turno(Base):
    __tablename__ = "Turno"
    
    idTurno = Column(Integer, primary_key=True, index=True, autoincrement=True)
    Nombre = Column(String(10), nullable=False, index=True)
    
    turnoPS = relationship("PersonalSeguridad", back_populates="turnoSeguridad", lazy="joined")
    turnoETS = relationship("ETS", back_populates="turno", lazy="joined")
    
class CargoPS(Base):
    __tablename__ = "CargoPS"
    
    idCargo = Column(Integer, primary_key=True, index=True)
    Nombre = Column(String(100), nullable=False, index=True)
    
    cargoPS = relationship("PersonalSeguridad", back_populates="cargoSeguridad", lazy="joined")

class PersonalSeguridad(Base):
    __tablename__ = "PersonalSeguridad"
    
    CURP = Column(
        String(18),
        ForeignKey("Persona.CURP", ondelete="CASCADE", onupdate="CASCADE"),
        primary_key=True,
        nullable=False,
        index=True,
    )
    Turno = Column(Integer, ForeignKey("Turno.idTurno"), nullable=False)
    Cargo = Column(Integer, ForeignKey("CargoPS.idCargo"), nullable=False)
    
    persona = relationship("Persona", back_populates="personalSeguridad", lazy="joined")
    turnoSeguridad = relationship("Turno", back_populates="turnoPS", lazy="joined")
    cargoSeguridad = relationship("CargoPS", back_populates="cargoPS", lazy="joined")
    
class Alumno(Base):
    __tablename__ = "Alumno"
    
    Boleta = Column(String(15), primary_key=True, index=True)
    CURP = Column(
        String(18),
        ForeignKey("Persona.CURP", ondelete="CASCADE", onupdate="CASCADE"),
        nullable=False,
        index=True,
    )
    CorreoI = Column(String(100), nullable=False, index=True)
    idPA = Column(String(20), ForeignKey("ProgramaAcademico.idPA"), nullable=False)
    imagenCredencual = Column(String(200), nullable=False, index=True)
    
    carrera = relationship("ProgramaAcademico", back_populates="alumnoCarrera", lazy="joined")
    persona = relationship("Persona", back_populates="Alumno", lazy="joined")
    nets = relationship("InscripcionETS", back_populates="alumno", lazy="joined")
    
class periodoETS(Base):
    __tablename__ = "periodoETS"
    
    idPeriodo = Column(Integer, primary_key=True, index=True, autoincrement=True)
    Periodo = Column(String(20), nullable=False, index=False)
    Tipo = Column(CHAR, nullable=False)
    Fecha_Inicio = Column(Date, nullable=False)
    Fecha_Fin = Column(Date, nullable=False)
    
    nombrePeriodo = relationship("ETS", back_populates="periodo", lazy="joined")
    
class UnidadAprendizaje(Base):
    __tablename__ = "UnidadAprendizaje"
    
    idUA = Column(String(20), primary_key=True, index=True)
    Nombre = Column(String(150), index=True, nullable=False)
    Descripcion = Column(String(200), nullable=False)
    idPA = Column(String(20), ForeignKey("ProgramaAcademico.idPA", ondelete="CASCADE", onupdate="CASCADE"), index=True, nullable=False)
    
    programaAcademico = relationship("ProgramaAcademico", back_populates="nombrePrograma", lazy="joined")
    nombreUA = relationship("ETS", back_populates="UAETS", lazy="joined")

class ETS(Base):
    __tablename__ = "ETS"
    
    idETS = Column(Integer, primary_key=True, index=True, autoincrement=True)
    idPeriodo = Column(Integer, ForeignKey("periodoETS.idPeriodo", onupdate="CASCADE", ondelete="CASCADE"), nullable=False, index=True)
    Turno = Column(Integer, ForeignKey("Turno.idTurno"), nullable=False)
    Fecha = Column(DateTime, nullable=False, index=True)
    Cupo = Column(Integer, nullable=False)
    idUA = Column(String(20), ForeignKey("UnidadAprendizaje.idUA", ondelete="CASCADE", onupdate="CASCADE"), nullable=False, index=True)
    Duracion = Column(Integer, nullable=False)
    
    periodo = relationship("periodoETS", back_populates="nombrePeriodo", lazy="joined")
    turno = relationship("Turno", back_populates="turnoETS", lazy="joined")
    UAETS = relationship("UnidadAprendizaje", back_populates="nombreUA", lazy="joined")
    salon = relationship("SalonETS", back_populates="ets", lazy="joined")
    alumno = relationship("InscripcionETS", back_populates="ets", lazy="joined")
    AplicarETS = relationship("Aplica", back_populates="docenteaplicador", lazy="joined")

class TipoSalon(Base):
    __tablename__ = "TipoSalon"
    
    idTS = Column(Integer, primary_key=True, index=True)
    Tipo = Column(String(11), nullable=False, index=True)
    
    nts = relationship("Salon", back_populates="salonType", lazy="joined")
    
class Salon(Base):
    __tablename__ = "Salon"
    
    numSalon = Column(Integer, primary_key=True, index=True)
    Edificio = Column(Integer, nullable=False, index=True)
    Piso = Column(Integer, nullable=False, index=True)
    tipoSalon = Column(Integer, ForeignKey("TipoSalon.idTS"), nullable=False, index=True)
    
    salonType = relationship("TipoSalon", back_populates="nts", lazy="joined")
    ets = relationship("SalonETS", back_populates="salon", lazy="joined")
    
class SalonETS(Base):
    __tablename__ = "SalonETS"
    
    numSalon = Column(Integer, ForeignKey("Salon.numSalon", ondelete="CASCADE", onupdate="CASCADE"), primary_key=True, index=True)
    idETS = Column(Integer, ForeignKey("ETS.idETS", ondelete="CASCADE", onupdate="CASCADE"), primary_key=True, index=True)
    
    salon = relationship("Salon", back_populates="ets", lazy="joined")
    ets = relationship("ETS", back_populates="salon", lazy="joined")
    
class InscripcionETS(Base):
    __tablename__ = "InscripcionETS"
    
    Boleta = Column(String(15), ForeignKey("Alumno.Boleta", ondelete="CASCADE", onupdate="CASCADE"), primary_key=True, index=True)
    idETS = Column(Integer, ForeignKey("ETS.idETS", ondelete="CASCADE", onupdate="CASCADE"), primary_key=True, index=True)
    
    ets = relationship("ETS", back_populates="alumno", lazy="joined")
    alumno = relationship("Alumno", back_populates="nets", lazy="joined")
    insETSAlumno = relationship("AsistenciaInscripcion", back_populates="alumnoAsistencia", primaryjoin="and_(InscripcionETS.Boleta==AsistenciaInscripcion.InscripcionETSBoleta, InscripcionETS.idETS==AsistenciaInscripcion.InscripcionETSIdETS)", lazy="joined")
    
class Aplica(Base):
    __tablename__ = "Aplica"
    
    idETS = Column(Integer, ForeignKey("ETS.idETS", ondelete="CASCADE", onupdate="CASCADE"), primary_key=True, index=True)
    DocenteRFC = Column(String(13), ForeignKey("PersonalAcademico.RFC", ondelete="CASCADE", onupdate="CASCADE"), primary_key=True, index=True)
    Titular = Column(Boolean, nullable=False)
    
    docenteaplicador = relationship("ETS", back_populates="AplicarETS", lazy="joined")
    ETSAplicado = relationship("PersonalAcademico", back_populates="ETSaAplicar", lazy="joined")
    
class AsistenciaInscripcion(Base):
    __tablename__ = "AsistenciaInscripcion"
    
    FechaAsistencia = Column(DateTime, primary_key=True, index=True)
    InscripcionETSBoleta = Column(String(15), primary_key=True, index=True)
    InscripcionETSIdETS = Column(Integer, primary_key=True, index=True)
    Asistio = Column(Boolean, nullable=False, default=False)
    ResultadoRN = Column(Boolean, nullable=False, default=False)
    Aceptado = Column(Boolean, nullable=False, default=False)
    
    __table_args__ = (
        ForeignKeyConstraint(
            ['InscripcionETSBoleta', 'InscripcionETSIdETS'],
            ['InscripcionETS.Boleta', 'InscripcionETS.idETS'],
            ondelete="CASCADE",
            onupdate="CASCADE"
        ),
    )
    
    alumnoAsistencia = relationship(
        "InscripcionETS",
        back_populates="insETSAlumno",
        primaryjoin="and_(AsistenciaInscripcion.InscripcionETSBoleta == InscripcionETS.Boleta, AsistenciaInscripcion.InscripcionETSIdETS == InscripcionETS.idETS)",
        lazy="joined"
    )
    
################# INSERTAR VALORES PREDETERMINADOS ####################
def insertUA(target, connection, **kwargs):
    connection.execute(
        UnidadAcademica.__table__.insert(),
        [
            {"idEscuela": 1, "Nombre": "ESCA"},
        ]
    )
    
def insertPA(target, connection, **kwargs):
    connection.execute(
        ProgramaAcademico.__table__.insert(),
        [
            {"idPA": "ISC-2024", "Nombre": "Ingeniería en Sistemas Computacionales", "Descripcion": "Caca"},
        ]
    )

def insertEscuelaPrograma(target, connection, **kwargs):
    connection.execute(
        EscuelaPrograma.__table__.insert(),
        [
            {"idEscuela":"1","idPA": "ISC-2024"},
        ]
    )
    
def insertSexo(target, connection, **kwargs):
    connection.execute(
        Sexo.__table__.insert(),
        [
            {"idSexo": 1, "Nombre": "Masculino"},
            {"idSexo": 2, "Nombre": "Femenino"},
        ]
    )
    
def insertPersona(target, connection, **kwargs):
    connection.execute(
        Persona.__table__.insert(),
        [
            {"CURP": "1", "Nombre": "José Alfredo", "ApellidoP": "Jiménez", "ApellidoM": "Rodríguez", "Sexo": 1, "idEscuela": 1},
            {"CURP": "2", "Nombre": "Alejandra", "ApellidoP": "De la cruz", "ApellidoM": "De la cruz", "Sexo": 2, "idEscuela": 1},
            {"CURP": "3", "Nombre": "Luis Antonio", "ApellidoP": "Flores", "ApellidoM": "Esquivel", "Sexo": 1, "idEscuela": 1},
            {"CURP": "4", "Nombre": "Daniel", "ApellidoP": "Huertas", "ApellidoM": "Ramírez", "Sexo": 1, "idEscuela": 1}
        ]
    )
    
def insertTipoU(target, connection, **kwargs):
    connection.execute(
        TipoUsuario.__table__.insert(),
        [
            {"idTU": 1, "Tipo": "Alumno"},
            {"idTU": 2, "Tipo": "Docente"},
        ]
    )
    
def insertUsuario(target, connection, **kwargs):
    connection.execute(
        Usuario.__table__.insert(),
        [
            {"Usuario": "Alfredo", "Password": "$2b$12$KejElPRgHbWDWF2BmlukSOMb8rqEzhNSVBbgndRPbhU.YqdDxI8US", "TipoU": 1, "CURP": "1"},
        ]
    )
    
def insertAlumno(target, connection, **kwargs):
    connection.execute(
        Alumno.__table__.insert(),
        [
            {"Boleta": "2022325410", "CURP": "1", "CorreoI": "1@gmail.com", "idPA": "ISC-2024","imagenCredencual":"IMG"},
        ]
    )
    
def insertperiodoETS(target, connection, **kwargs):
    connection.execute(
        periodoETS.__table__.insert(),
        [
            {"idPeriodo": 1, "Periodo": "2023", "Tipo": "O", "Fecha_Inicio": "2024-12-10", "Fecha_Fin": "2024-12-10"},
        ]
    )


event.listen(UnidadAcademica.__table__, 'after_create', insertUA)
event.listen(ProgramaAcademico.__table__, 'after_create', insertPA)
event.listen(EscuelaPrograma.__table__, 'after_create', insertEscuelaPrograma)
event.listen(Sexo.__table__, 'after_create', insertSexo)
event.listen(Persona.__table__, 'after_create', insertPersona)
event.listen(TipoUsuario.__table__, 'after_create', insertTipoU)
event.listen(Usuario.__table__, 'after_create', insertUsuario)
event.listen(Alumno.__table__, 'after_create', insertAlumno)
event.listen(periodoETS.__table__, 'after_create', insertperiodoETS)