from backend import application
from backend import data
from flask import request, about
@application.route('/login/<user>', methods=['POST'])
def login():
    user = request.get_json()
    name = user['username']
    password = user['password']
    try_user = data.User.query.filter(data.User.username == name).first()
    if not try_user:
        return 'Wrong username or password'
    if try_user.check_password(password):
        return try_user.generate_auth_token()
    else:
        return "Oooops"

@application.route('/logout', methods=['POST'])
def logout():


@application.route('/create/<user>', methods=['POST'])
def create_user():
    user = request.get_json()
    try:
        name = user['username']
        password = user['password']
    except KeyError:
        return abort(400)
    return data.add_user(name, password)


@application.route('/send_message/<receiver>/<text>', methods=['POST'])
def send_message(receiver, text):
    return None


@application.route('/get_unread/<receiver>/<sender>', methods=['GET'])
def get_unread(receiver, sender):


@application.route('/get_read/<receiver>/<sender>', methods=['GET'])
def get_read(receiver, sender):


@application.route('/send_friend_request/<receiver>/<sender>', methods=['POST'])
def friend_request(receiver, sender):


@application.route('/get_friend_request/<user>', methods=['GET'])
def get_friend_request(user):


@application.route('/accept_friend_request/<receiver>/<sender>', methods=['POST'])
def accept_friend_request(receiver, sender):


@application.route('number_of_friends/<user>', methods=['GEt'])
def get_friends(user):
