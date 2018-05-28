"""This module provides all the classes to access the media features for the room clients."""

from typing import Dict, Any

from sqlalchemy import Column, Integer, String

from .base import Base

#############################
# Channel class declaration #
#############################

class Channel(Base):
    """Represents a TV channel."""

    __tablename__ = "channels"

    # Attributes
    channelID = Column(Integer, primary_key=True, nullable=False)
    name = Column(String, nullable=False)
    link = Column(String, nullable=False)

    def __repr__(self):
        return "Channel #{} - {}".format(self.channelID, self.name)
    
    def to_dict(self) -> Dict[str, Any]:
        """Returns a dictionary representation of the class.
        
        :return: A dictionary containing all the class attributes
        :rtype: Dict[str, Any]
        """
        
        return {
            "channelID": self.channelID,
            "name": self.name,
            "link": self.link
        }

##############################
# Playlist class declaration #
##############################

class Playlist(Base):
    """Represents a music playlist."""

    __tablename__ = "playlists"

    # Attributes
    playlistID = Column(Integer, primary_key=True, nullable=False)
    name = Column(String, nullable=False)
    link = Column(String, nullable=False)

    def __repr__(self):
        return "Playlist #{} - {}".format(self.playlistID, self.name)
    
    def to_dict(self) -> Dict[str, Any]:
        """Returns a dictionary representation of the class.
        
        :return: A dictionary containing all the class attributes
        :rtype: Dict[str, Any]
        """
        
        return {
            "playlistID": self.playlistID,
            "name": self.name,
            "link": self.link
        }