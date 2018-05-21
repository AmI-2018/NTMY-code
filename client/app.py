import datetime
import json

import pause
import requests

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
    "userID": 0,
    "password": config["rootpwd"]
})

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

    # Get the facilities
    facilities = session.get("{}/events/{}/facilities".format(config["serveruri"], next_event["event"]["eventID"])).json()

    # Prepare the room
    for f in facilities:
        if f["facility"]["name"] == "lights":
            pass
        elif f["facility"]["name"] == "tv":
            pass
        elif f["facility"]["name"] == "audio":
            pass

    # Wait until the end of the event
    end_time = datetime.datetime.strptime(next_event["event"]["end"], "%x %X")
    print("Event will end at:")
    print(end_time)
    pause.until(end_time.timestamp())