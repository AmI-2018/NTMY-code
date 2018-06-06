from gpiozero import LED, PWMLED
from time import sleep


"""
    RGB leds position          LED SCANNING CONFIG            L(0-7) --> buffer current driving in open emitter
          0                       MATRIX 3X8                  R,G,B --> buffer current pwm driving in open collector
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
# PWM color driving definitions
color_buffer = {
    "red": PWMLED(13),   # REDs
    "green": PWMLED(19), # GREENs
    "blue": PWMLED(26)   # BLUEs
}

# Balancing  factors for better color accuracy
K_corr = {
    "R": 0.7,
    "G": 0.8,
    "B": 1
}
def light_standby():
    for led in leds:
            led.on()
    color_buffer["red"].value =K_corr["R"]
    color_buffer["green"].value =K_corr["G"]
    color_buffer["blue"].value =K_corr["B"]

    color_buffer["red"].pulse()
    color_buffer["green"].pulse()
    color_buffer["blue"].pulse()

#def light_arrow(direction: int, red: float, green: float, blue: float):
""" Make the panel show an arrow pointing to the given direction.

    :param direction: The direction the arrow will point
    :type direction: int
    :param red: The red value for the LEDs
    :type red: float
    :param green: The green value for the LEDs
    :type green: float
    :param blue: The blue value for the LEDs
    :type blue: float
    """
directions=0

    # Shut down every LED and set color


for led in leds:
        led.off()

    # Light up the neeeded LEDs
arrow_leds = []

if directions == 0:
        # East direction
        arrow_leds = [0, 1, 2, 3, 4]
        for i in range (0,5):
              leds[i].on()

elif direction == 1:
        # South direction
        arrow_leds = [2, 3, 4, 5, 6]
elif direction == 2:
        # West direction
        arrow_leds = [4, 5, 6, 7, 0]
elif direction == 3:
        # North direction
        arrow_leds = [6, 7, 0, 1, 2]

    # Set the colors
color_buffer["red"].value = K_corr["R"] * 1
color_buffer["green"].value = K_corr["G"] * 1
color_buffer["blue"].value = K_corr["B"] * 0


    # Sleep to watch Led's pulse
sleep(5)

# Test main
"""fd
if __name__ == "__main__":
    color = [0, 0.6, 1]
    while True:
        for i in range(4):
            light_arrow(i, *color)
"""
