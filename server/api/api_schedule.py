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

@schedule_bp.route("/schedule/<int:roomID>", methods=["GET"])
def handler_get_schedule_from_id(roomID):
    """Get the schedule for the room with the given ID.

    .. :quickref: Schedule; Get the schedule for the room with the given ID.
    
    :param int roomID: The ID of the room to retrieve the schedule
    :status 200: The schedule was correctly retrieved
    :status 400: The room could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded user
    """
    if roomID in allocation.sched.to_dict():
        return jsonify(allocation.sched.to_dict()[roomID])
    else:
        return abort(400, "The requested object could not be found.")