from backend import application, data

from flask import request, jsonify

from flask_jwt_extended import JWTManager, jwt_required, \
    get_jwt_identity, revoke_token, \
    get_stored_tokens, get_all_stored_tokens, create_access_token, \
    create_refresh_token, jwt_refresh_token_required, \
    get_raw_jwt, get_stored_token


jwt = JWTManager(application)


# -- OAuth2 --
# Create user
@application.route('/create', methods=['POST'])
def create():
    username = request.json.get('username', None)
    password = request.json.get('password', None)
    email = request.json.get('email', None)
    first_name = request.json.get('first_name', None)
    surname = request.json.get('surname', None)
    birth_date = request.json.get('birth_date', None)
    domicile = request.json.get('domicile', None)
    description = request.json.get('description', None)
    if data.User.query.filter_by(username=username).count():
        return jsonify({"msg": "Username taken"}), 401
    if data.User.query.filter_by(username=username).count():
        return jsonify({"msg": "Email taken"}), 401
    data.create_user(username, password, email, first_name, surname, birth_date, domicile, description)
    return jsonify({"msg": "User created"}), 200


# Standard login endpoint
@application.route('/login', methods=['POST'])
def login():
    """
    Log in user
    """
    username = request.json.get('username', None)
    password = request.json.get('password', None)
    try_user = data.User.query.filter_by(username=username).first()
    if not try_user or not try_user.check_password(password):
        return jsonify({"msg": "Bad username or password"}), 401

    ret = {
        'access_token': create_access_token(identity=username),
        'refresh_token': create_refresh_token(identity=username)
    }
    return jsonify(ret), 200


# Endpoint for revoking the current users access token
@application.route('/logout', methods=['POST'])
@jwt_required
def logout():
    """
    Log out user by revoking access token
    """
    try:
        _revoke_current_token()
    except KeyError:
        return jsonify({
            'msg': 'Access token not found in the blacklist store'
        }), 500
    return jsonify({"msg": "Successfully logged out"}), 200


# -- JWT-token functions --

# Endpoint for revoking the current users refresh token
@application.route('/logout2', methods=['POST'])
@jwt_refresh_token_required
def logout2():
    """
    Log out user by revoking refresh token
    """
    try:
        _revoke_current_token()
    except KeyError:
        return jsonify({
            'msg': 'Refresh token not found in the blacklist store'
        }), 500
    return jsonify({"msg": "Successfully logged out"}), 200


# Standard refresh endpoint
@application.route('/refresh', methods=['POST'])
@jwt_refresh_token_required
def refresh():
    """
    Refresh token
    """
    current_user = get_jwt_identity()
    ret = {
        'access_token': create_access_token(identity=current_user)
    }
    return jsonify(ret), 200


# Helper method to revoke the current token used to access
# a protected endpoint
def _revoke_current_token():
    current_token = get_raw_jwt()
    jti = current_token['jti']
    revoke_token(jti)


# Endpoint for listing tokens that have the same identity as you
# NOTE: This is currently very inefficient.
@application.route('/auth/tokens', methods=['GET'])
@jwt_required
def list_identity_tokens():
    username = get_jwt_identity()
    return jsonify(get_stored_tokens(username)), 200


# Endpoint for listing all tokens. In your app, you should either
# not expose this endpoint, or put some addition security on top
# of it so only trusted users (administrators, etc) can access it
@application.route('/auth/all-tokens')
def list_all_tokens():
    return jsonify(get_all_stored_tokens()), 200


# Endpoint for allowing users to revoke their own tokens.
@application.route('/auth/tokens/revoke/<string:jti>', methods=['PUT'])
@jwt_required
def change_jwt_revoke_state(jti):
    username = get_jwt_identity()
    try:
        token_data = get_stored_token(jti)
        if token_data['token']['identity'] != username:
            raise KeyError
        revoke_token(jti)
        return jsonify({"msg": "Token successfully revoked"}), 200
    except KeyError:
        return jsonify({'msg': 'Token not found'}), 404


# -- Messages --

@application.route('/send_message', methods=['POST'])
@jwt_required
def send_message():
    """
    Send a message
    """
    receiver = request.json.get('receiver', None)
    message = request.json.get('message', None)
    username = get_jwt_identity()
    mailer = data.User.query.filter_by(username=username)
    return mailer.send_message(receiver, message)


@application.route('/get_messages', methods=['GET'])
@jwt_required
def get_messages():
    """
    Get all messages by an user
    """
    mailer = request.json.get('mailer', None)
    username = get_jwt_identity()
    user = data.User.query.filter_by(username=username)
    return user.get_messages(mailer)


# -- Friends --

@application.route('/send_friend_request', methods=['POST'])
@jwt_required
def send_friend_request():
    requested = request.json.get('requested', None)
    username = get_jwt_identity()
    requester = data.User.query.filter_by(username=username)
    return requester.send_friend_request(requested)


@application.route('/get_friend_requests', methods=['GET'])
@jwt_required
def get_friend_requests():
    username = get_jwt_identity()
    requested = data.User.query.filter_by(username=username)
    return requested.get_friend_requests()


@application.route('/accept_friend_request', methods=['POST'])
@jwt_required
def accept_friend_request():
    requester = request.json.get('requester', None)
    username = get_jwt_identity()
    requested = data.User.query.filter_by(username=username)
    return requested.send_friend_request(requester)


@application.route('/deny_friend_request', methods=['POST'])
@jwt_required
def deny_friend_request():
    requester = request.json.get('requester', None)
    username = get_jwt_identity()
    requested = data.User.query.filter_by(username=username)
    return requested.remove_friend_request(requester)


@application.route('/block_user', methods=['POST'])
@jwt_required
def block_user():
    user_to_block = request.json.get('user_to_block', None)
    username = get_jwt_identity()
    user = data.User.query.filter_by(username=username)
    return user.block_user(user_to_block)


@application.route('/get_number_of_friends', methods=['GET'])
@jwt_required
def get_number_of_friends():
    username = get_jwt_identity()
    user = data.User.query.filter_by(username=username)
    return user.get_number_of_friends()
