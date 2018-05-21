"""This module provides all the functions that can be used to access the database."""

from typing import Type, List, Dict, Any

from sqlalchemy.sql.expression import text

from . import session
from .model.base import Base
from .exceptions import DatabaseError

def get(get_type: Type(Base), get_id: int=None) -> List[Base]:
    """Retrieves the elements of the given type from the database.
    
    :param get_type: The type of the elements to be found (subclass of Base)
    :type get_type: Type(Base)
    :param get_id: The ID of the element to retrieve, defaults to None
    :param get_id: int, optional
    :raises DatabaseError: If the requested element can not be found
    :return: The entire collection if get_id is not given, or a list composed of the element with the given ID
    :rtype: List[Base]
    """

    if get_id is None:
        return session.query(get_type).all()
    else:
        obj = session.query(get_type).get(get_id)
        if obj is None:
            raise DatabaseError("The requested object could not be found.")
        return [obj]

def filter(get_type: Type(Base), expr: str) -> List[Base]:
    """Retrieves the elements of the given type from the database with the given filter.
    
    :param get_type: The type of the elements to be found (subclass of Base)
    :type get_type: Type(Base)
    :param expr: The SQL expression to filter the results
    :type expr: str
    :return: The list of the elements that match the given expression
    :rtype: List[Base]
    """

    return session.query(get_type).filter(text(expr)).all()

def add(add_object: Base) -> Base:
    """Add the given element to the database.
    
    :param add_object: The object to add to the database
    :type add_object: Base
    :return: The newly created object
    :rtype: Base
    """

    try:
        session.add(add_object)
        session.commit()
    except Exception:
        session.rollback()
        raise DatabaseError("The provided object can not be inserted.")
    return add_object

def rem(del_object: Base):
    """Removes the given object from the database.
    
    :param del_object: The object to remove from the database
    :type del_object: Base
    """

    try:
        session.delete(del_object)
        session.commit()
    except Exception:
        session.rollback()
        raise DatabaseError("The provided object can not be removed.")

def upd(upd_object: Base, upd_dict: Dict[str, Any]) -> Base:
    """Updates the given object on the database.
    
    :param upd_object: The object to update on the database
    :type upd_object: Base
    :return: The updated object
    :rtype: Base
    """

    for (key, value) in upd_dict.items():
        if hasattr(upd_object, key):
            setattr(upd_object, key, value)
    session.commit()
    return upd_object