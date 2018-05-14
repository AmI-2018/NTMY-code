"""This module provides all the functions that can be used to access the database."""

from . import session
from .model.base import Base
from .exceptions import DatabaseError

from typing import Type, List, Dict, Any

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

def add(add_object: Base) -> Base:
    """Add the given element to the database.
    
    :param add_object: The object to add to the database
    :type add_object: Base
    :return: The newly created object
    :rtype: Base
    """

    session.add(add_object)
    session.commit()
    return add_object

def rem(del_object: Base):
    """Removes the given object from the database.
    
    :param del_object: The object to remove from the database
    :type del_object: Base
    """

    session.delete(del_object)
    session.commit()

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
    return upd_object

def get_as_dict(get_type: Type(Base), get_id: int=None) -> List[Dict[str, Any]]:
    """Retrieves the elements of the given type from the database and returns them as dictionaries to be jsonified.
    
    :param get_type: The type of the elements to be found (subclass of Base)
    :type get_type: Type(Base)
    :param get_id: The ID of the element to retrieve, defaults to None
    :param get_id: int, optional
    :raises DatabaseError: If the requested element can not be found
    :return: The entire collection if get_id is not given, or a list composed of the element with the given ID
    :rtype: List[Dict[str, Any]]
    """
    return [obj.to_dict() for obj in get(get_type, get_id)]
