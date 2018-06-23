""" NTMY main server application """

# Imports
import atexit
from time import sleep

import allocation
import api
import database

# Run allocator thread
allocation.allocation_thread.start()

# Run API server
sleep(0.5)
api.app.run(host='0.0.0.0')

# Commit changes on exit
atexit.register(database.commit_on_exit)