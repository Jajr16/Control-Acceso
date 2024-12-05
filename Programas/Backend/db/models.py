from sqlalchemy import *
from db.session import Base
from sqlalchemy.orm import relationship

class UnidadAcademica(Base):
    __tablename__ = "UnidadAcademica"

    idEscuela = Column(Integer, primary_key=True, index=True)
    Nombre = Column(String(200), index=True, nullable=False)

    escuelaPrograma = relationship("EscuelaPrograma", backref="unidadAcademica")
    personaEscuela = relationship("Persona", backref="unidadAcademica")

class ProgramaAcademico(Base):
    __tablename__ = "ProgramaAcademico"
    
    idPA = Column(String(20), primary_key=True, index=True)
    Nombre = Column(String(150), nullable=False, index=True)
    Descripcion = Column(String(200), nullable=False)
    
    escuelaPrograma = relationship("EscuelaPrograma", backref="programaAcademico")
    
class EscuelaPrograma(Base):
    __tablename__ = "EscuelaPrograma"    
    
    idEscuela = Column(Integer, ForeignKey("UnidadAcademica.idEscuela", ondelete="CASCADE", onupdate="CASCADE"), primary_key=True, index=True)
    idPA = Column(String(20), ForeignKey("ProgramaAcademico.idPA", ondelete="CASCADE", onupdate="CASCADE"), primary_key=True, index=True)
    
    unidadAcademica = relationship("UnidadAcademica", backref="escuelaPrograma")
    programaAcademico = relationship("ProgramaAcademico", backref="escuelaPrograma")
    
class Sexo(Base):
    __tablename__ = "Sexo"
    
    idSexo = Column(Integer, primary_key=True, index=True)
    Nombre = Column(String(9), index=True, nullable=False)
    
class Persona(Base):
    __tablename__ = "Persona"
    
    CURP = Column(String(18), primary_key=True, index=True)
    Nombre = Column(String(100), nullable=False, index=True)
    ApellidoP = Column(String(150), nullable=False, index=True)
    ApellidoM = Column(String(150), nullable=False, index=True)
    Sexo = Column(Integer, ForeignKey("Sexo.idSexo"), nullable=False)
    idEscuela = Column(Integer, ForeignKey("UnidadAcademica.idEscuela"), nullable=False)
    
    sex = relationship("Sexo", backref="persona")
    UnidadAcademica = relationship("UnidadAcademica", backref="persona")
    
class PersonalAcademico(Base):
    __tablename__ = "PersonalAcademico"
    
    CURP = Column(String(18), ForeignKey("Persona.CURP", ondelete="CASCADE", onupdate="CASCADE"), nullable=False, index=True)
    