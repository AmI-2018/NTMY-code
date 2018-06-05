import json
import requests

import app_functions as af
import logic as pl
import panel_setter as ps

# Load Settings
with open("panel_config.json") as f:
    config = json.load(f)

session = requests.session()

# Login
url = config['serveruri'] + "/login"
json = {'email': config['email'], 'password': config['rootpwd']}
session.post(url, json=json)

# Main Loop
while True:
    # Search for a nearby user
    user_id = af.detect_user()

    # Obtain his/her destination from the server
    dest_event = af.obtain_dest_by_user(config, user_id, session)

    # Generate the exit point according to the direction
    exit_point = pl.route.generate_direction(config['panelID'], dest_event["room"]["nodeID"])

    # Light up the related arrow
    ps.make_arrow(exit_point, dest_event["color"]["red"], dest_event["color"]["green"], dest_event["color"]["blue"])
