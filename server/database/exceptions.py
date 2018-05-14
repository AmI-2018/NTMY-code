"""This module provides the definition of the exceptions that can be raised from the database module."""

class DatabaseError(Exception):
    """Raised when the requested database operation can not be completed."""
    pass

class InvalidDictError(Exception):
    """Raised when the object can not be created from the provided dict."""
    pass