"""Events API"""

from flask import jsonify, request, Blueprint, abort
import database

events_bp = Blueprint("events_bp", __name__)

# Basic usage

@events_bp.route("/events", methods=["GET"])
def handler_get_events():
    """Get the list of the events.

    .. :quickref: Events; Get the list of the events.
    
    :status 200: The list was correctly retrieved
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """
    return jsonify([e.to_dict() for e in database.functions.get(database.model.standard.Event)])

@events_bp.route("/events", methods=["POST"])
def handler_add_event():
    """Add an event.

    .. :quickref: Events; Add an event.
    
    :json string name: The name of the new event
    :json string description: The description of the new event
    :json string start: The date and time of the new event start
    :json string end: The date and time of the new event end
    :status 200: The event was correctly inserted
    :status 400: The provided JSON is invalid or there is a database error
    :status 401: The user has not logged in
    :return: The JSON-encoded newly created event
    """
    try:
        new_event = database.functions.add(database.model.standard.Event.from_dict(request.json))
        return jsonify(new_event.to_dict())
    except (database.exceptions.InvalidDictError, database.exceptions.DatabaseError) as e:
        return abort(400, str(e))

# ID indexed

@events_bp.route("/events/<int:eventID>", methods=["GET"])
def handler_get_event_from_id(eventID):
    """Get the event with the given ID.

    .. :quickref: Events; Get the event with the given ID.
    
    :param int eventID: The ID of the event to retrieve
    :status 200: The event was correctly retrieved
    :status 400: The event could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded event
    """
    try:
        return jsonify(database.functions.get(database.model.standard.Event, eventID)[0].to_dict())
    except database.exceptions.DatabaseError as e:
        return abort(400, str(e))

@events_bp.route("/events/<int:eventID>", methods=["PUT"])
def handler_patch_event_from_id(eventID):
    """Update the event with the given ID.

    .. :quickref: Events; Update the event with the given ID.
    
    :json string name: The name of the updated event
    :json string description: The description of the updated event
    :json string start: The date and time of the updated event start
    :json string end: The date and time of the updated event end
    :param int eventID: The ID of the event to update
    :status 200: The event was correctly updated
    :status 400: The event could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded event
    """
    try:
        upd_event = database.functions.get(database.model.standard.Event, eventID)[0]
        return jsonify(database.functions.upd(upd_event, request.json).to_dict())
    except database.exceptions.DatabaseError as e:
        return abort(400, str(e))

@events_bp.route("/events/<int:eventID>", methods=["DELETE"])
def handler_delete_event_from_id(eventID):
    """Delete the event with the given ID.

    .. :quickref: Events; Delete the event with the given ID.
    
    :param int eventID: The ID of the event to delete
    :status 200: The event was correctly deleted
    :status 400: The event could not be found
    :status 401: The user has not logged in
    :return: Empty response
    """
    try:
        del_event = database.functions.get(database.model.standard.Event, eventID)[0]
        database.functions.rem(del_event)
        return ""
    except database.exceptions.DatabaseError as e:
        return abort(400, str(e))

# Categories subcollection

@events_bp.route("/events/<int:eventID>/categories", methods=["GET"])
def handler_get_event_categories_from_id(eventID):
    """Get the categories of the event with the given ID.

    .. :quickref: Events; Get the categories of the event with the given ID.
    
    :param int eventID: The ID of the event to retrieve the collection from
    :status 200: The list was correctly retrieved
    :status 400: The event could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """
    try:
        event = database.functions.get(database.model.standard.Event, eventID)[0]
        categories = [c.to_dict() for c in event.categories]
        return jsonify(categories)
    except database.exceptions.DatabaseError as e:
        return abort(400, str(e))

@events_bp.route("/events/<int:eventID>/categories", methods=["POST"])
def handler_add_event_category_from_id(eventID):
    """Add a category to the event with the given ID.

    .. :quickref: Events; Add a category to the event with the given ID.
    
    :param int eventID: The ID of the event to add the category
    :json int categoryID: The ID of the category to add to the categories
    :status 200: The category was correctly inserted
    :status 400: The event could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded newly created category
    """
    try:
        new_cat = database.functions.add(database.model.relationships.EventCategory.from_dict({**request.json, **{"eventID": eventID}}))
        return jsonify(new_cat.to_dict())
    except (database.exceptions.InvalidDictError, database.exceptions.DatabaseError) as e:
        return abort(400, str(e))

@events_bp.route("/events/<int:eventID>/categories/<int:categoryID>", methods=["GET"])
def handler_get_event_category_from_id_from_id(eventID, categoryID):
    """Get the category of the event with the given IDs.

    .. :quickref: Events; Get the category of the event with the given IDs.
    
    :param int eventID: The ID of the event to retrieve
    :param int categoryID: The ID of the category to retrieve
    :status 200: The category was correctly retrieved
    :status 400: The event or category could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded category
    """
    try:
        event_cat = database.functions.get(database.model.relationships.EventCategory, (eventID, categoryID))[0]
        return jsonify(event_cat.to_dict())
    except database.exceptions.DatabaseError as e:
        return abort(400, str(e))

@events_bp.route("/events/<int:eventID>/categories/<int:categoryID>", methods=["DELETE"])
def handler_delete_event_category_from_id_from_id(eventID, categoryID):
    """Delete the category of the event with the given IDs.
    
    .. :quickref: Events; Delete the category of the event with the given IDs.
    
    :param int eventID: The ID of the event to delete
    :param int categoryID: The ID of the category to delete
    :status 200: The category was correctly deleted
    :status 400: The event or the category could not be found
    :status 401: The user has not logged in
    :return: Empty response
    """
    try:
        event_cat = database.functions.get(database.model.relationships.EventCategory, (eventID, categoryID))[0]
        database.functions.rem(event_cat)
        return ""
    except database.exceptions.DatabaseError as e:
        return abort(400, str(e))

# Facilities subcollection

@events_bp.route("/events/<int:eventID>/facilities", methods=["GET"])
def handler_get_event_facilities(eventID):
    """Get the facilities of the event with the given ID.

    .. :quickref: Events; Get the facilities of the event with the given ID.
    
    :param int eventID: The ID of the event to retrieve the collection from
    :status 200: The list was correctly retrieved
    :status 400: The event could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """
    try:
        event = database.functions.get(database.model.standard.Event, eventID)[0]
        facilities = [f.to_dict() for f in event.facilities]
        return jsonify(facilities)
    except database.exceptions.DatabaseError as e:
        return abort(400, str(e))

@events_bp.route("/events/<int:eventID>/facilities", methods=["POST"])
def handler_add_event_facility(eventID):
    """Add a facility to the event with the given ID.

    .. :quickref: Events; Add a facility to the event with the given ID.
    
    :param int eventID: The ID of the event to add the facility
    :json int facilityID: The ID of the facility to add to the facilities
    :status 200: The facility was correctly inserted
    :status 400: The event could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded newly created facility
    """
    try:
        new_fac =  database.functions.add(database.model.relationships.EventFacility.from_dict({**request.json, **{"eventID": eventID}}))
        return jsonify(new_fac.to_dict())
    except (database.exceptions.InvalidDictError, database.exceptions.DatabaseError) as e:
        return abort(400, str(e))

@events_bp.route("/events/<int:eventID>/facilities/<int:facilityID>", methods=["GET"])
def handler_get_event_facility_from_id_from_id(eventID, facilityID):
    """Get the facility of the event with the given IDs.

    .. :quickref: Events; Get the facility of the event with the given IDs.
    
    :param int eventID: The ID of the event to retrieve
    :param int facilityID: The ID of the facility to retrieve
    :status 200: The facility was correctly retrieved
    :status 400: The event or facility could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded facility
    """
    try:
        event_fac = database.functions.get(database.model.relationships.EventFacility, (eventID, facilityID))[0]
        return jsonify(event_fac.to_dict())
    except database.exceptions.DatabaseError as e:
        return abort(400, str(e))

@events_bp.route("/events/<int:eventID>/facilities/<int:facilityID>", methods=["DELETE"])
def handler_delete_event_facility_from_id_from_id(eventID, facilityID):
    """Delete the facility of the event with the given IDs.
    
    .. :quickref: Events; Delete the facility of the event with the given IDs.
    
    :param int eventID: The ID of the event to delete
    :param int facilityID: The ID of the facility to delete
    :status 200: The facility was correctly deleted
    :status 400: The event or the facility could not be found
    :status 401: The user has not logged in
    :return: Empty response
    """
    try:
        event_fac = database.functions.get(database.model.relationships.EventFacility, (eventID, facilityID))[0]
        database.functions.rem(event_fac)
        return ""
    except database.exceptions.DatabaseError as e:
        return abort(400, str(e))

# Participants subcollection

@events_bp.route("/events/<int:eventID>/participants", methods=["GET"])
def handler_get_event_participants(eventID):
    """Get the participants of the event with the given ID.

    .. :quickref: Events; Get the participants of the event with the given ID.
    
    :param int eventID: The ID of the event to retrieve the collection from
    :status 200: The list was correctly retrieved
    :status 400: The event could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """
    try:
        event = database.functions.get(database.model.standard.Event, eventID)[0]
        users = [u.to_dict() for u in event.users]
        return jsonify(users)
    except database.exceptions.DatabaseError as e:
        return abort(400, str(e))

@events_bp.route("/events/<int:eventID>/participants", methods=["POST"])
def handler_add_event_participant(eventID):
    """Add a participant to the event with the given ID.

    .. :quickref: Events; Add a participant to the event with the given ID.
    
    :param int eventID: The ID of the event to add the participant
    :json int userID: The ID of the user to add to the participants
    :status 200: The participant was correctly inserted
    :status 400: The event could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded newly created participant
    """
    try:
        new_event =  database.functions.add(database.model.relationships.UserConnection.from_dict({**request.json, **{"eventID": eventID}}))
        return jsonify(new_event.to_dict())
    except (database.exceptions.InvalidDictError, database.exceptions.DatabaseError) as e:
        return abort(400, str(e))

@events_bp.route("/events/<int:eventID>/participants/<int:userID>", methods=["GET"])
def handler_get_event_participant_from_id_from_id(eventID, userID):
    """Get the participant of the event with the given IDs.

    .. :quickref: Events; Get the participant of the event with the given IDs.
    
    :param int eventID: The ID of the event to retrieve
    :param int userID: The ID of the participant to retrieve
    :status 200: The participant was correctly retrieved
    :status 400: The event or participant could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded participant
    """
    try:
        event_part = database.functions.get(database.model.relationships.EventParticipant, (eventID, userID))[0]
        return jsonify(event_part.to_dict())
    except database.exceptions.DatabaseError as e:
        return abort(400, str(e))

@events_bp.route("/events/<int:eventID>/participants/<int:userID>", methods=["DELETE"])
def handler_delete_event_participant_from_id_from_id(eventID, userID):
    """Delete the participant of the event with the given IDs.
    
    .. :quickref: Events; Delete the participant of the event with the given IDs.
    
    :param int eventID: The ID of the event to delete
    :param int userID: The ID of the participant to delete
    :status 200: The participant was correctly deleted
    :status 400: The event or the participant could not be found
    :status 401: The user has not logged in
    :return: Empty response
    """
    try:
        event_part = database.functions.get(database.model.relationships.EventParticipant, (eventID, userID))[0]
        database.functions.rem(event_part)
        return ""
    except database.exceptions.DatabaseError as e:
        return abort(400, str(e))