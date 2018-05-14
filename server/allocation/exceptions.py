"""This module provides the definition of the exceptions that can be raised from the allocation module."""

class InvalidScheduleError(Exception):
    """Raised when the provided schedule is invalid (e.g. some events overlap)."""
    pass