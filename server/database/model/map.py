"""This module provides all the classes to map the residence on a graph."""

from typing import Dict, Any

from sqlalchemy import Column, Integer, Float, String, ForeignKey
from sqlalchemy.orm import relationship, backref

from .base import Base
from ..exceptions import InvalidDictError

##########################
# Node class declaration #
##########################

class Node(Base):
    """Represents a node of the graph."""

    __tablename__ = "map_nodes"

    # Attributes
    nodeID = Column(Integer, primary_key=True, nullable=False)
    name = Column(String, nullable=False)
    X = Column(Float, nullable=False)
    Y = Column(Float, nullable=False)
    orientation = Column(Float, nullable=False)
    roomID = Column(Integer, ForeignKey("rooms.roomID"), nullable=True)

    # Relationships
    edges = []
    room = relationship("Room", backref=backref("node", uselist=False))

    def __repr__(self):
        return "Node #{}: {}".format(self.nodeID, self.name)

    def to_dict(self) -> Dict[str, Any]:
        """Returns a dictionary representation of the class.
        
        :return: A dictionary containing all the class attributes
        :rtype: Dict[str, Any]
        """
        
        return {
            "nodeID": int(self.nodeID),
            "name": str(self.name),
            "X": float(self.X),
            "Y": float(self.Y),
            "orientation": float(self.orientation)
        }
    
    @staticmethod
    def from_dict(node_dict: Dict[str, Any]) -> "Node":
        """Returns a class created from the provided dictionary.
        
        :param category_dict: The dictionary to create the class from
        :type category_dict: Dict[str, Any]
        :raises InvalidDictError: If the provided dictionary is not correct
        :return: The class created from the provided dictionary
        :rtype: Node
        """
        
        try:
            return Node(
                name=node_dict["name"],
                X=node_dict["X"],
                Y=node_dict["Y"],
                orientation=node_dict["orientation"],
                roomID=node_dict["roomID"]
            )
        except KeyError as e:
            raise InvalidDictError("The provided dictionary is missing the key {}".format(str(e)))

##########################
# Edge class declaration #
##########################

class Edge(Base):
    """Represents an edge of the graph."""

    __tablename__ = "map_edges"

    # Attributes
    nodeID1 = Column(Integer, ForeignKey("map_nodes.nodeID"), primary_key=True, nullable=False)
    nodeID2 = Column(Integer, ForeignKey("map_nodes.nodeID"), primary_key=True, nullable=False)
    distance = Column(Float, nullable=False)
    color = Column(String, nullable=False)

    # Relationships
    node1 = relationship("Node", backref="edges", foreign_keys=[nodeID1])
    node2 = relationship("Node", foreign_keys=[nodeID2])

    def __repr__(self):
        return "Edge {} - {} (distance {})".format(self.nodeID1, self.nodeID2, self.distance)

    def to_dict(self) -> Dict[str, Any]:
        """Returns a dictionary representation of the class.
        
        :return: A dictionary containing all the class attributes
        :rtype: Dict[str, Any]
        """
        
        return {
            "node1": self.node1.to_dict(),
            "node2": self.node2.to_dict(),
            "distance": float(self.distance),
            "color": str(self.color)
        }
    
    @staticmethod
    def from_dict(edge_dict: Dict[str, Any]) -> "Edge":
        """Returns a class created from the provided dictionary.
        
        :param category_dict: The dictionary to create the class from
        :type category_dict: Dict[str, Any]
        :raises InvalidDictError: If the provided dictionary is not correct
        :return: The class created from the provided dictionary
        :rtype: Edge
        """
        
        try:
            return Edge(
                nodeID1=edge_dict["nodeID1"],
                nodeID2=edge_dict["nodeID2"],
                distance=edge_dict["distance"],
                color=edge_dict["color"]
            )
        except KeyError as e:
            raise InvalidDictError("The provided dictionary is missing the key {}".format(str(e)))

# Remove imports so they won't be exposed
del Dict, Any, Column, Integer, Float, String, ForeignKey, relationship, backref, Base