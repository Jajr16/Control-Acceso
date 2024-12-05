from urllib.parse import quote_plus
import os

class Settings:
    DATABASE_URL: str = os.getenv(
        "DATABASE_URL",
        f"postgresql://postgres:{quote_plus('n0m3l0')}@localhost/PruebaTT"
    )

settings = Settings()
