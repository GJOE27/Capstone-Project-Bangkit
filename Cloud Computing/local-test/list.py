import requests

url = 'https://nutrisee-66o65w2gbq-et.a.run.app'  # Replace with the actual URL of your API server

id_token = 'eyJhbGciOiJSUzI1NiIsImtpZCI6IjY3YmFiYWFiYTEwNWFkZDZiM2ZiYjlmZjNmZjVmZTNkY2E0Y2VkYTEiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vY2Fwc3RvbmUtbnV0cmlzZWUiLCJhdWQiOiJjYXBzdG9uZS1udXRyaXNlZSIsImF1dGhfdGltZSI6MTY4NjkxNzc3MiwidXNlcl9pZCI6ImVqTmxTb3gycTBXWHVoaXhqOGp2N09yU0JzajIiLCJzdWIiOiJlak5sU294MnEwV1h1aGl4ajhqdjdPclNCc2oyIiwiaWF0IjoxNjg2OTE3NzcyLCJleHAiOjE2ODY5MjEzNzIsImVtYWlsIjoiZXhhbXBsZUBleGFtcGxlLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwiZmlyZWJhc2UiOnsiaWRlbnRpdGllcyI6eyJlbWFpbCI6WyJleGFtcGxlQGV4YW1wbGUuY29tIl19LCJzaWduX2luX3Byb3ZpZGVyIjoiY3VzdG9tIn19.E4-WHZGAAkFaBzrKU0EW67cXimKG9m2riELboO8n7uCdAksRigPWI_o05prlMl3NLQoo652NiArvEK9v9qhmzFgqLxgaCUJeiXQ1js9IU3E30z3sHaT2VtYPCCAyWuIRJ2RzkgSTc84u_UzrREh0V_DRipZ0mOpTBcd7pHbovgGUSK7pVqecAbrTaJS8YlA4NJkEoGHZ-PnzaSM7bW-u9qjyMXJ-9KiLEb1-GtXtknUb4Ub_X6wX541_2n59rpaaxOpr6QrCYCSUj0G0PRpRrDJB496kHokXCDR-_z5ir8q8YSAPlWXifslyGdi5C6lMc6QZM0EatDVBv65J4jQf5Q'
refresh_token = 'APZUo0QVV31mJqTaHQFGm2LSvx1YUZSeCKisUvrvOCJDsX2SO_ucVPdst-hPRqhKPEz_TYhwGarQGzqdd8YK1gyFzG0nDlE41HicTIwLw8YnGfa2jjJ_REBMy1Rh3YS1etbEHiEJaErojpnHM9c90-1bHEy90qDQcmcx9aF2Y0swSV1JtSqDMMJHFIrkVNuOjF25wtzmNhDW'

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
