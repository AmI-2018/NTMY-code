"""This module provides all the functions and classes to smartly allocate rooms for the events in the residence."""

# Import and expose allocation modules

from . import allocator, dailysched, exceptions, targetfun

__all__ = ["allocator", "dailysched", "exceptions", "targetfun"]

# Define the functions for the schedule

sched = None

def update_sched():
    from datetime import datetime
    import database
    global sched

    start_check = datetime(2018, 7, 1)
    end_check = datetime(2018, 7, 2)

    rooms = database.functions.get(database.model.standard.Room)
    events = database.functions.filter(
        database.model.standard.Event,
        "start >= '{}' AND end < '{}'".format(start_check, end_check)
    )
    sched = allocator.allocate(rooms, events)

# Get the first schedule
update_sched()