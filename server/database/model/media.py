"""This module provides all the classes to access the media features for the room clients."""

from typing import Dict, Any

from sqlalchemy import Column, Integer, String

from .base import Base
from ..exceptions import InvalidDictError

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
    
    @staticmethod
    def from_dict(channel_dict: Dict[str, Any]) -> "Channel":
        """Returns a class created from the provided dictionary.
        
        :param category_dict: The dictionary to create the class from
        :type category_dict: Dict[str, Any]
        :raises InvalidDictError: If the provided dictionary is not correct
        :return: The class created from the provided dictionary
        :rtype: Channel
        """
        
        try:
            return Channel(
                name=channel_dict["name"],
                link=channel_dict["link"]
            )
        except KeyError as e:
            raise InvalidDictError("The provided dictionary is missing the key {}".format(str(e)))

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
    
    @staticmethod
    def from_dict(playlist_dict: Dict[str, Any]) -> "Playlist":
        """Returns a class created from the provided dictionary.
        
        :param category_dict: The dictionary to create the class from
        :type category_dict: Dict[str, Any]
        :raises InvalidDictError: If the provided dictionary is not correct
        :return: The class created from the provided dictionary
        :rtype: Playlist
        """
        
        try:
            return Playlist(
                name=playlist_dict["name"],
                link=playlist_dict["link"]
            )
        except KeyError as e:
            raise InvalidDictError("The provided dictionary is missing the key {}".format(str(e)))

# Remove imports so they won't be exposed
del Dict, Any, Column, Integer, String, Base