from flask import request, abort

from Project import data

data.initialize_db()


@application.route('/')
def hello():
    return "hello world"


@application.route('/messages', methods=['POST'])
@data.verify_login
def save_message():  # Save a messages
    try:
        message = request.get_json()['message']
    except KeyError:  # Om inget meddelande är mottaget
        return abort(400)
    return data.save_message(message)


@application.route('/messages', methods=['GET'])
def get_all_messages():  # Get all messages
    return data.get_all()


@application.route('/messages/<message_id>', methods=['GET'])
def get_message(message_id):  # Get or delete a message by its MessageID
    return data.get_message(message_id)


@application.route('/messages/<message_id>', methods=['DELETE'])
@data.verify_login
def delete_message(message_id):  # Get or delete a message by its MessageID
    return data.delete_message(message_id)


@application.route('/messages/<message_id>/flag/<username>', methods=['POST'])
@data.verify_login
def mark_read(message_id, username):  # Mark a message as read
    return data.mark_read(message_id, username)


@application.route('/messages/unread/<username>')
@data.verify_login
def get_unread(username):  # Get all unread messages
    return data.get_unread(username)


@application.route('/user', methods=['POST'])
def create_user():
    user = request.get_json()
    try:
        name = user['username']
        password = user['password']
    except KeyError:
        return abort(400)
    return data.add_user(name, password)


@application.route('/user/login', methods=['POST'])
def login():
    user = request.get_json()
    name = user['username']
    password = user['password']
    try_user = data.User.query.filter(data.User.username == name).first()
    if not try_user:
        return 'Wrong username or password'
    if try_user.check_password(password):
        print(1)
        return try_user.generate_auth_token()
    else:
        return "Oooops"