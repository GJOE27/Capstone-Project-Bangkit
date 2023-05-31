import requests

url = 'https://app-66o65w2gbq-et.a.run.app/register'  # Replace with your server URL

# User data for registration
data = {
    'email': 'example@example.com',
    'password': 'password123'
}

# Send POST request to register a user
response = requests.post(url, json=data)

# Check the response
if response.status_code == 200:
    print('User registered successfully')
else:
    print('Registration error:', response.json()['error'])
