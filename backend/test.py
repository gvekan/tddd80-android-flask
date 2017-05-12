from backend import data, config
import json
import random

import requests

data.initialize_db()

root_uri = 'http://localhost:' + str(config.port) + '/'
# root_uri = 'http://app-project-tddd80.apps.ida.liu.se/'

r1 = requests.post(root_uri + 'register', json={'email': 'gusan092@student.liu.se', 'password': 'password', 'first_name': 'Gustav', 'last_name': 'Andersson', 'city': 'Linkoping'})
print(json.loads(r1.text)['msg'])

r2 = requests.post(root_uri + 'login', json={'email': 'gusan092@student.liu.se', 'password': 'password'})
token = json.loads(r2.text)['access_token']
print("Token: " + token)
for i in range(1, 16):
    r3 = requests.post(root_uri + 'create-post', json={'text': 'Hej! Här är mitt ' + str(i) + ' inlägg'},
                       headers={'Authorization': 'Bearer ' + token})
    print(json.loads(r3.text)['msg'])

r5 = requests.get(root_uri + 'get-latest-posts', json={'post': -1}, headers={'Authorization': 'Bearer ' + token})
posts1 = json.loads(r5.text)['posts']
print(posts1)

r4 = requests.get(root_uri + 'get-latest-posts-from', json={'post': 6}, headers={'Authorization': 'Bearer ' + token})
posts2 = json.loads(r4.text)['posts']
print(posts2)

for i in range(1, 21):
    if random.randrange(2) == 0:
        post_id = posts1[random.randrange(10)]['post']['id']
    else:
        post_id = posts2[random.randrange(5)]['post']['id']

    r6 = requests.post(root_uri + 'create-comment',
                       json={'text': 'Hej! Här är min ' + str(i) + ' kommentar', "post": post_id},
                       headers={'Authorization': 'Bearer ' + token})
    print(json.loads(r6.text)['msg'])

for i in range(1, 16):
    r6 = requests.get(root_uri + 'get-latest-comments', json={'post': i}, headers={'Authorization': 'Bearer ' + token})
    comments = json.loads(r6.text)['comments']
    print(comments)

#CREATING USERS
simon = requests.post(root_uri + 'register', json={'email': 'simsu451@student.liu.se', 'password': 'password', 'first_name': 'Simon', 'last_name': 'Sundberg', 'city': 'Stockholm'})
print(json.loads(simon.text)['msg'])
ronny = requests.post(root_uri + 'register', json={'email': 'ronny451@student.liu.se', 'password': 'password', 'first_name': 'Ronny', 'last_name': 'Connysson', 'city': 'Stockholm'})
print(json.loads(simon.text)['msg'])

#SENDING MESSAGES
m1 = requests.post(root_uri + 'send_message', json={'message': 'Mitt meddelande', 'receiver': 'simsu451@student.liu.se'},headers={'Authorization': 'Bearer ' + token})
print(json.loads(m1.text)['msg'])
m2 = requests.post(root_uri + 'send_message', json={'message': 'Mitt meddelande 2', 'receiver': 'simsu451@student.liu.se'},headers={'Authorization': 'Bearer ' + token})
print(json.loads(m2.text)['msg'])
m3 = requests.post(root_uri + 'send_message', json={'message': 'Mitt meddelande 3', 'receiver': 'simsu451@student.liu.se'},headers={'Authorization': 'Bearer ' + token})
print(json.loads(m3.text)['msg'])
m4 = requests.post(root_uri + 'send_message', json={'message': 'Mitt meddelande 4', 'receiver': 'ronny451@student.liu.se'},headers={'Authorization': 'Bearer ' + token})
print(json.loads(m4.text)['msg'])
m5 = requests.post(root_uri + 'send_message', json={'message': 'Mitt meddelande 5', 'receiver': 'ronny451@student.liu.se'},headers={'Authorization': 'Bearer ' + token})
print(json.loads(m5.text)['msg'])

#GET CHATS
c1 = requests.get(root_uri + 'get_chats', headers={'Authorization': 'Bearer ' + token})
print(json.loads(c1.text)['chats'])

#GET MESSAGES
g1 = requests.get(root_uri + 'get_messages', json={'receiver': 'simsu451@student.liu.se'},headers={'Authorization': 'Bearer ' + token})
print(json.loads(g1.text)['messages'])
g2 = requests.get(root_uri + 'get_messages', json={'receiver': 'ronny451@student.liu.se'},headers={'Authorization': 'Bearer ' + token})
print(json.loads(g2.text)['messages'])

