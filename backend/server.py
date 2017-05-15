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
    user = data.get_user(get_jwt_identity())
    user.create_post(text)
    return jsonify({"msg": "Post successfully made"}), 200


@application.route('/get-latest-posts/<post_id>', methods=['get'])
@jwt_required
def get_latest_posts(post_id):
    """
    Get the ten latest posts on the wall
    """
    latest = int(post_id)
    user = data.get_user("gusan092@student.liu.se")
    post_list = user.get_latest_posts(latest)
    return jsonify({"posts": post_list}), 200


@application.route('/get-latest-posts-from/<post_id>', methods=['get'])
@jwt_required
def get_latest_posts_from(post_id):
    """
    Get the ten latest posts on the wall
    """
    latest = int(post_id)
    if latest < 11:
        oldest = 1
    else:
        oldest = latest - 10
    user = data.get_user(get_jwt_identity())
    post_list = user.get_latest_posts_from(latest, oldest)
    return jsonify({"posts": post_list}), 200


@application.route('/get-latest-posts-from-user', methods=['get'])
@jwt_required
def get_latest_posts_from_user():
    """
    Get the ten latest posts on the wall
    """
    user = data.get_user(get_jwt_identity())
    post_list = user.get_latest_posts_from_user()
    return jsonify({"posts": post_list}), 200


@application.route('/like-post', methods=['post'])
@jwt_required
def like_post():
    id = request.json.get('post', None)
    post = data.Post.query.get(id)
    user = data.get_user(get_jwt_identity())
    user.like_post(post)
    return jsonify({"msg": "Post liked"}), 200


@application.route('/dislike-post', methods=['post'])
@jwt_required
def dislike_post():
    id = request.json.get('post', None)
    post = data.Post.query.get(id)
    user = data.get_user(get_jwt_identity())
    user.dislike_post(post)
    return jsonify({"msg": "Post unliked"}), 200


@application.route('/create-comment', methods=['post'])
@jwt_required
def create_comment():
    """
    Creates a post to the wall
    """
    text = request.json.get('text', None)
    id = request.json.get('post', None)
    post = data.Post.query.get(id)
    user = data.get_user(get_jwt_identity())
    user.create_comment(post, text)
    return jsonify({"msg": "Comment successfully made"}), 200


@application.route('/get-latest-comments/<post_id>/<comment_index>', methods=['get'])
@jwt_required
def get_latest_comments(post_id, comment_index):
    """
    Get the ten latest posts on the wall
    """
    id = int(post_id)
    post = data.Post.query.get(id)
    oldest = int(comment_index)
    user = data.get_user(get_jwt_identity())
    comment_list = user.get_latest_comments(post, oldest)
    return jsonify({"comments": comment_list}), 200


@application.route('/get-latest-comments-from/<post_id>/<comment_index>', methods=['get'])
@jwt_required
def get_latest_comments_from(post_id, comment_index):
    """
    Get the ten latest posts on the wall
    """
    id = int(post_id)
    post = data.Post.query.get(id)
    latest = int(comment_index)
    if latest < 11:
        oldest = 0
    else:
        oldest = latest - 10
    user = data.get_user(get_jwt_identity())
    comment_list = user.get_latest_comments_from(post, latest, oldest)
    return jsonify({"comments": comment_list}), 200


@application.route('/get-latest-comments-from-user/<post_id>', methods=['get'])
@jwt_required
def get_latest_comments_from_user(post_id):
    """
    Get the ten latest posts on the wall
    """
    id = int(post_id)
    post = data.Post.query.get(id)
    user = data.get_user(get_jwt_identity())
    comment_list = user.get_latest_comments_from_user(post)
    return jsonify({"comments": comment_list}), 200


@application.route('/send_message', methods=['POST'])
@jwt_required
def send_message():
    """
    Sends a message to another user
    """
    receiver_email = request.json.get('receiver', None)
    message = request.json.get('message', None)
    user = data.get_user(get_jwt_identity())
    receiver = data.get_user(receiver_email)
    user.send_message(receiver, message)
    return jsonify({"msg": "Message successfully sent"}), 200


@application.route('/get_messages/<receiver_email>', methods=['GET'])
@jwt_required
def get_messages(receiver_email):
    """
    Get all messages between two users
    """
    user = data.get_user(get_jwt_identity())
    receiver = data.get_user(receiver_email)
    messages = user.get_messages(receiver)
    return jsonify({"messages": messages}), 200


@application.route('/get_chats', methods=['GET'])
@jwt_required
def get_chats():
    """
    Get all active chat a user has
    """
    user = data.get_user(get_jwt_identity())
    chats = user.get_chats()
    return jsonify({'chats': chats}), 200



@application.route('/send_friend_request/<receiver_email>', methods=['POST'])
@jwt_required
def send_friend_request(receiver_email):
    user = data.get_user(get_jwt_identity())
    receiver = data.get_user(receiver_email)
    user.send_friend_request(receiver)
    return jsonify({'msg': 'Friend request successfully sent'}), 200


@application.route('/get_friend_requests', methods=['GET'])
@jwt_required
def get_friend_requests():
    user = data.get_user(get_jwt_identity())
    friend_requests = user.get_friend_requests()
    return jsonify({'friend_requests': friend_requests}), 200


@application.route('/accept_friend_request/<requester_email>', methods=['POST'])
@jwt_required
def accept_friend_request(requester_email):
    user = data.get_user(get_jwt_identity())
    requester = data.get_user(requester_email)
    user.send_friend_request(requester)
    return jsonify({'msg': 'You are now friends with ' + requester.first_name + ' ' + requester.last_name}), 200
