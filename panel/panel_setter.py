from gpiozero import RGBLED
from app_functions import generate_arrow , reset_arrow

"""
    RGB positions
                      0
                    7   1
                  6       2
                    5   3
                      4

"""

# definizione LED -> da mettere pin
leds = [
    RGBLED(), # 0
    RGBLED(), # 1
    RGBLED(), # 2
    RGBLED(), # 3
    RGBLED(), # 4
    RGBLED(), # 5
    RGBLED(), # 6
    RGBLED()  # 7
]

def rgb_setter(num_led, red_val, green_val, blue_val):
    leds[num_led].color = (red_val, green_val, blue_val)

# direction returned by function "generate_arrow" from app_function
def set_arrow(direction, red_val, green_val, blue_val):
    # right direction
    if (direction == 0):
        for num in range(0, 4):
            rgb_setter(num, red_val, green_val, blue_val)

        for num in range(5, 7):
            rgb_setter(num, 0, 0, 0)
    
    # up direction
    elif (direction == 1):
        for num in range(0, 2):
            rgb_setter(num, red_val, green_val, blue_val)
        rgb_setter(6, red_val, green_val, blue_val)
        rgb_setter(7, red_val, green_val, blue_val)

        for num in range(3, 5):  # spegne gli altri led
            rgb_setter(num, 0, 0, 0)
    
    # left direction
    elif (direction == 2):
        for num in range(4, 7):  # accende i led per la freccia a sinistra
            rgb_setter(num, red_val, green_val, blue_val)
        rgb_setter(0, red_val, green_val, blue_val)

        for num in range(1, 3):  # spegne gli altri led
            rgb_setter(num, 0, 0, 0)

    # down direction
    elif (direction == 3):
        for num in range(2, 6):
            rgb_setter(num, red_val, green_val, blue_val)

        rgb_setter(7, red_val, green_val, blue_val)
        rgb_setter(0, red_val, green_val, blue_val)
        rgb_setter(1, red_val, green_val, blue_val)

