"""Schedule API"""

from flask import jsonify, request, session, Blueprint, abort

import allocation
import database

schedule_bp = Blueprint("schedule_bp", __name__)

@schedule_bp.route("/schedule", methods=["GET"])
def handler_get_schedule():
    """Get the current events' schedule.

    .. :quickref: Schedule; Get the current events' schedule.

    :status 200: The schedule was correctly retrieved
    :status 401: The user has not logged in
    :return: The JSON-encoded schedule
    """
    
    return jsonify(allocation.sched.to_dict())

@schedule_bp.route("/schedule/room/<int:roomID>", methods=["GET"])
def handler_get_schedule_from_room_id(roomID):
    """Get the schedule for the room with the given ID.

    .. :quickref: Schedule; Get the schedule for the room with the given ID.
    
    :param int roomID: The ID of the room to retrieve the schedule
    :status 200: The schedule was correctly retrieved
    :status 400: The room could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded user
    """

    schedule = allocation.sched.to_dict()
    room_schedule = list(filter(lambda e: e["room"]["roomID"] == roomID, schedule))
    return jsonify(room_schedule) if len(room_schedule) > 0 else abort(400, "The requested object could not be found.")

@schedule_bp.route("/schedule/event/<int:eventID>", methods=["GET"])
def handler_get_schedule_from_event_id(eventID):
    """Get the schedule for the event with the given ID.

    .. :quickref: Schedule; Get the schedule for the event with the given ID.
    
    :param int eventID: The ID of the event to retrieve the schedule
    :status 200: The schedule was correctly retrieved
    :status 400: The event could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded user
    """
    
    schedule = allocation.sched.to_dict()
    event_schedule = list(filter(lambda e: e["event"]["eventID"] == eventID, schedule))
    return jsonify(event_schedule) if len(event_schedule) > 0 else abort(400, "The requested object could not be found.")