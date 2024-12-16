from flask import Flask, request, jsonify
from deepface import DeepFace
import os

app = Flask(__name__)

# Folder to save uploaded images
UPLOAD_FOLDER = "uploaded_images"

# Folder with known images
DATABASE_FOLDER = "known_images"
os.makedirs(UPLOAD_FOLDER, exist_ok=True)

@app.route("/upload", methods=["POST"])
def upload_image():
    
    # Verify if the image field is present
    if "image" not in request.files:
        return jsonify({"error": "No image provided"}), 400

    # Save the image on the server

    image = request.files["image"]
    
    # Obtain the student ID
    
    student_id = image.filename
    
    saved_path = os.path.abspath(os.path.join(UPLOAD_FOLDER, student_id))  # Absolute path

    # Save the image uploaded via Mobile app
    image.save(saved_path)
    
    # Path to the reference image
    reference_image_path = os.path.abspath(os.path.join(DATABASE_FOLDER, student_id))  # Absolute path

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
        return {"match": result["verified"], "distance": result["distance"], "threshold": result["threshold"]}
    except Exception as e:
        return {"error": str(e)}

if __name__ == "__main__":
    app.run(port=4000, debug=True)