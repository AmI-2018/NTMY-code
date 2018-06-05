from gpiozero import LED, PWMLED
from time import sleep

"""
    RGB positions              LED SCANNING CONFIG
          0                       MATRIX 3X8
        7   1                 L0 L1 L2 L3 L4 L5 L6 L7
      6       2      =>     R
        5   3               G
          4                 B
"""

# LEDs definitions
leds = [
    LED(18), # 0
    LED(23), # 1
    LED(24), # 2
    LED(25), # 3
    LED(12), # 4
    LED(16), # 5
    LED(20), # 6
    LED(21)  # 7
]

# PWM LEDs definitions
colors = {
    "red": PWMLED(13),   # REDs
    "green": PWMLED(19), # GREENs
    "blue": PWMLED(26)   # BLUEs
}

# Correction factors for better color accuracy
Kcorr = {
    "red": 0.3,
    "green": 0.7,
    "blue": 1
}

def make_arrow(direction: int, red: float, green: float, blue: float):
    """Make the panel show an arrow pointing to the given direction.

    :param direction: The direction the arrow will point
    :type direction: int
    :param red: The red value for the LEDs
    :type red: float
    :param green: The green value for the LEDs
    :type green: float
    :param blue: The blue value for the LEDs
    :type blue: float
    """

    # Shut down every LED and set color
    for led in leds:
        led.off()

    # Set the colors
    colors["red"].value = Kcorr["red"] * red
    colors["green"].value = Kcorr["green"] * green
    colors["blue"].value = Kcorr["blue"] * blue

    # Light up the neeeded LEDs
    arrow_leds = []

    if direction == 0:
        # East direction
        arrow_leds = [0, 1, 2, 3, 4]
    elif direction == 1:
        # South direction
        arrow_leds = [2, 3, 4, 5, 6]
    elif direction == 2:
        # West direction
        arrow_leds = [4, 5, 6, 7, 0]
    elif direction == 3:
        # North direction
        arrow_leds = [6, 7, 0, 1, 2]
    
    for i in arrow_leds:
        leds[i].blink()

    # Sleep to watch LEDs blinking
    sleep(5)

# Test main
if __name__ == "__main__":
    color = [0, 0.6, 1]
    while True:
        for i in range(4):
            make_arrow(i, *color)
