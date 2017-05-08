from backend import data, config
import json

import requests

data.initialize_db()

root_uri = 'http://localhost:' + str(config.port) + '/'

r1 = requests.post(root_uri + 'register', json={ 'email': 'gusan092@student.liu.se', 'password': 'password', 'first_name': 'Gustav', 'last_name': 'Andersson', 'birth_date': '1996-05-08', 'city': 'Linkoping'})
print(json.loads(r1.text)['msg'])

r2 = requests.post(root_uri + 'login', json={ 'email': 'gusan092@student.liu.se', 'password': 'password'})
token = json.loads(r2.text)['access_token']
print("Token: " + token)

r3 = requests.post(root_uri + 'create-post', json={ 'text': 'Hej! Här är mitt första inlägg'}, headers={'Authorization': 'Bearer ' + token})
print(json.loads(r3.text)['msg'])
r4 = requests.post(root_uri + 'create-post', json={ 'text': 'Hej! Här är mitt andra inlägg'}, headers={'Authorization': 'Bearer ' + token})
print(json.loads(r4.text)['msg'])

r5 = requests.get(root_uri + 'get-latest-posts', headers={'Authorization': 'Bearer ' + token})
posts = json.loads(r5.text)['posts']
print(posts)
post_id = posts[1]['post']['id']

r6 = requests.post(root_uri + 'create-comment', json={ 'text': 'Hej! Här är min första kommentar', "post": post_id}, headers={'Authorization': 'Bearer ' + token})
print(json.loads(r6.text)['msg'])

r6 = requests.get(root_uri + 'get-latest-comments', json={ 'post': post_id}, headers={'Authorization': 'Bearer ' + token})
comments = json.loads(r5.text)['posts']
print(comments)
