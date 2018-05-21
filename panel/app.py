from . import logic as pl
import json
import app_functions as af

with open("panel_config.json") as f:
    config = json.loads(f.read())

while True:
    user_id = af.detect_user(config)
    dest_id = af.obtain_dest_by_user(config, user_id)
    exit_point = pl.route.generate_direction(config,dest_id)
    af.generate_arrow(exit_point)

