import requests

url = 'https://app-66o65w2gbq-et.a.run.app/update_profile' 

# User data for profile update
data = {
    'user_uid': 'FT9K0G2RWAWr9Y9VmsuB0jbQk1a2', 
    'display_name': 'Nutrisee', 
    'photo_url': 'https://fastly.picsum.photos/id/946/500/500.jpg?hmac=e790b958XsD9Y04pYBuhTjFq7FNETblcqo1KdbSz5Tk'  # Replace with the new photo URL
}

# Send POST request to update the profile
response = requests.post(url, json=data)

# Check the response
if response.status_code == 200:
    print('Profile updated successfully')
else:
    print('Registration error:', response.json()['error'])