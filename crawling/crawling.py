from selenium import webdriver
from selenium.webdriver.common.by import By
from PIL import Image, ImageTk
import tkinter as tk
from tkinter import simpledialog
import requests
from io import BytesIO

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
driver.get('https://www.saes.escom.ipn.mx/default.aspx?AspxAutoDetectCookieSupport=1')
driver.implicitly_wait(10)

try:
    captcha_element = getElement('#c_default_ctl00_leftcolumn_loginuser_logincaptcha_CaptchaImage', driver)
    driver.get_screenshot_as_file('full_screenshot.png')
    
    if captcha_element:
        location = captcha_element.location
        size = captcha_element.size
        
        left = location['x'] + 1
        top = location['y']
        right = left + size['width']
        bottom = top + size['height']
        
        screenshot = Image.open('full_screenshot.png')
        captcha_image = screenshot.crop((left, top, right, bottom))
        captcha_image.save('captcha_image.png')

        # Mostrar la imagen al usuario y obtener el texto
        captcha_text = show_captcha('captcha_image.png')

        
        textEntrys = getElements('.textEntry', driver)
        passEntry = getElement('.passwordEntry', driver)
        
        if (textEntrys and passEntry):
            textEntrys[1].send_keys(captcha_text)
            
            boleta = int(input('Ingresa tu número de boleta: '))
            passw = input('Ingresa tu contraseña: ')
            
            textEntrys[0].send_keys(boleta)
            print(f"Boleta {boleta} ingresada")
            passEntry.send_keys(passw)
            print(f"Contraseña {passw} ingresada")

finally:
    driver.quit()
