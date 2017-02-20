from backend import application
from flask_sqlalchemy import SQLAlchemy
import uuid

db = SQLAlchemy(application)


#Tables
friendships = db.Table('friendships', db.Column('User', db.Integer, db.ForeignKey('user.anonymous_id')),
                   db.Column('Friends', db.Integer, db.ForeignKey('user.anonymous_id')))

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

    friends = db.relationship('Friends', secondary=friendships, primaryjoin=anonymous_id==friendships.backref=db.backref('friends', lazy='dynamic'))


    def __init__(self, first_name, surname, date_of_birth, domicile, description, email):
        self.anonymous_id = uuid.uuid4().int
        self.first_name = first_name
        self.surname = surname
        self.date_of_birth = date_of_birth
        self.domicile = domicile
        self.description = description
        self.email = email


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