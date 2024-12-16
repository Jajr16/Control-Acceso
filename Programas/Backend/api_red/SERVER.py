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
    image_path = os.path.join(UPLOAD_FOLDER, secure_filename(image.filename))
    image.save(image_path)
    
    # Recupera la boleta enviada desde el cliente
    boleta = request.form.get('boleta')  # Lee la boleta como string del form-data
    if not boleta:
        return jsonify({"error": "No boleta provided"}), 400
    
    # Imprime la boleta en consola
    print(f"Boleta recibida: {boleta}")
    
    # Respuesta del servidor
    status = "Imagen y boleta recibidas correctamente"
    detalles = f"Boleta recibida: {boleta}"
    
    return jsonify({
        "status": status,
        "detalles": detalles
    }), 200

if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=5000)

