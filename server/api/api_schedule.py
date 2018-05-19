"""Schedule API"""

from flask import jsonify, request, session, Blueprint
import database

schedule_bp = Blueprint("schedule_bp", __name__)

@schedule_bp.route("/schedule", methods=["GET"])
def handler_get_schedule():
    pass