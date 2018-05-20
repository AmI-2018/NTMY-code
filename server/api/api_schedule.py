"""Schedule API"""

from flask import jsonify, request, session, Blueprint
import allocation
import database

schedule_bp = Blueprint("schedule_bp", __name__)

@schedule_bp.route("/schedule", methods=["GET"])
def handler_get_schedule():
    """Get the current events' schedule.

    .. :quickref: Rooms; Get the list of the rooms.

    :status 200: The schedule was correctly retrieved
    :status 401: The user has not logged in
    :return: The JSON-encoded schedule
    """
    return jsonify(allocation.sched.to_dict())