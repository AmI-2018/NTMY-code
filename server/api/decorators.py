"""Decorators for the HTTP APIs"""

from flask import abort, session
from functools import wraps
from typing import Callable

import database

def require_root(func: Callable) -> Callable:
    """Decorator that returns the view function only if the user is root.
    
    :param func: The function to decorate
    :type func: Callable
    :return: The view function, or a 401 error
    :rtype: Callable
    """

    @wraps(func)
    def wrapped(*args, **kwargs):
        if session["user"] != 0:
            return abort(401)
        return func(*args, **kwargs)
    return wrapped

def is_me(func: Callable) -> Callable:
    """Decorator that executes the given function if the userID matches with the logged user.
    
    :param func: The function to decorate
    :type func: Callable
    :return: The view function, or a 401 error
    :rtype: Callable
    """

    @wraps(func)
    def wrapped(*args, **kwargs):
        with database.session.DatabaseSession() as db_session:
            userID = kwargs["userID"]
            try:
                if session["user"] == 0 or session["user"] == userID:
                    return func(*args, **kwargs)
                elif db_session.get(database.model.standard.User, userID)[0]:
                    return abort(401)
            except (database.exceptions.DatabaseError, database.exceptions.InvalidDictError) as e:
                return abort(400, str(e))
    return wrapped

def is_known(func: Callable) -> Callable:
    """Decorator that executes the given function if the userID is known by the logged user.
    
    :param func: The function to decorate
    :type func: Callable
    :return: The view function, or a 401 error
    :rtype: Callable
    """

    @wraps(func)
    def wrapped(*args, **kwargs):
        userID = kwargs["userID"]
        with database.session.DatabaseSession() as db_session:
            try:
                user = db_session.get(database.model.standard.User, session["user"])[0]
                connections_ids = [c.userID2 for c in user.connections]
                if session["user"] == 0 or session["user"] == userID or userID in connections_ids:
                    return func(*args, **kwargs)
                elif db_session.get(database.model.standard.User, userID)[0]:
                    return abort(401)
            except (database.exceptions.DatabaseError, database.exceptions.InvalidDictError) as e:
                return abort(400, str(e))
    return wrapped

def is_mine(func: Callable) -> Callable:
    """Decorator that executes the given function if the event was created by the logged user.
    
    :param func: The function to decorate
    :type func: Callable
    :return: The view function, or a 401 error
    :rtype: Callable
    """
    
    @wraps(func)
    def wrapped(*args, **kwargs):
        eventID = kwargs["eventID"]
        with database.session.DatabaseSession() as db_session:
            try:
                event = db_session.get(database.model.standard.Event, eventID)[0]
                if session["user"] == 0 or event.creator.userID == session["user"]:
                    return func(*args, **kwargs)
                else:
                    return abort(401)
            except (database.exceptions.DatabaseError, database.exceptions.InvalidDictError) as e:
                return abort(400, str(e))
    return wrapped