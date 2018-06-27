"""This module provides all the classes to describe the relationships between objects from the standard module."""

from typing import Dict, Any

from sqlalchemy import Column, Integer, String, ForeignKey
from sqlalchemy.orm import backref, relationship

from .base import Base
from ..exceptions import InvalidDictError

###################################
# EventCategory class declaration #
###################################

class EventCategory(Base):
    """Links events with their categories."""

    __tablename__ = "events_categories"

    eventID = Column(Integer, ForeignKey("events.eventID"), primary_key=True)
    categoryID = Column(Integer, ForeignKey("categories.categoryID"), primary_key=True)

    event = relationship("Event", backref=backref("categories", cascade="all,delete"))
    category = relationship("Category", backref=backref("events", cascade="all,delete"))

    def to_dict(self) -> Dict[str, Any]:
        """Returns a dictionary representation of the class.
        
        :return: A dictionary containing all the class attributes
        :rtype: Dict[str, Any]
        """

        return {
            "event": self.event.to_dict(),
            "category": self.category.to_dict()
        }
    
    @staticmethod
    def from_dict(input_dict: Dict[str, Any]) -> "EventCategory":
        """Returns a class created from the provided dictionary.
        
        :param input_dict: The dictionary to create the class from
        :type input_dict: Dict[str, Any]
        :raises InvalidDictError: If the provided dictionary is not correct
        :return: The class created from the provided dictionary
        :rtype: EventCategory
        """

        try:
            return EventCategory(
                eventID=input_dict["eventID"],
                categoryID=input_dict["categoryID"]
            )
        except KeyError as e:
            raise InvalidDictError("The provided dictionary is missing the key {}".format(str(e)))

###################################
# EventFacility class declaration #
###################################

class EventFacility(Base):
    """Links events with their facilities."""

    __tablename__ = "events_facilities"

    eventID = Column(Integer, ForeignKey("events.eventID"), primary_key=True)
    facilityID = Column(Integer, ForeignKey("facilities.facilityID"), primary_key=True)
    options = Column(String, nullable=True)

    event = relationship("Event", backref=backref("facilities", cascade="all,delete"))
    facility = relationship("Facility", backref=backref("events", cascade="all,delete"))

    def to_dict(self) -> Dict[str, Any]:
        """Returns a dictionary representation of the class.
        
        :return: A dictionary containing all the class attributes
        :rtype: Dict[str, Any]
        """

        return {
            "event": self.event.to_dict(),
            "facility": self.facility.to_dict(),
            "options": self.options
        }
    
    @staticmethod
    def from_dict(input_dict: Dict[str, Any]) -> "EventFacility":
        """Returns a class created from the provided dictionary.
        
        :param input_dict: The dictionary to create the class from
        :type input_dict: Dict[str, Any]
        :raises InvalidDictError: If the provided dictionary is not correct
        :return: The class created from the provided dictionary
        :rtype: EventFacility
        """

        try:
            return EventFacility(
                eventID=input_dict["eventID"],
                facilityID=input_dict["facilityID"],
                options=input_dict["options"]
            )
        except KeyError as e:
            raise InvalidDictError("The provided dictionary is missing the key {}".format(str(e)))

######################################
# EventParticipant class declaration #
######################################

class EventParticipant(Base):
    """Links events with their participants."""

    __tablename__ = "events_participants"

    eventID = Column(Integer, ForeignKey("events.eventID"), primary_key=True)
    userID = Column(Integer, ForeignKey("users.userID"), primary_key=True)

    event = relationship("Event", backref=backref("participants", cascade="all,delete"))
    user = relationship("User", backref=backref("events", cascade="all,delete"))

    def to_dict(self) -> Dict[str, Any]:
        """Returns a dictionary representation of the class.
        
        :return: A dictionary containing all the class attributes
        :rtype: Dict[str, Any]
        """

        return {
            "event": self.event.to_dict(),
            "user": self.user.to_dict()
        }
    
    @staticmethod
    def from_dict(input_dict: Dict[str, Any]) -> "EventParticipant":
        """Returns a class created from the provided dictionary.
        
        :param input_dict: The dictionary to create the class from
        :type input_dict: Dict[str, Any]
        :raises InvalidDictError: If the provided dictionary is not correct
        :return: The class created from the provided dictionary
        :rtype: EventParticipant
        """

        try:
            return EventParticipant(
                eventID=input_dict["eventID"],
                userID=input_dict["userID"]
            )
        except KeyError as e:
            raise InvalidDictError("The provided dictionary is missing the key {}".format(str(e)))

##################################
# RoomFacility class declaration #
##################################

class RoomFacility(Base):
    """Links rooms with their facilities."""

    __tablename__ = "rooms_facilities"

    roomID = Column(Integer, ForeignKey("rooms.roomID"), primary_key=True)
    facilityID = Column(Integer, ForeignKey("facilities.facilityID"), primary_key=True)

    room = relationship("Room", backref=backref("facilities", cascade="all,delete"))
    facility = relationship("Facility", backref=backref("rooms", cascade="all,delete"))

    def to_dict(self) -> Dict[str, Any]:
        """Returns a dictionary representation of the class.
        
        :return: A dictionary containing all the class attributes
        :rtype: Dict[str, Any]
        """

        return {
            "room": self.room.to_dict(),
            "facility": self.facility.to_dict()
        }
    
    @staticmethod
    def from_dict(input_dict: Dict[str, Any]) -> "RoomFacility":
        """Returns a class created from the provided dictionary.
        
        :param input_dict: The dictionary to create the class from
        :type input_dict: Dict[str, Any]
        :raises InvalidDictError: If the provided dictionary is not correct
        :return: The class created from the provided dictionary
        :rtype: RoomFacility
        """

        try:
            return RoomFacility(
                roomID=input_dict["roomID"],
                facilityID=input_dict["facilityID"]
            )
        except KeyError as e:
            raise InvalidDictError("The provided dictionary is missing the key {}".format(str(e)))

####################################
# UserConnection class declaration #
####################################

class UserConnection(Base):
    """Links users with the other users met during events."""

    __tablename__ = "users_connections"

    userID1 = Column(Integer, ForeignKey("users.userID"), primary_key=True)
    userID2 = Column(Integer, ForeignKey("users.userID"), primary_key=True)
    eventID = Column(Integer, ForeignKey("events.eventID"), primary_key=True)

    user1 = relationship("User", backref=backref("connections", cascade="all,delete"), foreign_keys=[userID1])
    user2 = relationship("User", foreign_keys=[userID2])
    event = relationship("Event", backref=backref("connections", cascade="all,delete"))

    def to_dict(self) -> Dict[str, Any]:
        """Returns a dictionary representation of the class.
        
        :return: A dictionary containing all the class attributes
        :rtype: Dict[str, Any]
        """

        return {
            "user1": self.user1.to_dict(),
            "user2": self.user2.to_dict(),
            "event": self.event.to_dict()
        }
    
    @staticmethod
    def from_dict(input_dict: Dict[str, Any]) -> "UserConnection":
        """Returns a class created from the provided dictionary.
        
        :param input_dict: The dictionary to create the class from
        :type input_dict: Dict[str, Any]
        :raises InvalidDictError: If the provided dictionary is not correct
        :return: The class created from the provided dictionary
        :rtype: UserConnection
        """

        try:
            return UserConnection(
                userID1=input_dict["userID1"],
                userID2=input_dict["userID2"],
                eventID=input_dict["eventID"]
            )
        except KeyError as e:
            raise InvalidDictError("The provided dictionary is missing the key {}".format(str(e)))

##################################
# UserInterest class declaration #
##################################

class UserInterest(Base):
    """Links users with their interests."""

    __tablename__ = "users_interests"

    userID = Column(Integer, ForeignKey("users.userID"), primary_key=True)
    categoryID = Column(Integer, ForeignKey("categories.categoryID"), primary_key=True)

    user = relationship("User", backref=backref("interests", cascade="all,delete"))
    category = relationship("Category", backref=backref("users", cascade="all,delete"))

    def to_dict(self) -> Dict[str, Any]:
        """Returns a dictionary representation of the class.
        
        :return: A dictionary containing all the class attributes
        :rtype: Dict[str, Any]
        """

        return {
            "user": self.user.to_dict(),
            "category": self.category.to_dict()
        }
    
    @staticmethod
    def from_dict(input_dict: Dict[str, Any]) -> "UserInterest":
        """Returns a class created from the provided dictionary.
        
        :param input_dict: The dictionary to create the class from
        :type input_dict: Dict[str, Any]
        :raises InvalidDictError: If the provided dictionary is not correct
        :return: The class created from the provided dictionary
        :rtype: UserInterest
        """

        try:
            return UserInterest(
                userID=input_dict["userID"],
                categoryID=input_dict["categoryID"]
            )
        except KeyError as e:
            raise InvalidDictError("The provided dictionary is missing the key {}".format(str(e)))

# Remove imports so they won't be exposed
del Dict, Any, Column, Integer, String, ForeignKey, backref, relationship, Base