import webbrowser
import os
from . import exceptions

ROCK = "https://www.youtube.com/watch?v=HbaQ9xnoMGQ&list=PLhd1HyMTk3f7KwWMyGRfyTE12IahrHk52"
POP = "https://www.youtube.com/watch?v=Jkf1mG0YYU4&list=PLDcnymzs18LXHh9m-CyGtzC-6MiPrE2JV"
LATIN = "https://www.youtube.com/watch?v=j1W5An7eo2g&list=PLcfQmtiAG0X_3a1RP-bcjWImDwdYaOW4b"
CLASSIC = "https://www.youtube.com/watch?v=XUcJaXjMwRw&list=PLVXq77mXV53-Np39jM456si2PeTrEm9Mj"
channels = [ROCK, POP, CLASSIC, LATIN]


def start_music(channel):
    """ Open the browser and start the music """

    """ Check parameter """
    try:
        if channel not in channels:
            raise exceptions.WrongStyle
    except exceptions.WrongStyle:
        return

    """ Close the browser if already opened """
    os.system("pkill chromium-browse")

    """  Open the browser with the received link  """
    webbrowser.get("/usr/bin/chromium-browser").open(channel)
