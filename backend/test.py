from backend import data, config
import json
import random

import requests

#data.initialize_db()
#root_uri = 'http://localhost:' + str(config.port) + '/'
root_uri = 'http://app-project-tddd80.apps.ida.liu.se/'


# CREATING USERS
requests.post(root_uri + 'register', json={'email': 'gusan092@student.liu.se', 'password': 'password', 'first_name': 'Gustav', 'last_name': 'Andersson', 'city': 'Linkoping'})
requests.post(root_uri + 'register', json={'email': 'simsu451@student.liu.se', 'password': 'password', 'first_name': 'Simon', 'last_name': 'Sundberg', 'city': 'Stockholm'})
requests.post(root_uri + 'register', json={'email': 'ronny451@student.liu.se', 'password': 'password', 'first_name': 'Ronny', 'last_name': 'Connysson', 'city': 'Stockholm'})

# LOGIN
login_gustav = requests.post(root_uri + 'login', json={'email': 'gusan092@student.liu.se', 'password': 'password'})
token_gustav = json.loads(login_gustav.text)['access_token']
login_simon = requests.post(root_uri + 'login', json={'email': 'simsu451@student.liu.se', 'password': 'password'})
token_simon = json.loads(login_simon.text)['access_token']
login_ronny = requests.post(root_uri + 'login', json={'email': 'ronny451@student.liu.se', 'password': 'password'})
token_ronny = json.loads(login_ronny.text)['access_token']

# CREATING POSTS
r = 0
for i in range(1, 6):
    r += 1
    requests.post(root_uri + 'create-post', json={'text': 'Hej! Här är Gustavs inlägg med id: ' + str(r), 'city': ''},
                        headers={'Authorization': 'Bearer ' + token_gustav})
    r += 1
    requests.post(root_uri + 'create-post', json={'text': 'Hej! Här är Simons inlägg med id: ' + str(r), 'city': ''},
                            headers={'Authorization': 'Bearer ' + token_simon})
    r += 1
    requests.post(root_uri + 'create-post', json={'text': 'Hej! Här är Ronnys inlägg med id: ' + str(r), 'city': ''},
                            headers={'Authorization': 'Bearer ' + token_ronny})

# CREATE COMMENTS
for i in range(1, 6):
    requests.post(root_uri + 'create-comment/6',
                       json={'text': 'Hej! Här är Gustavs ' + str(i) + ' kommentar'},
                       headers={'Authorization': 'Bearer ' + token_gustav})

for i in range(1, 6):
    requests.post(root_uri + 'create-comment/6',
                       json={'text': 'Hej! Här är Simons ' + str(i) + ' kommentar'},
                       headers={'Authorization': 'Bearer ' + token_simon})


for i in range(1,6):
    requests.post(root_uri + 'create-comment/6',
                       json={'text': 'Hej! Här är Ronnys ' + str(i) + ' kommentar'},
                       headers={'Authorization': 'Bearer ' + token_ronny})

# LIKE POSTS:
requests.post(root_uri + 'like-post/1',
                   headers={'Authorization': 'Bearer ' + token_gustav})
requests.post(root_uri + 'like-post/14',
                   headers={'Authorization': 'Bearer ' + token_gustav})
requests.post(root_uri + 'like-post/14',
                   headers={'Authorization': 'Bearer ' + token_simon})
requests.post(root_uri + 'like-post/14',
                   headers={'Authorization': 'Bearer ' + token_ronny})

# DISLIKE POST
requests.post(root_uri + 'dislike-post/14',
                   headers={'Authorization': 'Bearer ' + token_gustav})

# GET POSTS
get_latest_posts = requests.get(root_uri + 'get-latest-posts/-1', headers={'Authorization': 'Bearer ' + token_gustav})
print(json.loads(get_latest_posts.text))


get_latest_posts = requests.get(root_uri + 'get-latest-posts/11', headers={'Authorization': 'Bearer ' + token_gustav})
print(json.loads(get_latest_posts.text))


get_latest_posts = requests.get(root_uri + 'get-latest-posts-from-user', headers={'Authorization': 'Bearer ' + token_gustav})
print(json.loads(get_latest_posts.text))

get_latest_posts = requests.get(root_uri + 'get-latest-posts-from/6', headers={'Authorization': 'Bearer ' + token_gustav})
print(json.loads(get_latest_posts.text))

# GET_COMMENTS

get_latest_comments = requests.get(root_uri + 'get-latest-comments/6/-1', headers={'Authorization': 'Bearer ' + token_gustav})
print(json.loads(get_latest_comments.text))

get_latest_comments = requests.get(root_uri + 'get-latest-comments/6/11', headers={'Authorization': 'Bearer ' + token_gustav})
print(json.loads(get_latest_comments.text))

get_latest_comments = requests.get(root_uri + 'get-latest-comments-from-user/6', headers={'Authorization': 'Bearer ' + token_simon})
print(json.loads(get_latest_comments.text))

get_latest_comments = requests.get(root_uri + 'get-latest-comments-from/6/11', headers={'Authorization': 'Bearer ' + token_gustav})
print(json.loads(get_latest_comments.text))

get_profile_info = requests.get(root_uri + 'get-profile-info', headers={'Authorization': 'Bearer ' + token_gustav})
print(json.loads(get_profile_info.text))

# # SENDING MESSAGES
m1 = requests.post(root_uri + 'send-message', json={'text': 'Mitt meddelande', 'receiver': 'simsu451@student.liu.se'}, headers={'Authorization': 'Bearer ' + token_gustav})
print(json.loads(m1.text)['msg'])
m2 = requests.post(root_uri + 'send-message', json={'text': 'Mitt meddelande 2', 'receiver': 'simsu451@student.liu.se'}, headers={'Authorization': 'Bearer ' + token_gustav})
print(json.loads(m2.text)['msg'])

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
g1 = requests.get(root_uri + 'get-latest-messages/simsu451@student.liu.se', headers={'Authorization': 'Bearer ' + token_gustav})
print(json.loads(g1.text)['messages'])
# g2 = requests.get(root_uri + 'get_messages', json={'receiver': 'ronny451@student.liu.se'}, headers={'Authorization': 'Bearer ' + token})
# print(json.loads(g2.text)['messages'])
#
# # SEND FRIEND REQUEST
f1 = requests.post(root_uri + 'send-friend-request/gusan092@student.liu.se', headers={'Authorization': 'Bearer ' + token_simon})
print(json.loads(f1.text)['msg'])

#   GET FRIEND REQUESTS
gfr1 = requests.get(root_uri + 'get-friend-requests', headers={'Authorization': 'Bearer ' + token_gustav})
print("Friend requests: " + str(json.loads(gfr1.text)['users']))
#   GET FRIENDS
fr1 = requests.get(root_uri + 'get-friends', headers={'Authorization': 'Bearer ' + token_gustav})
print("Friends: " + str(json.loads(fr1.text)['users']))
# # ACCEPT FRIEND REQUEST
f2 = requests.post(root_uri + 'accept-friend-request/simsu451@student.liu.se', headers={'Authorization': 'Bearer ' + token_gustav})
print(json.loads(f2.text)['msg'])
#   GET FRIENDS
fr1 = requests.get(root_uri + 'get-friends', headers={'Authorization': 'Bearer ' + token_gustav})
print("Friends: " + str(json.loads(fr1.text)['users']))
#   GET ALL USERS
gau = requests.get(root_uri + 'get-all-users', headers={'Authorization': 'Bearer ' + token_gustav})
print("Users : " + str(json.loads(gau.text)['users']))

# GET LATEST MESSAGES

lm1 = requests.get(root_uri + 'get-latest-messages/simsu451@student.liu.se', headers={'Authorization': 'Bearer ' + token_gustav})
print(json.loads(lm1.text)['messages'])
lm2 = requests.get(root_uri + 'get-latest-messages/simsu451@student.liu.se', headers={'Authorization': 'Bearer ' + token_gustav})
print(json.loads(lm2.text)['messages'])
# # LOGIN ANOTHER USER
# logout1 = requests.post(root_uri + 'logout', headers={'Authorization': 'Bearer ' + token})
# login2 = requests.post(root_uri + 'login', json={'email': 'simsu451@student.liu.se', 'password': 'password'})
# print(json.loads(login2.text)['access_token'])
# token = json.loads(login2.text)['access_token']
#
# # GET FRIEND REQUEST
# #get_f = requests.get(root_uri + 'get_friend_requests', headers={'Authorization': 'Bearer ' + token})
# #print(json.loads(get_f.text)['friend_requests'])


# GET PROFILE INFO
#get_info = requests.get(root_uri + 'get_profile_info', headers={'Authorization': 'Bearer ' + token})
#print(json.loads(get_info.text))

# FACIT
# RAD 1; Post 6-15
# RAD 2: Post 12-15
# RAD 3: Post 4-13
# RAD 4: Post 1-5
# Post 1; likes: 0, liking: False
# Post 6; comments: 15
# Post 14; likes: 3, liking: True
# RAD 5; Comment index 6-15
# RAD 6: Comment index 12-15
# RAD 7: Comment index 1-10
# RAD 8: Comment index 1-10

