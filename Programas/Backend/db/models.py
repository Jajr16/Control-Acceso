from sqlalchemy import *
from sqlalchemy.orm import relationship
from db.session import Base


class UnidadAcademica(Base):
    __tablename__ = "UnidadAcademica"

    idEscuela = Column(Integer, primary_key=True, index=True)
    Nombre = Column(String(200), index=True, nullable=False)

    # Relación con EscuelaPrograma
    escuelaPrograma = relationship("EscuelaPrograma", back_populates="unidadAcademica")
    # Relación con Persona
    personas = relationship("Persona", back_populates="unidadAcademica")


class ProgramaAcademico(Base):
    __tablename__ = "ProgramaAcademico"

    idPA = Column(String(20), primary_key=True, index=True)
    Nombre = Column(String(150), nullable=False, index=True)
    Descripcion = Column(String(200), nullable=False)

    # Relación con EscuelaPrograma
    escuelaPrograma = relationship("EscuelaPrograma", back_populates="programaAcademico")
    alumnoCarrera = relationship("Alumno", back_populates="carrera")
    nombrePrograma = relationship("UnidadAprendizaje", back_populates="programaAcademico")

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
    unidadAcademica = relationship("UnidadAcademica", back_populates="escuelaPrograma")
    # Relación inversa con ProgramaAcademico
    programaAcademico = relationship("ProgramaAcademico", back_populates="escuelaPrograma")


class Sexo(Base):
    __tablename__ = "Sexo"

    idSexo = Column(Integer, primary_key=True, index=True)
    Nombre = Column(String(9), index=True, nullable=False)

    # Relación con Persona
    personas = relationship("Persona", back_populates="sexo")


class Persona(Base):
    __tablename__ = "Persona"

    CURP = Column(String(18), primary_key=True, index=True)
    Nombre = Column(String(100), nullable=False, index=True)
    ApellidoP = Column(String(150), nullable=False, index=True)
    ApellidoM = Column(String(150), nullable=False, index=True)
    Sexo = Column(Integer, ForeignKey("Sexo.idSexo"), nullable=False)
    idEscuela = Column(Integer, ForeignKey("UnidadAcademica.idEscuela"), nullable=False)

    sexo = relationship("Sexo", back_populates="personas")
    unidadAcademica = relationship("UnidadAcademica", back_populates="personas")
    personalAcademico = relationship("PersonalAcademico", back_populates="persona")
    personalSeguridad = relationship("PersonalSeguridad", back_populates="persona")
    Alumno = relationship("Alumno", back_populates="persona")
    Usuario = relationship("Usuario", back_populates="persona")
    
class TipoUsuario(Base):
    __tablename__ = "TipoUsuario"
    
    idTU = Column(Integer, primary_key=True, index=True)
    Tipo = Column(String(18), nullable=False, index=True)
    
    tipoNombre = relationship("Usuario", back_populates="tipoUser")
    
class Usuario(Base):
    __tablename__ = "Usuario"
    
    Usuario = Column(String(18), primary_key=True, index=True)
    Password = Column(String(50), nullable=False)
    TipoU = Column(Integer, ForeignKey("TipoUsuario.idTU"), nullable=False)
    CURP = Column(
        String(18),
        ForeignKey("Persona.CURP", ondelete="CASCADE", onupdate="CASCADE"),
        nullable=False,
        index=True,
    )
    
    tipoUser = relationship("TipoUsuario", back_populates="tipoNombre")
    persona = relationship("Persona", back_populates="Usuario")

class TipoPersonal(Base):
    __tablename__ = "tipoPersonal"

    tipoPA = Column(Integer, primary_key=True, index=True)
    Cargo = Column(String(100), nullable=False, index=True)

    # Relación con PersonalAcademico
    personalAcademicos = relationship("PersonalAcademico", back_populates="tipoPersonal")


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

    persona = relationship("Persona", back_populates="personalAcademico")
    tipoPersonal = relationship("TipoPersonal", back_populates="personalAcademicos")
    cargoPersonalAcademico = relationship("CargoDocente", back_populates="nombrePersona")
    ETSaAplicar = relationship("Aplica", back_populates="ETSAplicado")
    
class Cargo(Base):
    __tablename__ = "Cargo"
     
    idCargo = Column(Integer, primary_key=True, index=True)
    Cargo = Column(String(100), nullable=False, index=True)
    
    cargoDocente = relationship("CargoDocente", back_populates="nombreCargo")
       
class CargoDocente(Base):
    __tablename__ = "CargoDocente"
    
    RFC = Column(String(13), ForeignKey("PersonalAcademico.RFC", ondelete="CASCADE", onupdate="CASCADE"), nullable=False, primary_key=True)
    idCargo = Column(Integer, ForeignKey("Cargo.idCargo"), nullable=False, primary_key=True)
    
    nombrePersona = relationship("PersonalAcademico", back_populates="cargoPersonalAcademico")
    nombreCargo = relationship("Cargo", back_populates="cargoDocente")

class Turno(Base):
    __tablename__ = "Turno"
    
    idTurno = Column(Integer, primary_key=True, index=True)
    Nombre = Column(String(10), nullable=False, index=True)
    
    turnoPS = relationship("PersonalSeguridad", back_populates="turnoSeguridad")
    turnoETS = relationship("ETS", back_populates="turno")
    
class CargoPS(Base):
    __tablename__ = "CargoPS"
    
    idCargo = Column(Integer, primary_key=True, index=True)
    Nombre = Column(String(100), nullable=False, index=True)
    
    cargoPS = relationship("PersonalSeguridad", back_populates="cargoSeguridad")

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
    
    persona = relationship("Persona", back_populates="personalSeguridad")
    turnoSeguridad = relationship("Turno", back_populates="turnoPS")
    cargoSeguridad = relationship("CargoPS", back_populates="cargoPS")
    
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
    
    carrera = relationship("ProgramaAcademico", back_populates="alumnoCarrera")
    persona = relationship("Persona", back_populates="Alumno")
    nets = relationship("InscripcionETS", back_populates="alumno")
    
class periodoETS(Base):
    __tablename__ = "periodoETS"
    
    idPeriodo = Column(Integer, primary_key=True, index=True)
    Periodo = Column(String(20), nullable=False, index=False)
    Tipo = Column(CHAR, nullable=False)
    Fecha_Inicio = Column(Date, nullable=False)
    Fecha_Fin = Column(Date, nullable=False)
    
    nombrePeriodo = relationship("ETS", back_populates="periodo")
    
class UnidadAprendizaje(Base):
    __tablename__ = "UnidadAprendizaje"
    
    idUA = Column(String(20), primary_key=True, index=True)
    Nombre = Column(String(150), index=True, nullable=False)
    Descripcion = Column(String(200), nullable=False)
    idPA = Column(String(20), ForeignKey("ProgramaAcademico.idPA", ondelete="CASCADE", onupdate="CASCADE"), index=True, nullable=False)
    
    programaAcademico = relationship("ProgramaAcademico", back_populates="nombrePrograma")
    nombreUA = relationship("ETS", back_populates="UAETS")

class ETS(Base):
    __tablename__ = "ETS"
    
    idETS = Column(Integer, primary_key=True, index=True)
    idPeriodo = Column(Integer, ForeignKey("periodoETS.idPeriodo", onupdate="CASCADE", ondelete="CASCADE"), nullable=False, index=True)
    Turno = Column(Integer, ForeignKey("Turno.idTurno"), nullable=False)
    Fecha = Column(DateTime, nullable=False, index=True)
    Cupo = Column(Integer, nullable=False)
    idUA = Column(String(20), ForeignKey("UnidadAprendizaje.idUA", ondelete="CASCADE", onupdate="CASCADE"), nullable=False, index=True)
    Duracion = Column(Integer, nullable=False)
    
    periodo = relationship("periodoETS", back_populates="nombrePeriodo")
    turno = relationship("Turno", back_populates="turnoETS")
    UAETS = relationship("UnidadAprendizaje", back_populates="nombreUA")
    salon = relationship("SalonETS", back_populates="ets")
    alumno = relationship("InscripcionETS", back_populates="ets")
    AplicarETS = relationship("Aplica", back_populates="docenteaplicador")

class TipoSalon(Base):
    __tablename__ = "TipoSalon"
    
    idTS = Column(Integer, primary_key=True, index=True)
    Tipo = Column(String(11), nullable=False, index=True)
    
    nts = relationship("Salon", back_populates="salonType")
    
class Salon(Base):
    __tablename__ = "Salon"
    
    numSalon = Column(Integer, primary_key=True, index=True)
    Edificio = Column(Integer, nullable=False, index=True)
    Piso = Column(Integer, nullable=False, index=True)
    tipoSalon = Column(Integer, ForeignKey("TipoSalon.idTS"), nullable=False, index=True)
    
    salonType = relationship("TipoSalon", back_populates="nts")
    ets = relationship("SalonETS", back_populates="salon")
    
class SalonETS(Base):
    __tablename__ = "SalonETS"
    
    numSalon = Column(Integer, ForeignKey("Salon.numSalon", ondelete="CASCADE", onupdate="CASCADE"), primary_key=True, index=True)
    idETS = Column(Integer, ForeignKey("ETS.idETS", ondelete="CASCADE", onupdate="CASCADE"), primary_key=True, index=True)
    
    salon = relationship("Salon", back_populates="ets")
    ets = relationship("ETS", back_populates="salon")
    
class InscripcionETS(Base):
    __tablename__ = "InscripcionETS"
    
    Boleta = Column(String(15), ForeignKey("Alumno.Boleta", ondelete="CASCADE", onupdate="CASCADE"), primary_key=True, index=True)
    idETS = Column(Integer, ForeignKey("ETS.idETS", ondelete="CASCADE", onupdate="CASCADE"), primary_key=True, index=True)
    
    ets = relationship("ETS", back_populates="alumno")
    alumno = relationship("Alumno", back_populates="nets")
    insETSAlumno = relationship("AsistenciaInscripcion", back_populates="alumnoAsistencia", primaryjoin="and_(InscripcionETS.Boleta==AsistenciaInscripcion.InscripcionETSBoleta, InscripcionETS.idETS==AsistenciaInscripcion.InscripcionETSIdETS)")
    insETSETS = relationship("AsistenciaInscripcion", back_populates="ETSAsistencia", primaryjoin="and_(InscripcionETS.Boleta==AsistenciaInscripcion.InscripcionETSBoleta, InscripcionETS.idETS==AsistenciaInscripcion.InscripcionETSIdETS)")
    
class Aplica(Base):
    __tablename__ = "Aplica"
    
    idETS = Column(Integer, ForeignKey("ETS.idETS", ondelete="CASCADE", onupdate="CASCADE"), primary_key=True, index=True)
    DocenteRFC = Column(String(13), ForeignKey("PersonalAcademico.RFC", ondelete="CASCADE", onupdate="CASCADE"), primary_key=True, index=True)
    Titular = Column(Boolean, nullable=False)
    
    docenteaplicador = relationship("ETS", back_populates="AplicarETS")
    ETSAplicado = relationship("PersonalAcademico", back_populates="ETSaAplicar")
    
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
        primaryjoin="and_(AsistenciaInscripcion.InscripcionETSBoleta == InscripcionETS.Boleta, AsistenciaInscripcion.InscripcionETSIdETS == InscripcionETS.idETS)"
    )

    ETSAsistencia = relationship(
        "InscripcionETS",
        back_populates="insETSETS",
        primaryjoin="and_(AsistenciaInscripcion.InscripcionETSBoleta == InscripcionETS.Boleta, AsistenciaInscripcion.InscripcionETSIdETS == InscripcionETS.idETS)"
    )
