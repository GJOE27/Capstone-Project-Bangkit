import os
from flask import Flask, request, jsonify
import tensorflow as tf
from tensorflow import keras
from PIL import Image
import numpy as np

class_mapping = {
    0: "ayam_goreng",
    1: "ayam_pop",
    2: "daging_rendang",
    3: "dendeng_batokok",
    4: "gulai_ikan",
    5: "gulai_tambusu",
    6: "gulai_tunjang",
    7: "telur_balado",
    8: "telur_dadar"
}

app = Flask(__name__)
model = keras.models.load_model("savedModel1.h5")

@app.route('/upload', methods=['POST'])
def upload():
    file = request.files['file']
    image = Image.open(file)
    image = image.resize((224, 224)) 
    prediction = model.predict(np.expand_dims(image, axis=0))
    prediction = tf.argmax(prediction, axis=1).numpy().tolist()
    class_names = class_mapping.get(prediction[0], "Unknown")
    result = {
        'Makanan anda adalah': class_names ,
    }
    return jsonify(result)

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=int(os.environ.get("PORT", 8080)))

