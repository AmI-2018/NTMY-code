from re import match, split
import bluetooth


def obtain_dest_by_user(config, userID, session):
    """ Obtain the destination's node id by the user's id """

    # Perform the HTTP request to the server
    url = config['serveruri'] + "/users/" + str(userID) + "/events/next"
    r = session.get(url)

    # Retrieve the events and takes the first
    user_events = r.json()
    event = user_events[0]['event']
    eventID = event['eventID']

    # Retrieve the nearest node to the event's room
    url = config['serveruri'] + "/schedule/event/" + str(eventID)
    r = session.get(url)
    if r.status_code == 400:
        return None

    # Return the scheduled event
    return r.json()


def detect_user() -> int:
    """Return the userID of one of the nearby users.
    
    :return: The userID of a nearby user
    :rtype: int
    :return: the matching device flag
    """

    # Loop until a device is found
    devices = bluetooth.discover_devices(lookup_names=True)

    # Search for devices with matching name
    for device in devices:
        if match("NTMY[0-9]+", device[1]):
            userID = split("NTMY", device[1])[1]
            return int(userID)
    
    return False
