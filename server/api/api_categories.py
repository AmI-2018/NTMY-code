"""Categories API"""

from . import app, jsonify, database

@app.route("/categories", methods=["GET"])
def handler_get_categories():
    return jsonify([c.to_dict() for c in database.functions.get(database.model.standard.Category)])