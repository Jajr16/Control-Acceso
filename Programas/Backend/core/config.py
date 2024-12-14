from urllib.parse import quote_plus
import os

class Settings:
    DATABASE_URL: str = os.getenv(
        "DATABASE_URL",
        f"postgresql://postgres:{quote_plus('1234')}@localhost/PruebaTT"
    )

settings = Settings()
