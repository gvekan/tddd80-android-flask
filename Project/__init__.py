from flask import Flask
from Project import config
app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = config.sqldb_uri
app.config['SECRET_KEY'] = 'Gustav'
import Project.server
