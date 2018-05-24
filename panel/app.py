from . import logic as pl
import json
import app_functions as af
import time


""" Load Settings """
with open("panel_config.json") as f:
    config = json.loads(f.read())

""" Main Loop """
while True:
    """ wait a nearby user """
    user_id = af.detect_user()

    """ obtain his/her destination from the server """
    dest_id = af.obtain_dest_by_user(config, user_id)

    """ generate the exit point according to the direction """
    exit_point = pl.route.generate_direction(config,dest_id)

    """ light up the related arrow """

    af.generate_arrow(exit_point)

    """ give time to look the arrow """
    time.sleep(5)

    """ turn off the arrows"""
    af.reset_arrow()
