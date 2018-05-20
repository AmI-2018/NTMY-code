"""Login API"""

from flask import jsonify, request, session, Blueprint
import database

login_bp = Blueprint("login_bp", __name__)

@login_bp.route("/login", methods=["POST"])
def login_user():
    """Logs the user in.

    .. :quickref: Login; Logs the user in.
    
    :json int userID: The userID of the user
    :json string password: The password of the user
    :status 200: The user was correctly logged in
    :status 401: The user could not log in
    :return: A confirmation or error message
    """

    try:
        # Check the info is correct and start session
        userID = request.json["userID"]
        password = request.json["password"]

        user = database.functions.get(database.model.standard.User, userID)[0]
        if user.check_password(password):
            session["user"] = userID
            return jsonify({"msg": "Login OK"})
        return jsonify({"msg": "Login error"}), 401
    except Exception:
        return jsonify({"msg": "Login error"}), 401

@login_bp.route("/logout", methods=["GET"])
def logout_user():
    """Logs the user out.

    .. :quickref: Login; Logs the user out.
    
    :status 200: The user was correctly logged out
    :return: A confirmation message
    """

    del session["user"]
    return jsonify({"msg": "Logout OK"})