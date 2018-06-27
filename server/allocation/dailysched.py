"""This module provides the definition of the class used to describe a daily schedule."""

from typing import List, Dict
from random import choice, uniform

from database.model.standard import Room, Event
from .exceptions import InvalidScheduleError

# List of all the available colors for the events
colors = [
    {"red": 1, "green": 0, "blue": 0}, # Red
    {"red": 0, "green": 1, "blue": 0}, # Green
    {"red": 0, "green": 0, "blue": 1}, # Blue
    {"red": 1, "green": 1, "blue": 0}, # Yellow
    {"red": 0, "green": 1, "blue": 1}, # Aqua
    {"red": 1, "green": 0, "blue": 1}  # Fuchsia
]

ass_colors = {}

class DailySchedule:
    """Represents daily events' schedule for the residence."""

    def __init__(self, rooms: List[Room], events: List[Event], alloc: List[int]):
        """Initializes the DailySchedule.
        
        :param rooms: List of the residence rooms
        :type rooms: List[Room]
        :param events: List of the residence events
        :type events: List[Event]
        :param alloc: List of integers where alloc[i] = j means that the i-th event will be hosted in the j-th room
        :type alloc: List[int]
        :raises InvalidScheduleError: The alloc vector is invalid (events overlap or the assigned room is not big enough or does not have the required facilities)
        """

        # Empty lists
        self.sched = {}
        for r in rooms:
            self.sched[r] = []

        aval_colors = list(colors)

        # Check available colors or get random
        for event in events:
            if event in ass_colors and ass_colors[event] in aval_colors:
                aval_colors.remove(ass_colors[event])
            else:
                try:
                    ass_colors[event] = choice(aval_colors)
                    aval_colors.remove(ass_colors[event])
                except IndexError:
                    ass_colors[event] = {
                        "red": round(uniform(0, 1), 2),
                        "green": round(uniform(0, 1), 2),
                        "blue": round(uniform(0, 1), 2)
                    }

        # Populate lists
        for i in range(0, len(alloc)):
            if alloc[i] != 0:
                self.sched[rooms[alloc[i]-1]].append({
                    "event": events[i],
                    "color": ass_colors[events[i]]
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

        sched_list = []
        for r in self.sched:
            for e in self.sched[r]:
                sched_list.append({
                    "event": e["event"].to_dict(),
                    "room": r.to_dict(),
                    "color": e["color"]
                })
        return sched_list

# Remove imports so they won't be exposed
del List, Dict, Room, Event