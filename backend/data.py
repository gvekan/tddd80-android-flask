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

friend_requests = db.Table('friend_requests',
                           db.Column('requester_id', db.Integer, db.ForeignKey('user.id')),
                           db.Column('requested_id', db.Integer, db.ForeignKey('user.id')))

friendships = db.Table('friendships',
                       db.Column('user_id', db.Integer, db.ForeignKey('user.id')),
                       db.Column('friend_id', db.Integer, db.ForeignKey('user.id')))

post_likes = db.Table('likes',
                      db.Column('user_id', db.Integer, db.ForeignKey('user.id')),
                      db.Column('post_id', db.Integer, db.ForeignKey('post.id')))


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

    sent_requests = db.relationship('User',
                                    secondary=friend_requests,
                                    primaryjoin=(friend_requests.c.requester_id == id),
                                    secondaryjoin=(friend_requests.c.requested_id == id),
                                    backref=db.backref('received_requests', lazy='dynamic'),
                                    lazy='dynamic')

    friends = db.relationship('User',
                              secondary=friendships,
                              primaryjoin=(friendships.c.user_id == id),
                              secondaryjoin=(friendships.c.friend_id == id),
                              backref=db.backref('friendship', lazy='dynamic'),
                              lazy='dynamic')

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

    def get_profile_info(self):
        info = {'firstName': self.first_name,
                'lastName': self.last_name,
                'city': self.city,
                'email': self.email}
        return info

    def send_message(self, receiver, text):
        chat = get_chat(self, receiver)
        if not chat:
            chat = Chat()
            chat.members.append(self)
            chat.members.append(receiver)
            db.session.add(chat)
        message = Message(text, chat.messages.count() + 1)
        chat.messages.append(message)
        self.messages.append(message)
        db.session.add(message)
        db.session.commit()
        return 'Chat started'

    def get_friends(self):
        friends = self.friends.all()
        response = []
        for friend in friends:
            response.append({'firstName': friend.first_name,
                             'lastName': friend.last_name,
                             'email': friend.email,
                             'city': friend.city})
        return response

    def remove_friend(self, friend):
        self.friends.remove(friend)
        friend.friendship.remove(self)
        db.session.commit()
        return 'Friend removed'

    def get_all_users(self):
        """
        Get all users that is not self or a friend
        :return:
        """
        users = User.query.filter(User.id != self.id).all()
        response = []
        friends = self.friends.all()
        for user in users:
            if user not in friends:
                response.append({'firstName': user.first_name,
                                 'lastName': user.last_name,
                                 'email': user.email,
                                 'city': user.city})
        return response

    def send_friend_request(self, other):
        """
        Send a request to a user
        :param other: Other user
        """
        if not self.friend_request_sent(other):
            self.sent_requests.append(other)
            other.received_requests.append(self)
        db.session.commit()
        return 'Friend request sent'

    def accept_friend_request(self, other):
        if not other.friend_request_sent(self):
            return "No friend request from that User"
        other.sent_requests.remove(self)
        self.friends.append(other)
        other.friends.append(self)
        db.session.commit()
        return "Friend request accepted"

    def remove_friend_request(self, other):
        self.received_requests.remove(other)
        other.sent_requests.remove(self)
        db.session.commit()
        return "Friend request removed"

    def are_friends(self, other):
        return self.sent_requests.filter(friend_requests.c.requested_id == other.id).count() > 0

    def friend_request_sent(self, other):
        """
        Check if a user has sent a request to another user
        :param other: other user
        """
        return self.sent_requests.filter(friend_requests.c.requested_id == other.id).count() > 0

    def get_friend_requests(self):
        requests = self.received_requests.all()
        response = []
        for requester in requests:
            response.append({'firstName': requester.first_name,
                             'lastName': requester.last_name,
                             'email': requester.email,
                             'city': requester.city})
        return response

    def get_friend_request_amount(self):
        return self.received_requests.count()

    def get_number_of_friends(self):
        return self.friends.count()

    def create_post(self, text, city):
        if not city:
            city = self.city
        post = Post(text, city)
        self.posts.append(post)
        db.session.add(post)
        db.session.commit()
        return 'Post created'

    def get_latest_posts(self, oldest):
        oldest += 1
        if oldest == 0:
            latest = Post.query.count() + 1
            if latest < 11:
                oldest = 1
            else:
                oldest = latest - 10
        else:
            latest = oldest + 10
        return self.get_latest_posts_from(latest, oldest)

    def get_latest_posts_from(self, latest, oldest):
        """
        :param latest: The latest post
        :param oldest: The oldest post
        :return:the 10 latest posts from latest (latest not included)
        """
        posts = Post.query.filter(Post.id.in_(range(oldest, latest))).all()
        response = []
        for post in posts:
            likes = post.likes.count()
            if post.likes.filter(User.id == self.id).count() == 0:
                liking = False
            else:
                liking = True
            comments = Comment.query.filter(Comment.post_id == post.id).count()
            response.append({'id': post.id,
                             'name': post.user.first_name + ' ' + post.user.last_name,
                             'text': post.text, 'likes': likes, "liking": liking,
                             "comments": comments,
                             'city': post.city})
        return response

    def get_latest_posts_from_user(self):
        """
        Get 10 latest post where the latest is posted by user
        """
        post = Post.query.filter(Post.user_id == self.id).order_by(Post.id.desc()).first()
        latest = post.id + 1
        if latest < 11:
            oldest = 1
        else:
            oldest = latest - 10
        return self.get_latest_posts_from(latest, oldest)

    def create_comment(self, post, text):
        """
        Create a comment
        :param post: Which post the comment is related to
        :param text: The comment text
        :return:
        """
        comment = Comment(text, post.comments.count() + 1)
        self.comments.append(comment)
        post.comments.append(comment)
        db.session.add(comment)
        db.session.commit()
        return 'Comment created'

    def get_latest_comments(self, post, oldest):
        oldest += 1
        if oldest == 0:
            latest = post.comments.count() + 1
            if latest < 11:
                oldest = 1
            else:
                oldest = latest - 10
        else:
            latest = oldest + 10
        return self.get_latest_comments_from(post, latest, oldest)

    @staticmethod
    def get_latest_comments_from(post, latest, oldest):
        """
        :param post:
        :param latest:
        :param oldest:
        :return:the 10 latest posts from latest (latest not included)
        """
        comments = Comment.query.filter(Comment.index.in_(range(oldest, latest)), Comment.post_id == post.id).all()
        response = []
        for i in range(len(comments)):
            comment = comments[i]
            response.append({'index': comment.index,
                             'name': comment.user.first_name + ' ' + comment.user.last_name,
                             'text': comment.text})
        return response

    def get_latest_comments_from_user(self, post):
        """
        Get 10 latest comments where the latest is posted by user
        :param post: The post the comments are related to
        """
        comment = Comment.query.filter(Comment.user_id == self.id).order_by(Comment.index.desc()).first()
        latest = comment.index + 1
        if latest < 11:
            oldest = 1
        else:
            oldest = latest - 10
        return self.get_latest_comments_from(post, latest, oldest)

    def get_latest_messages(self, chat, oldest):
        oldest += 1
        latest = chat.messages.count() + 1
        return self.get_latest_messages_from(chat, latest, oldest)

    @staticmethod
    def get_latest_messages_from(chat, latest, oldest):
        """
        :param chat: The chat the messages are related to
        :param latest: The latest message
        :param oldest: The oldest message
        :return:the 10 latest posts from latest (latest not included)
        """
        messages = Message.query.filter(Message.index.in_(range(oldest, latest)), Message.chat_id == chat.id).all()
        response = []
        for i in range(len(messages)):
            message = messages[i]
            response.append({'index': message.index,
                             'sentBy': message.sent_by,
                             'text': message.text})
        return response


def register_user(email, password, first_name, last_name, city):
    user = User(email, password, first_name, last_name, city)
    db.session.add(user)
    db.session.commit()
    return 'User created'


def get_user(email):
    return User.query.filter_by(email=email).first()


def get_chat(user, other):
    return Chat.query.filter(Chat.members.any(User.id == user.id), Chat.members.any(User.id == other.id)).first()


class Chat(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    members = db.relationship('User', secondary=chat_members, backref=db.backref('chats', lazy='dynamic'))
    messages = db.relationship('Message', backref='Chat', lazy='dynamic')


class Message(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    text = db.Column(db.String, nullable=False)
    chat_id = db.Column(db.Integer, db.ForeignKey('chat.id'))
    sent_by = db.Column(db.Integer, db.ForeignKey('user.email'))
    index = db.Column(db.Integer, unique=False, nullable=False)

    def __init__(self, text, index):
        self.text = text
        self.index = index


class Post(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    text = db.Column(db.String, nullable=False)
    city = db.Column(db.String, nullable=False)

    likes = db.relationship('User', secondary=post_likes,
                            backref=db.backref('liked_posts', lazy='dynamic'), lazy='dynamic')
    comments = db.relationship("Comment", backref="post", lazy='dynamic')

    user_id = db.Column(db.Integer, db.ForeignKey('user.id'))

    def __init__(self, text, city):
        self.text = text
        self.city = city

    def like_post(self, user):
        self.likes.append(user)
        db.session.commit()

    def dislike_post(self, user):
        self.likes.remove(user)
        db.session.commit()


class Comment(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    text = db.Column(db.String, unique=False, nullable=False)
    index = db.Column(db.Integer, unique=False, nullable=False)

    user_id = db.Column(db.Integer, db.ForeignKey('user.id'))

    post_id = db.Column(db.Integer, db.ForeignKey('post.id'))

    def __init__(self, text, index):
        self.text = text
        self.index = index
