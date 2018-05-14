"""Events API"""

from . import app, jsonify, database, request

# Basic usage

@app.route("/events", methods=["GET"])
def handler_get_events():
    return jsonify([e.to_dict() for e in database.functions.get(database.model.standard.Event)])

@app.route("/events", methods=["POST"])
def handler_add_event():
    try:
        new_event = database.functions.add(database.model.standard.Event.from_dict(request.json))
        return jsonify(new_event.to_dict())
    except (database.exceptions.InvalidDictError, database.exceptions.DatabaseError) as e:
        return jsonify({
            "msg": str(e)
        }), 400

# ID indexed

@app.route("/events/<int:eventID>", methods=["GET"])
def handler_get_event_from_id(eventID):
    return jsonify([e.to_dict() for e in database.functions.get(database.model.standard.Event, eventID)])

@app.route("/events/<int:eventID>", methods=["PUT"])
def handler_patch_event_from_id(eventID):
    try:
        upd_event = database.functions.get(database.model.standard.Event, eventID)[0]
        return jsonify(database.functions.upd(upd_event, request.json).to_dict())
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400

@app.route("/events/<int:eventID>", methods=["DELETE"])
def handler_delete_event_from_id(eventID):
    try:
        del_event = database.functions.get(database.model.standard.Event, eventID)[0]
        database.functions.rem(del_event)
        return ""
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400

# Categories subcollection

@app.route("/events/<int:eventID>/categories", methods=["GET"])
def handler_get_event_categories_from_id(eventID):
    try:
        event = database.functions.get(database.model.standard.Event, eventID)[0]
        categories = [c.to_dict() for c in event.categories]
        return jsonify(categories)
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400

@app.route("/events/<int:eventID>/categories", methods=["POST"])
def handler_add_event_category_from_id(eventID):
    try:
        new_cat = database.functions.add(database.model.relationships.EventCategory.from_dict({**request.json, **{"eventID": eventID}}))
        return jsonify(new_cat.to_dict())
    except (database.exceptions.InvalidDictError, database.exceptions.DatabaseError) as e:
        return jsonify({
            "msg": str(e)
        }), 400

@app.route("/events/<int:eventID>/categories/<int:categoryID>", methods=["GET"])
def handler_get_event_category_from_id_from_id(eventID, categoryID):
    try:
        event_cat = database.functions.get(database.model.relationships.EventCategory, (eventID, categoryID))[0]
        return jsonify(event_cat.to_dict())
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400

@app.route("/events/<int:eventID>/categories/<int:categoryID>", methods=["DELETE"])
def handler_delete_event_category_from_id_from_id(eventID, categoryID):
    try:
        event_cat = database.functions.get(database.model.relationships.EventCategory, (eventID, categoryID))[0]
        database.functions.rem(event_cat)
        return ""
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400

# Facilities subcollection

@app.route("/events/<int:eventID>/facilities", methods=["GET"])
def handler_get_event_facilities(eventID):
    try:
        event = database.functions.get(database.model.standard.Event, eventID)[0]
        facilities = [f.to_dict() for f in event.facilities]
        return jsonify(facilities)
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400

@app.route("/events/<int:eventID>/facilities", methods=["POST"])
def handler_add_event_facility(eventID):
    try:
        new_fac =  database.functions.add(database.model.relationships.EventFacility.from_dict({**request.json, **{"eventID": eventID}}))
        return jsonify(new_fac.to_dict())
    except (database.exceptions.InvalidDictError, database.exceptions.DatabaseError) as e:
        return jsonify({
            "msg": str(e)
        }), 400

@app.route("/events/<int:eventID>/facilities/<int:facilityID>", methods=["GET"])
def handler_get_event_facility_from_id_from_id(eventID, facilityID):
    try:
        event_fac = database.functions.get(database.model.relationships.EventFacility, (eventID, facilityID))[0]
        return jsonify(event_fac.to_dict())
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400

@app.route("/events/<int:eventID>/facilities/<int:facilityID>", methods=["DELETE"])
def handler_delete_event_facility_from_id_from_id(eventID, facilityID):
    try:
        event_fac = database.functions.get(database.model.relationships.EventFacility, (eventID, facilityID))[0]
        database.functions.rem(event_fac)
        return ""
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400

# Participants subcollection

@app.route("/events/<int:eventID>/participants", methods=["GET"])
def handler_get_event_participants(eventID):
    try:
        event = database.functions.get(database.model.standard.Event, eventID)[0]
        users = [u.to_dict() for u in event.users]
        return jsonify(users)
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400

@app.route("/events/<int:eventID>/participants", methods=["POST"])
def handler_add_event_participant(eventID):
    try:
        new_event =  database.functions.add(database.model.relationships.UserConnection.from_dict({**request.json, **{"eventID": eventID}}))
        return jsonify(new_event.to_dict())
    except (database.exceptions.InvalidDictError, database.exceptions.DatabaseError) as e:
        return jsonify({
            "msg": str(e)
        }), 400

@app.route("/events/<int:eventID>/participants/<int:userID>", methods=["GET"])
def handler_get_event_participant_from_id_from_id(eventID, userID):
    try:
        event_part = database.functions.get(database.model.relationships.EventParticipant, (eventID, userID))[0]
        return jsonify(event_part.to_dict())
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400

@app.route("/events/<int:eventID>/participants/<int:userID>", methods=["DELETE"])
def handler_delete_event_participant_from_id_from_id(eventID, userID):
    try:
        event_part = database.functions.get(database.model.relationships.EventParticipant, (eventID, userID))[0]
        database.functions.rem(event_part)
        return ""
    except database.exceptions.DatabaseError as e:
        return jsonify({
            "msg": str(e)
        }), 400