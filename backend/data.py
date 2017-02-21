from backend import application
from flask_sqlalchemy import SQLAlchemy
import uuid
from werkzeug.security import generate_password_hash, check_password_hash
db = SQLAlchemy(application)


def initialize_db():
    db.session.remove()
    db.drop_all()
    db.create_all()

    create_user("Simon", "Sundberg", "hej123", 940216, "hej@example.com")
    create_user("Gustav", "Andersson", "tja123", 960311, "tja@example.com")

# Tables
friendships = db.Table('friend_requests',
                db.Column('Requester', db.Integer, db.ForeignKey('user.id')),
                db.Column('Requested', db.Integer, db.ForeignKey('user.id')))

temp_friendships = db.Table('temp_friendships',
                    db.Column('User_2', db.Integer, db.ForeignKey('user.id')),
                    db.Column('User_1', db.Integer, db.ForeignKey('user.id')))

blocked_users = db.Table('blocked',
                    db.Column('User', db.Integer, db.ForeignKey('user.id')),
                    db.Column('Blocked', db.Integer, db.ForeignKey('user.id')))


class Message(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    extra_id = db.Column(db.Integer, unique=True, primary_key=True)
    sender_id = db.Column(db.Integer, db.ForeignKey('users.user_id'), nullable=False)
    receiver_id = db.Column(db.Integer, db.ForeignKey('users.user_id'), nullable=False)
    read = db.Column(db.Boolean, nullable=False)
    text = db.Column(db.String, nullable=False)
    # Add timestamp

    #sender = db.relationship(User, foreign_keys=[sender_id], backref='sent')
    #receiver = db.relationship(User, foreign_keys=[receiver_id], backref='received')

    def __init__(self, sender_id, receiver_id, text):
        self.extra_id = uuid.uuid4().int
        self.sender_id = sender_id
        self.receiver_id = receiver_id
        self.text = text
        self.read = False


class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.Integer, unique=True, nullable=False)
    first_name = db.Column(db.String, nullable=False)
    surname = db.Column(db.String, nullable=False)
    birthdate = db.Column(db.Integer, nullable=False)
    domicile = db.Column(db.String, nullable=False)
    description = db.Column(db.String)
    email = db.Column(db.String, unique=True, nullable=False)
    pw_hash = db.Column(db.String, nullable=False)

    def __init__(self, first_name, surname, password, birthdate, email):
        self.anonymous_id = uuid.uuid4().int
        self.pw_hash = generate_password_hash(password)
        self.first_name = first_name
        self.surname = surname
        self.birthdate = birthdate
        self.email = email

    friends = db.relationship('Requests',
                               secondary=friendships,
                               primaryjoin=(friendships.c.Requester == id),
                               secondaryjoin=(friendships.c.Requested == id),
                               backref=db.backref('friendships', lazy='dynamic'),
                               lazy='dynamic')
    """
    temp_friends = db.relationship('Temporary friend',
                                    secondary=temp_friendships,
                                    primaryjoin=(temp_friendships.c.User_1 == id),
                                    secondaryjoin=(temp_friendships.c.User_2== id),
                                    backref=db.backref('temp_friendships', lazy='dynamic'),
                                    lazy='dynamic')
    """
    blocked = db.relationship('Blocked users',
                              secondary=blocked_users,
                              primaryjoin=(blocked_users.c.User == id),
                              secondaryjoin=(blocked_users.c.Blocked == id),
                              backref=db.backref('blocked_user', lazy='dynamic'),
                              lazy='dynamic')

    def check_password(self, password):
        return check_password_hash(self.pw_hash, password)


    def send_friend_request(self, other_id):
        """
        Send a request to a user
        """
        other = get_user(other_id)
        if not self.friend_request_sent(other):
            self.friends.append(other)
            if self.are_friends(other_id):
                return 'You are now friends!'
            else:
                return 'Friend request sent!'


    def are_friends(self, other_id):
        """
        If both users has a friend request sent to
        each other, they are friends
        """
        other = get_user(other_id)
        return self.friend_request_sent(other) and other.friend_request_sent(self)


    def get_friend_requests(self):
        """
        Get all friend requests
        """
        requests = User.friends.filter(friendships.c.Requested == self.id).all()
        return requests


    def friend_request_sent(self, other_id):
        """
        Check if a user has sent a request to another user
        """
        return self.friends.filter(friendships.c.Requested == other_id).count() > 0


    def remove_friend_request(self, other_id):
        """
        Remove a friend request. (Also used to
        remove a friend.)
        """
        other = get_user(other_id)
        if self.are_friends(other_id):
            other.friends.remove(self)
        self.friends.remove(other)
        return ''


    def get_number_of_friends(self):
        """
        Get the number of friends a user has
        """
        others = User.friends.filter(friendships.c.Requested == self.id).all()
        count = 0
        for other in others:
            if self.are_friends(other.id):
                count += 1
        return count


    def block_user(self, other_id):
        """
        Block a user. If they are friends,
        also remove the friendship
        """
        other = get_user(other_id)
        if self.are_friends(other_id):
            self.remove_friend_request(other_id)
            other.remove_friend_request(self)
        self.blocked.append(other_id)
        return 'User blocked'


    def get_messages(self, sender_id):
        """
        Get all messages
        """
        messages = User.query.filter_by(receiver=self.id, sender=sender_id).all()
        return messages


    def send_message(self, receiver_id, text):
        """
        Send message to a user
        """
        message = Message(self.id, receiver_id, text)
        db.session.add(message)
        db.session.commit(message)
        return ''


    def mark_read(self, sender_id):
        """
        Mark a message as read
        """
        messages = User.query.filter_by(received=self.id, sent=sender_id).all()
        for message in messages:
            message.read = True
        db.session.commit()
        return 'Message read'


def create_user(first_name, surname, password, birthdate, email):
    """
    Creates a user
    """
    user = User(first_name, surname, password, birthdate, email)
    db.session.add(user)
    db.session.commit()
    return 'User created'


def get_user(user):
    """
    Transform an user id to an user object
    """
    return User.query.filter_by(id=user).first()
