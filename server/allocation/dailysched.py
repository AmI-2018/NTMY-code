"""This module provides the definition of the class used to describe a daily schedule."""

from database.model.standard import Room, Event
from .exceptions import InvalidScheduleError
from typing import List, Dict

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
                self.sched[rooms[alloc[i]-1]].append(events[i])
        
        # Check fits and overlaps
        for r in self.sched:
            for e1 in self.sched[r]:
                # Check fits
                if not e1.fits(r):
                    raise InvalidScheduleError
                
                # Check overlaps
                for e2 in self.sched[r]:
                    if e1 is not e2 and e1.overlaps(e2):
                        raise InvalidScheduleError
    
    def __repr__(self):
        return str(self.sched)