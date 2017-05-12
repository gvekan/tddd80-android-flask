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
                        db.Column('chat_id', db.Integer, db.ForeignKey('chat.id')))

friendships = db.Table('friendships',
                       db.Column('Requester', db.Integer, db.ForeignKey('user.id')),
                       db.Column('Requested', db.Integer, db.ForeignKey('user.id')))

blocked_users = db.Table('blocked',
                         db.Column('User', db.Integer, db.ForeignKey('user.id')),
                         db.Column('Blocked', db.Integer, db.ForeignKey('user.id')))



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


    def send_message(self, receiver, text):
        chat = Chat.query.filter(Chat.members.any(User.id == self.id), Chat.members.any(User.id == receiver.id)).first()
        if not chat:
            chat = Chat()
            chat.members.append(self)
            chat.members.append(receiver)
            db.session.add(chat)
        message = Message(text)
        chat.messages.append(message)
        self.messages.append(message)
        db.session.add(message)
        db.session.commit()
        return 'Chat started'

    def get_messages(self, receiver):
        chat = Chat.query.filter(Chat.members.any(User.id == self.id), Chat.members.any(User.id == receiver.id)).first()
        messages = Message.query.join(Chat.messages).filter(Chat.id == chat.id).all()
        response = []
        for message in messages:
            response.append({'message': message.text, 'id': message.sent_by})
        return response

    def get_chats(self):
        chats = Chat.query.join(User.chats).filter(User.id == self.id).all()
        response = []
        for chat in chats:
            friends = User.query.join(Chat.members).filter(Chat.id == chat.id).all()
            for friend in friends:
                if friend.id != self.id:
                    response.append({'chat': {'id': friend.id, 'name': friend.first_name + ' ' + friend.last_name}})
        return response

    def send_friend_request(self, other):
        """
        Send a request to a user
        """
        self.friends.append(other)
        return 'Friend request sent'

    def are_friends(self, other):
        """
        If both users has a friend request sent to
        each other, they are friends
        """
        return self.friend_request_sent(other) and other.friend_request_sent(self)

    def friend_request_sent(self, other):
        """
        Check if a user has sent a request to another user
        """
        return self.friends.filter(friendships.c.Requested == other).count() > 0

    def remove_friend_request(self, other):
        """
        Remove a friend request. (Also used to
        remove a friend.)
        """
        if self.are_friends(other):
            other.friends.remove(self)
        self.friends.remove(other)
        return ''

    def get_number_of_requests(self):
        """
        Get the number of friend requests
        """
        return User.friends.filter(friendships.c.Requested == self.id).count()

    def get_number_of_friends(self):
        """
        Get the number of friends a user has
        """
        others = User.friends.filter(friendships.c.Requested == self.id).all()
        count = 0
        for other in others:
            if self.are_friends(other):
                count += 1
        return count

    def block_user(self, other):
        """
        Block a user. If they are friends,
        also remove the friendship
        """
        if self.are_friends(other):
            self.remove_friend_request(other)
            other.remove_friend_request(self)
        self.blocked.append(other)
        return 'User blocked'




    # # def mark_read(self, friend):
    # #     """
    # #     Mark all messages from a user as read
    # #     """
    # #     messages = Message.query.filter_by(receiver=self.id, sent_by=friend).all()
    # #     for message in messages:
    # #         message.read = True
    # #     db.session.commit()
    # #     return 'Message read'
    #
    # def get_number_of_unread(self):
    #     """
    #     Get the number of unread messages from all users
    #     """
    #     return Message.query.filter_by(receiver=self.id, read=False).count()

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

    members = db.relationship('User', secondary=chat_members, backref=db.backref('chats', lazy='dynamic'))
    messages = db.relationship('Message', backref='Chat', lazy='dynamic')


class Message(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    text = db.Column(db.String, nullable=False)
    chat_id = db.Column(db.Integer, db.ForeignKey('chat.id'))
    sent_by = db.Column(db.Integer, db.ForeignKey('user.id'))

    def __init__(self, text):
        self.text = text


def get_user(email):
    user = User.query.filter_by(email=email).first()
    return user





class Post(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    text = db.Column(db.String, nullable=False)
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
    oldest = oldest + 1
    if oldest == 0:
        latest = Post.query.count() + 1
        if latest < 11:
            oldest = 1
        else:
            oldest = latest - 10
    else:
        latest = oldest + 10
    return get_latest_posts_from(latest, oldest)


def get_latest_posts_from(latest, oldest):
    """
    :param latest:
    :return:the 10 latest posts from latest (latest not included)
    """
    # posts = Post.query.order_by(desc(Post.posted_at)).limit(10).all()

    posts = Post.query.filter(Post.id.in_(range(oldest, latest))).all()
    response = []  # http://stackoverflow.com/questions/13530967/parsing-data-to-create-a-json-data-object-with-python
    for post in posts:
        response.append({'post': {'id': post.id, 'name': post.user.first_name + ' ' + post.user.last_name, 'text': post.text}})
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

    comments = Comment.query.filter(Comment.index.in_(range(oldest, latest)), Comment.post_id == post.id).all()
    response = []  # http://stackoverflow.com/questions/13530967/parsing-data-to-create-a-json-data-object-with-python
    for i in range(len(comments)):
        comment = comments[i]
        response.append({'comment': {'id': comment.id, 'index': comment.index,
                                     'name': comment.user.first_name + ' ' + comment.user.last_name,
                                     'text': comment.text}})
    return response

    # # friends = db.relationship('User',
    # #                            secondary=friendships,
    # #                            primaryjoin=(friendships.c.Requester == id),
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



# Tables
