"""This module provides the definition of the classes to describe the target functions to be optimized to obtain the best daily schedule."""

from typing import Callable, List

from .dailysched import DailySchedule

####################################
# TargetFunction class declaration #
####################################

class TargetFunction:
    """Represents a target function to optimize."""

    def __init__(self, name: str, criterion: Callable[[int, int], bool], calculator: Callable[[DailySchedule], int]):
        """Initializes the TargetFunction.
        
        :param name: The name of the target function
        :type name: str
        :param criterion: The function to compare values for the target function
        :type criterion: Callable[[int, int], bool]
        :param calculator: The function to compute the target function
        :type calculator: Callable[[DailySchedule], int]
        """
        
        self.name = name
        self.criterion = criterion
        self.calculator = calculator
    
    def better(self, val1: int, val2: int) -> bool:
        """Checks if the value val1 is better of val2 for the particular target function.
        
        :param val1: First value to be checked
        :type val1: int
        :param val2: Second value to be checked
        :type val2: int
        :return: True if val1 is better than val2, False otherwise
        :rtype: bool
        """

        return self.criterion(val1, val2)
    
    def calculate(self, schedule: DailySchedule) -> int:
        """Computes the actual value of the target function for the given schedule.
        
        :param schedule: The schedule on which the target function must be evaluated
        :type schedule: DailySchedule
        :return: The value of the target function for the given schedule
        :rtype: int
        """

        return self.calculator(schedule)

#######################################
# TargetFunctionSet class declaration #
#######################################

class TargetFunctionSet:
    """Represents a set of target functions to be optimized."""

    def __init__(self, target_funcs: List[TargetFunction]):
        """Initializes the TargetFuctionSet.

        param target_funcs: List of the target functions to evaluate
        :type target_funcs: List[TargetFunction]
        """
        self.target_funcs = list(target_funcs)
    
    def better(self, sched1: DailySchedule, sched2: DailySchedule) -> bool:
        """Checks if the schedule sched1 is better than sched2. That happens when f1 is better, f1 is equal and f2 is better, f1 and f2 are equal and f3 is better, and so on.
        
        :param sched1: The first schedule to be checked
        :type sched1: DailySchedule
        :param sched2: The second schedule to be checked
        :type sched2: DailySchedule
        :return: True if sched1 is better than sched2, False otherwise
        :rtype: bool
        """

        for funct in self.target_funcs:
            # Compute values for the two schedules
            val1 = funct.calculate(sched1)
            val2 = funct.calculate(sched2)

            # If a function is better, True; if it is not, False; else, proceed to next
            if funct.better(val1, val2):
                return True
            elif val1 == val2:
                continue
            else:
                return False
        return False

# Remove imports so they won't be exposed
del Callable, List, DailySchedule