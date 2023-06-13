import requests

url = 'https://app-66o65w2gbq-et.a.run.app'

data = {
    'user_uid': '2hgsBxPQoyd28pRLstGJZPlGZC02',
    'display_name': 'asassasasj',
    'photo_url': 'https://fastly.picsum.photos/id/946/500/500.jpg?hmac=e790b958XsD9Y04pYBuhTjFq7FNETblcqo1KdbSz5Tk'  # Replace with the new photo URL
}

id_token = 'eyJhbGciOiJSUzI1NiIsImtpZCI6IjU0NWUyNDZjNTEwNmExMGQ2MzFiMTA0M2E3MWJiNTllNWJhMGM5NGQiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vY2Fwc3RvbmUtbnV0cmlzZWUiLCJhdWQiOiJjYXBzdG9uZS1udXRyaXNlZSIsImF1dGhfdGltZSI6MTY4NjAyMTI3MCwidXNlcl9pZCI6IjJoZ3NCeFBRb3lkMjhwUkxzdEdKWlBsR1pDMDIiLCJzdWIiOiIyaGdzQnhQUW95ZDI4cFJMc3RHSlpQbEdaQzAyIiwiaWF0IjoxNjg2MDIxMjcwLCJleHAiOjE2ODYwMjQ4NzAsImVtYWlsIjoiZXhhbXBsZUBleGFtcGxlLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwiZmlyZWJhc2UiOnsiaWRlbnRpdGllcyI6eyJlbWFpbCI6WyJleGFtcGxlQGV4YW1wbGUuY29tIl19LCJzaWduX2luX3Byb3ZpZGVyIjoiY3VzdG9tIn19.J643y5vYLZMJiZLgrxe0J8BKscuvHmx1bcgSXoR2Ssg76NRkoVbCw4hRlX-sZUsLruLHKneMkX9uyOG_jjiEYcyqF1QBkNxA8-Atesll3h5VSqJlOhPB77cPzq85O0XoQxNbtoCoLCk5b7oPjVoVKAdUJkS6fDVBLP3kZjW_om5BGB99OwqN4m7BgqvUzcptSHW4J7pcUmQpb-Skmp3TgqedzwOsZtuftT0RXyzK0UAhfWxzwWWInTMLca28c8FxEkMG_S3Ce71dP1dO9MnWYqjKqLzhXu7pba4hMlSO5TVYAexcOe_4M4VAG2p6PkGjR2fEq36kVAIyzMFw0blmqA'  # Replace with the actual ID token
refresh_token = 'APZUo0T7dH7wLKFH6XHRslvDCWTOHzrb49s0-MJPs6oelG3j3aSX6OATZZ0QmmCirVakTwS412j-09UIZtu5QYyXteAoCfQhqEfwoacbZkvlSGpajH_cAp_7QVhsyAndoCDavJiJV7XlKMIiE8jd49FT4bqTlE9UqRLFStz12nGSquUB5dU6iOZ_FFuomxRFfdZfh6Lqb40w'  # Replace with the actual refresh token

headers = {
    'Authorization': id_token,
    'X-Refresh-Token': refresh_token
}

response = requests.post(f"{url}/update_profile", json=data, headers=headers)

if response.status_code == 200:
    print('Profile changed successfully')
else:
    print('Update profile error:', response.json()['error'])
