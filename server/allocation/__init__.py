"""This module provides all the functions and classes to smartly allocate rooms for the events in the residence."""

from threading import Thread, Semaphore

# Import and expose allocation modules

from . import allocator, dailysched, exceptions, targetfun

__all__ = ["allocator", "dailysched", "exceptions", "targetfun"]

# Define the functions for the schedule

sched = None
db_session = None
debug_mode = True

def update_sched():
    import datetime
    import database

    global sched
    global db_session

    if db_session is None:
        db_session = database.session.DatabaseSession()

    start_check = datetime.date.today()
    end_check = datetime.date.today() + datetime.timedelta(days=1)

    rooms = db_session.get(database.model.standard.Room)

    if debug_mode:
        events = db_session.filter(database.model.standard.Event, "start >= '{}'".format(datetime.datetime.today()))
    else:
        events = db_session.filter(database.model.standard.Event, "start >= '{}' AND end < '{}'".format(start_check, end_check))
    
    sched = allocator.allocate(rooms, events)

# Generate the allocator thread object

sem_alloc = Semaphore(1)

def allocation_thread_fun():
    global sched
    while True:
        sem_alloc.acquire()
        print("Allocator is running...")
        update_sched()
        print("Allocator has generated today's schedule.")
        print("Allocated events: {}".format(sched))

allocation_thread = Thread(target=allocation_thread_fun, daemon=True)

# Remove imports so they won't be exposed
del Thread, Semaphore