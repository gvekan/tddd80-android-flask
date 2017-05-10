from backend import application, data

from flask import request, jsonify

from flask_jwt_extended import JWTManager, jwt_required, \
    get_jwt_identity, revoke_token, \
    get_stored_tokens, get_all_stored_tokens, create_access_token, \
    create_refresh_token, jwt_refresh_token_required, \
    get_raw_jwt, get_stored_token


jwt = JWTManager(application)

data.initialize_db()


# -- OAuth2 --
# Create user
@application.route('/register', methods=['POST'])
def register():
    email = request.json.get('email', None)
    password = request.json.get('password', None)
    first_name = request.json.get('first_name', None)
    last_name = request.json.get('last_name', None)
    city = request.json.get('city', None)
    if data.User.query.filter_by(email=email).count():
        return jsonify({"msg": "Email taken"}), 401
    data.register_user(email,password, first_name, last_name, city)
    return jsonify({"msg": "User created"}), 200


# Standard login endpoint
@application.route('/login', methods=['POST'])
def login():
    """
    Log in user
    """
    email = request.json.get('email', None)
    password = request.json.get('password', None)
    try_user = data.User.query.filter_by(email=email).first()
    if not try_user or not try_user.check_password(password):
        return jsonify({"msg": "Bad email or password"}), 401

    ret = {
        'access_token': create_access_token(identity=email),
        'refresh_token': create_refresh_token(identity=email)
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
    email = get_jwt_identity()
    return jsonify(get_stored_tokens(email)), 200


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
    email = get_jwt_identity()
    try:
        token_data = get_stored_token(jti)
        if token_data['token']['identity'] != email:
            raise KeyError
        revoke_token(jti)
        return jsonify({"msg": "Token successfully revoked"}), 200
    except KeyError:
        return jsonify({'msg': 'Token not found'}), 404


# -- Wall --
@application.route('/create-post', methods=['post'])
@jwt_required
def create_post():
    """
    Creates a post to the wall
    """
    text = request.json.get('text', None)
    email = get_jwt_identity()
    user = data.User.query.filter_by(email=email).first()
    data.create_post(user, text)
    return jsonify({"msg": "Post successfully made"}), 200


@application.route('/get-latest-posts', methods=['get'])
@jwt_required
def get_latest_posts():
    """
    Get the ten latest posts on the wall
    """
    latest = request.json.get('post', None)
    post_list = data.get_latest_posts(latest)
    return jsonify({"posts": post_list}), 200


@application.route('/get-latest-posts-from', methods=['get'])
@jwt_required
def get_latest_posts_from():
    """
    Get the ten latest posts on the wall
    """
    latest = request.json.get('post', None)
    if latest < 11:
        oldest = 1
    else:
        oldest = latest - 10
    post_list = data.get_latest_posts_from(latest, oldest)
    return jsonify({"posts": post_list}), 200


@application.route('/create-comment', methods=['post'])
@jwt_required
def create_comment():
    """
    Creates a post to the wall
    """
    text = request.json.get('text', None)
    id = request.json.get('post', None)
    post = data.Post.query.get(id)
    email = get_jwt_identity()
    user = data.User.query.filter_by(email=email).first()
    data.create_comment(user, post, text)
    return jsonify({"msg": "Comment successfully made"}), 200


@application.route('/get-latest-comments', methods=['get'])
@jwt_required
def get_latest_comments():
    """
    Get the ten latest posts on the wall
    """
    id = request.json.get('post', None)
    post = data.Post.query.get(id)
    comment_list = data.get_latest_comments(post)
    return jsonify({"comments": comment_list}), 200


@application.route('/get-latest-comments-from', methods=['get'])
@jwt_required
def get_latest_comments_from():
    """
    Get the ten latest posts on the wall
    """
    id = request.json.get('post', None)
    post = data.Post.query.get(id)
    index = request.json.get('comment', None)
    comment_list = data.get_latest_comments_from(post, index)
    return jsonify({"comments": comment_list}), 200


@application.route('/open_chat', methods=['POST'])
@jwt_refresh_token_required
def open_chat():
    email = get_jwt_identity()
    friend_email = request.json.get('friend', None)

    user = data.User.query.filter_by(email=email).first()
    friend = data.User.query.filter_by(email=friend_email).first()

    sent_messages = data.get_sent_messages(user, friend)
    received_messages = data.get_received_messages(user, friend)
    return jsonify({"sent_messages": sent_messages, "received_messages": received_messages}), 200



# -- Messages --

# @application.route('/send_message', methods=['POST'])
# @jwt_required
# def send_message():
#     """
#     Send a message
#     """
#     receiver = request.json.get('receiver', None)
#     message = request.json.get('message', None)
#     email = get_jwt_identity()
#     mailer = data.User.query.filter_by(email=email)
#     return mailer.send_message(receiver, message)
#
#
# @application.route('/get_messages', methods=['GET'])
# @jwt_required
# def get_messages():
#     """
#     Get all messages by an user
#     """
#     mailer = request.json.get('mailer', None)
#     email = get_jwt_identity()
#     user = data.User.query.filter_by(email=email)
#     return user.get_messages(mailer)


# -- Friends --

# @application.route('/send_friend_request', methods=['POST'])
# @jwt_required
# def send_friend_request():
#     requested = request.json.get('requested', None)
#     email = get_jwt_identity()
#     requester = data.User.query.filter_by(email=email)
#     return requester.send_friend_request(requested)
#
#
# @application.route('/get_friend_requests', methods=['GET'])
# @jwt_required
# def get_friend_requests():
#     email = get_jwt_identity()
#     requested = data.User.query.filter_by(email=email)
#     return requested.get_friend_requests()
#
#
# @application.route('/accept_friend_request', methods=['POST'])
# @jwt_required
# def accept_friend_request():
#     requester = request.json.get('requester', None)
#     email = get_jwt_identity()
#     requested = data.User.query.filter_by(email=email)
#     return requested.send_friend_request(requester)
#
#
# @application.route('/deny_friend_request', methods=['POST'])
# @jwt_required
# def deny_friend_request():
#     requester = request.json.get('requester', None)
#     email = get_jwt_identity()
#     requested = data.User.query.filter_by(email=email)
#     return requested.remove_friend_request(requester)
#
#
# @application.route('/block_user', methods=['POST'])
# @jwt_required
# def block_user():
#     user_to_block = request.json.get('user_to_block', None)
#     email = get_jwt_identity()
#     user = data.User.query.filter_by(email=email)
#     return user.block_user(user_to_block)
#
#
# @application.route('/get_number_of_friends', methods=['GET'])
# @jwt_required
# def get_number_of_friends():
#     email = get_jwt_identity()
#     user = data.User.query.filter_by(email=email)
#     return user.get_number_of_friends()
