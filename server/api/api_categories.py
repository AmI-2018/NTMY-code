"""Categories API"""

from flask import jsonify, request, Blueprint
import database

categories_bp = Blueprint("categories_bp", __name__)

@categories_bp.route("/categories", methods=["GET"])
def handler_get_categories():
    """Get the list of the categories.
    
    .. :quickref: Categories; Get the list of the categories.
    
    :status 200: The list was correctly retrieved
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """

    with database.session.DatabaseSession() as db_session:
        return jsonify([c.to_dict() for c in db_session.get(database.model.standard.Category)])