"""This module provides the SQLAlchemy base class from which all the other model classes are derived."""

from sqlalchemy.ext.declarative import declarative_base

# Base class declaration
Base = declarative_base()
"""The SQLAlchemy base class"""

# Remove imports so they won't be exposed
del declarative_base