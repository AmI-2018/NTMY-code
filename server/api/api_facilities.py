"""Facilities API"""

from . import app, jsonify, database

@app.route("/facilities", methods=["GET"])
def handler_get_facilities():
    """Get the list of the facilities.
    
    .. :quickref: Facilities; Get the list of the facilities.
    
    :status 200: The list was correctly retrieved
    :return: The JSON-encoded list
    """

    return jsonify([f.to_dict() for f in database.functions.get(database.model.standard.Facility)])