import io
import requests

url = 'https://nutrisee-66o65w2gbq-et.a.run.app'

id_token = 'eyJhbGciOiJSUzI1NiIsImtpZCI6IjU0NWUyNDZjNTEwNmExMGQ2MzFiMTA0M2E3MWJiNTllNWJhMGM5NGQiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vY2Fwc3RvbmUtbnV0cmlzZWUiLCJhdWQiOiJjYXBzdG9uZS1udXRyaXNlZSIsImF1dGhfdGltZSI6MTY4NjUzODI1NCwidXNlcl9pZCI6IlY3clZ0aklZaXNQMTE0SmpVZkxxajBUQUwxSDIiLCJzdWIiOiJWN3JWdGpJWWlzUDExNEpqVWZMcWowVEFMMUgyIiwiaWF0IjoxNjg2NTM4MjU0LCJleHAiOjE2ODY1NDE4NTQsImVtYWlsIjoiZXhhbXBsZUBleGFtcGxlLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwiZmlyZWJhc2UiOnsiaWRlbnRpdGllcyI6eyJlbWFpbCI6WyJleGFtcGxlQGV4YW1wbGUuY29tIl19LCJzaWduX2luX3Byb3ZpZGVyIjoiY3VzdG9tIn19.ylwoP_1Oo07N9tUfsyxpaZNCUooAsCcleCo8Y271pXSZWkTYIjQXlgZUdQ_-9QQ5bnuFbFEaNHkzNq7z6PdBEpB-jHnapF2nIAQz4c-b6_EgDEP8DsnLpVt0d6ttobDEiaU7F7lN68-T01WqA4JFeu5KO_diouGq85DuQ2dejFiK8lIW0onV2cb40wbJDc6YPhM3mFepUy15O14QAgkEcZnzBuqbdExxHb9TMRbWL7-mxbMBSxdm8yTMVGnE6XzlYbCQvMhfj5I_eL1hFUrm-AM1rsyUl1Ys_LfrIpMBFBoT3HdaC644Uq3TQ9yb0m_pCKXk6ZWDtrTHKjKLw5BaOg'
refresh_token = 'APZUo0Qin2V5uZjBxxJduAK6lFR8LiB9yQBNqJGqOxaQ2LVbI6kWhp2GkvcIvNPiAREkrEDA1YeryYnjYap83Uh4BzsYZtx-Rcgxodp9zVu-6eRYU47m96BkSxpO_OBZz4AUg8xoH1a7zDIF99SDTP4CAzJGUz22hEzhLcP3ZzIwczVAmNWalHNtWOHWLPTgiY_aOJCNGElS'

headers = {
    'Authorization': id_token,
    'X-Refresh-Token': refresh_token
}

image_path = 'ayam1.jpg'
with open(image_path, 'rb') as image_file:
    files = {'photo': image_file}
    response = requests.post(f"{url}/upload", headers=headers, files=files)

if response.status_code == 200:
    response_data = response.json()
    if 'error' in response_data:
        print('Image upload error:', response_data['error'])
    else:
        response_data = response.json()
        print(response_data)