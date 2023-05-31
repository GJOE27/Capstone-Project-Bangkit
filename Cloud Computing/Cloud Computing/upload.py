import requests

url = 'https://app-66o65w2gbq-et.a.run.app/upload'  # Replace with your server URL

# ID token obtained from authentication
id_token = 'your id token'

# Upload image to user UID folder
image_url = 'img1.jpg'  # Replace with the path to the image file
headers = {'Authorization': id_token}
files = {'photo': open(image_url, 'rb')}
response = requests.post(url, headers=headers, files=files)

if response.status_code == 200:
    response_data = response.json()
    if 'error' in response_data:
        print('Image upload error:', response_data['error'])
    else:
        print('Image uploaded successfully')
else:
    print('Image upload error:', response.text)
