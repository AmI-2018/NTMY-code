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
    def wrapped(*args):
        if session["user"] != 0:
            return abort(401)
        return func(*args)
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
        userID = kwargs["userID"]
        if session["user"] == 0 or session["user"] == userID:
            return func(*args, **kwargs)
        else:
            return abort(401)
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
        connections = database.functions.filter(database.model.relationships.UserConnection, "userID1 = '{}'".format(session["user"]))
        connections_ids = [c.userID2 for c in connections]
        if session["user"] == 0 or session["user"] == userID or userID in connections_ids:
            return func(*args, **kwargs)
        else:
            return abort(401)
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
        event = database.functions.get(database.model.standard.Event, eventID)[0]
        if event.creator.userID == session["user"]:
            return func(*args, **kwargs)
        else:
            return abort(401)
    return wrapped