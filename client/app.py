import datetime
import json

import pause
import requests

from lights import LightManager
from player import Player

# Read config file

config = {}
with open("config.json") as f:
    config = json.loads(f.read())

print("Current config:")
print(config)

# Login

session = requests.Session()
session.post(config["serveruri"] + "/login", json={
    "email": config["rootmail"],
    "password": config["rootpwd"]
})

# LightManager instantiation

lm = LightManager(config["lightsuri"], config["lightsuser"])

# Player instantiation

p = Player()

# Main loop

while True:
    # Get the schedule
    schedule = session.get("{}/schedule/{}".format(config["serveruri"], config["roomID"])).json()
    print("Current schedule:")
    print(schedule)

    # Get the next event
    next_event = min(schedule, key=(lambda e: e["event"]["start"]))
    print("Next event:")
    print(next_event)

    # Set the color of the lights
    lm.set_color_all(next_event["color"]["red"], next_event["color"]["green"], next_event["color"]["blue"], 254, 254)
    print("Lights have been set.")

    # Get the facilities
    facilities = session.get("{}/events/{}/facilities".format(config["serveruri"], next_event["event"]["eventID"])).json()

    # Prepare the room
    for f in facilities:
        pass
    
    p.play()

    # Wait until the end of the event
    end_time = datetime.datetime.strptime(next_event["event"]["end"], "%x %X")
    print("Event will end at:")
    print(end_time)
    pause.until(end_time.timestamp())