""" NTMY main server application """

# Imports
import allocation
import database

rooms = database.functions.get(database.model.standard.Room)
events = database.functions.get(database.model.standard.Event)

print(allocation.allocator.allocate(rooms, events))