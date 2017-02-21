from backend import application
from flask_sqlalchemy import SQLAlchemy, orm, abort
import uuid
from werkzeug.security import generate_password_hash, check_password_hash
db = SQLAlchemy(application)


def initialize_db():
    db.drop_all()
    db.create_all()

# Tables
friendships = db.Table('friend_requests', db.Column('Requester', db.Integer, db.ForeignKey('user.id')),
                    db.Column('Requested', db.Integer, db.ForeignKey('user.id')))

temp_friendships = db.Table('temp_friendships', db.Column('User_2', db.Integer, db.ForeignKey('user.id')),
                    db.Column('User_1', db.Integer, db.ForeignKey('user.id')))

blocked_users = db.Table('blocked', db.Column('User', db.Integer, db.ForeignKey('user.id')),
                   db.Column('Blocked', db.Integer, db.ForeignKey('user.id')))


class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.Integer, unique=True, nullable=False)
    first_name = db.Column(db.String, nullable=False)
    surname = db.Column(db.String, nullable=False)
    date_of_birth = db.Column(db.Integer, nullable=False)
    domicile = db.Column(db.String, nullable=False)
    description = db.Column(db.String)
    email = db.Column(db.String, unique=True, nullable=False)
    pw_hash = db.Column(db.String, nullable=False)

    friends = db.relationship('Requests',
                               secondary=friendships,
                               primaryjoin=(friendships.c.Requester == id),
                               secondaryjoin=(friendships.c.Requested == id),
                               backref=db.backref('friendships', lazy='dynamic'),
                               lazy='dynamic')

    temp_friend = db.relationship('Temporary friend',
                                    secondary=temp_friendships,
                                    primaryjoin=(temp_friendships.c.user_id == id),
                                    secondaryjoin=(temp_friendships.c.user_id_other == id),
                                    backref=db.backref('temp_friend', lazy='dynamic'),
                                    lazy='dynamic')

    blocked = db.relationship('Blocked users',
                              secondary=blocked_users,
                              primaryjoin=(blocked_users.c.user_id == id),
                              secondaryjoin=(blocked_users.c.user_id == id),
                              backref=db.backref('blocked_user', lazy='dynamic'),
                              lazy='dynamic')


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
        requested = User.query.filter_by(id=requested).first()
        self.requests.append(requested)
        db.session.add(requested)
        db.session.commit()
        return ''

    def get_friend_requests(self):
        requests = friendships.query.filter_by(id=friendships.recuested).all()
        return requests

    def accept_friend_request(self, requester_id):
        requester = User.query.filter_by(id=requester_id).first()
        self.friends.append(requester)
        db.session.add(requester)
        db.session.commit()
        return ''

    def deny_friend_request(self, requester):
        return None

    def get_number_of_friends(self):
        return None

    def block_user(self, user_to_block):
        return None

    def get_unread_messages(self, sender):
        return None

    def get_all_unread(self):
        return None

    def get_messages(self, mailer):
        return None

    def send_message(self, receiver, text):
        message = Message(self.id, receiver, text)
        return ''


class Message(db.Model):
    message_id = db.Column(db.Integer, unique=True, primary_key=True)
    sender_id = db.Column(db.Integer, db.ForeignKey('users.user_id'), nullable=False)
    receiver_id = db.Column(db.Integer, db.ForeignKey('users.user_id'), nullable=False)
    read = db.Column(db.Boolean, nullable=False)
    text = db.Column(db.String, nullable=False)

    sender = db.relationship(User, foreign_keys=[sender_id], backref='sent')
    receiver = db.relationship(User, foreign_keys=[receiver_id], backref='received')

    def __init__(self, sender_id, receiver_id, text):
        self.message_id = uuid.uuid4().int
        self.sender_id = sender_id
        self.receiver_id = receiver_id
        self.read = False
        self.text = text


def match_users(user_id, user_id_other):
    try:
        user1 = User.query.filter_by(id=user_id).one()
    except orm.exc.NoResultFound:
        return abort(400)
    try:
        user2 = User.query.filter_by(id=user_id_other).one()
    except orm.exc.NoResultFound:
        return abort(400)
    user1.temp_friends.append(user2)
    db.session.add(user2)
    db.session.commit()
    return ''


def add_user(firstname, surname, date_of_birth, domicile, description, email):
    user = User(firstname, surname, date_of_birth, domicile, description, email)
    db.session.add(user)
    db.session.commit()
    return 'User created'
