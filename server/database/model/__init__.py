"""This module provides all the classes to describe the data models, seamlessly mapped on database tables."""

# Import and expose model modules

from . import base
from . import login
from . import standard
from . import relationships

__all__ = ["base", "login", "standard", "relationships"]