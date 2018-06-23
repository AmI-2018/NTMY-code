"""Facilities API"""

from flask import jsonify, request, Blueprint
import database

facilities_bp = Blueprint("facilities_bp", __name__)

@facilities_bp.route("/facilities", methods=["GET"])
def handler_get_facilities():
    """Get the list of the facilities.
    
    .. :quickref: Facilities; Get the list of the facilities.
    
    :status 200: The list was correctly retrieved
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """

    with database.session.DatabaseSession() as db_session:
        return jsonify([f.to_dict() for f in db_session.get(database.model.standard.Facility)])