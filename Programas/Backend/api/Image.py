from fastapi import APIRouter, HTTPException
from fastapi.responses import FileResponse
import requests
import fitz  # PyMuPDF
import tempfile
import os
from selenium import webdriver
from selenium.webdriver.common.by import By

# Configuración del driver Selenium
def optionsCharge():
    chrome_options = webdriver.ChromeOptions()
    chrome_options.add_argument('--disable-blink-features=AutomationControlled')
    chrome_options.add_argument('--ignore-ssl-errors=yes')
    chrome_options.add_argument(
        "user-agent=Mozilla/5.0 (Windows Phone 10.0; Android 4.2.1; Microsoft; Lumia 640 XL LTE) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.6045.160 Mobile Safari/537.36 Edge/12.10166"
    )
    chrome_options.add_argument('--enable-automation')
    chrome_options.add_argument('--disable-popup-blocking')
    chrome_options.add_argument('--headless')  # Para no mostrar la UI del navegador
    driver = webdriver.Chrome(options=chrome_options)
    return driver

# Obtener elementos con Selenium
def getElement(element, driver):
    return driver.find_element(By.CSS_SELECTOR, element)

router = APIRouter(prefix="/ImagePDF", tags=["ImagePDF"])

@router.get("/", response_class=FileResponse)
def getCalendar():
    """
    Descargar un PDF desde un enlace en la página y convertirlo en una imagen.
    Devuelve la imagen generada directamente.
    """
    driver = optionsCharge()
    
    headers = {
    "User-Agent": "Mozilla/5.0 (Windows Phone 10.0; Android 4.2.1; Microsoft; Lumia 640 XL LTE) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.6045.160 Mobile Safari/537.36 Edge/12.10166",
    "Accept": "application/pdf",
    "Referer": "https://www.ipn.mx/calendario-academico.html"
}
    
    try:
        # Acceder al sitio web con Selenium
        driver.get('https://www.ipn.mx/calendario-academico.html')

        # Obtener el enlace del PDF
        link_calendar = getElement('.col-12.col-md-4.offset-md-1 a', driver)  # Seleccionar el <a> dentro del div
        pdf_url = link_calendar.get_attribute('href')
        driver.quit()

        if not pdf_url.endswith(".pdf"):
            raise HTTPException(status_code=400, detail="El enlace no apunta a un archivo PDF.")

        response = requests.get(pdf_url, headers=headers, verify=False)
        if response.status_code != 200:
            raise HTTPException(status_code=400, detail="Error al descargar el PDF.")

        # Guardar temporalmente el PDF
        with tempfile.NamedTemporaryFile(delete=False, suffix=".pdf") as temp_pdf:
            temp_pdf.write(response.content)
            temp_pdf_path = temp_pdf.name

        # Convertir la primera página del PDF en imagen usando PyMuPDF
        try:
            pdf_document = fitz.open(temp_pdf_path)
            page = pdf_document[0]  # Primera página del PDF
            pix = page.get_pixmap(dpi=300)  # Renderizar como imagen
            os.makedirs(os.path.dirname(temp_pdf_path), exist_ok=True)
            temp_img_path = temp_pdf_path.replace(".pdf", ".png")
            pix.save(temp_img_path)  # Guardar como PNG

            # Devolver la imagen como respuesta
            return FileResponse(temp_img_path, media_type="image/png", filename="calendario.png")

        except Exception as e:
            raise HTTPException(status_code=500, detail=f"Error al procesar el PDF: {str(e)}")

    except requests.exceptions.Timeout:
        print("La solicitud ha superado el tiempo de espera.")
        raise HTTPException(status_code=500, detail="Tiempo de espera agotado para la solicitud.")
    except requests.exceptions.TooManyRedirects:
        print("Demasiados redireccionamientos.")
        raise HTTPException(status_code=500, detail="Demasiados redireccionamientos.")
    except requests.exceptions.RequestException as e:
        print(f"Error al realizar la solicitud HTTP: {e}")
        raise HTTPException(status_code=500, detail=f"Error en la solicitud HTTP: {str(e)}")