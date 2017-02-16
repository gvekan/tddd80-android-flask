import os
port = 5001
basedir = os.path.abspath(os.path.dirname(__file__))
sqldb_uri = 'sqlite:///' + os.path.join(basedir, 'app.db')