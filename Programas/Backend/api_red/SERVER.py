from flask import Flask, request, jsonify
import os
from werkzeug.utils import secure_filename

app = Flask(__name__)

UPLOAD_FOLDER = r'D:\Envios'  # Carpeta donde se guardarán las imágenes
os.makedirs(UPLOAD_FOLDER, exist_ok=True)

@app.route('/upload/', methods=['POST'])
def upload_image():
    # Verifica si el campo 'image' está presente
    if 'image' not in request.files:
        return jsonify({"error": "No image found"}), 400
    
    # Guarda la imagen en el servidor
    image = request.files['image']
    image_path = os.path.join(UPLOAD_FOLDER, image.filename)
    image.save(image_path)
    
    # Asigna directamente valores de placeholder a 'status' y 'detalles'
    status = "default de momento"
    detalles = "default de momento"
    
    # Devuelve solo 'status' y 'detalles'
    return jsonify({
        "status": status,
        "detalles": detalles
    }), 200

if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=5000)
