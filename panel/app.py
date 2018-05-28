import logic as pl
import json
import app_functions as af
import time
import requests

""" Load Settings """
with open("panel_config.json") as f:
    config = json.load(f)

session = requests.session()

""" login """
url = config['serveruri'] + "/login"
json = {'email': config['email'], 'password': config['rootpwd']}
session.post(url, json=json)

users = af.populate_users(config,session)

""" Main Loop """
while True:
    """ wait a nearby user """
    user_id = af.detect_user(users)

    """ obtain his/her destination from the server """
    dest_id = af.obtain_dest_by_user(config, user_id, session)
    print(dest_id)
    """ generate the exit point according to the direction """
    exit_point = pl.route.generate_direction(config,dest_id)

    """ light up the related arrow """

    af.generate_arrow(exit_point)

    """ give time to look the arrow """
    time.sleep(5)

    """ turn off the arrows"""
    af.reset_arrow()

