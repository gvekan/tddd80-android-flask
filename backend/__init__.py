import datetime

from backend import config
import simplekv.memory
from flask import Flask


application = Flask(__name__)
application.config['SECRET-KEY'] = 'super-secret'

application.config['SQLALCHEMY_DATABASE_URI'] = config.db_uri
application.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = True
# Enable and configure the JWT blacklist / token revoke. We are using
# an in memory store for this example. In production, you should
# use something persistent (such as redis, memcached, sqlalchemy).
# See here for options: http://pythonhosted.org/simplekv/
application.config['JWT_BLACKLIST_ENABLED'] = True
application.config['JWT_BLACKLIST_STORE'] = simplekv.memory.DictStore()

# Check all tokens (access and refresh) to see if they have been revoked.
# You can alternately check only the refresh tokens here, by setting this
# to 'refresh' instead of 'all'
application.config['JWT_BLACKLIST_TOKEN_CHECKS'] = 'all'
application.config['JWT_ACCESS_TOKEN_EXPIRES'] = datetime.timedelta(minutes=5)

import backend.server
