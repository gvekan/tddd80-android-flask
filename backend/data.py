from backend import application

from flask_sqlalchemy import SQLAlchemy
from werkzeug.security import generate_password_hash, check_password_hash

db = SQLAlchemy(application)

def initialize_db():
    db.session.remove()
    db.drop_all()
    db.create_all()


chat_members = db.Table('chat_members',
    db.Column('user_id', db.Integer, db.ForeignKey('user.id')),
    db.Column('chat_id', db.Integer, db.ForeignKey('chat.id'))
)

class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    email = db.Column(db.String, unique=True, nullable=False)
    pw_hash = db.Column(db.String, nullable=False)
    first_name = db.Column(db.String, nullable=False)
    last_name = db.Column(db.String, nullable=False)
    city = db.Column(db.String, nullable=False)

    posts = db.relationship("Post", backref="user", lazy='dynamic')
    comments = db.relationship("Comment", backref="user", lazy='dynamic')
    messages = db.relationship("Message", backref="user", lazy="dynamic")

    def __init__(self, email, password, first_name, last_name, city):
        self.email = email
        self.set_password(password)
        self.first_name = first_name
        self.last_name = last_name
        self.city = city

    def set_password(self, password):
        self.pw_hash = generate_password_hash(password)

    def check_password(self, password):
        return check_password_hash(self.pw_hash, password)


def register_user(email, password, first_name, last_name, city):
    """
    Creates a user
    """
    user = User(email, password, first_name, last_name, city)
    db.session.add(user)
    db.session.commit()
    return 'User created'

class Chat(db.Model):
    id = db.Column(db.Integer, primary_key=True)

    user_id = db.Column(db.Integer, db.ForeignKey('user.id'))

    #chat_memb = db.relationship('user', secondary=chat_members, backref=db.backref('chats', lazy='dynamic'))

    messages = db.relationship('Message', backref='Chat', lazy='dynamic')

    def __init__(self, receiver):
        self.receiver_id = receiver


def start_chat(user, friend):
    chat = Chat(user, friend)
    db.session.add(chat)
    db.session.commit()
    return 'Chat started'

def get_sent_messages(user, receiver):
    messages = Message.query.filter_by(user=user_id, receiver=receiver_id).all()
    return messages

def get_received_messages(user, receiver):
    messages = Message.query.filter_by(user=receiver_id, receiver=user_id).all()
    return messages


class Message(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    text = db.Column(db.String, nullable=False)

    user_id = db.Column(db.Integer, db.ForeignKey('user.id'))

    def __init__(self, text, receiver):
        self.text = text
        self.receiver_id = receiver


class Post(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    text = db.Column(db.String,  nullable=False)
    # posted_at = db.Column(db.DateTime)

    comments = db.relationship("Comment", backref="post", lazy='dynamic')

    user_id = db.Column(db.Integer, db.ForeignKey('user.id'))

    def __init__(self, text):
        self.text = text
        # self.posted_at = datetime.now()


def create_post(user, text):
    post = Post(text)
    user.posts.append(post)
    db.session.add(post)
    db.session.commit()
    return 'Post created'


def get_latest_posts(oldest):
    latest = Post.query.count() + 1
    if oldest == -1:
        if latest < 11:
            oldest = 1
        else:
            oldest = latest - 10
    return get_latest_posts_from(latest, oldest)


def get_latest_posts_from(latest, oldest):
    """
    :param latest:
    :return:the 10 latest posts from latest (latest not included)
    """
    # posts = Post.query.order_by(desc(Post.posted_at)).limit(10).all()

    posts = Post.query.filter(Post.id.in_(range(oldest,latest))).all()
    response = []  # http://stackoverflow.com/questions/13530967/parsing-data-to-create-a-json-data-object-with-python
    for i in range(len(posts)):
        post = posts[i]
        response.append({'post': {'id': post.id, 'name': post.user.first_name+' '+post.user.last_name, 'text': post.text}})
    return response


class Comment(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    text = db.Column(db.String, unique=False, nullable=False)
    index = db.Column(db.Integer, unique=False, nullable=False)

    user_id = db.Column(db.Integer, db.ForeignKey('user.id'))

    post_id = db.Column(db.Integer, db.ForeignKey('post.id'))

    def __init__(self, text, index):
        self.text = text
        self.index = index


def create_comment(user, post, text):
    comment = Comment(text, post.comments.count())
    user.comments.append(comment)
    post.comments.append(comment)
    db.session.add(comment)
    db.session.commit()
    return 'Comment created'


def get_latest_comments(post):
    return get_latest_comments_from(post, post.comments.count() + 1)


def get_latest_comments_from(post, latest):
    """
    :param latest:
    :return:the 10 latest posts from latest (latest not included)
    """
    # posts = Post.query.order_by(desc(Post.posted_at)).limit(10).all()
    if latest < 10:
        oldest = 0
    else:
        oldest = latest - 10

    comments = Comment.query.filter(Comment.index.in_(range(oldest,latest)), Comment.post_id == post.id).all()
    response = []  # http://stackoverflow.com/questions/13530967/parsing-data-to-create-a-json-data-object-with-python
    for i in range(len(comments)):
        comment = comments[i]
        response.append({'comment': {'id': comment.id, 'index': comment.index, 'name': comment.user.first_name+' '+comment.user.last_name, 'text': comment.text}})
    return response

    # friends = db.relationship('User',
    #                            secondary=friendships,
    #                            primaryjoin=(friendships.c.Requester == id),
    #                            secondaryjoin=(friendships.c.Requested == id),
    #                            backref=db.backref('friendships', lazy='dynamic'),
    #                            lazy='dynamic')
    #
    # blocked = db.relationship('User',
    #                           secondary=blocked_users,
    #                           primaryjoin=(blocked_users.c.User == id),
    #                           secondaryjoin=(blocked_users.c.Blocked == id),
    #                           backref=db.backref('blocked_users', lazy='dynamic'),
    #                           lazy='dynamic')

    # def send_friend_request(self, other_id):
    #     """
    #     Send a request to a user
    #     """
    #     other = get_user(other_id)
    #     self.friends.append(other)
    #     return 'Friend request sent'
    #
    # def are_friends(self, other_id):
    #     """
    #     If both users has a friend request sent to
    #     each other, they are friends
    #     """
    #     other = get_user(other_id)
    #     return self.friend_request_sent(other) and other.friend_request_sent(self)
    #
    # def friend_request_sent(self, other_id):
    #     """
    #     Check if a user has sent a request to another user
    #     """
    #     return self.friends.filter(friendships.c.Requested == other_id).count() > 0
    #
    # def remove_friend_request(self, other_id):
    #     """
    #     Remove a friend request. (Also used to
    #     remove a friend.)
    #     """
    #     other = get_user(other_id)
    #     if self.are_friends(other_id):
    #         other.friends.remove(self)
    #     self.friends.remove(other)
    #     return ''
    #
    # def get_number_of_requests(self):
    #     """
    #     Get the number of friend requests
    #     """
    #     return User.friends.filter(friendships.c.Requested == self.id).count()
    #
    # def get_number_of_friends(self):
    #     """
    #     Get the number of friends a user has
    #     """
    #     others = User.friends.filter(friendships.c.Requested == self.id).all()
    #     count = 0
    #     for other in others:
    #         if self.are_friends(other.id):
    #             count += 1
    #     return count
    #
    # def block_user(self, other_id):
    #     """
    #     Block a user. If they are friends,
    #     also remove the friendship
    #     """
    #     other = get_user(other_id)
    #     if self.are_friends(other_id):
    #         self.remove_friend_request(other_id)
    #         other.remove_friend_request(self)
    #     self.blocked.append(other_id)
    #     return 'User blocked'
    #
    # def get_messages(self, sender_id):
    #     """
    #     Get all messages from a user
    #     """
    #     messages = Message.query.filter_by(receiver=self.id, sender=sender_id).all()
    #     return messages
    #
    # def send_message(self, receiver_id, text):
    #     """
    #     Send message to a user
    #     """
    #     message = Message(self.id, receiver_id, text)
    #     db.session.add(message)
    #     db.session.commit(message)
    #     return 'Message sent'
    #
    # def mark_read(self, sender_id):
    #     """
    #     Mark all messages from a user as read
    #     """
    #     messages = Message.query.filter_by(receiver=self.id, sender=sender_id).all()
    #     for message in messages:
    #         message.read = True
    #     db.session.commit()
    #     return 'Message read'
    #
    # def get_number_of_unread(self):
    #     """
    #     Get the number of unread messages from all users
    #     """
    #     return Message.query.filter_by(receiver=self.id, read=False).count()

# Tables
# friendships = db.Table('friendships',
#                 db.Column('Requester', db.Integer, db.ForeignKey('user.id')),
#                 db.Column('Requested', db.Integer, db.ForeignKey('user.id')))
#
# blocked_users = db.Table('blocked',
#                     db.Column('User', db.Integer, db.ForeignKey('user.id')),
#                     db.Column('Blocked', db.Integer, db.ForeignKey('user.id')))


# class Message(db.Model):
#     id = db.Column(db.Integer, primary_key=True)
#     sender_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
#     receiver_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
#     read = db.Column(db.Boolean, nullable=False)
#     text = db.Column(db.String, nullable=False)
#     # Add a timestamp
#
#     sender = db.relationship('User', foreign_keys=[sender_id], backref='sent')
#     receiver = db.relationship('User', foreign_keys=[receiver_id], backref='received')
#
#     def __init__(self, sender_id, receiver_id, text):
#         self.sender_id = sender_id
#         self.receiver_id = receiver_id
#         self.text = text
#         self.read = False
