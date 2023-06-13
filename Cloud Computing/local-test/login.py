import requests

url = 'https://nutrisee-66o65w2gbq-et.a.run.app'  

data = {
    'email': 'example@example.com',
    'password': 'password123'
}

response = requests.post(f"{url}/login", json=data)

# Check the response status code
if response.status_code == 200:
    # Successful login, retrieve the response data
    response_data = response.json()
    print(response_data)
else:
    # Error occurred during login
    print("Login failed. Status code:", response.status_code)
