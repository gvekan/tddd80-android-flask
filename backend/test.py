from backend import data, config

import requests

data.initialize_db()

root_uri = 'http://localhost:' + str(config.port) + '/'
r1 = requests.post(root_uri + 'create', json={'username': 'gusan092', 'password': 'password', 'email': 'gusan092@student.liu.se', 'first_name': 'Gustav', 'surname': 'Andersson', 'birth_date': '1996-05-08', 'domicile': 'Linkoping', 'description': 'test'})
print(r1.text)