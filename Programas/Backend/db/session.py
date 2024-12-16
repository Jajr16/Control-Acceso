from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from core.config import settings

DATABASE_URL = settings.DATABASE_URL

engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

def init_db():
    try:
        from db.models import UnidadAcademica, ProgramaAcademico, EscuelaPrograma
        Base.metadata.create_all(bind=engine)
        print("Conexión a la base de datos establecida")
    except Exception as e:
        print(f"Error al conectar a la base de datos: {e}")

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()