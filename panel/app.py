import json
import requests

import app_functions as af
import logic as pl
import panel_interface as pi

# Load Settings
with open("panel_config.json") as f:
    config = json.load(f)

session = requests.session()

# Login
url = config['serveruri'] + "/login"
json = {'email': config['email'], 'password': config['rootpwd']}
session.post(url, json=json)

pl.graph.load_graph(session,config)

print("the SMART ROAD SIGN " + config['panelId'] + " is ON!")
# Main Loop
while True:
    # Search for a nearby user
    print("I'm searching...")
    user_id = af.detect_user()
    if user_id == False:
        pi.light_standby()
        print("No user nearby..")
        continue
    # Obtain his/her destination from the server
    dest_event = af.obtain_dest_by_user(config, user_id, session)[0]
    # Generate the exit point according to the direction
    exit_point = pl.route.generate_direction(config['panelID'], dest_event["room"]["node"]["nodeID"])

    # Light up the related arrow
    print("Found user "+str(user_id)+" going to the "+dest_event["room"]["name"]+" room ")
    pi.light_arrow(exit_point, dest_event["color"]["red"], dest_event["color"]["green"], dest_event["color"]["blue"])
   
