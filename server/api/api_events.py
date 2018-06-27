"""Events API"""

from flask import jsonify, request, Blueprint, abort, session
from datetime import datetime

from .decorators import require_root, is_me, is_mine
import allocation
import database

events_bp = Blueprint("events_bp", __name__)

# Run the allocator after every modification

@events_bp.after_request
def update_schedule_after_request(resp):
    if request.method != "GET":
        allocation.sem_alloc.release()
    return resp

# Basic usage

@events_bp.route("/events", methods=["GET"])
def handler_get_events():
    """Get the list of the events.

    .. :quickref: Events; Get the list of the events.
    
    :status 200: The list was correctly retrieved
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """

    with database.session.DatabaseSession() as db_session:
        return jsonify([e.to_dict() for e in db_session.get(database.model.standard.Event)])

@events_bp.route("/events/next", methods=["GET"])
def handler_get_next_events():
    """Get the list of the next events sorted by date.

    .. :quickref: Events; Get the list of the next events sorted by date.
    
    :status 200: The list was correctly retrieved
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """

    with database.session.DatabaseSession() as db_session:
        return jsonify([e.to_dict() for e in sorted(db_session.get(database.model.standard.Event), key=(lambda e: e.start)) if e.end > datetime.now()])

@events_bp.route("/events/today", methods=["GET"])
def handler_get_today_events():
    """Get the list of the today events sorted by time.

    .. :quickref: Events; Get the list of the today events sorted by time.
    
    :status 200: The list was correctly retrieved
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """

    with database.session.DatabaseSession() as db_session:
        return jsonify([e.to_dict() for e in sorted(db_session.get(database.model.standard.Event), key=(lambda e: e.start)) if e.start.date() == datetime.today().date()])

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

    with database.session.DatabaseSession() as db_session:
        try:
            new_event = db_session.add(database.model.standard.Event.from_dict({**request.json, **{"creatorID": session["user"]}}))
            db_session.add(database.model.relationships.EventParticipant(eventID=new_event.eventID, userID=session["user"]))
            return jsonify(new_event.to_dict())
        except (database.exceptions.InvalidDictError, database.exceptions.DatabaseError, ValueError) as e:
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

    with database.session.DatabaseSession() as db_session:
        try:
            return jsonify(db_session.get(database.model.standard.Event, eventID)[0].to_dict())
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

@events_bp.route("/events/<int:eventID>", methods=["PUT"])
@is_mine
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

    with database.session.DatabaseSession() as db_session:
        try:
            upd_event = db_session.get(database.model.standard.Event, eventID)[0]
            if "start" in request.json:
                request.json["start"] = datetime.strptime(request.json["start"], "%m/%d/%Y %H:%M")
            if "end" in request.json:
                request.json["end"] = datetime.strptime(request.json["end"], "%m/%d/%Y %H:%M")
            return jsonify(db_session.upd(upd_event, request.json).to_dict())
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

@events_bp.route("/events/<int:eventID>", methods=["DELETE"])
@is_mine
def handler_delete_event_from_id(eventID):
    """Delete the event with the given ID.

    .. :quickref: Events; Delete the event with the given ID.
    
    :param int eventID: The ID of the event to delete
    :status 200: The event was correctly deleted
    :status 400: The event could not be found
    :status 401: The user has not logged in
    :return: Empty response
    """

    with database.session.DatabaseSession() as db_session:
        try:
            del_event = db_session.get(database.model.standard.Event, eventID)[0]
            db_session.rem(del_event)
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

    with database.session.DatabaseSession() as db_session:
        try:
            event = db_session.get(database.model.standard.Event, eventID)[0]
            categories = [c.to_dict() for c in event.categories]
            return jsonify(categories)
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

@events_bp.route("/events/<int:eventID>/categories", methods=["POST"])
@is_mine
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

    with database.session.DatabaseSession() as db_session:
        try:
            new_cat = db_session.add(database.model.relationships.EventCategory.from_dict({**request.json, **{"eventID": eventID}}))
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

    with database.session.DatabaseSession() as db_session:
        try:
            event_cat = db_session.get(database.model.relationships.EventCategory, (eventID, categoryID))[0]
            return jsonify(event_cat.to_dict())
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

@events_bp.route("/events/<int:eventID>/categories/<int:categoryID>", methods=["DELETE"])
@is_mine
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

    with database.session.DatabaseSession() as db_session:
        try:
            event_cat = db_session.get(database.model.relationships.EventCategory, (eventID, categoryID))[0]
            db_session.rem(event_cat)
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

    with database.session.DatabaseSession() as db_session:
        try:
            event = db_session.get(database.model.standard.Event, eventID)[0]
            facilities = [f.to_dict() for f in event.facilities]
            return jsonify(facilities)
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

@events_bp.route("/events/<int:eventID>/facilities", methods=["POST"])
@is_mine
def handler_add_event_facility(eventID):
    """Add a facility to the event with the given ID.

    .. :quickref: Events; Add a facility to the event with the given ID.
    
    :param int eventID: The ID of the event to add the facility
    :json int facilityID: The ID of the facility to add to the facilities
    :json string options: The options for the facility
    :status 200: The facility was correctly inserted
    :status 400: The event could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded newly created facility
    """

    with database.session.DatabaseSession() as db_session:
        try:
            new_fac = db_session.add(database.model.relationships.EventFacility.from_dict({**request.json, **{"eventID": eventID}}))
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

    with database.session.DatabaseSession() as db_session:
        try:
            event_fac = db_session.get(database.model.relationships.EventFacility, (eventID, facilityID))[0]
            return jsonify(event_fac.to_dict())
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

@events_bp.route("/events/<int:eventID>/facilities/<int:facilityID>", methods=["DELETE"])
@is_mine
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

    with database.session.DatabaseSession() as db_session:
        try:
            event_fac = db_session.get(database.model.relationships.EventFacility, (eventID, facilityID))[0]
            db_session.rem(event_fac)
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

    with database.session.DatabaseSession() as db_session:
        try:
            event = db_session.get(database.model.standard.Event, eventID)[0]
            users = [u.to_dict() for u in event.participants]
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

    with database.session.DatabaseSession() as db_session:
        try:
            if session["user"] != request.json["userID"] and session["user"] != 0:
                abort(401)
            event_part = db_session.add(database.model.relationships.EventParticipant.from_dict({**request.json, **{"eventID": eventID}}))
            return jsonify(event_part.to_dict())
        except (database.exceptions.InvalidDictError, database.exceptions.DatabaseError, KeyError) as e:
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

    with database.session.DatabaseSession() as db_session:
        try:
            event_part = db_session.get(database.model.relationships.EventParticipant, (eventID, userID))[0]
            return jsonify(event_part.to_dict())
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

@events_bp.route("/events/<int:eventID>/participants/<int:userID>", methods=["DELETE"])
@is_me
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

    with database.session.DatabaseSession() as db_session:
        try:
            event_part = db_session.get(database.model.relationships.EventParticipant, (eventID, userID))[0]
            db_session.rem(event_part)
            return ""
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))