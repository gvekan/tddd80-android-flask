from backend import application
from flask_sqlalchemy import SQLAlchemy
import uuid
from werkzeug.security import generate_password_hash, check_password_hash
db = SQLAlchemy(application)

def initialize_db():
    db.drop_all()
    db.create_all()

#Tables
friendships = db.Table('friendships', db.Column('user_id', db.Integer, db.ForeignKey('user.id')),
                   db.Column('user_id_other', db.Integer, db.ForeignKey('user.id')))

friend_requests = db.Table('friend_requests', db.Column('sender', db.Integer, db.ForeignKey('user.id')),
                    db.Column('receiver', db.Integer, db.ForeignKey('user.id')))

day_friends = db.Table('day_friends', db.Column('user_id', db.Integer, db.ForeignKey('user.id')),
                    db.Column('user_id_other', db.Integer, db.ForeignKey('user.id')))

blocked_friends = db.Table('blocked', db.Column('User', db.Integer, db.ForeignKey('user.id'))
                   db.Column('Blocked', db.Integer, db.ForeignKey('user.id')))

sent_messages = db.Table('sent_messages', db.Column('message', db.Integer, db.ForeignKey('message.id')),
                    db.Column('receiver', db.Integer, db.ForeignKey('user.id')))



class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, unique=True, primary_key=True)
    first_name = db.Column(db.String)
    surname = db.Column(db.String)
    date_of_birth = db.Column(db.Integer, unique=True)
    domicile = db.Column(db.String)
    description = db.Column(db.String)
    email = db.Column(db.String, unique=True)
    pw_hash = db.Column(db.String(160))

    friends = db.relationship('Friends',
                            secondary=friendships,
                            primaryjoin=(friendships.c.user_id == id),
                            secondaryjoin=(friendships.c.user_id_other == id),
                            backref=db.backref('friendships', lazy='dynamic'),
                            lazy='dynamic')

    requests = db.relationship('Requests',
                               secondary=friend_requests,
                               primaryjoin=(friend_requests.c.sender == id),
                               secondaryjoin=(friend_requests.c.receiver == id),
                               backref=db.backref('friend_requests', lazy='dynamic'),
                               lazy='dynamic')

    friends_today = db.relationship('Friends today',
                                    secondary=day_friends,
                                    primaryjoin=(day_friends.c.user_id == id),
                                    secondaryjoin=(day_friends.c.user_id_other == id),
                                    backref=db.backref('day_friends', lazy='dynamic'),
                                    lazy='dynamic')

    blocked = db.relationship('Blocked friends',
                              secondary=blocked_friends,
                              primaryjoin=(blocked_friends.c.user_id == id),
                              secondaryjoin=(blocked_friends.c.user_id == id),
                              backref=db.backref('blocked_friends', lazy='dynamic'),
                              lazy='dynamic')

    messages = db.relationship('Message',
                                secondary=sent_messages,
                                backref=db.backref('users', lazy='dynamic'))





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


    def send_friend_request(self, requested):
        return None

    def get_friend_requests(self):
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
        message = Message(text)

        return None


class Message(db.Model):
    message_id = db.Column(db.Integer, unique=True, primary_key=True)
    sender_id = db.Column(db.Integer, db.ForeignKey('users.user_id'), nullable=False)
    receiver_id = db.Column(db.Integer, db.ForeignKey('users.user_id'), nullable=False)
    text = db.Column(db.String)

    def __init__(self, text):
        self.message_id = uuid.uuid4().int
        self.text = text

def match_users(user_id, user_id_other):
    user1 = User.query.filter()
    return

def add_user(name, password):
    user = User(name, password)
    db.session.add(user)
    db.session.commit()
    return 'User created'