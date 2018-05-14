"""This module provides all the classes to describe the relationships between objects from the standard module."""

from sqlalchemy import Column, Integer, ForeignKey
from sqlalchemy.orm import relationship
from .base import Base

###################################
# EventCategory class declaration #
###################################

class EventCategory(Base):
    """Links events with their categories."""

    __tablename__ = "events_categories"

    eventID = Column(Integer, ForeignKey("events.eventID"), primary_key=True)
    categoryID = Column(Integer, ForeignKey("categories.categoryID"), primary_key=True)

    event = relationship("Event", backref="categories")
    category = relationship("Category", backref="events")

###################################
# EventFacility class declaration #
###################################

class EventFacility(Base):
    """Links events with their facilities."""

    __tablename__ = "events_facilities"

    eventID = Column(Integer, ForeignKey("events.eventID"), primary_key=True)
    facilityID = Column(Integer, ForeignKey("facilities.facilityID"), primary_key=True)

    event = relationship("Event", backref="facilities")
    facility = relationship("Facility", backref="events")

######################################
# EventParticipant class declaration #
######################################

class EventParticipant(Base):
    """Links events with their participants."""

    __tablename__ = "events_participants"

    eventID = Column(Integer, ForeignKey("events.eventID"), primary_key=True)
    userID = Column(Integer, ForeignKey("users.userID"), primary_key=True)

    event = relationship("Event", backref="participants")
    user = relationship("User", backref="events")

##################################
# RoomFacility class declaration #
##################################

class RoomFacility(Base):
    """Links rooms with their facilities."""

    __tablename__ = "rooms_facilities"

    roomID = Column(Integer, ForeignKey("rooms.roomID"), primary_key=True)
    facilityID = Column(Integer, ForeignKey("facilities.facilityID"), primary_key=True)

    room = relationship("Room", backref="facilities")
    facility = relationship("Facility", backref="rooms")

####################################
# UserConnection class declaration #
####################################

class UserConnection(Base):
    """Links users with the other users met during events."""

    __tablename__ = "users_connections"

    userID1 = Column(Integer, ForeignKey("users.userID"), primary_key=True)
    userID2 = Column(Integer, ForeignKey("users.userID"), primary_key=True)
    eventID = Column(Integer, ForeignKey("events.eventID"))

    user1 = relationship("User", backref="connections", foreign_keys=[userID1])
    user2 = relationship("User", foreign_keys=[userID2])
    event = relationship("Event", backref="connections")

##################################
# UserInterest class declaration #
##################################

class UserInterest(Base):
    """Links users with their interests."""

    __tablename__ = "users_interests"

    userID = Column(Integer, ForeignKey("users.userID"), primary_key=True)
    categoryID = Column(Integer, ForeignKey("categories.categoryID"), primary_key=True)

    user = relationship("User", backref="interests")
    category = relationship("Category", backref="users")

# Remove imports so they won't be exposed
del Column, Integer, ForeignKey, relationship, Base