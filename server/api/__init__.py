"""This module provides all the handlers for the API routing paths."""

from flask import Flask, request, session, Response, jsonify
import database

# Server initialization

app = Flask(__name__)
"""The Flask application"""
app.secret_key = "ntmysupersecretkey"

# API routing imports
from . import api_categories, api_events, api_facilities, api_rooms, api_schedule, api_users

# ------------------------------
# |     Server to user API     |
# ------------------------------

# @app.route("/users/login", methods=["POST"])
# def handler_start_user_session():
#     try:
#         # Check the info is correct and start session
#         data = request.json

#         if check_login(data["userID"], data["password"]):
#             session["userID"] = int(data["userID"])
#             return jsonify({"message": "Login OK"})
#         else:
#             return jsonify({"message": "Wrong username or password"}), 401
#     except KeyError:
#         return jsonify({"message": "No username or password provided"}), 401

# @app.route("/users/logout", methods=["GET"])
# def handler_end_user_session():
#     # Delete the session data
#     try:
#         del session["userID"]
#     except KeyError:
#         pass
#     return jsonify({"message": "Logout OK"})

# @app.route("/users/<int:userID>", methods=["GET"])
# def handler_get_user_info(userID):
#     # Check if the user has logged in
#     try:
#         sessionID = session["userID"]
#     except KeyError:
#         return jsonify({"message": "Unauthorized"}), 401
    
#     # Check the user is authorized to see info
#     connections = get_connections(users, sessionID)
#     if userID == sessionID or userID in connections:
#         return jsonify(users.get(userID))
#     else:
#         return jsonify({"message": "Forbidden"}), 403

# @app.route("/users/connections", methods=["GET"])
# def handler_get_user_connections():
#     # Check if the user has logged in
#     try:
#         sessionID = session["userID"]
#     except KeyError:
#         return jsonify({"message": "Unauthorized"}), 401
    
#     connections = get_connections(users, sessionID)
#     return jsonify(connections)

# @app.route("/shake/<int:userID1>/<int:userID2>")
# def handler_register_new_shake(userID1, userID2):
#     if make_connection(userID1, userID2, "now"):
#         return jsonify({
#             "status": "OK",
#             "userID1": userID1,
#             "userID2": userID2
#         })
#     else:
#         return jsonify({
#             "status": "Error",
#             "userID1": userID1,
#             "userID2": userID2
#         })

# # --------------------------------
# # |     Server to client API     |
# # --------------------------------

# @app.route("/rooms/login")
# def handler_start_room_session():
#     try:
#         # Get login info from Authorization header
#         username = request.authorization["username"]
#         password = request.authorization["password"]

#         # Check the info is correct and start session
#         if check_login(username, password):
#             session["roomID"] = int(username)
#             return "OK"
#         else:
#             return "Wrong username or password"
#     except KeyError:
#         return "Error"

# @app.route("/rooms/logout")
# def handler_end_room_session():
#     # Delete the session data
#     try:
#         del session["roomID"]
#     except KeyError:
#         pass
#     return "OK"

# @app.route("/rooms/next")
# def handler_get_next_event():
#     # Check if the user has logged in
#     try:
#         sessionID = session["roomID"]
#     except KeyError:
#         return Response("Unauthorized", status=401)
    
#     return jsonify(next_event(sessionID))

if __name__ == "__main__":
    app.run()