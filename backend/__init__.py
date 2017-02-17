from backend import config
from flask import Flask

application = Flask(__name__)
application.config['SQLALCHEMY_DATABASE_URI'] = config.sqldb_uri
application.config['SECRET_KEY'] = 'Gustav'

import backend.server
