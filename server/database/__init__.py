"""This module provides all the functions and classes to manage the database."""

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, scoped_session

# Configuration parameters
path = "sqlite:///ntmy.db?check_same_thread=False"
"""The path for the database"""

echo = False
"""The echo config flag"""

# DB connection
engine = create_engine(path, echo=echo)
"""The SQLAlchemy engine"""

# Session binding and creating
SessionFactory = scoped_session(sessionmaker(bind=engine))

# Remove imports so they won't be exposed
del create_engine, sessionmaker, scoped_session

# Module imports
from . import exceptions, model, session

__all__ = ["exceptions", "model", "session"]

# Tables creation if not already present
model.base.Base.metadata.create_all(engine)