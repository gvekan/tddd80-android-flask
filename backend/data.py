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
                       db.Column('requester', db.Integer, db.ForeignKey('user.id')),
                       db.Column('requested', db.Integer, db.ForeignKey('user.id')))

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

    friends = db.relationship('User',
                              secondary=friendships,
                              primaryjoin=(friendships.c.requester == id),
                              secondaryjoin=(friendships.c.requested == id),
                              backref=db.backref('friendships', lazy='dynamic'),
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
        info = {'firstName': self.first_name, 'lastName': self.last_name, 'city': self.city, 'email': self.email}
        return info

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
            response.append({'message': message.text, 'sentBy': message.sent_by})
        return response

    def get_friends(self):
        friends = User.friends.filter(friendships.c.requested == self.id, friendships.c.requested == self.id).all()
        response = []
        for friend in friends:
            #if friend.id != self.id:
            response.append({'name': friend.first_name + ' ' + friend.last_name, 'email': friend.email, 'isFriend': True})
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
        return self.friends.filter(friendships.c.requested == other).count() > 0

    def remove_friend_request(self, other):
        """
        Remove a friend request. (Also used to
        remove a friend.)
        """
        if self.are_friends(other):
            other.friends.remove(self)
        self.friends.remove(other)
        return ''

    def get_friend_requests(self):
        requests = User.friends.filter(friendships.c.requested == self.id).all()
        response = []
        for requester in requests:
            response.append({'name': requester.first_name + ' ' + requester.last_name, 'email': requester.email, 'isFriend': False})
        return response


    def get_number_of_requests(self):
        """
        Get the number of friend requests
        """
        return User.friends.filter(friendships.c.requested == self.id).count()

    def get_number_of_friends(self):
        """
        Get the number of friends a user has
        """
        others = User.friends.filter(friendships.c.requested == self.id).all()
        count = 0
        for other in others:
            if self.are_friends(other):
                count += 1
        return count

    def create_post(self, text):
        post = Post(text)
        self.posts.append(post)
        db.session.add(post)
        db.session.commit()
        return 'Post created'



    def get_latest_posts(self, oldest):
        oldest = oldest + 1
        if oldest == 0:
            latest = Post.query.count() + 1
            if latest < 11:
                oldest = 1
            else:
                oldest = latest - 10
        else:
            latest = oldest + 10
        return self.get_latest_posts_from(latest, oldest)


    def get_latest_posts_from(self, latest, oldest): # stödjer inte att posts tas bort

        """
        :param latest:
        :param oldest
        :return:the 10 latest posts from latest (latest not included)
        """
        # posts = Post.query.order_by(desc(Post.posted_at)).limit(10).all()

        posts = Post.query.filter(Post.id.in_(range(oldest, latest))).all()
        response = []  # http://stackoverflow.com/questions/13530967/parsing-data-to-create-a-json-data-object-with-python
        for post in posts:
            likes = User.query.filter(User.liked_posts.any(Post.id == post.id)).count()
            if User.query.filter(Post.likes.any(User.id == self.id), Post.id == post.id).count() == 0: # kanske ger fel, syns i appen
                liking = False
            else:
                liking = True
            comments = Comment.query.filter(Comment.post_id == post.id).count()
            response.append({'id': post.id, 'name': post.user.first_name + ' ' + post.user.last_name, 'text': post.text, 'likes': likes, "liking": liking, "comments": comments})
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
        comment = Comment(text, post.comments.count() + 1)
        self.comments.append(comment)
        post.comments.append(comment)
        db.session.add(comment)
        db.session.commit()
        return 'Comment created'


    def get_latest_comments(self, post, oldest):
        oldest = oldest + 1
        if oldest == 0:
            latest = post.comments.count() + 1
            if latest < 11:
                oldest = 1
            else:
                oldest = latest - 10
        else:
            latest = oldest + 10
        return self.get_latest_comments_from(post, latest, oldest)


    def get_latest_comments_from(self, post, latest, oldest): # stödjer inte att posts tas bort
        """
        :param post:
        :param latest:
        :param oldest:
        :return:the 10 latest posts from latest (latest not included)
        """
        comments = Comment.query.filter(Comment.index.in_(range(oldest, latest)), Comment.post_id == post.id).all() # index.in_ ger en varning
        response = []  # http://stackoverflow.com/questions/13530967/parsing-data-to-create-a-json-data-object-with-python
        for i in range(len(comments)):
            comment = comments[i]
            response.append({'id': comment.id, 'index': comment.index,
                                         'name': comment.user.first_name + ' ' + comment.user.last_name,
                                         'text': comment.text})
        return response

    def get_latest_comments_from_user(self, post):
        """
        Get 10 latest comments where the latest is posted by user
        """
        comment = Comment.query.filter(Comment.user_id == self.id).order_by(Comment.index.desc()).first()
        latest = comment.index + 1
        if latest < 11:
            oldest = 1
        else:
            oldest = latest - 10
        return self.get_latest_comments_from(post, latest, oldest)

    def like_post(self, post):
        post.likes.append(self)
        db.session.commit()

    def dislike_post(self, post):
        post.likes.remove(self)
        db.session.commit()





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
    return User.query.filter_by(email=email).first()



class Post(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    text = db.Column(db.String, nullable=False)
    # posted_at = db.Column(db.DateTime)

    likes = db.relationship('User', secondary=post_likes, backref=db.backref('liked_posts', lazy='dynamic'))
    comments = db.relationship("Comment", backref="post", lazy='dynamic')

    user_id = db.Column(db.Integer, db.ForeignKey('user.id'))

    def __init__(self, text):
        self.text = text
        # self.posted_at = datetime.now()


class Comment(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    text = db.Column(db.String, unique=False, nullable=False)
    index = db.Column(db.Integer, unique=False, nullable=False)

    user_id = db.Column(db.Integer, db.ForeignKey('user.id'))

    post_id = db.Column(db.Integer, db.ForeignKey('post.id'))

    def __init__(self, text, index):
        self.text = text
        self.index = index





# Tables
