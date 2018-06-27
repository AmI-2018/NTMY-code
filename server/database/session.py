"""This module provides the DatabaseSession class to interact with the database."""

from typing import Type, List, Dict, Any

from sqlalchemy.sql.expression import text

from . import SessionFactory
from .model.base import Base
from .exceptions import DatabaseError

class DatabaseSession():
    """Represents a database session.
    
    :raises DatabaseError: If the requested action is not valid.
    """

    def __init__(self):
        self.session = SessionFactory()
    
    def __del__(self):
        self.session.close()

    def __enter__(self):
        # self.__init__()
        return self
    
    def __exit__(self, exc_type, exc_val, exc_tb):
        self.__del__()

    def get(self, get_type: Type(Base), get_id: int=None) -> List[Base]:
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
            ret_obj = self.session.query(get_type).all()
        else:
            ret_obj = [self.session.query(get_type).get(get_id)]
            if ret_obj[0] is None:
                raise DatabaseError("The requested object could not be found.")
        return ret_obj

    def filter(self, get_type: Type(Base), expr: str) -> List[Base]:
        """Retrieves the elements of the given type from the database with the given filter.
        
        :param get_type: The type of the elements to be found (subclass of Base)
        :type get_type: Type(Base)
        :param expr: The SQL expression to filter the results
        :type expr: str
        :return: The list of the elements that match the given expression
        :rtype: List[Base]
        """

        return self.session.query(get_type).filter(text(expr)).all()

    def add(self, add_object: Base) -> Base:
        """Add the given element to the database.
        
        :param add_object: The object to add to the database
        :type add_object: Base
        :return: The newly created object
        :rtype: Base
        """

        try:
            self.session.add(add_object)
            self.session.commit()
        except Exception:
            self.session.rollback()
            raise DatabaseError("The provided object can not be inserted.")
        return add_object

    def rem(self, del_object: Base):
        """Removes the given object from the database.
        
        :param del_object: The object to remove from the database
        :type del_object: Base
        """

        try:
            self.session.delete(del_object)
            self.session.commit()
        except Exception:
            self.session.rollback()
            raise DatabaseError("The provided object can not be removed.")

    def upd(self, upd_object: Base, upd_dict: Dict[str, Any]) -> Base:
        """Updates the given object on the database.
        
        :param upd_object: The object to update on the database
        :type upd_object: Base
        :return: The updated object
        :rtype: Base
        """

        for (key, value) in upd_dict.items():
            if hasattr(upd_object, key):
                setattr(upd_object, key, value)
        self.session.commit()
        return upd_object

# Remove imports so they won't be exposed
del Type, List, Dict, Any, Base