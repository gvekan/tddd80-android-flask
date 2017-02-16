import os
import Project
import unittest
import tempfile
from Project import data
import json

class FlaskrTestCase(unittest.TestCase):

    def setUp(self):
        self.db_fd, Project.app.config['DATABASE'] = tempfile.mkstemp()
        Project.app.config['TESTING'] = True
        self.app = Project.app.test_client()
        with Project.app.app_context():
            data.initialize_db()

    def tearDown(self):
        os.close(self.db_fd)
        os.unlink(Project.app.config['DATABASE'])