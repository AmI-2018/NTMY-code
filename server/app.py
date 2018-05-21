""" NTMY main server application """

# Imports
import allocation
import api
import database

# Run API server
api.app.run(host='0.0.0.0')