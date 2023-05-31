import requests

url = 'https://app-66o65w2gbq-et.a.run.app/login'  # Replace with your server URL

# User data for login
data = {
    'email': 'example@example.com',
    'password': 'password123'
}

# Send POST request to login a user
response = requests.post(url, json=data)

if response.status_code == 200:
    print('User logged in successfully')
    response_data = response.json()
    if 'user' in response_data:
        user_data = response_data.get('user')
        if user_data:
            print('User data:')
            for key, value in user_data.items():
                print(f'{key}: {value}')
        else:
            print('User data not found in response')

    if 'id_token' in response_data:
        id_token = response_data.get('id_token')
        if id_token:
            print('id token:', id_token)
        else:
            print('Id token not found in response')
else:
    print('Login error:', response.json().get('error'))
