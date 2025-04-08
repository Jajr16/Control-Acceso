from flask import Flask, request, jsonify
import io
from PIL import Image
import numpy as np
import matplotlib
matplotlib.use('Agg')  # Configurar backend Agg
import matplotlib.pyplot as plt

app = Flask(__name__)

def mostrar_imagen(imagen_bytes, boleta):
    """Función para mostrar la imagen (aislada del hilo principal de Flask)."""
    imagen_pil = Image.open(io.BytesIO(imagen_bytes))
    plt.imshow(np.array(imagen_pil))
    plt.title(f'Imagen recibida (Boleta: {boleta})')
    plt.show()

@app.route('/procesar_imagen', methods=['POST'])
def procesar_imagen():
    if 'image' not in request.files or 'boleta' not in request.form:
        return jsonify({'error': 'Imagen o boleta no proporcionada'}), 400

    imagen = request.files['image']
    boleta = request.form['boleta']

    try:
        # Mostrar la imagen (usar la función aislada)
        # mostrar_imagen(imagen.read(), boleta)

        # Simulación de la precisión y los detalles (reemplaza con tu lógica real)
        precision = 0.95
        

        return jsonify({'precision': precision})

    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)