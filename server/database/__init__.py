"""This module provides all the functions and classes to manage the database."""

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

# Configuration parameters
path = "sqlite:///ntmy.db?check_same_thread=False"
"""The path for the database"""

echo = False
"""The echo config flag"""

# DB connection
engine = create_engine(path, echo=echo)
"""The SQLAlchemy engine"""

# Session binding and creating
Session = sessionmaker(bind=engine)
session = Session()
"""The SQLAlchemy session"""

# Remove imports so they won't be exposed
del create_engine, sessionmaker

# Module imports
from . import model
from . import exceptions
from . import functions

__all__ = ["model", "exceptions", "functions"]

# Tables creation if not already present
model.base.Base.metadata.create_all(engine)