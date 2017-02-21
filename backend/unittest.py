import os
import backend
import unittest
import tempfile
from backend import data
import json

class FlaskrTestCase(unittest.TestCase):

    def setUp(self):
        self.db_fd, backend.app.config['DATABASE'] = tempfile.mkstemp()
        backend.app.config['TESTING'] = True
        self.app = backend.app.test_client()
        with backend.app.app_context():
            data.initialize_db()

    def tearDown(self):
        os.close(self.db_fd)
        os.unlink(backend.app.config['DATABASE'])
