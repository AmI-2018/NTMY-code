from gpiozero import LED, PWMLED
from time import sleep


"""
    RGB leds position          LED SCANNING CONFIG            L(0-7) --> buffer current driving in open emitter
          0                       MATRIX 3X8                  R,G,B --> buffer current pwm driving in open collector
        7   1                 L0 L1 L2 L3 L4 L5 L6 L7         R,G,B --> drivers used for SW PWM modulation
      6       2      =>     R  x  x  x  x  x  x  x  x 
        5   3               G  x  x  x  x  x  x  x  x
          4                 B  x  x  x  x  x  x  x  x
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
"""Make the panel show a standby mode switching on all led and creating a pulse effect to """
    for led in leds:
            led.on()
    color_buffer["red"].value =K_corr["R"]
    color_buffer["green"].value =K_corr["G"]
    color_buffer["blue"].value =K_corr["B"]

    color_buffer["red"].pulse()
    color_buffer["green"].pulse()
    color_buffer["blue"].pulse()

def light_arrow(direction: int, red: float, green: float, blue: float):
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


    # Shut down every LED and set color
    for led in leds:
        led.off()


    if directions == 0:
        # East direction
        for i in range (0,5):
              leds[i].on()

    elif direction == 1:
        # South direction
        for i in range (2, 7):
              leds[i].on()
    elif direction == 2:
        # West direction
        for i in range (4, 8):
              leds[i].on()
        leds[0].on()

    elif direction == 3:
        # North direction
        for i in range(6, 8):
            leds[i].on()
        for i in range (0, 3):
              leds[i].on()
    # Set the colors
    color_buffer["red"].value = K_corr["R"] * red
    color_buffer["green"].value = K_corr["G"] * green
    color_buffer["blue"].value = K_corr["B"] * blue


    # Sleep to watch Led's pulse
    sleep(5)

