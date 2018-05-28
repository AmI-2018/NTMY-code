import requests
import bluetooth


def obtain_dest_by_user(config, user_id):
    """ Obtain the destination's node id by the user's id """

    """ 
    Generate the url according to the server's API in order to get
    the user's events 
    """
    url = config['serveruri'] + "/users/" + user_id + "/events"

    """ Perform the HTTP request  """
    r = requests.get(url)

    """ Retrieve the events and takes the first """
    user_events = r.json()
    event = user_events[0]
    event_id = event.eventID
    """ Retrieve the node nearest to the event's room """

    url = config['serveruri'] + "/schedule/event/" + event_id
    r = requests.get(url)
    schedule = r.json()
    room = schedule.room

    """ Return the destination's node id """
    return room['node']

def detect_user():

    """ Wait until a user (Device) is detected """

    devices = []

    """ Device name version """

    while not devices:
        devices = bluetooth.discover_devices(lookup_names=True)

    user_id = devices[0][1]

    """ 
    Device MAC version :
    user_id = devices[0][0]
    """

    """ Once detected return the user id """

    return user_id


def generate_arrow(exit_point):
    """ Lights the arrow in the circuit

        :param exit_point: arrow id
        :type exit_point: int

    """
    pass


def reset_arrow():
    """ Turn off the arrows"""

    pass

