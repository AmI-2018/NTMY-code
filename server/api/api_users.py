"""Users API"""

from flask import jsonify, request, Blueprint, abort, Response
from datetime import datetime

from .decorators import require_root, is_me, is_known
import database

users_bp = Blueprint("users_bp", __name__)

# Basic usage

@users_bp.route("/users", methods=["GET"])
@require_root
def handler_get_users():
    """Get the list of the users.

    .. :quickref: Users; Get the list of the users.
    
    :status 200: The list was correctly retrieved
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """
    
    with database.session.DatabaseSession() as db_session:
        return jsonify([u.to_dict() for u in db_session.get(database.model.standard.User)])

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

    with database.session.DatabaseSession() as db_session:
        try:
            new_user = db_session.add(database.model.standard.User.from_dict(request.json))
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

    with database.session.DatabaseSession() as db_session:
        try:
            return jsonify(db_session.get(database.model.standard.User, userID)[0].to_dict())
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

@users_bp.route("/users/<int:userID>", methods=["PUT"])
@is_me
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

    with database.session.DatabaseSession() as db_session:
        try:
            upd_user = db_session.get(database.model.standard.User, userID)[0]
            return jsonify(db_session.upd(upd_user, request.json).to_dict())
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

@users_bp.route("/users/<int:userID>", methods=["DELETE"])
@is_me
def handler_delete_user_from_id(userID):
    """Delete the user with the given ID.

    .. :quickref: Users; Delete the user with the given ID.
    
    :param int userID: The ID of the user to delete
    :status 200: The user was correctly deleted
    :status 400: The user could not be found
    :status 401: The user has not logged in
    :return: Empty response
    """

    with database.session.DatabaseSession() as db_session:
        try:
            del_user = db_session.get(database.model.standard.User, userID)[0]
            db_session.rem(del_user)
            return ""
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

# Photo access

@users_bp.route("/users/<int:userID>/photo", methods=["GET"])
def handler_get_user_photo_from_id(userID):
    """Get the photo of the user with the given ID.

    .. :quickref: Users; Get the photo of the user with the given ID.
    
    :param int userID: The ID of the user to retrieve the photo from
    :status 200: The user was correctly retrieved
    :status 400: The user could not be found
    :status 401: The user has not logged in
    :return: The jpeg photo of the user
    """

    with database.session.DatabaseSession() as db_session:
        try:
            user = db_session.get(database.model.standard.User, userID)[0]
            return user.photo, {"Content-Type": "image/jpeg"} if user.photo is not None else abort(400)
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

@users_bp.route("/users/<int:userID>/photo", methods=["POST"])
@is_me
def handler_set_user_photo_from_id(userID):
    """Set the photo of the user with the given ID.

    .. :quickref: Users; Set the photo of the user with the given ID.
    
    :param int userID: The ID of the user to set the photo to
    :status 200: The user was correctly retrieved
    :status 400: The user could not be found
    :status 401: The user has not logged in
    :return: The jpeg photo of the user
    """

    with database.session.DatabaseSession() as db_session:
        try:
            user = db_session.get(database.model.standard.User, userID)[0]
            db_session.upd(user, {"photo": request.data})
            return user.photo, {"Content-Type": "image/jpeg"}
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

# Connections subcollection

@users_bp.route("/users/<int:userID>/connections", methods=["GET"])
@is_me
def handler_get_user_connections_from_id(userID):
    """Get the connections of the user with the given ID.

    .. :quickref: Users; Get the connections of the user with the given ID.
    
    :param int userID: The ID of the user to retrieve the collection from
    :status 200: The list was correctly retrieved
    :status 400: The user could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """

    with database.session.DatabaseSession() as db_session:
        try:
            connections = db_session.filter(database.model.relationships.UserConnection, "userID1 = '{}'".format(userID))
            return jsonify([c.to_dict() for c in connections])
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

@users_bp.route("/users/<int:userID>/connections/user/<int:otherID>", methods=["GET"])
@is_me
def handler_get_user_connections_from_id_from_user_id(userID, otherID):
    """Get the connections of the user with the given IDs (two users).

    .. :quickref: Users; Get the connections of the user with the given IDs (two users).
    
    :param int userID: The ID of the user to retrieve the collection from
    :param int otherID: The ID of the user which whom the connection happened
    :status 200: The list was correctly retrieved
    :status 400: The user could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """

    with database.session.DatabaseSession() as db_session:
        try:
            connections = db_session.filter(database.model.relationships.UserConnection, "userID1 = '{}' AND userID2 = '{}'".format(userID, otherID))
            return jsonify([c.to_dict() for c in connections])
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

@users_bp.route("/users/<int:userID>/connections/event/<int:eventID>", methods=["GET"])
@is_me
def handler_get_user_connections_from_id_from_event_id(userID, eventID):
    """Get the connections of the user with the given IDs (user and event).

    .. :quickref: Users; Get the connections of the user with the given IDs (user and event).
    
    :param int userID: The ID of the user to retrieve the collection from
    :param int eventID: The ID of the event where the connection happened
    :status 200: The list was correctly retrieved
    :status 400: The user or event could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """

    with database.session.DatabaseSession() as db_session:
        try:
            connections = db_session.filter(database.model.relationships.UserConnection, "userID1 = '{}' AND eventID = '{}'".format(userID, eventID))
            return jsonify([c.to_dict() for c in connections])
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

@users_bp.route("/users/<int:userID>/connections", methods=["POST"])
@is_me
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

    with database.session.DatabaseSession() as db_session:
        try:
            new_connection = db_session.add(database.model.relationships.UserConnection.from_dict({**request.json, **{"userID1": userID}}))
            return jsonify(new_connection.to_dict())
        except (database.exceptions.InvalidDictError, database.exceptions.DatabaseError) as e:
            return abort(400, str(e))

# Events subcollection

@users_bp.route("/users/<int:userID>/events", methods=["GET"])
@is_me
def handler_get_user_events_from_id(userID):
    """Get the events of the user with the given ID.

    .. :quickref: Users; Get the events of the user with the given ID.
    
    :param int userID: The ID of the user to retrieve the collection from
    :status 200: The list was correctly retrieved
    :status 400: The user could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """

    with database.session.DatabaseSession() as db_session:
        try:
            user = db_session.get(database.model.standard.User, userID)[0]
            events = [e.to_dict() for e in user.events]
            return jsonify(events)
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

@users_bp.route("/users/<int:userID>/events/created", methods=["GET"])
@is_me
def handler_get_user_created_events_from_id(userID):
    """Get the events created by the user with the given ID.

    .. :quickref: Users; Get the events created by the user with the given ID.
    
    :param int userID: The ID of the user to retrieve the collection from
    :status 200: The list was correctly retrieved
    :status 400: The user could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """

    with database.session.DatabaseSession() as db_session:
        try:
            user = db_session.get(database.model.standard.User, userID)[0]
            events = [e.to_dict() for e in user.created_events]
            return jsonify(events)
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

@users_bp.route("/users/<int:userID>/events/next", methods=["GET"])
@is_me
def handler_get_user_next_events_from_id(userID):
    """Get the future events of the user with the given ID.

    .. :quickref: Users; Get the future events of the user with the given ID.
    
    :param int userID: The ID of the user to retrieve the collection from
    :status 200: The list was correctly retrieved
    :status 400: The user could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """

    with database.session.DatabaseSession() as db_session:
        try:
            user = db_session.get(database.model.standard.User, userID)[0]
            events = [e.to_dict() for e in sorted(user.events, key=(lambda e: e.event.start)) if e.event.end > datetime.now()]
            return jsonify(events)
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

# Interests subcollection

@users_bp.route("/users/<int:userID>/interests", methods=["GET"])
@is_me
def handler_get_user_interests_from_id(userID):
    """Get the interests of the user with the given ID.

    .. :quickref: Users; Get the interests of the user with the given ID.
    
    :param int userID: The ID of the user to retrieve the collection from
    :status 200: The list was correctly retrieved
    :status 400: The user could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """

    with database.session.DatabaseSession() as db_session:
        try:
            user = db_session.get(database.model.standard.User, userID)[0]
            interests = [c.to_dict() for c in user.interests]
            return jsonify(interests)
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

@users_bp.route("/users/<int:userID>/interests", methods=["POST"])
@is_me
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

    with database.session.DatabaseSession() as db_session:
        try:
            new_int = db_session.add(database.model.relationships.UserInterest.from_dict({**request.json, **{"userID": userID}}))
            return jsonify(new_int.to_dict())
        except (database.exceptions.InvalidDictError, database.exceptions.DatabaseError) as e:
            return abort(400, str(e))

@users_bp.route("/users/<int:userID>/interests/<int:categoryID>", methods=["GET"])
@is_me
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

    with database.session.DatabaseSession() as db_session:
        try:
            user_int = db_session.get(database.model.relationships.UserInterest, (userID, categoryID))[0]
            return jsonify(user_int.to_dict())
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

@users_bp.route("/users/<int:userID>/interests/<int:categoryID>", methods=["DELETE"])
@is_me
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

    with database.session.DatabaseSession() as db_session:
        try:
            user_int = db_session.get(database.model.relationships.UserInterest, (userID, categoryID))[0]
            db_session.rem(user_int)
            return ""
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))