from backend import data, config
import json
import random

import requests

data.initialize_db()

root_uri = 'http://localhost:' + str(config.port) + '/'
# root_uri = 'http://app-project-tddd80.apps.ida.liu.se/'


# CREATING USERS
register_gustav = requests.post(root_uri + 'register', json={'email': 'gusan092@student.liu.se', 'password': 'password', 'first_name': 'Gustav', 'last_name': 'Andersson', 'city': 'Linkoping'})
register_simon = requests.post(root_uri + 'register', json={'email': 'simsu451@student.liu.se', 'password': 'password', 'first_name': 'Simon', 'last_name': 'Sundberg', 'city': 'Stockholm'})
register_ronny = requests.post(root_uri + 'register', json={'email': 'ronny451@student.liu.se', 'password': 'password', 'first_name': 'Ronny', 'last_name': 'Connysson', 'city': 'Stockholm'})

# LOGIN
login_gustav = requests.post(root_uri + 'login', json={'email': 'gusan092@student.liu.se', 'password': 'password'})
token_gustav = json.loads(login_gustav.text)['access_token']
print("Gustavs token: " + token_gustav)
login_simon = requests.post(root_uri + 'login', json={'email': 'simsu451@student.liu.se', 'password': 'password'})
token_simon = json.loads(login_simon.text)['access_token']
print("Simons token: " + token_simon)
login_ronny = requests.post(root_uri + 'login', json={'email': 'ronny451@student.liu.se', 'password': 'password'})
token_ronny = json.loads(login_ronny.text)['access_token']
print("Ronnys token: " + token_ronny)

# CREATING POSTS
post_gustav = requests.post(root_uri + 'create-post', json={'text': 'Hej! Här är Gustavs inlägg'},
                        headers={'Authorization': 'Bearer ' + token_gustav})
post_simon = requests.post(root_uri + 'create-post', json={'text': 'Hej! Här är Simons inlägg'},
                        headers={'Authorization': 'Bearer ' + token_simon})
post_ronny = requests.post(root_uri + 'create-post', json={'text': 'Hej! Här är Ronnys inlägg'},
                        headers={'Authorization': 'Bearer ' + token_simon})

# CREATE COMMENTS
for i in range(1, 16):
    comment_gustav = requests.post(root_uri + 'create-comment',
                       json={'text': 'Hej! Här är Gustavs ' + str(i) + ' kommentar', "post": 2},
                       headers={'Authorization': 'Bearer ' + token_gustav})

for i in range(1, 11):
    comment_simon = requests.post(root_uri + 'create-comment',
                       json={'text': 'Hej! Här är Simons ' + str(i) + ' kommentar', "post": 1},
                       headers={'Authorization': 'Bearer ' + token_simon})


for i in range(1, 6):
    comment_ronny = requests.post(root_uri + 'create-comment',
                       json={'text': 'Hej! Här är Ronnys ' + str(i) + ' kommentar', "post": 1},
                       headers={'Authorization': 'Bearer ' + token_ronny})

r5 = requests.get(root_uri + 'get-latest-posts', json={'post': -1}, headers={'Authorization': 'Bearer ' + token_gustav})
posts1 = json.loads(r5.text)['posts']
print(posts1)

for i in range(1, 4):
    r6 = requests.get(root_uri + 'get-latest-comments', json={'post': i, 'comment': -1}, headers={'Authorization': 'Bearer ' + token_gustav})
    print(json.loads(r6.text)['comments'])

# for i in range(1, 16):
#     r3 = requests.post(root_uri + 'create-post', json={'text': 'Hej! Här är mitt ' + str(i) + ' inlägg'},
#                        headers={'Authorization': 'Bearer ' + token_gustav})
#     print(json.loads(r3.text)['msg'])
#
# # GET POSTS
# r5 = requests.get(root_uri + 'get-latest-posts', json={'post': -1}, headers={'Authorization': 'Bearer ' + token_gustav})
#
# r4 = requests.get(root_uri + 'get-latest-posts-from', json={'post': 6}, headers={'Authorization': 'Bearer ' + token_gustav})
# posts2 = json.loads(r4.text)['posts']
# print(posts2)
#
# # CREATE COMMENTS
# for i in range(1, 21):
#     if random.randrange(2) == 0:
#         post_id = posts1[random.randrange(10)]['post']['id']
#     else:
#         post_id = posts2[random.randrange(5)]['post']['id']
#
#     r6 = requests.post(root_uri + 'create-comment',
#                        json={'text': 'Hej! Här är min ' + str(i) + ' kommentar', "post": post_id},
#                        headers={'Authorization': 'Bearer ' + token_gustav})
#     print(json.loads(r6.text)['msg'])
#
# for i in range(1, 16):
#     r6 = requests.get(root_uri + 'get-latest-comments', json={'post': i, 'comment': -1}, headers={'Authorization': 'Bearer ' + token_gustav})
#     print(json.loads(r6.text)['comments'])

# # SENDING MESSAGES
# m1 = requests.post(root_uri + 'send_message', json={'message': 'Mitt meddelande', 'receiver': 'simsu451@student.liu.se'}, headers={'Authorization': 'Bearer ' + token})
# print(json.loads(m1.text)['msg'])
# m2 = requests.post(root_uri + 'send_message', json={'message': 'Mitt meddelande 2', 'receiver': 'simsu451@student.liu.se'}, headers={'Authorization': 'Bearer ' + token})
# print(json.loads(m2.text)['msg'])
# m3 = requests.post(root_uri + 'send_message', json={'message': 'Mitt meddelande 3', 'receiver': 'simsu451@student.liu.se'}, headers={'Authorization': 'Bearer ' + token})
# print(json.loads(m3.text)['msg'])
# m4 = requests.post(root_uri + 'send_message', json={'message': 'Mitt meddelande 4', 'receiver': 'ronny451@student.liu.se'}, headers={'Authorization': 'Bearer ' + token})
# print(json.loads(m4.text)['msg'])
# m5 = requests.post(root_uri + 'send_message', json={'message': 'Mitt meddelande 5', 'receiver': 'ronny451@student.liu.se'}, headers={'Authorization': 'Bearer ' + token})
# print(json.loads(m5.text)['msg'])
#
# # GET CHATS
# c1 = requests.get(root_uri + 'get_chats', headers={'Authorization': 'Bearer ' + token})
# print(json.loads(c1.text)['chats'])
#
# # GET MESSAGES
# g1 = requests.get(root_uri + 'get_messages', json={'receiver': 'simsu451@student.liu.se'}, headers={'Authorization': 'Bearer ' + token})
# print(json.loads(g1.text)['messages'])
# g2 = requests.get(root_uri + 'get_messages', json={'receiver': 'ronny451@student.liu.se'}, headers={'Authorization': 'Bearer ' + token})
# print(json.loads(g2.text)['messages'])
#
# # SEND FRIEND REQUEST
# f1 = requests.post(root_uri + 'send_friend_request', json={'receiver': 'simsu451@student.liu.se'}, headers={'Authorization': 'Bearer ' + token})
# print(json.loads(f1.text)['msg'])
# # LOGOUT
#
# # LOGIN ANOTHER USER
# logout1 = requests.post(root_uri + 'logout', headers={'Authorization': 'Bearer ' + token})
# login2 = requests.post(root_uri + 'login', json={'email': 'simsu451@student.liu.se', 'password': 'password'})
# print(json.loads(login2.text)['access_token'])
# token = json.loads(login2.text)['access_token']
#
# # GET FRIEND REQUEST
# #get_f = requests.get(root_uri + 'get_friend_requests', headers={'Authorization': 'Bearer ' + token})
# #print(json.loads(get_f.text)['friend_requests'])
# # ACCEPT FRIEND REQUEST
# f2 = requests.post(root_uri + 'accept_friend_request', json={'requester': 'gusan092@student.liu.se'}, headers={'Authorization': 'Bearer ' + token})
# print(json.loads(f2.text)['msg'])

