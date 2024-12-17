from flask import Flask, request, jsonify
from deepface import DeepFace
import os
from werkzeug.utils import secure_filename


app = Flask(__name__)

# Folder to save uploaded images
UPLOAD_FOLDER = "Backend\\uploaded_images"
os.makedirs(UPLOAD_FOLDER, exist_ok=True)

# Folder with known images
DATABASE_FOLDER = "Backend\\known_images"
os.makedirs(DATABASE_FOLDER, exist_ok=True)

@app.route("/upload/", methods=["POST"])
def upload_image():
    
    # Verify if the image field is present
    if "image" not in request.files:
        return jsonify({"error": "No image provided"}), 400

    # Save the image on the server

    image = request.files["image"]
    
    # Retrieve the student ID from the client
    
    boleta = request.form.get("boleta")  # Lee la boleta como string del form-data
    if not boleta:
        return jsonify({"error": "No boleta provided"}), 400
    
    # Print the student ID
    print(f"Boleta recibida: {boleta}")
    
    # Strip any extraneous whitespace or newline characters from boleta
    boleta = boleta.strip()
    
    # Update the student ID file
    
    final_boleta = f"{boleta}.jpg"
    
    # Save the image on the upload folder
    
    saved_path = os.path.abspath(os.path.join(UPLOAD_FOLDER, final_boleta)) # Absolute path

    print(saved_path)



    # Save the image uploaded via Mobile app
    image.save(saved_path)
    
    print("Image saved")
    
    # Path to the reference image
    reference_image_path = os.path.abspath(os.path.join(DATABASE_FOLDER, final_boleta))  # Absolute path

    # Call face recognition internally
    result = recognize_faces(saved_path, reference_image_path)
    
    return jsonify(result)

@app.route("/recognize", methods=["POST"])
def recognize_faces_api():
    data = request.get_json()
    img1_path = data.get("img1")
    img2_path = data.get("img2")

    if not img1_path or not img2_path:
        return jsonify({"error": "Image paths are required"}), 400

    return jsonify(recognize_faces(img1_path, img2_path))

def recognize_faces(img1_path, img2_path):
    try:
        result = DeepFace.verify(img1_path=img1_path, img2_path=img2_path, model_name="Facenet", detector_backend="ssd", distance_metric="cosine", threshold=0.4)
        #return{"status": "hola","detalles": "XD"}
        return {"status": result["verified"], "detalles": result["distance"]}
    except Exception as e:
        return {"error": str(e)}

if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=5000)