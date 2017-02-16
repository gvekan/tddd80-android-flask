from flask_sqlalchemy import SQLAlchemy
from flask import abort
from S2 import app
import uuid
from sqlalchemy import orm, or_
db = SQLAlchemy(app)

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
