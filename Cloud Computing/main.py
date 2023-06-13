import os
from flask import Flask, request, jsonify
from google.cloud import storage
import firebase_admin
from firebase_admin import credentials
from firebase_admin import auth
from firebase_admin import firestore
import requests
import datetime

import tensorflow as tf
from tensorflow import keras
from PIL import Image
import numpy as np


# Initialize Flask app
app = Flask(__name__)

# Initialize Firebase Admin SDK
cred = credentials.Certificate('capstone-nutrisee-firebase-adminsdk-kwk52-207cd7de84.json')
default_app = firebase_admin.initialize_app(cred)

FIREBASE_API_KEY = '-'

# Initialize Firestore
db = firestore.client()

# Initialize Cloud Storage client
storage_client = storage.Client()
bucket_name = "capstone-nutrisee.appspot.com"

# create id token from custom token
def create_id_token_from_custom_token(uid):
    try:
        custom_token = auth.create_custom_token(uid).decode('utf-8')

        data = {
            'token': custom_token,
            'returnSecureToken': True
        }

        response = requests.post(
            f'https://www.googleapis.com/identitytoolkit/v3/relyingparty/verifyCustomToken?key={FIREBASE_API_KEY}',
            json=data
        )

        response_data = response.json()
        id_token = response_data.get('idToken')
        refresh_token = response_data.get('refreshToken')

        return id_token, refresh_token

    except Exception as e:
        print('Error creating token:', str(e))
        return None, None  

# create refresh token from id token
def refresh_id_token(refresh_token):
    try:
        data = {
            'grant_type': 'refresh_token',
            'refresh_token': refresh_token
        }

        response = requests.post(
            f'https://securetoken.googleapis.com/v1/token?key={FIREBASE_API_KEY}',
            data=data
        )

        response_data = response.json()
        new_id_token = response_data.get('id_token')

        return new_id_token

    except Exception as e:
        print('Error refreshing ID token:', str(e))
        return None

# Load model and class mapping
model = keras.models.load_model("savedModel1.h5")

class_mapping = {
    0: {"name": "ayam_goreng", "kalori": 358.8},
    1: {"name": "ayam_pop", "kalori": 265},
    2: {"name": "daging_rendang", "kalori": 285.5},
    3: {"name": "dendeng_batokok", "kalori": 338},
    4: {"name": "gulai_ikan", "kalori": 218.8},
    5: {"name": "gulai_tambusu", "kalori": 204},
    6: {"name": "gulai_tunjang", "kalori": 251},
    7: {"name": "telur_balado", "kalori": 142},
    8: {"name": "telur_dadar", "kalori": 188}
}


# API endpoint to register a user
@app.route("/register", methods=["POST"])
def register():
    email = request.json.get('email')
    password = request.json.get('password')
    display_name = request.json.get('display_name')
    photo_url = request.json.get('photo_url')

    if not email or not password:
        return jsonify({"error": "Email and password are required."}), 400

    try:
        user = auth.create_user(
            email=email,
            password=password
        )

        # Create the initial user document in Firestore
        user_doc = db.collection('users').document(user.uid)
        user_doc.set({
            'display_name': display_name,
            'photo_url': photo_url
        })

        return jsonify({"message": "User registered successfully"})
    except auth.EmailAlreadyExistsError:
        return jsonify({"error": "Email is already registered."}), 409
    except Exception as e:
        return jsonify({"error": str(e)}), 500


# API endpoint to login a user
@app.route("/login", methods=["POST"])
def login():
    email = request.json.get('email')
    password = request.json.get('password')

    try:
        user = auth.get_user_by_email(email)
        uid = user.uid

        # Create the ID token
        id_token, refresh_token = create_id_token_from_custom_token(uid)

        # Get the user document from Firestore
        user_ref = db.collection('users').document(uid)
        user_doc = user_ref.get()

        # Get user details
        user_data = {
            'uid': user.uid,
            'email': user.email,
            'display_name': user_doc.get('display_name'),
            'photo_url': user_doc.get('photo_url')
        }

        response_data = {
            'message': 'User logged in successfully',
            'user': user_data,
            'id_token': id_token,
            'refresh_token': refresh_token
        }

        return jsonify(response_data)

    except Exception as e:
        return jsonify({'error': str(e)})


# API endpoint to logout a user
@app.route("/logout", methods=["POST"])
def logout():
    try:
        id_token = request.headers.get('Authorization')
        if id_token is None:
            return jsonify({"error": "ID token not provided"})

        # Revoke the ID token
        try:
            auth.revoke_refresh_tokens(id_token)
            return jsonify({"message": "User logged out successfully"})
        except auth.InvalidIdTokenError:
            return jsonify({"error": "Invalid ID token. Revocation failed."})
        except Exception as e:
            return jsonify({"error": "Error revoking refresh tokens."})
    
    except Exception as e:
        return jsonify({"error": str(e)})


# API endpoint to update user profile
@app.route("/update_profile", methods=["POST"])
def update_profile():
    user_uid = request.json.get('user_uid')
    display_name = request.json.get('display_name')
    photo_url = request.json.get('photo_url')

    if not user_uid or not display_name or not photo_url:
        return jsonify({"error": "user_uid, display_name, and photo_url are required."}), 400

    try:
        id_token = request.headers.get('Authorization')
        if id_token is None:
            return jsonify({"error": "ID token not provided"})

        # Verify the ID token and handle expiration
        try:
            decoded_token = auth.verify_id_token(id_token)
            uid = decoded_token['uid']
        except auth.ExpiredIdTokenError:
            refresh_token = decoded_token.get('refresh_token')
            if refresh_token:
                # Refresh the ID token using the refresh token
                new_id_token = refresh_id_token(refresh_token)

                if new_id_token:
                    # Update the ID token in the request headers for future requests
                    request.headers['Authorization'] = new_id_token
                    decoded_token = auth.verify_id_token(new_id_token)
                    uid = decoded_token['uid']
                else:
                    return jsonify({"error": "Failed to refresh ID token"})

            else:
                return jsonify({"error": "Expired ID token and no refresh token available"})

        user_ref = db.collection('users').document(user_uid)

        # Update the display name and photo URL fields
        user_ref.update({
            'display_name': display_name,
            'photo_url': photo_url
        })

        return jsonify({"message": "Profile updated successfully"})

    except Exception as e:
        return jsonify({"error": str(e)}), 500


# API endpoint to upload image
@app.route("/upload", methods=["POST"])
def upload():
    if 'photo' not in request.files:
        return jsonify({"error": "no file"})

    file = request.files['photo']
    if file.filename == '':
        return jsonify({"error": "no filename"})

    try:
        id_token = request.headers.get('Authorization')
        if id_token is None:
            return jsonify({"error": "ID token not provided"})

        # Verify the ID token and handle expiration
        try:
            decoded_token = auth.verify_id_token(id_token)
            uid = decoded_token['uid']
        except auth.ExpiredIdTokenError:
            refresh_token = decoded_token.get('refresh_token')
            if refresh_token:
                # Refresh the ID token using the refresh token
                new_id_token = refresh_id_token(refresh_token)

                if new_id_token:
                    # Update the ID token in the request headers for future requests
                    request.headers['Authorization'] = new_id_token
                    decoded_token = auth.verify_id_token(new_id_token)
                    uid = decoded_token['uid']
                else:
                    return jsonify({"error": "Failed to refresh ID token"})

            else:
                return jsonify({"error": "Expired ID token and no refresh token available"})

        # Perform image processing
        image = Image.open(file)
        image = image.resize((224, 224)) 
        prediction = model.predict(np.expand_dims(image, axis=0))
        prediction = tf.argmax(prediction, axis=1).numpy().tolist()
        class_info = class_mapping.get(prediction[0], {"name": "Unknown", "kalori": "Unknown"})
        name = class_info["name"]
        kalori = class_info["kalori"]

        # Rename the image file to the predicted class name
        current_time = datetime.datetime.now().strftime("%Y%m%d%H%M%S")
        filename = f"{name}_{current_time}.jpg"

        # Upload image to Cloud Storage with user UID folder and updated metadata
        folder_name = uid 
        destination_blob_name = f"{folder_name}/{filename}"
        bucket = storage_client.bucket(bucket_name)
        blob = bucket.blob(destination_blob_name)

        # Set the content type of the blob to 'image/jpeg'
        blob.content_type = 'image/jpeg'

        # Set metadata properties
        metadata = {
            'kalori': kalori
        }
        blob.metadata = metadata

        # Upload the image file
        file.seek(0)
        blob.upload_from_file(file)

        response_data = {
            "message": "Image uploaded successfully",
            "name": name,
            "kalori": kalori
        }

        return jsonify(response_data)

    except Exception as e:
        return jsonify({"error": str(e)}), 500



@app.route("/list_images", methods=["GET"])
def list_images():
    try:
        id_token = request.headers.get('Authorization')
        if id_token is None:
            return jsonify({"error": "ID token not provided"})

        # Verify the ID token and handle expiration
        try:
            decoded_token = auth.verify_id_token(id_token)
            uid = decoded_token['uid']
        except auth.ExpiredIdTokenError:
            refresh_token = decoded_token.get('refresh_token')
            if refresh_token:
                # Refresh the ID token using the refresh token
                new_id_token = refresh_id_token(refresh_token)

                if new_id_token:
                    # Update the ID token in the request headers for future requests
                    request.headers['Authorization'] = new_id_token
                    decoded_token = auth.verify_id_token(new_id_token)
                    uid = decoded_token['uid']
                else:
                    return jsonify({"error": "Failed to refresh ID token"})

            else:
                return jsonify({"error": "Expired ID token and no refresh token available"})

        # Fetch the list of images from Cloud Storage with user UID folder
        folder_name = uid
        bucket = storage_client.bucket(bucket_name)
        blobs = bucket.list_blobs(prefix=folder_name)
        image_data = []

        for blob in blobs:
            image_name = blob.name.split("/")[-1].split(".")[0]  # Extract the image name without the UID and extension
            image_url = blob.public_url
            created_at = blob.time_created

            # Format the created_at timestamp as a string
            created_at_str = created_at.strftime("%Y-%m-%d %H:%M:%S")

            # Create the image object
            image_object = {
                "name": image_name,
                "url": image_url,
                "created_at": created_at_str
            }

            # Append the image object to the image data list
            image_data.append(image_object)

        return jsonify({"images": image_data})
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=int(os.environ.get("PORT", 8080)))
