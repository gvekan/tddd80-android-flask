import random
import json

import requests

root_uri = 'http://app-project-tddd80.apps.ida.liu.se/'

# CREATING USERS
requests.post(root_uri + 'register', json={'email': 'lisa@mail.se', 'password': 'password', 'first_name': 'Lisa', 'last_name': 'Borg', 'city': 'Halmstad'})
requests.post(root_uri + 'register', json={'email': 'ronny@mail.se', 'password': 'password', 'first_name': 'Ronny', 'last_name': 'Augustsson', 'city': 'Jönköping'})
requests.post(root_uri + 'register', json={'email': 'frida@mail.se', 'password': 'password', 'first_name': 'Frida', 'last_name': 'Strid', 'city': 'Linköping'})
requests.post(root_uri + 'register', json={'email': 'kalle@mail.se', 'password': 'password', 'first_name': 'Kalle', 'last_name': 'Lindqvist', 'city': 'Uppsala'})
requests.post(root_uri + 'register', json={'email': 'jonas@mail.se', 'password': 'password', 'first_name': 'Jonas', 'last_name': 'Borg', 'city': 'Linköping'})
requests.post(root_uri + 'register', json={'email': 'britta@mail.se', 'password': 'password', 'first_name': 'Britta', 'last_name': 'Karlsson', 'city': 'Stockholm'})

# LOGIN
r = requests.post(root_uri + 'login', json={'email': 'lisa@mail.se', 'password': 'password'})
token_lisa = json.loads(r.text)['access_token']
r = requests.post(root_uri + 'login', json={'email': 'ronny@mail.se', 'password': 'password'})
token_ronny = json.loads(r.text)['access_token']
r = requests.post(root_uri + 'login', json={'email': 'frida@mail.se', 'password': 'password'})
token_frida = json.loads(r.text)['access_token']
r = requests.post(root_uri + 'login', json={'email': 'kalle@mail.se', 'password': 'password'})
token_kalle = json.loads(r.text)['access_token']
r = requests.post(root_uri + 'login', json={'email': 'jonas@mail.se', 'password': 'password'})
token_jonas = json.loads(r.text)['access_token']
r = requests.post(root_uri + 'login', json={'email': 'britta@mail.se', 'password': 'password'})
token_britta = json.loads(r.text)['access_token']

# CREATING POSTS
requests.post(root_uri + 'create-post', json={'text': 'Första inlägget...', 'city': ''}, headers={'Authorization': 'Bearer ' + token_ronny})
requests.post(root_uri + 'create-post', json={'text': 'Älskar sommaren!', 'city': ''}, headers={'Authorization': 'Bearer ' + token_britta})
requests.post(root_uri + 'create-post', json={'text': 'Den här appen är verkligen den mest sociala appen någonsin (y)', 'city': ''}, headers={'Authorization': 'Bearer ' + token_kalle})
requests.post(root_uri + 'create-post', json={'text': 'Vad händer idag då?', 'city': ''}, headers={'Authorization': 'Bearer ' + token_kalle})
requests.post(root_uri + 'create-post', json={'text': 'Hallå någon där?', 'city': ''}, headers={'Authorization': 'Bearer ' + token_kalle})
requests.post(root_uri + 'create-post', json={'text': 'Börjar ångra det jag skrev först :S', 'city': ''}, headers={'Authorization': 'Bearer ' + token_kalle})
requests.post(root_uri + 'create-post', json={'text': 'Chilla Kalle vi är här, du är inte ensam...', 'city': ''}, headers={'Authorization': 'Bearer ' + token_ronny})
requests.post(root_uri + 'create-post', json={'text': 'Så varmt i Paris idag :D', 'city': 'Paris'}, headers={'Authorization': 'Bearer ' + token_frida})
requests.post(root_uri + 'create-post', json={'text': 'Känslan när kalendern tyder på sommar men vädret tyder på vinter.. Welcome to Sweden :/', 'city': 'Paris'}, headers={'Authorization': 'Bearer ' + token_frida})
requests.post(root_uri + 'create-post', json={'text': 'Semester med Frida <3', 'city': 'Paris'}, headers={'Authorization': 'Bearer ' + token_jonas})
requests.post(root_uri + 'create-post', json={'text': 'När bakfyllan börjar och man ångrar de sista öl man tog :S', 'city': 'Köpenhamn'}, headers={'Authorization': 'Bearer ' + token_ronny})
requests.post(root_uri + 'create-post', json={'text': 'Är det bara jag eller borde inte alla byta från facebook till Super Social App, asgrym ju :P', 'city': ''}, headers={'Authorization': 'Bearer ' + token_kalle})


# CREATING COMMENTS
requests.post(root_uri + 'create-comment/8', json={'text': 'Skit på dig'}, headers={'Authorization': 'Bearer ' + token_kalle})
requests.post(root_uri + 'create-comment/8', json={'text': 'Moget kalle..'}, headers={'Authorization': 'Bearer ' + token_jonas})
requests.post(root_uri + 'create-comment/8', json={'text': 'Hoppas ni har det bra! :*'}, headers={'Authorization': 'Bearer ' + token_britta})

# LIKE POSTS:
new = 12
old = []
while new not in old:
    requests.post(root_uri + 'like-post/' + str(new), headers={'Authorization': 'Bearer ' + token_ronny})
    old.append(new)
    new = random.randint(1,12)


new = 12
old = []
while new not in old:
    requests.post(root_uri + 'like-post/' + str(new), headers={'Authorization': 'Bearer ' + token_frida})
    old.append(new)
    new = random.randint(1,12)


new = 12
old = []
while new not in old:
    requests.post(root_uri + 'like-post/' + str(new), headers={'Authorization': 'Bearer ' + token_kalle})
    old.append(new)
    new = random.randint(1,12)


new = 12
old = []
while new not in old:
    requests.post(root_uri + 'like-post/' + str(new), headers={'Authorization': 'Bearer ' + token_jonas})
    old.append(new)
    new = random.randint(1,12)


new = 12
old = []
while new not in old:
    requests.post(root_uri + 'like-post/' + str(new), headers={'Authorization': 'Bearer ' + token_britta})
    old.append(new)
    new = random.randint(1,12)

# SEND FRIENDREQUEST
requests.post(root_uri + 'send-friend-request/lisa@mail.se', headers={'Authorization': 'Bearer ' + token_frida})
requests.post(root_uri + 'send-friend-request/lisa@mail.se', headers={'Authorization': 'Bearer ' + token_jonas})
requests.post(root_uri + 'send-friend-request/lisa@mail.se', headers={'Authorization': 'Bearer ' + token_kalle})

# ACCEPT FRIENDREQUEST
requests.post(root_uri + 'accept-friend-request/jonas@mail.se', headers={'Authorization': 'Bearer ' + token_lisa})
requests.post(root_uri + 'accept-friend-request/frida@mail.se', headers={'Authorization': 'Bearer ' + token_lisa})
