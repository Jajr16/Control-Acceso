from deepface import DeepFace


    
img1_path = r"D:\Repositorio\Control-Acceso\Programas\Backend\known_images\2022404040.jpg"
img2_path = r"D:\Repositorio\Control-Acceso\Programas\Backend\uploaded_images\2022404040.jpg"


result = DeepFace.verify(img1_path, img2_path, model_name="Facenet", detector_backend="ssd")