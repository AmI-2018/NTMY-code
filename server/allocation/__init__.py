"""This module provides all the functions and classes to smartly allocate rooms for the events in the residence."""

# Import and expose allocation modules

from . import allocator, dailysched, exceptions, targetfun

__all__ = ["allocator", "dailysched", "exceptions", "targetfun"]

# Define the functions for the schedule

sched = None

def update_sched():
    import database
    global sched

    rooms = database.functions.get(database.model.standard.Room)
    events = database.functions.get(database.model.standard.Event)
    sched = allocator.allocate(rooms, events)

# Get the first schedule
update_sched()