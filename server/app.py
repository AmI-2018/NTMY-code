""" NTMY main server application """

# Imports
from time import sleep

import allocation
import api
import database

# Run allocator thread
allocation.allocation_thread.start()

# Run API server
sleep(0.5)
api.app.run(host='0.0.0.0')