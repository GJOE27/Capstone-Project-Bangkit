import requests

# Specify the API endpoint
api_url = 'https://predict-66o65w2gbq-et.a.run.app/upload'  # Replace with your API URL

# Open the image file
image_file = open('rendang.jpg', 'rb')  # Replace 'telur1.jpg' with your image file path

# Create the payload for the request
files = {'file': image_file}

# Send the POST request to the API
response = requests.post(api_url, files=files)

# Check the response status code
if response.status_code == 200:
    data = response.json()
    print(data)
else:
    print(f"Error: {response.text}")
