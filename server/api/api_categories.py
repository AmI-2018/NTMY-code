"""Categories API"""

from . import app, jsonify, database

@app.route("/categories", methods=["GET"])
def handler_get_categories():
    """Get the list of the categories.
    
    .. :quickref: Categories; Get the list of the categories.
    
    :status 200: The list was correctly retrieved
    :return: The JSON-encoded list
    """

    return jsonify([c.to_dict() for c in database.functions.get(database.model.standard.Category)])