"""Schedule API"""

from . import app, jsonify, database

@app.route("/schedule", methods=["GET"])
def handler_get_schedule():
    pass