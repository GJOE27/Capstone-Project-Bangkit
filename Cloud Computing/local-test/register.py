import requests

url = 'https://nutrisee-66o65w2gbq-et.a.run.app' 

data = {
    'email': 'example@example.com',
    'password': 'password123',
    'display_name': 'anon',
    'photo_url': 'https://fastly.picsum.photos/id/946/500/500.jpg?hmac=e790b958XsD9Y04pYBuhTjFq7FNETblcqo1KdbSz5Tk'
}

response = requests.post(f"{url}/register", json=data)

if response.status_code == 200:
    print('User registered successfully')
else:
    print('Registration error:', response.json()['error'])
