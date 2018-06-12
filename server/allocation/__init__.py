"""This module provides all the functions and classes to smartly allocate rooms for the events in the residence."""

import pause
from threading import Thread

# Import and expose allocation modules

from . import allocator, dailysched, exceptions, targetfun

__all__ = ["allocator", "dailysched", "exceptions", "targetfun"]

# Define the functions for the schedule

sched = None
debug_mode = True

def update_sched():
    import datetime
    import database
    global sched

    start_check = datetime.date.today()
    end_check = datetime.date.today() + datetime.timedelta(days=1)

    rooms = database.functions.get(database.model.standard.Room)

    if debug_mode:
        events = database.functions.filter(database.model.standard.Event, "start >= '{}'".format(datetime.datetime.today()))
    else:
        events = database.functions.filter(database.model.standard.Event, "start >= '{}' AND end < '{}'".format(start_check, end_check))
    
    sched = allocator.allocate(rooms, events)

# Get the first schedule

update_sched()

# Generate the allocator thread object

def allocation_thread_fun():
    global sched
    while True:
        pause.seconds(30)
        print("Allocator is running...")
        update_sched()
        print("Allocator has generated today's schedule.")

allocation_thread = Thread(target=allocation_thread_fun, daemon=True)