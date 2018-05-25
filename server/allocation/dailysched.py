"""This module provides the definition of the class used to describe a daily schedule."""

from typing import List, Dict

from database.model.standard import Room, Event
from .exceptions import InvalidScheduleError

colors = [
    {"red": 1, "green": 0, "blue": 0}, # Red
    {"red": 0, "green": 1, "blue": 0}, # Green
    {"red": 0, "green": 0, "blue": 1}, # Blue
    {"red": 1, "green": 1, "blue": 0}, # Yellow
    {"red": 0, "green": 1, "blue": 1}, # Aqua
    {"red": 1, "green": 0, "blue": 1}, # Fuchsia
    {"red": 1, "green": 1, "blue": 1} # White
]
"""List of the available colors for the events"""

class DailySchedule:
    """Represents daily events' schedule for the residence.

    :param rooms: List of the residence rooms
    :type rooms: List[Room]
    :param events: List of the residence events
    :type events: List[Event]
    :param alloc: List of integers where alloc[i] = j means that the i-th event will be hosted in the j-th room
    :type alloc: List[int]
    :raises InvalidScheduleError: The alloc vector is invalid (events overlap or the assigned room is not big enough or does not have the required facilities)
    """

    def __init__(self, rooms: List[Room], events: List[Event], alloc: List[int]):
        self.sched = {}

        # Build new lists for every room
        for r in rooms:
            self.sched[r] = []

        # Populate lists
        for i in range(0, len(alloc)):
            if alloc[i] != 0:
                self.sched[rooms[alloc[i]-1]].append({
                    "event": events[i],
                    "color": colors[i]
                })
        
        # Check fits and overlaps
        for r in self.sched:
            for e1 in self.sched[r]:
                # Check fits
                if not e1["event"].fits(r):
                    raise InvalidScheduleError("{} does not fit {}".format(e1["event"], r))
                
                # Check overlaps
                for e2 in self.sched[r]:
                    if e1["event"] is not e2["event"] and e1["event"].overlaps(e2["event"]):
                        raise InvalidScheduleError("{} overlaps with {}".format(e1["event"], e2["event"]))
    
    def __repr__(self):
        return str(self.sched)
    
    def to_dict(self) -> Dict[int, List[int]]:
        """Returns a dictionary representation of the class.
        
        :return: A dictionary containing a list of eventIDs and colors for every roomID
        :rtype: Dict[int, List[int]]
        """

        sched_dict = {}
        for r in self.sched:
            sched_dict[r.roomID] = []
            for e in self.sched[r]:
                sched_dict[r.roomID].append({
                    "event": e["event"].to_dict(),
                    "color": e["color"]
                })
        return sched_dict