import requests
import bluetooth


def populate_users(config,session):
    """ Downloads from the server all the users' names """

    """ initializes the collection """
    users = []

    """ Get users data """
    url = config['serveruri'] + "/users"
    r = session.get(url)
    request = r.json()

    """ Add each users' info in the collection"""
    for user in request:
        users.append({'id': user['userID'], 'fullname': user['name'] + " " + user['surname']})

    return users


def obtain_dest_by_user(config, user_id, session):
    """ Obtain the destination's node id by the user's id """

    """ 
    Generate the url according to the server's API in order to get
    the user's events 
    """
    url = config['serveruri'] + "/users/" + str(user_id) + "/events"

    """ Perform the HTTP request  """
    r = session.get(url)

    """ Retrieve the events and takes the first """
    user_events = r.json()
    event = user_events[0]['event']
    event_id = event['eventID']
    """ Retrieve the node nearest to the event's room """

    url = config['serveruri'] + "/schedule/event/" + str(event_id)
    r = session.get(url)
    schedule = r.json()
    room = schedule['room']

    """ Return the destination's node id """
    return room['node']


def detect_user(users):
    """ Wait until a registered user (Device) is detected """

    found = False
    """ Device name version """
    while not found:
        """ loops until finds a device """
        devices = []
        while not devices:
            devices = bluetooth.discover_devices(lookup_names=True)

        """ search registered users in detected devices """
        for device in devices:
            for user in users:
                if device[1] == user['fullname']:
                    user_id = user['id']
                    found = True
                    break

    """ 
    Device MAC version :
    user_id = devices[0][0]
    """

    """ Once detected return the user id """
    return user_id


def generate_arrow(exit_point, color):
    """ Lights the arrow in the circuit

        :param exit_point: arrow id
        :type exit_point: int
        exit_point is a number in [0; 3]:
        0 -> E;
        1 -> N;
        2 -> O;
        3 -> S.

    """
    pass


def reset_arrow():
    """ Turn off the arrows"""

    pass

