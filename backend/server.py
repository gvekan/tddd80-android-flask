from backend import application


@application.route('/send_message/<receiver>/<text>', methods=['POST'])
def send_message(receiver, text):
    return None