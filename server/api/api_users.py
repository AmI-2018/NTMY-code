"""Users API"""

from flask import jsonify, request, Blueprint, abort
import database

users_bp = Blueprint("users_bp", __name__)

# Basic usage

@users_bp.route("/users", methods=["GET"])
def handler_get_users():
    """Get the list of the users.

    .. :quickref: Users; Get the list of the users.
    
    :status 200: The list was correctly retrieved
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """
    return jsonify([u.to_dict() for u in database.functions.get(database.model.standard.User)])

@users_bp.route("/users", methods=["POST"])
def handler_add_user():
    """Add an user.

    .. :quickref: Users; Add an user.
    
    :json string name: The name of the new user
    :json string surname: The surname of the new user
    :json string phone: The phone of the new user
    :json string link: The link of the new user
    :json string password: The password of the new user
    :status 200: The user was correctly inserted
    :status 400: The provided JSON is invalid or there is a database error
    :status 401: The user has not logged in
    :return: The JSON-encoded newly created user
    """
    try:
        new_user =  database.functions.add(database.model.standard.User.from_dict(request.json))
        return jsonify(new_user.to_dict())
    except (database.exceptions.InvalidDictError, database.exceptions.DatabaseError) as e:
        return abort(400, str(e))

# ID indexed

@users_bp.route("/users/<int:userID>", methods=["GET"])
def handler_get_user_from_id(userID):
    """Get the user with the given ID.

    .. :quickref: Users; Get the user with the given ID.
    
    :param int userID: The ID of the user to retrieve
    :status 200: The user was correctly retrieved
    :status 400: The user could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded user
    """
    try:
        return jsonify(database.functions.get(database.model.standard.User, userID)[0].to_dict())
    except database.exceptions.DatabaseError as e:
        return abort(400, str(e))

@users_bp.route("/users/<int:userID>", methods=["PUT"])
def handler_patch_user_from_id(userID):
    """Update the user with the given ID.

    .. :quickref: Users; Update the user with the given ID.
    
    :json string name: The name of the updated user
    :json string surname: The surname of the updated user
    :json string phone: The phone of the updated user
    :json string link: The link of the updated user
    :json string password: The password of the updated user
    :param int userID: The ID of the user to update
    :status 200: The user was correctly updated
    :status 400: The user could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded user
    """
    try:
        upd_user = database.functions.get(database.model.standard.User, userID)[0]
        return jsonify(database.functions.upd(upd_user, request.json).to_dict())
    except database.exceptions.DatabaseError as e:
        return abort(400, str(e))

@users_bp.route("/users/<int:userID>", methods=["DELETE"])
def handler_delete_user_from_id(userID):
    """Delete the user with the given ID.

    .. :quickref: Users; Delete the user with the given ID.
    
    :param int userID: The ID of the user to delete
    :status 200: The user was correctly deleted
    :status 400: The user could not be found
    :status 401: The user has not logged in
    :return: Empty response
    """
    try:
        del_user = database.functions.get(database.model.standard.User, userID)[0]
        database.functions.rem(del_user)
        return ""
    except database.exceptions.DatabaseError as e:
        return abort(400, str(e))

# Connections subcollection

@users_bp.route("/users/<int:userID>/connections", methods=["GET"])
def handler_get_user_connections_from_id(userID):
    """Get the connections of the user with the given ID.

    .. :quickref: Users; Get the connections of the user with the given ID.
    
    :param int userID: The ID of the user to retrieve the collection from
    :status 200: The list was correctly retrieved
    :status 400: The user could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """
    try:
        connections = database.functions.filter(database.model.relationships.UserConnection, "userID1 = '{}'".format(userID))
        return jsonify([c.to_dict() for c in connections])
    except database.exceptions.DatabaseError as e:
        return abort(400, str(e))

@users_bp.route("/users/<int:userID>/connections/<int:eventID>", methods=["GET"])
def handler_get_user_connections_from_id_from_id(userID, eventID):
    """Get the connections of the user with the given IDs.

    .. :quickref: Users; Get the connections of the user with the given IDs.
    
    :param int userID: The ID of the user to retrieve the collection from
    :param int eventID: The ID of the event where the connection happened
    :status 200: The list was correctly retrieved
    :status 400: The user or event could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """
    try:
        connections = database.functions.filter(database.model.relationships.UserConnection, "userID1 = '{}' AND eventID = '{}'".format(userID, eventID))
        return jsonify([c.to_dict() for c in connections])
    except database.exceptions.DatabaseError as e:
        return abort(400, str(e))

@users_bp.route("/users/<int:userID>/connections", methods=["POST"])
def handler_add_user_connection_from_id(userID):
    """Add a connection to the user with the given ID.

    .. :quickref: Users; Add a connection to the user with the given ID.
    
    :param int userID: The ID of the user to add the connection
    :json int userID2: The ID of the user to create the connection with
    :json int eventID: The ID of the event where the connection was created
    :status 200: The connection was correctly inserted
    :status 400: The user could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded newly created connection
    """
    try:
        new_connection =  database.functions.add(database.model.relationships.UserConnection.from_dict({**request.json, **{"userID1": userID}}))
        return jsonify(new_connection.to_dict())
    except (database.exceptions.InvalidDictError, database.exceptions.DatabaseError) as e:
        return abort(400, str(e))

# Events subcollection

@users_bp.route("/users/<int:userID>/events", methods=["GET"])
def handler_get_user_events_from_id(userID):
    """Get the events of the user with the given ID.

    .. :quickref: Users; Get the events of the user with the given ID.
    
    :param int userID: The ID of the user to retrieve the collection from
    :status 200: The list was correctly retrieved
    :status 400: The user could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """
    try:
        user = database.functions.get(database.model.standard.User, userID)[0]
        events = [e.to_dict() for e in user.events]
        return jsonify(events)
    except database.exceptions.DatabaseError as e:
        return abort(400, str(e))

# Interests subcollection

@users_bp.route("/users/<int:userID>/interests", methods=["GET"])
def handler_get_user_interests_from_id(userID):
    """Get the interests of the user with the given ID.

    .. :quickref: Users; Get the interests of the user with the given ID.
    
    :param int userID: The ID of the user to retrieve the collection from
    :status 200: The list was correctly retrieved
    :status 400: The user could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """
    try:
        user = database.functions.get(database.model.standard.User, userID)[0]
        interests = [c.to_dict() for c in user.interests]
        return jsonify(interests)
    except database.exceptions.DatabaseError as e:
        return abort(400, str(e))

@users_bp.route("/users/<int:userID>/interests", methods=["POST"])
def handler_add_user_interest_from_id(userID):
    """Add an interest to the user with the given ID.

    .. :quickref: Users; Add an interest to the user with the given ID.
    
    :param int userID: The ID of the user to add the interest
    :json int categoryID: The ID of the category to add to the interests
    :status 200: The insterest was correctly inserted
    :status 400: The user could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded newly created interest
    """
    try:
        new_int =  database.functions.add(database.model.relationships.UserInterest.from_dict({**request.json, **{"userID": userID}}))
        return jsonify(new_int.to_dict())
    except (database.exceptions.InvalidDictError, database.exceptions.DatabaseError) as e:
        return abort(400, str(e))

@users_bp.route("/users/<int:userID>/interests/<int:categoryID>", methods=["GET"])
def handler_get_user_interest_from_id_from_id(userID, categoryID):
    """Get the interest of the user with the given IDs.

    .. :quickref: Users; Get the interest of the user with the given IDs.
    
    :param int userID: The ID of the user to retrieve
    :param int categoryID: The ID of the category to retrieve
    :status 200: The interest was correctly retrieved
    :status 400: The user or category could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded interest
    """
    try:
        user_int = database.functions.get(database.model.relationships.UserInterest, (userID, categoryID))[0]
        return jsonify(user_int.to_dict())
    except database.exceptions.DatabaseError as e:
        return abort(400, str(e))

@users_bp.route("/users/<int:userID>/interests/<int:categoryID>", methods=["DELETE"])
def handler_delete_user_interest_from_id_from_id(userID, categoryID):
    """Delete the interest of the user with the given IDs.
    
    .. :quickref: Users; Delete the interest of the user with the given IDs.
    
    :param int userID: The ID of the user to delete
    :param int categoryID: The ID of the category to delete
    :status 200: The interest was correctly deleted
    :status 400: The user or the category could not be found
    :status 401: The user has not logged in
    :return: Empty response
    """
    try:
        user_int = database.functions.get(database.model.relationships.UserInterest, (userID, categoryID))[0]
        database.functions.rem(user_int)
        return ""
    except database.exceptions.DatabaseError as e:
        return abort(400, str(e))