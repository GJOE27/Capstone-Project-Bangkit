import requests

url = 'https://app-66o65w2gbq-et.a.run.app'  # Replace with the actual URL of your API server

id_token = 'eyJhbGciOiJSUzI1NiIsImtpZCI6IjU0NWUyNDZjNTEwNmExMGQ2MzFiMTA0M2E3MWJiNTllNWJhMGM5NGQiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vY2Fwc3RvbmUtbnV0cmlzZWUiLCJhdWQiOiJjYXBzdG9uZS1udXRyaXNlZSIsImF1dGhfdGltZSI6MTY4NjQ5ODYxOCwidXNlcl9pZCI6InRacXA5bDE4SmlRQUtOUmFwMEowQ2x2ZDB6ODIiLCJzdWIiOiJ0WnFwOWwxOEppUUFLTlJhcDBKMENsdmQwejgyIiwiaWF0IjoxNjg2NDk4NjE4LCJleHAiOjE2ODY1MDIyMTgsImVtYWlsIjoiZXhhbXBsZUBleGFtcGxlLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwiZmlyZWJhc2UiOnsiaWRlbnRpdGllcyI6eyJlbWFpbCI6WyJleGFtcGxlQGV4YW1wbGUuY29tIl19LCJzaWduX2luX3Byb3ZpZGVyIjoiY3VzdG9tIn19.AchfHtMclW0LeRxrEkN55Php2O4DgvRVdSN8xKq2pLIJ9LavbK7gzsI9K42jmV0rqYjhQ6lLZftD09Usq9JBW-gFobUBTgFcZiPvXlTXQ-gJyIrHIEUvN7juZZIj9sE3a4w6AY4ekcAFgl0DgEn3lT8Sm-dt_iDBpxFmy7OhzTSZafSMNtTFjPzQ6pND8rlwF4jdVu5_68sWHqcqzIi4Rrp1Ni_jnpZN46TQXBrCZSzvwxPKMrEnZ9MeERX3jMHuU5x3Za2ay93A-n99dYfqZN6M0sqD4sak1d6zbmCaFySgrh2zDvpJ6prK0IqFymse1kw76EFblZ_sMDvEIlTH7w'
refresh_token = 'APZUo0TID2La3AhY4zTwWMk42JWQSHjA0k1zBnLPqBq22nDhAeQQ3PjNHxkQMdU_VTUjekLLcNdhjHVlFyEV0HpRAEXaEU16wL1fgvpiTb5KnroCcZh7Fa6dM0KguOE-ZEKrY5l58URqCKYdCRx5VUtxZiH6RJb3sT_0e5c0stwZZQDu5Nsa8LL6KzAFyg5k6hzivRSZeXw1'  # Replace with the actual refresh token

headers = {
    'Authorization': id_token,
    'X-Refresh-Token': refresh_token
}

response = requests.get(f"{url}/list_images", headers=headers)

if response.status_code == 200:
    response_data = response.json()
    if 'error' in response_data:
        print('Image upload error:', response_data['error'])
    else:
        response_data = response.json()
        print(response_data)
