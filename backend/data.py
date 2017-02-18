from backend import application
from flask_sqlalchemy import SQLAlchemy
import uuid
)from itsdangerous import (TimedJSONWebSignatureSerializer as Serializer, BadSignature, SignatureExpired
from werkzeug.security import generate_password_hash, check_password_hash
db = SQLAlchemy(application)


#Tables
friendships = db.Table('friendships', db.Column('User', db.Integer, db.ForeignKey('user.anonymous_id')),
                   db.Column('Friends', db.Integer, db.ForeignKey('user.anonymous_id')))

read_by = db.Table('read_by', db.Column('message_id', db.Integer, db.ForeignKey('message.id')),
                   db.Column('user_id', db.Integer, db.ForeignKey('user.anonymous_id')))

friend_requests = db.Table('friend_requests', db.Column('Sender', db.Integer, db.ForeignKey('user.anonymous_id')),
                    db.Column('Receiver', db.Integer, db.ForeignKey('user.anonymous_id')))

day_friends = db.Table('day_friends', db.Column('User', db.Integer, db.ForeignKey('user.anonymous_id')),
                    db.Column('Friend', db.Integer, db.ForeignKey('user.anonymous_id')))

blocked = db.Table('blocked', db.Column('User', db.Integer, db.ForeignKey('user.anonymous_id'))
                   db.Column('Blocked', db.Integer, db.ForeignKey('user.anonymous_id')))

sent_messages = db.Table('sent_messages', db.Column('Message', db.Integer, db.ForeignKey('message.id')),
                    db.Column('Receiver', db.Integer, db.ForeignKey('user.anonymous_id')))


def initialize_db():
    db.drop_all()
    db.create_all()


def match_users():
    return None


class User(db.Model):
    anonymous_id = db.Column(db.Integer, unique=True, primary_key=True)
    first_name = db.Column(db.String)
    surname = db.Column(db.String)
    date_of_birth = db.Column(db.Integer, unique=True)
    domicile = db.Column(db.String)
    description = db.Column(db.String)
    email = db.Column(db.String, unique=True)
    pw_hash = db.Column(db.String(160))

    friends = db.relationship('Friends', secondary=friendships,
                                        primaryjoin=anonymous_id==friendships.backref=db.backref('friends', lazy='dynamic'))


    def __init__(self, first_name, surname, date_of_birth, domicile, description, email):
        self.anonymous_id = uuid.uuid4().int
        self.first_name = first_name
        self.surname = surname
        self.date_of_birth = date_of_birth
        self.domicile = domicile
        self.description = description
        self.email = email


    def set_password(self, password):
        self.pw_hash=generate_password_hash(password)

    def check_password(self, password):
        return check_password_hash(self.pw_hash, password)


    def generate_auth_token(self, expiration=600):
        s = Serializer(app.config['SECRET_KEY'], expires_in=expiration)
        return s.dumps({'id': self.anonymous_id})


    def send_friend_request(self, requested):
        return None

    def get_friend_requsts(self):
        return None

    def accept_friend_request(self, requester):
        return None

    def deny_friend_request(self, requester):
        return None

    def get_number_of_friends(self):
        return None

    def block_user(self, user_to_block):
        return None

    def get_unread_messages(self, mailer):
        return None

    def get_all_unread(self):
        return None

    def get_messages(self, mailer):
        return None

    def send_message(self, receiver, text):
        return None


class Message(db.Model):
    message_id = db.Column(db.Integer, unique=True, primary_key=True)
    text = db.Column(db.String(140))

    def __init__(self, text):
        self.message_id = uuid.uuid4().int
        self.text = text


def verify_auth_token(token):
    s = Serializer(app.config['SECRET_KEY'])
    try:
        data = s.loads(token)
    except SignatureExpired:
        return None
    except BadSignature:
        return None
    user = User.query.get(data['id'])
    return user

def save_user(user_id):
    try:
        user = User.query.filter_by(user_id=user_id).one()
        return abort(400)  # User already exists
    except orm.exc.NoResultFound:
        new = User(user_id)
        db.session.add(new)
        db.session.commit()
        return ''

def get_users(): #Get a list of all users
    users = User.query.all()
    user_list = []
    for user in users:
        user_list.append(user.user_id)
    return user_list

def mark_read(message_id, user_id): #Mark a message as read by user
    try:
        message = Message.query.filter_by(message_id=message_id).one()  # Parent
    except orm.exc.NoResultFound:
        return abort(400)
    try:
        user = User.query.filter_by(anonymous_id=user_id).one()  # Child
    except orm.exc.NoResultFound:
        user = User(user_id)  # Child
    message.users.append(user)
    db.session.add(user)
    db.session.commit()
    # http://docs.sqlalchemy.org/en/rel_0_9/orm/basic_relationships.html#association-object
    return ''

def save_message(text):
    new = Message(text)
    db.session.add(new)
    db.session.commit()
    return new.message_id

def add_user(name, password):
    user = User(name, password)
    db.session.add(user)
    db.session.commit()
    return 'User created'