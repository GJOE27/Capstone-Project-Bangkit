import requests

url = 'https://app-66o65w2gbq-et.a.run.app'

id_token = 'eyJhbGciOiJSUzI1NiIsImtpZCI6IjU0NWUyNDZjNTEwNmExMGQ2MzFiMTA0M2E3MWJiNTllNWJhMGM5NGQiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vY2Fwc3RvbmUtbnV0cmlzZWUiLCJhdWQiOiJjYXBzdG9uZS1udXRyaXNlZSIsImF1dGhfdGltZSI6MTY4NjAyMzU5NCwidXNlcl9pZCI6Ikgyd1BnMlFBOUpkb0FhZm1oZVgyeVVTVUF4azEiLCJzdWIiOiJIMndQZzJRQTlKZG9BYWZtaGVYMnlVU1VBeGsxIiwiaWF0IjoxNjg2MDIzNTk0LCJleHAiOjE2ODYwMjcxOTQsImVtYWlsIjoiZXhhbXBsZUBleGFtcGxlLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwiZmlyZWJhc2UiOnsiaWRlbnRpdGllcyI6eyJlbWFpbCI6WyJleGFtcGxlQGV4YW1wbGUuY29tIl19LCJzaWduX2luX3Byb3ZpZGVyIjoiY3VzdG9tIn19.L5QKz_Zhsvj22bdVID7MdVgfEPK0NDR2lXMhDv2jIsUCGoudeXI3KnfcGS94Crb1yOkC0eL4R6O4KYYQ69-G_Bq6_WfEEM8kuiFBFWcO5rbzLpK4jtfluANarETMwGDBREXgHT5cYgHrGogtVMS3QwLok1x7UMHw3wHGOPS4x8TKKqPyfNqEGksNCSpjhfi7i5w4hiNdqqXk90U7SOwXVzTkcxzMjAONY0u5Ps7GjJqCpODoIw0lBOAjhKmRi639ib_DQyKP5qpwAqUT1U8tp_a13V28Vfrai2xerstXt_vusYszU0PzRx_CoZPk3w7q3UP50chVdhbpT28rFBM_Yg'
refresh_token = 'APZUo0RdZVpeiRVnrpQ6mhTe43GPmXJaQc3CL_DrTN9iaCNL7vX2RpUWFFpCwWzMK3AX3PboWk6b6Z7rbrEcvljrJT57YjMMWRctVvS2FjsCrJfwS5yzwBHHSiq6rLL1HiTPVH6vYYUXfPbigmEM5TgIdAXdWHEMc3N0Inm9C4pgeba6_56lqOncorgQvQaBZAVG94GVl9Ps'  # Replace with the actual refresh token

headers = {
    'Authorization': id_token,
    'X-Refresh-Token': refresh_token
}

image_url = 'ayamm.jpg'  
files = {'photo': open(image_url, 'rb')}
response = requests.post(f"{url}/upload", headers=headers, files=files)

if response.status_code == 200:
    response_data = response.json()
    if 'error' in response_data:
        print('Image upload error:', response_data['error'])
    else:
        print('Image uploaded successfully')
else:
    print('Image upload error:', response.text)
