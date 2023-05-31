import requests

API_URL = "https://app-66o65w2gbq-et.a.run.app"  # Replace with your server URL

# Assuming you have the ID token available
id_token = "your id token"

headers = {
    "Authorization": id_token
}

# Send POST request to log out the user
response = requests.post(f"{API_URL}/logout", headers=headers)

# Check the response status code
if response.status_code == 200:
    print("User logged out successfully")
else:
    print("Logout error:", response.json().get('error'))
