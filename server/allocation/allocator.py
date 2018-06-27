"""This module provides the function to compute the best daily schedule for the events."""

from typing import List

from database.model.standard import Room, Event
from .dailysched import DailySchedule, InvalidScheduleError
from .targetfun import TargetFunction, TargetFunctionSet

################################################################
# Definition of the target functions for optimization purposes #
################################################################

# Total number of participants (better if bigger)

def total_part_calc(schedule: DailySchedule) -> int:
    # Sum participants for every event in every room
    tot = sum([len(e["event"].participants) for r in schedule.sched for e in schedule.sched[r]])    
    return tot
total_part = TargetFunction("total_part", int.__gt__, total_part_calc)

# Total scheduled events (better if bigger)

def total_events_calc(schedule: DailySchedule) -> int:
    # Sum the number of the scheduled events
    tot = sum([len(schedule.sched[r]) for r in schedule.sched])
    return tot
total_events = TargetFunction("total_events", int.__gt__, total_events_calc)

# Events distribution among different rooms (better if lower)

def events_distrib_calc(schedule: DailySchedule) -> int:
    # Get the difference between the room with the most events and the one with the least
    events_num = [len(schedule.sched[r]) for r in schedule.sched]
    return max(events_num) - min(events_num)
events_distrib = TargetFunction("events_distrib", int.__lt__, events_distrib_calc)

# Better fit of the rooms (better if lower)

def best_fit_calc(schedule: DailySchedule) -> int:
    # Get the maximum difference between room size and the number of participants
    max_delta = max([r.size - len(e["event"].participants) for r in schedule.sched for e in schedule.sched[r]])
    return max_delta
best_fit = TargetFunction("best_room_fit", int.__lt__, best_fit_calc)

# Definition of the TargetFunctionSet

target = TargetFunctionSet([total_part, total_events, events_distrib, best_fit])
del total_part, total_part_calc, total_events, total_events_calc, events_distrib, events_distrib_calc, best_fit, best_fit_calc

##########################################
# Declaration of the allocator functions #
##########################################

def allocate(rooms: List[Room], events: List[Event]) -> DailySchedule:
    """Computes and returns the best daily schedule for the events.
    
    :param rooms: List of the residence's room
    :type rooms: List[Room]
    :param events: List of the daily residence's events
    :type events: List[Event]
    :return: The daily schedule for the next events
    :rtype: DailySchedule
    """

    # Declaration of the best event
    sol = [0]*len(events)

    # Recursive calculation of the best schedule
    return allocate_rec(0, sol, None, rooms, events)

def allocate_rec(pos: int, sol: List[int], best: DailySchedule, rooms: List[Room], events: List[Event]) -> DailySchedule:
    """Recursive allocator that generates the powerset of the room and returns the best solution.
    
    :param pos: The position to fill in the sol vector; starts at 0
    :type pos: int
    :param sol: A list of integers where sol[i] = j means that the i-th event will be hosted in the j-th room
    :type sol: List[int]
    :param best: The best solution found so far; starts at None
    :type best: DailySchedule
    :param rooms: The list of the rooms to allocate events
    :type rooms: List[Room]
    :param events: The list of events to be allocated
    :type events: List[Event]
    :return: The best solution found overall
    :rtype: DailySchedule
    """

    if pos == len(sol):
        try:
            sched = DailySchedule(rooms, events, sol)
            if best is None or target.better(sched, best):
                return sched
        except InvalidScheduleError:
            pass
        return best
    
    for i in range(0, len(rooms)+1):
        sol[pos] = i
        best = allocate_rec(pos+1, sol, best, rooms, events)
    return best

# Remove imports so they won't be exposed
del List, Room, Event, TargetFunction, TargetFunctionSet