"""Users API"""

from . import app, jsonify, database, request

# Basic usage

@app.route("/users", methods=["GET"])
def handler_get_users():
    return jsonify([u.to_dict() for u in database.functions.get(database.model.standard.User)])

@app.route("/users", methods=["POST"])
def handler_add_user():
    try:
        new_user =  database.functions.add(database.model.standard.User.from_dict(request.json))
        return jsonify(new_user.to_dict())
    except (database.exceptions.InvalidDictError, database.exceptions.DatabaseError) as e:
        return jsonify({
            "msg": str(e)
        }), 400

# ID indexed

@app.route("/users/<int:userID>", methods=["GET"])
def handler_get_user_from_id(userID):
    return jsonify([u.to_dict() for u in database.functions.get(database.model.standard.User, userID)])

@app.route("/users/<int:userID>", methods=["PUT"])
def handler_patch_user_from_id(userID):
    try:
        upd_user = database.functions.get(database.model.standard.User, userID)[0]
        return jsonify(database.functions.upd(upd_user, request.json).to_dict())
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400

@app.route("/users/<int:userID>", methods=["DELETE"])
def handler_delete_user_from_id(userID):
    try:
        del_user = database.functions.get(database.model.standard.User, userID)[0]
        database.functions.rem(del_user)
        return ""
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400

# Connections subcollection

@app.route("/users/<int:userID>/connections", methods=["GET"])
def handler_get_user_connections_from_id(userID):
    try:
        user = database.functions.get(database.model.standard.User, userID)[0]
        connections = [u.to_dict() for u in user.connections]
        return jsonify(connections)
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400

@app.route("/users/<int:userID>/connections", methods=["POST"])
def handler_add_user_connection_from_id(userID):
    try:
        new_connection =  database.functions.add(database.model.relationships.UserConnection.from_dict({**request.json, **{"userID1": userID}}))
        return jsonify(new_connection.to_dict())
    except (database.exceptions.InvalidDictError, database.exceptions.DatabaseError) as e:
        return jsonify({
            "msg": str(e)
        }), 400

# Events subcollection

@app.route("/users/<int:userID>/events", methods=["GET"])
def handler_get_user_events_from_id(userID):
    try:
        user = database.functions.get(database.model.standard.User, userID)[0]
        events = [e.to_dict() for e in user.events]
        return jsonify(events)
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400

# Interests subcollection

@app.route("/users/<int:userID>/interests", methods=["GET"])
def handler_get_user_interests_from_id(userID):
    try:
        user = database.functions.get(database.model.standard.User, userID)[0]
        interests = [c.to_dict() for c in user.interests]
        return jsonify(interests)
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400

@app.route("/users/<int:userID>/interests", methods=["POST"])
def handler_add_user_interest_from_id(userID):
    try:
        new_int =  database.functions.add(database.model.relationships.UserInterest.from_dict({**request.json, **{"userID": userID}}))
        return jsonify(new_int.to_dict())
    except (database.exceptions.InvalidDictError, database.exceptions.DatabaseError) as e:
        return jsonify({
            "msg": str(e)
        }), 400

@app.route("/users/<int:userID>/interests/<int:categoryID>", methods=["GET"])
def handler_get_user_interest_from_id_from_id(userID, categoryID):
    try:
        user_int = database.functions.get(database.model.relationships.UserInterest, (userID, categoryID))[0]
        return jsonify(user_int.to_dict())
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400

@app.route("/users/<int:userID>/interests/<int:categoryID>", methods=["DELETE"])
def handler_delete_user_interest_from_id_from_id(userID, categoryID):
    try:
        user_int = database.functions.get(database.model.relationships.UserInterest, (userID, categoryID))[0]
        database.functions.rem(user_int)
        return ""
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400