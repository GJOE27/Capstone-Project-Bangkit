import requests

url = 'https://app-66o65w2gbq-et.a.run.app' 

data = {
    'email': 'example@example.com',
    'password': 'password123'
}

response = requests.post(f"{url}/register", json=data)

if response.status_code == 200:
    print('User registered successfully')
else:
    print('Registration error:', response.json()['error'])
