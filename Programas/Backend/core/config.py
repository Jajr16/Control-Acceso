from urllib.parse import quote_plus
import os

class Settings:
    DATABASE_URL: str = os.getenv(
        "DATABASE_URL",
        f"postgresql://postgres:{quote_plus('cosa12')}@localhost/PruebaTT"
    )

settings = Settings()
