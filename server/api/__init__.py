"""This module provides all the handlers for the API routing paths."""

from flask import Flask, session, jsonify

# Server initialization

app = Flask(__name__, static_folder=None)
"""The Flask application"""
app.secret_key = "ntmysupersecretkey"

# API routing imports

from . import api_categories, api_events, api_facilities, api_login, api_rooms, api_schedule, api_users

# Check that the user has logged in

@api_categories.categories_bp.before_request
@api_events.events_bp.before_request
@api_facilities.facilities_bp.before_request
@api_rooms.rooms_bp.before_request
@api_schedule.schedule_bp.before_request
@api_users.users_bp.before_request
def require_login():
    try:
        if session["user"]:
            return None
    except KeyError:
        return jsonify({"msg": "Unauthorized"}), 401

app.register_blueprint(api_categories.categories_bp)
app.register_blueprint(api_events.events_bp)
app.register_blueprint(api_login.login_bp)
app.register_blueprint(api_facilities.facilities_bp)
app.register_blueprint(api_rooms.rooms_bp)
app.register_blueprint(api_schedule.schedule_bp)
app.register_blueprint(api_users.users_bp)