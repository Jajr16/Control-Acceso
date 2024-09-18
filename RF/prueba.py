import cv2

# Capturar video desde la cámara
cap = cv2.VideoCapture(0)

while True:
    # Leer cuadro a cuadro
    ret, frame = cap.read()

    if not ret:
        print("No se puede recibir frame (fin de stream o error).")
        break

    # Mostrar el frame en una ventana
    cv2.imshow('Frame de la cámara', frame)

    # Salir con 'q'
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# Liberar los recursos
cap.release()
cv2.destroyAllWindows()


# Convertir el frame a escala de grises (sin usar librerías como cv2)
from PIL import Image
import numpy as np

def frame_a_grises(frame):
    # Convertir el frame a una imagen PIL
    imagen = Image.fromarray(frame)
    # Convertir la imagen a escala de grises
    imagen_grises = imagen.convert("L")
    # Convertir la imagen de grises en un array numpy para procesar manualmente
    pixeles = np.array(imagen_grises)
    return pixeles

# Ejemplo de cómo podrías procesar el frame
while True:
    ret, frame = cap.read()
    
    if not ret:
        break
    
    # Procesar el frame a escala de grises
    pixeles_grises = frame_a_grises(frame)
    
    # Aquí podrías aplicar tus algoritmos de reconocimiento facial manual
    
    # Mostrar el frame original (sin procesamiento facial)
    cv2.imshow('Frame', frame)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break
