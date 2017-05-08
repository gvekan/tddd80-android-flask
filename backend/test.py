from backend import data, config
import json
import random

import requests

# data.initialize_db()

# root_uri = 'http://localhost:' + str(config.port) + '/'
root_uri = 'http://app-project-tddd80.app.se/'

r1 = requests.post(root_uri + 'register', json={ 'email': 'gusan092@student.liu.se', 'password': 'password', 'first_name': 'Gustav', 'last_name': 'Andersson', 'birth_date': '1996-05-08', 'city': 'Linkoping'})
print(json.loads(r1.text)['msg'])

r2 = requests.post(root_uri + 'login', json={ 'email': 'gusan092@student.liu.se', 'password': 'password'})
token = json.loads(r2.text)['access_token']
print("Token: " + token)
for i in range(1,16):
    r3 = requests.post(root_uri + 'create-post', json={ 'text': 'Hej! Här är mitt '+str(i)+' inlägg'}, headers={'Authorization': 'Bearer ' + token})
    print(json.loads(r3.text)['msg'])

r5 = requests.get(root_uri + 'get-latest-posts', headers={'Authorization': 'Bearer ' + token})
posts1 = json.loads(r5.text)['posts']
print(posts1)

r4 = requests.get(root_uri + 'get-latest-posts-from', json={'post': 6}, headers={'Authorization': 'Bearer ' + token})
posts2 = json.loads(r4.text)['posts']
print(posts2)

for i in range(1,21):
    if random.randrange(2) == 0:
        post_id = posts1[random.randrange(10)]['post']['id']
    else:
        post_id = posts2[random.randrange(5)]['post']['id']


    r6 = requests.post(root_uri + 'create-comment', json={ 'text': 'Hej! Här är min '+str(i)+' kommentar', "post": post_id}, headers={'Authorization': 'Bearer ' + token})
    print(json.loads(r6.text)['msg'])

for i in range(1,16):
    r6 = requests.get(root_uri + 'get-latest-comments', json={'post': i}, headers={'Authorization': 'Bearer ' + token})
    comments = json.loads(r6.text)['comments']
    print(comments)
