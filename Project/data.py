from flask_sqlalchemy import SQLAlchemy
from flask import abort, Flask,g,render_template, url_for, jsonify, request
from functools import wraps
from itsdangerous import (TimedJSONWebSignatureSerializer as Serializer, BadSignature, SignatureExpired)
from Project import app
import uuid
from sqlalchemy import orm, or_
db = SQLAlchemy(application)
from werkzeug.security import generate_password_hash, check_password_hash

def initialize_db():
    db.drop_all()
    db.create_all()

class User(db.Model):
    __tablename__ = 'user
    anonymous_id = db.Column(db.Integer, unique=True, primary_key=True)
    first_name = db.Column(db.String)
    surname = db.Column(db.String)
    date_of_birth = db.Column(db.Integer, unique=True)
    domicile = db.Column(db.String)
    description = db.Column(db.String)
    email = db.Column(db.String, unique=True)

    def __init__(self, first_name, surname, date_of_birth, domicile, description, email):
        self.anonymous_id = uuid.uuid4().int
        self.first_name = first_name
        self.surname = surname
        self.date_of_birth = date_of_birth
        self.domicile = domicile
        self.description = description
        self.email = email

    def send_friend_request(self, requested):
        self.anonymous_id
        requested

    def get_friend_requsts(self):
        self.anonymous_id

    def accept_friend_request(self, requester):
        self.anonymous_id
        requester

    def deny_friend_request(self, requester):
        self.anonymous_id
        requester

    def get_number_of_friends(self):
        self.anonymous_id

    def block_user(self, user_to_block):
        self.anonymous_id
        user_to_block

    def get_unread_messages(self, mailer):
        self.anonymous_id

    def get_all_unread(self):
        self.anonymous_id

    def get_messages(self, mailer):
        self.anonymous_id

    def send_message(self, receiver, text):
        self.anonymous_id
        receiver
        Message(text)


class Message(db.Model):
    __tablename__ = 'message'
    message_id = db.Column(db.Integer, unique=True, primary_key=True)
    text = db.Column(db.String(140))

    def __init__(self, text):
        self.message_id = uuid.uuid4().int
        self.text = text


def match_users():


# Tables
read_by = db.Table('read_by', db.Column('message_id', db.Integer, db.ForeignKey('message.id')),
                   db.Column('user_id', db.Integer, db.ForeignKey('user.id')))


class User(db.Model):
    __tablename__ = 'user'
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String, unique=True)
    pw_hash = db.Column(db.String(160))
    messages = db.relationship('Message', secondary=read_by, backref=db.backref('users', lazy='dynamic'))

    def __init__(self, username, password):
        self.username = username
        self.set_password(password)

    def set_password(self, password):
        self.pw_hash=generate_password_hash(password)

    def check_password(self, password):
        return check_password_hash(self.pw_hash, password)

    def generate_auth_token(self, expiration=600):
        s = Serializer(application.config['SECRET_KEY'], expires_in=expiration)
        return s.dumps({'id': self.username})

# länk till flask salted passwords : (http://flask.pocoo.org/snippets/54/)
# Kolla om användare är inloggad: http://flask-login.readthedocs.io/en/latest/#login-example


class Message(db.Model):
    __tablename__ = 'message'
    id = db.Column(db.Integer, primary_key=True)
    message_id = db.Column(db.Integer, unique=True)
    message = db.Column(db.String(140))

    def __init__(self, message_id, message):
        self.message_id = message_id
        self.message = message

def verify_auth_token(token):
    s = Serializer(application.config['SECRET_KEY'])
    try:
        data = s.loads(token)
    except SignatureExpired:
        return None
    except BadSignature:
        return None
    user = User.query.get(data['id'])
    return user

def verify_login(func):
    @wraps(func)
    def wrapper(*args, **kwargs):
        try:
            token = request.headers['Authorization']
        except KeyError:
            abort(401)
        g.user = verify_auth_token(token)
        if not g.user:
            return abort(401)
        return func(*args, **kwargs)
    return wrapper

# Functions
def initialize_db():
    db.drop_all()
    db.create_all()


def save_user(user_id):
    try:
        user = User.query.filter_by(user_id=user_id).one()
        return abort(400)  # User already exists
    except orm.exc.NoResultFound:
        user = User(user_id)
        new = User(user_id)
        db.session.add(new)
        db.session.commit()
        return ''

def get_users():
    users = User.query.all()
    user_list = []
    for user in users:
        user_list.append(user.user_id)
    return user_list


def mark_read(message_id, user_id):
    try:
        message = Message.query.filter_by(message_id=message_id).one()  # Parent
    except orm.exc.NoResultFound:
        return abort(400)
    try:
        user = User.query.filter_by(user_id=user_id).one()  # Child
    except orm.exc.NoResultFound:
        user = User(user_id)  # Child
    message.users.append(user)
    db.session.add(user)
    db.session.commit()
    # http://docs.sqlalchemy.org/en/rel_0_9/orm/basic_relationships.html#association-object
    return ''


def get_unread(user_id):
    try:
        table_id = User.query.filter_by(user_id=user_id).one().id
    except orm.exc.NoResultFound:
        return abort(400)
    messages = Message.query.filter(or_(Message.users.any(User.id != table_id), ~Message.users.any())).all()
    # Alla meddelanden som inte har lästs av user_id samt alla meddelanden som inte har lästs av någon
    message_list = []
    for message in messages:
        read_by_list = []
        users = User.query.filter(User.messages.any(Message.id == message.id)).all()
        # Alla användare som har läst meddelandet
        for user in users:
            read_by_list.append(user.user_id)
        message_list.append({'id': message.message_id, 'message': message.message, 'readBy': read_by_list})
    return str(message_list)


def get_all():
    message_list = []
    messages = Message.query.all()
    for message in messages:
        read_by_list = []
        users = User.query.filter(User.messages.any(Message.id == message.id)).all()
        # Alla användare som har läst meddelandet
        for user in users:
            read_by_list.append(user.user_id)
        message_list.append({'id': message.message_id, 'message': message.message, 'readBy': read_by_list})
    return str(message_list)


def save_message(message):
    message_id = str(uuid.uuid4().int)
    new = Message(message_id, message)
    db.session.add(new)
    db.session.commit()
    return message_id


def get_message(message_id):
    try:
        message = Message.query.filter_by(message_id=message_id).one()
    except orm.exc.NoResultFound:
        return abort(400)
    read_by_list = []
    users = User.query.filter(User.messages.any(Message.id == message.id)).all()
    # Alla användare som har läst meddelandet
    for user in users:
        read_by_list.append(user.user_id)
    return str({'id': message.message_id, 'message': message.message, 'readBy': read_by_list})


def delete_message(message_id):
    try:
        message = Message.query.filter_by(message_id=message_id).one()
    except orm.exc.NoResultFound:
        return abort(400)
    db.session.delete(message)
    # http://docs.sqlalchemy.org/en/rel_0_9/orm/basic_relationships.html#deleting-rows-from-the-many-to-many-table
    db.session.commit()
    return ''


def add_user(name, password):
    user = User(name, password)
    db.session.add(user)
    db.session.commit()
    return 'User created'

