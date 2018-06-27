"""Login API"""

from flask import jsonify, request, session, Blueprint, abort
from typing import Callable

import database

login_bp = Blueprint("login_bp", __name__)

@login_bp.route("/login", methods=["GET"])
def get_login_status():
    """Checks if the user has logged in.

    .. :quickref: Login; Checks if the user has logged in.
    
    :status 200: The user has logged in
    :status 401: The user has not logged in
    :return: The user's info or an error message
    """

    with database.session.DatabaseSession() as db_session:
        try:
            user = db_session.get(database.model.standard.User, session["user"])[0]
            return jsonify(user.to_dict())
        except KeyError:
            return abort(401)

@login_bp.route("/login", methods=["POST"])
def login_user():
    """Logs the user in.

    .. :quickref: Login; Logs the user in.
    
    :json int userID: The userID of the user
    :json string password: The password of the user
    :status 200: The user was correctly logged in
    :status 401: The user could not log in
    :return: The user's info or an error message
    """

    with database.session.DatabaseSession() as db_session:
        try:
            # Check the info is correct and start session
            email = request.json["email"]
            password = request.json["password"]

            user = db_session.filter(database.model.standard.User, "email='{}'".format(email))[0]
            if user.check_password(password):
                session["user"] = user.userID
                user = db_session.get(database.model.standard.User, session["user"])[0]
                return jsonify(user.to_dict())
            return abort(401)
        except Exception:
            return abort(401)

@login_bp.route("/login", methods=["DELETE"])
@login_bp.route("/logout", methods=["GET"])
def logout_user():
    """Logs the user out.

    .. :quickref: Login; Logs the user out.
    
    :status 200: The user was correctly logged out
    :return: A confirmation message
    """

    if "user" in session:
        del session["user"]
    return ""