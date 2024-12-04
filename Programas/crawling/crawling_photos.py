from selenium import webdriver
from selenium.webdriver.common.by import By
from PIL import Image, ImageTk
import tkinter as tk
from tkinter import simpledialog
import time
from pymongo import MongoClient
import gridfs
import requests

client = MongoClient('mongodb://localhost:27017/')
db = client['alumnos_bd']
fs = gridfs.GridFS(db)

def optionsCharge():
    chrome_options = webdriver.ChromeOptions()
    chrome_options.add_argument('--disable-blink-features=AutomationControlled')
    chrome_options.add_argument('--ignore-ssl-errors=yes')
    chrome_options.add_argument("user-agent=Mozilla/5.0 (Windows Phone 10.0; Android 4.2.1; Microsoft; Lumia 640 XL LTE) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.6045.160 Mobile Safari/537.36 Edge/12.10166")
    chrome_options.add_argument('--enable-automation')
    chrome_options.add_argument('--disable-popup-blocking')
    driver = webdriver.Chrome(options=chrome_options)
    return driver

def getElement(element, driver):
    return driver.find_element(By.CSS_SELECTOR, element)

def getElements(element, driver):
    return driver.find_elements(By.CSS_SELECTOR, element)

def show_captcha(image_path):
    # Crear ventana
    root = tk.Tk()
    root.title("Captcha")

    # Cargar imagen
    img = Image.open(image_path)
    img = img.resize((300, 150))  # Ajustar tamaño si es necesario
    img_tk = ImageTk.PhotoImage(img)

    # Mostrar imagen
    panel = tk.Label(root, image=img_tk)
    panel.pack()

    # Pedir texto del captcha
    captcha_text = simpledialog.askstring("Captcha", "Ingresa el texto del captcha:")
    
    root.destroy()
    return captcha_text

driver = optionsCharge()
driver.get('http://127.0.0.1:8000/Fotos')  # Cambia esto a la URL correcta
driver.implicitly_wait(10)

try:
    # Extraer los elementos de la tabla
    filas = getElements('tbody tr', driver)  # Selecciona todas las filas del cuerpo de la tabla

    for fila in filas:
        # Extraer la URL de la imagen
        img_element = fila.find_element(By.CSS_SELECTOR, "img.img_php")
        img_url = img_element.get_attribute("src")

        # Extraer los datos del alumno
        boleta = fila.find_element(By.CSS_SELECTOR, ".boletas_almn").text
        nombre = fila.find_element(By.CSS_SELECTOR, ".name_almn").text

        # Descargar la imagen
        img_response = requests.get(img_url)

        # Guardar la imagen en MongoDB usando GridFS
        img_file_id = fs.put(img_response.content, filename=f"{boleta}_foto.jpg")

        # Crear un documento con los datos del alumno y la referencia a la imagen
        alumno_data = {
            "boleta": boleta,
            "nombre": nombre,
            "imagen_id": img_file_id  # Guardamos el ID de la imagen en GridFS
        }

        # Insertar el documento en la colección 'alumnos'
        db.alumnos.insert_one(alumno_data)

        print(f"Datos del alumno {nombre} y su imagen subidos a MongoDB.")

    time.sleep(10)

finally:
    driver.quit()
