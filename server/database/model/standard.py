"""This module provides all the base classes of the data model."""

from typing import Dict, Any

from sqlalchemy import Column, Integer, String, LargeBinary, DateTime, ForeignKey
from sqlalchemy.orm import relationship
from passlib.hash import sha512_crypt

from .base import Base
from ..exceptions import InvalidDictError

##############################
# Category class declaration #
##############################

class Category(Base):
    """Represents a category an event can belong to."""

    __tablename__ = "categories"

    # Attributes
    categoryID = Column(Integer, primary_key=True, nullable=False)
    name = Column(String, nullable=False)
    description = Column(String, nullable=False)

    # Relationships
    events = []
    users = []

    def __repr__(self):
        return "Category #{}: {}".format(self.categoryID, self.name)
    
    def to_dict(self) -> Dict[str, Any]:
        """Returns a dictionary representation of the class.
        
        :return: A dictionary containing all the class attributes
        :rtype: Dict[str, Any]
        """

        return {
            "categoryID": int(self.categoryID),
            "name": str(self.name),
            "description": str(self.description)
        }
    
    @staticmethod
    def from_dict(category_dict: Dict[str, Any]) -> "Category":
        """Returns a class created from the provided dictionary.
        
        :param category_dict: The dictionary to create the class from
        :type category_dict: Dict[str, Any]
        :raises InvalidDictError: If the provided dictionary is not correct
        :return: The class created from the provided dictionary
        :rtype: Category
        """

        try:
            return Category(
                name=category_dict["name"],
                description=category_dict["description"])
        except KeyError as e:
            raise InvalidDictError("The provided dictionary is missing the key {}".format(str(e)))

##############################
# Facility class declaration #
##############################

class Facility(Base):
    """Represents a facility required by an event."""

    __tablename__ = "facilities"

    # Attributes
    facilityID = Column(Integer, primary_key=True, nullable=False)
    name = Column(String, nullable=False)
    description = Column(String, nullable=False)

    # Relationships
    events = []
    rooms = []
    
    def __repr__(self):
        return "Facility #{}: {}".format(self.facilityID, self.name)
    
    def to_dict(self) -> Dict[str, Any]:
        """Returns a dictionary representation of the class.
        
        :return: A dictionary containing all the class attributes
        :rtype: Dict[str, Any]
        """
        
        return {
            "facilityID": int(self.facilityID),
            "name": str(self.name),
            "description": str(self.description)
        }
    
    @staticmethod
    def from_dict(facility_dict: Dict[str, Any]) -> "Facility":
        """Returns a class created from the provided dictionary.
        
        :param category_dict: The dictionary to create the class from
        :type category_dict: Dict[str, Any]
        :raises InvalidDictError: If the provided dictionary is not correct
        :return: The class created from the provided dictionary
        :rtype: Facility
        """
        
        try:
            return Facility(
                name=facility_dict["name"],
                description=facility_dict["description"])
        except KeyError as e:
            raise InvalidDictError("The provided dictionary is missing the key {}".format(str(e)))

##########################
# User class declaration #
##########################

class User(Base):
    """Represents an user of the system."""

    __tablename__ = "users"

    # Attributes
    userID = Column(Integer, primary_key=True, nullable=False)
    email = Column(String, nullable=False, unique=True)
    name = Column(String, nullable=False)
    surname = Column(String, nullable=False)
    phone = Column(String, nullable=False)
    link = Column(String, nullable=False)
    password = Column(String, nullable=False)
    photo = Column(LargeBinary)

    # Relationships
    created_events = []
    events = []
    connections = []
    interests = []

    def __repr__(self):
        return "User #{}: {} {}".format(self.userID, self.name, self.surname)
    
    @staticmethod
    def make_password(password: str) -> str:
        """Returns the hashed password.
        
        :param password: The password to encrypt
        :type password: str
        :return: The encrypted password
        :rtype: str
        """

        return sha512_crypt.hash(password)

    def check_password(self, pwd_to_check: str) -> bool:
        """Checks if the provided password is correct.
        
        :param pwd_to_check: The password to check
        :type pwd_to_check: str
        :return: True if the password is correct, False otherwise
        :rtype: bool
        """

        return sha512_crypt.verify(pwd_to_check, self.password)

    def to_dict(self) -> Dict[str, Any]:
        """Returns a dictionary representation of the class.
        
        :return: A dictionary containing all the class attributes
        :rtype: Dict[str, Any]
        """
        
        return {
            "userID": int(self.userID),
            "email": str(self.email),
            "name": str(self.name),
            "surname": str(self.surname),
            "phone": str(self.phone),
            "link": str(self.link)
        }

    @staticmethod
    def from_dict(user_dict: Dict[str, Any]) -> "User":
        """Returns a class created from the provided dictionary.
        
        :param category_dict: The dictionary to create the class from
        :type category_dict: Dict[str, Any]
        :raises InvalidDictError: If the provided dictionary is not correct
        :return: The class created from the provided dictionary
        :rtype: User
        """
        
        try:
            return User(
                email=user_dict["email"],
                name=user_dict["name"],
                surname=user_dict["surname"],
                phone=user_dict["phone"],
                link=user_dict["link"],
                password=User.make_password(user_dict["password"])
            )
        except KeyError as e:
            raise InvalidDictError("The provided dictionary is missing the key {}".format(str(e)))

##########################
# Room class declaration #
##########################

class Room(Base):
    """Represents a room of the residence."""

    __tablename__ = "rooms"

    # Attributes
    roomID = Column(Integer, primary_key=True, nullable=False)
    name = Column(String, nullable=False)
    description = Column(String, nullable=False)
    size = Column(Integer, nullable=False)

    # Relationships
    facilities = []
    node = None

    def __repr__(self):
        return "Room #{}: {}".format(self.roomID, self.name)
    
    def to_dict(self) -> Dict[str, Any]:
        """Returns a dictionary representation of the class.
        
        :return: A dictionary containing all the class attributes
        :rtype: Dict[str, Any]
        """
        
        return {
            "roomID": int(self.roomID),
            "name": str(self.name),
            "description": str(self.description),
            "size": int(self.size),
            "node": self.node.to_dict() if self.node is not None else None
        }
    
    @staticmethod
    def from_dict(room_dict: Dict[str, Any]) -> "Room":
        """Returns a class created from the provided dictionary.
        
        :param category_dict: The dictionary to create the class from
        :type category_dict: Dict[str, Any]
        :raises InvalidDictError: If the provided dictionary is not correct
        :return: The class created from the provided dictionary
        :rtype: Room
        """
        
        try:
            return Room(
                name=room_dict["name"],
                description=room_dict["description"],
                size=room_dict["size"],
                node=room_dict["node"]
            )
        except KeyError as e:
            raise InvalidDictError("The provided dictionary is missing the key {}".format(str(e)))

###########################
# Event class declaration #
###########################

class Event(Base):
    """Represents an event to be hosted in the residence."""

    __tablename__ = "events"

    # Attributes
    eventID = Column(Integer, primary_key=True, nullable=False)
    name = Column(String, nullable=False)
    description = Column(String, nullable=False)
    start = Column(DateTime, nullable=False)
    end = Column(DateTime, nullable=False)
    creatorID = Column(Integer, ForeignKey("users.userID"), nullable=False)

    # Relationships
    creator = relationship("User", backref="created_events")
    categories = []
    facilities = []
    participants = []

    def __repr__(self):
        return "Event #{}: {}".format(self.eventID, self.name)
    
    def to_dict(self) -> Dict[str, Any]:
        """Returns a dictionary representation of the class.
        
        :return: A dictionary containing all the class attributes
        :rtype: Dict[str, Any]
        """
        
        from datetime import datetime
        return {
            "eventID": int(self.eventID),
            "name": str(self.name),
            "description": str(self.description),
            "start": datetime.strftime(self.start, "%m/%d/%Y %H:%M"),
            "end": datetime.strftime(self.end, "%m/%d/%Y %H:%M"),
            "creator": self.creator.to_dict()
        }
    
    @staticmethod
    def from_dict(event_dict: Dict[str, Any]) -> "Event":
        """Returns a class created from the provided dictionary.
        
        :param category_dict: The dictionary to create the class from
        :type category_dict: Dict[str, Any]
        :raises InvalidDictError: If the provided dictionary is not correct
        :return: The class created from the provided dictionary
        :rtype: Event
        """
        
        from datetime import datetime
        try:
            return Event(
                name=event_dict["name"],
                description=event_dict["description"],
                start=datetime.strptime(event_dict["start"], "%m/%d/%Y %H:%M"),
                end=datetime.strptime(event_dict["end"], "%m/%d/%Y %H:%M"),
                creatorID=event_dict["creatorID"]
            )
        except KeyError as e:
            raise InvalidDictError("The provided dictionary is missing the key {}".format(str(e)))
    
    def fits(self, room: Room) -> bool:
        """Checks if the event fits in the provided room.
        
        :param room: The room to be checked
        :type room: Room
        :return: True if the event fits, False otherwise
        :rtype: bool
        """

        # Check size
        if len(self.participants) > room.size:
            return False
        
        # Check facilities
        event_fac = map(lambda f: f.facility, self.facilities)
        for f in event_fac:
            room_fac = map(lambda f: f.facility, room.facilities)
            if f not in room_fac:
                return False

        # Everything ok!
        return True
    
    def overlaps(self, other: "Event") -> bool:
        """Checks if the event overlaps with the other provided.
        
        :param other: The event to be checked
        :type other: Event
        :return: True if it overlaps, False otherwise
        :rtype: bool
        """

        if (other.start < self.start and other.end < self.start) or (other.start > self.end):
            return False
        return True

# Remove imports so they won't be exposed
del Dict, Any, Column, Integer, String, LargeBinary, DateTime, Base