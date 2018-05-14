"""This module provides all the functions and classes to smartly allocate rooms for the events in the residence."""

# Import and expose allocation modules

from . import allocator
from . import dailysched
from . import exceptions
from . import targetfun

__all__ = ["allocator", "dailysched", "exceptions", "targetfun"]