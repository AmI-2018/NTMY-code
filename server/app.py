""" NTMY main server application """

# Imports
import atexit

import allocation
import api
import database

# Run allocator thread
allocation.allocation_thread.start()

# Run API server
api.app.run(host='0.0.0.0')

# Commit changes on exit
atexit.register(database.commit_on_exit)