"""Rooms API"""

from . import app, jsonify, request, database

# Basic usage

@app.route("/rooms", methods=["GET"])
def handler_get_rooms():
    return jsonify([r.to_dict() for r in database.functions.get(database.model.standard.Room)])

@app.route("/rooms", methods=["POST"])
def handler_add_room():
    try:
        new_room =  database.functions.add(database.model.standard.Room.from_dict(request.json))
        return jsonify(new_room.to_dict())
    except (database.exceptions.InvalidDictError, database.exceptions.DatabaseError) as e:
        return jsonify({
            "msg": str(e)
        }), 400

# ID indexed

@app.route("/rooms/<int:roomID>", methods=["GET"])
def handler_get_room_from_id(roomID):
    try:
        return jsonify([r.to_dict() for r in database.functions.get(database.model.standard.Room, roomID)])
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400

@app.route("/rooms/<int:roomID>", methods=["PUT"])
def handler_patch_room_from_id(roomID):
    try:
        upd_room = database.functions.get(database.model.standard.Room, roomID)[0]
        return jsonify(database.functions.upd(upd_room, request.json).to_dict())
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400

@app.route("/rooms/<int:roomID>", methods=["DELETE"])
def handler_delete_room_from_id(roomID):
    try:
        del_room = database.functions.get(database.model.standard.Room, roomID)[0]
        database.functions.rem(del_room)
        return ""
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400

# Facilities subcollection

@app.route("/rooms/<int:roomID>/facilities", methods=["GET"])
def handler_get_room_facilities(roomID):
    try:
        room = database.functions.get(database.model.standard.Room, roomID)[0]
        facilities = [f.to_dict() for f in room.facilities]
        return jsonify(facilities)
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400

@app.route("/rooms/<int:roomID>/facilities", methods=["POST"])
def handler_add_room_facility(roomID):
    try:
        new_fac =  database.functions.add(database.model.relationships.RoomFacility.from_dict({**request.json, **{"roomID": roomID}}))
        return jsonify(new_fac.to_dict())
    except (database.exceptions.InvalidDictError, database.exceptions.DatabaseError) as e:
        return jsonify({
            "msg": str(e)
        }), 400

@app.route("/rooms/<int:roomID>/facilities/<int:facilityID>", methods=["GET"])
def handler_get_room_facility_from_id_from_id(roomID, facilityID):
    try:
        room_fac = database.functions.get(database.model.relationships.RoomFacility, (roomID, facilityID))[0]
        return jsonify(room_fac.to_dict())
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400

@app.route("/rooms/<int:roomID>/facilities/<int:facilityID>", methods=["DELETE"])
def handler_delete_room_facility_from_id_from_id(roomID, facilityID):
    try:
        room_fac = database.functions.get(database.model.relationships.RoomFacility, (roomID, facilityID))[0]
        database.functions.rem(room_fac)
        return ""
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400