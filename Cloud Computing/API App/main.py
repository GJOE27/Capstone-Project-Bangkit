import os
from flask import Flask, request, jsonify
from google.cloud import storage
import firebase_admin
from firebase_admin import credentials
from firebase_admin import auth
from firebase_admin import firestore
import requests

# Initialize Flask app
app = Flask(__name__)

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

# API endpoint to register a user
@app.route("/register", methods=["POST"])
def register():
    email = request.json.get('email')
    password = request.json.get('password')

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
            'display_name': '',
            'photo_url': ''
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

        # Get user details
        user_data = {
            'uid': user.uid,
            'email': user.email,
            'display_name': user.display_name,
            'photo_url': user.photo_url
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

        # Upload image to Cloud Storage with user UID folder
        folder_name = uid 
        destination_blob_name = f"{folder_name}/{file.filename}"
        bucket = storage_client.bucket(bucket_name)
        blob = bucket.blob(destination_blob_name)
        blob.upload_from_file(file)

        return jsonify({"message": "Image uploaded successfully"})
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
        image_urls = [blob.public_url for blob in blobs]

        return jsonify({"image_urls": image_urls})
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=int(os.environ.get("PORT", 8080)))
