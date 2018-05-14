"""Facilities API"""

from . import app, jsonify, database

@app.route("/facilities", methods=["GET"])
def handler_get_facilities():
    return jsonify([f.to_dict() for f in database.functions.get(database.model.standard.Facility)])