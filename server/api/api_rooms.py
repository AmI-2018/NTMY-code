"""Rooms API"""

from flask import jsonify, request, Blueprint
import database

rooms_bp = Blueprint("rooms_bp", __name__)

# Basic usage

@rooms_bp.route("/rooms", methods=["GET"])
def handler_get_rooms():
    """Get the list of the rooms.

    .. :quickref: Rooms; Get the list of the rooms.

    :status 200: The list was correctly retrieved
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """
    return jsonify([r.to_dict() for r in database.functions.get(database.model.standard.Room)])

@rooms_bp.route("/rooms", methods=["POST"])
def handler_add_room():
    """Add a room.

    .. :quickref: Rooms; Add a room.
    
    :json string name: The name of the new room
    :json string description: The description of the new room
    :json int size: The size of the new room
    :status 200: The room was correctly inserted
    :status 400: The provided JSON is invalid or there is a database error
    :status 401: The user has not logged in
    :return: The JSON-encoded newly created room
    """
    try:
        new_room =  database.functions.add(database.model.standard.Room.from_dict(request.json))
        return jsonify(new_room.to_dict())
    except (database.exceptions.InvalidDictError, database.exceptions.DatabaseError) as e:
        return jsonify({
            "msg": str(e)
        }), 400

# ID indexed

@rooms_bp.route("/rooms/<int:roomID>", methods=["GET"])
def handler_get_room_from_id(roomID):
    """Get the room with the given ID.

    .. :quickref: Rooms; Get the room with the given ID.
    
    :param int roomID: The ID of the room to retrieve
    :status 200: The room was correctly retrieved
    :status 400: The room could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded room
    """
    try:
        return jsonify(database.functions.get(database.model.standard.Room, roomID)[0].to_dict())
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400

@rooms_bp.route("/rooms/<int:roomID>", methods=["PUT"])
def handler_patch_room_from_id(roomID):
    """Update the room with the given ID.

    .. :quickref: Rooms; Update the room with the given ID.
    
    :json string name: The name of the updated room
    :json string description: The description of the updated room
    :json int size: The size of the updated room
    :param int roomID: The ID of the room to update
    :status 200: The room was correctly updated
    :status 400: The room could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded room
    """
    try:
        upd_room = database.functions.get(database.model.standard.Room, roomID)[0]
        return jsonify(database.functions.upd(upd_room, request.json).to_dict())
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400

@rooms_bp.route("/rooms/<int:roomID>", methods=["DELETE"])
def handler_delete_room_from_id(roomID):
    """Delete the room with the given ID.

    .. :quickref: Rooms; Delete the room with the given ID.
    
    :param int roomID: The ID of the room to delete
    :status 200: The room was correctly deleted
    :status 400: The room could not be found
    :status 401: The user has not logged in
    :return: Empty response
    """
    try:
        del_room = database.functions.get(database.model.standard.Room, roomID)[0]
        database.functions.rem(del_room)
        return ""
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400

# Facilities subcollection

@rooms_bp.route("/rooms/<int:roomID>/facilities", methods=["GET"])
def handler_get_room_facilities(roomID):
    """Get the facilities of the room with the given ID.

    .. :quickref: Rooms; Get the facilities of the room with the given ID.
    
    :param int roomID: The ID of the room to retrieve the collection from
    :status 200: The list was correctly retrieved
    :status 400: The room could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """
    try:
        room = database.functions.get(database.model.standard.Room, roomID)[0]
        facilities = [f.to_dict() for f in room.facilities]
        return jsonify(facilities)
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400

@rooms_bp.route("/rooms/<int:roomID>/facilities", methods=["POST"])
def handler_add_room_facility(roomID):
    """Add a facility to the room with the given ID.

    .. :quickref: Rooms; Add a facility to the room with the given ID.
    
    :param int roomID: The ID of the room to add the facility
    :json int facilityID: The ID of the facility to add to the facilities
    :status 200: The facility was correctly inserted
    :status 400: The room could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded newly created facility
    """
    try:
        new_fac =  database.functions.add(database.model.relationships.RoomFacility.from_dict({**request.json, **{"roomID": roomID}}))
        return jsonify(new_fac.to_dict())
    except (database.exceptions.InvalidDictError, database.exceptions.DatabaseError) as e:
        return jsonify({
            "msg": str(e)
        }), 400

@rooms_bp.route("/rooms/<int:roomID>/facilities/<int:facilityID>", methods=["GET"])
def handler_get_room_facility_from_id_from_id(roomID, facilityID):
    """Get the facility of the room with the given IDs.

    .. :quickref: Rooms; Get the facility of the room with the given IDs.
    
    :param int roomID: The ID of the room to retrieve
    :param int facilityID: The ID of the facility to retrieve
    :status 200: The facility was correctly retrieved
    :status 400: The room or facility could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded facility
    """
    try:
        room_fac = database.functions.get(database.model.relationships.RoomFacility, (roomID, facilityID))[0]
        return jsonify(room_fac.to_dict())
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400

@rooms_bp.route("/rooms/<int:roomID>/facilities/<int:facilityID>", methods=["DELETE"])
def handler_delete_room_facility_from_id_from_id(roomID, facilityID):
    """Delete the facility of the room with the given IDs.
    
    .. :quickref: Rooms; Delete the facility of the room with the given IDs.
    
    :param int roomID: The ID of the room to delete
    :param int facilityID: The ID of the facility to delete
    :status 200: The facility was correctly deleted
    :status 400: The room or the facility could not be found
    :status 401: The user has not logged in
    :return: Empty response
    """
    try:
        room_fac = database.functions.get(database.model.relationships.RoomFacility, (roomID, facilityID))[0]
        database.functions.rem(room_fac)
        return ""
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400