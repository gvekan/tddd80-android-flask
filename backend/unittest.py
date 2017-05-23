import os
import backend
import unittest
import tempfile
from backend import data, config
import json

root_uri = 'http://localhost:' + str(config.port) + '/'


class FlaskTestCase(unittest.TestCase):
    def setUp(self):
        self.db_fd, backend.application.config['DATABASE'] = tempfile.mkstemp()
        backend.application.config['TESTING'] = True
        self.app = backend.application.test_client()
        with backend.application.app_context():
            data.initialize_db()

    def tearDown(self):
        os.close(self.db_fd)
        os.unlink(backend.application.config['DATABASE'])

    def test_register(self):
        rv = self.app.post(root_uri + 'register', data=json.dumps({'email': 'gusan092@student.liu.se',
                                                                   'password': 'password',
                                                                   'first_name': 'Gustav',
                                                                   'last_name': 'Andersson', 'city': 'Linkoping'}),
                           content_type='application/json')
        assert b'User created' in rv.data
        rv = self.app.post(root_uri + 'register', data=json.dumps({'email': 'gusan092@student.liu.se',
                                                                   'password': 'password',
                                                                   'first_name': 'Gustav',
                                                                   'last_name': 'Andersson', 'city': 'Linkoping'}),
                           content_type='application/json')
        assert b'Email taken' in rv.data

    def test_login(self):
        rv = self.app.post(root_uri + 'register', data=json.dumps({'email': 'gusan092@student.liu.se',
                                                                   'password': 'password',
                                                                   'first_name': 'Gustav',
                                                                   'last_name': 'Andersson',
                                                                   'city': 'Linkoping'}),
                           content_type='application/json')
        assert b'User created' in rv.data
        rv = self.app.post(root_uri + 'login', data=json.dumps({'email': 'gusan092@student.liu.se',
                                                                'password': 'password'}),
                           content_type='application/json')
        assert b'Bad email or password' not in rv.data
        rv = self.app.post(root_uri + 'login', data=json.dumps({'email': 'gusan092@student.liu.se',
                                                                'password': 'wrongpassword'}),
                           content_type='application/json')
        assert b'Bad email or password' in rv.data

    def test_posts(self):
        rv = self.app.post(root_uri + 'register', data=json.dumps({'email': 'gusan092@student.liu.se',
                                                                   'password': 'password', 'first_name': 'Gustav',
                                                                   'last_name': 'Andersson', 'city': 'Linkoping'}),
                           content_type='application/json')
        assert b'User created' in rv.data
        rv = self.app.post(root_uri + 'login', data=json.dumps({'email': 'gusan092@student.liu.se',
                                                                'password': 'password'}),
                           content_type='application/json')
        assert b'Bad email or password' not in rv.data
        token = json.loads(rv.data.decode("utf-8"))['access_token']
        rv = self.app.post(root_uri + 'create-post', data=json.dumps({'text': 'test'}),
                           content_type='application/json', headers={'Authorization': 'Bearer ' + token})
        assert b'Post successfully made' in rv.data
        rv = self.app.post(root_uri + 'create-comment/1', data=json.dumps({'text': 'test'}),
                           content_type='application/json', headers={'Authorization': 'Bearer ' + token})
        assert b'Comment successfully made' in rv.data
        rv = self.app.post(root_uri + 'like-post/1', headers={'Authorization': 'Bearer ' + token})
        assert b'Post liked' in rv.data
        rv = self.app.get(root_uri + 'get-latest-posts/-1', headers={'Authorization': 'Bearer ' + token})
        assert json.loads(rv.data.decode("utf-8"))['posts'][0]['likes'] == 1
        assert json.loads(rv.data.decode("utf-8"))['posts'][0]['comments'] == 1
        assert json.loads(rv.data.decode("utf-8"))['posts'][0]['id'] == 1
        assert json.loads(rv.data.decode("utf-8"))['posts'][0]['name'] == 'Gustav Andersson'
        assert json.loads(rv.data.decode("utf-8"))['posts'][0]['liking'] == True
        assert json.loads(rv.data.decode("utf-8"))['posts'][0]['text'] == 'test'

        rv = self.app.get(root_uri + 'get-latest-comments/1/-1', headers={'Authorization': 'Bearer ' + token})
        assert json.loads(rv.data.decode("utf-8"))['comments'][0]['index'] == 1
        assert json.loads(rv.data.decode("utf-8"))['comments'][0]['name'] == 'Gustav Andersson'
        assert json.loads(rv.data.decode("utf-8"))['comments'][0]['text'] == 'test'
