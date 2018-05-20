"""This module provides all the classes to describe the data models, seamlessly mapped on database tables."""

# Import and expose model modules

from . import base, standard, relationships

__all__ = ["base", "standard", "relationships"]