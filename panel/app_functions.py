import requests


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

    """ Retrieve the node nearest to the event's room """

    """ Return the destination's node id """


def detect_user(config):

    """ Wait until a user is detected """

    user_id = ""

    """ Once detected return the user id """


def generate_arrow(exit_point):
    """ Lights the arrow in the circuit

        :param exit_point: arrow id
        :type exit_point: int

    """
    pass
