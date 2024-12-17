from deepface import DeepFace


    
img1_path = r"E:\\Escritorio\\Semestres ESCOM\\Semestre 7 - ESCOM\\Tt\\Programas\\Backend\\known_images\\2022404040.jpg"
img2_path = r"E:\\Escritorio\\Semestres ESCOM\\Semestre 7 - ESCOM\\Tt\\Programas\\Backend\\uploaded_images\\2022404040.jpg"

result = DeepFace.verify(img1_path, img2_path, model_name="Facenet", detector_backend="ssd")