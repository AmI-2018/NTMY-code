from gpiozero import LED, RGBLED
from time import sleep


"""
    RGB leds position          LED SCANNING CONFIG            L(0-7) --> buffer current driving in open emitter
          0                       MATRIX 3X8                  R,G,B --> buffer current pwm driving in open collector
        7   1                 L0 L1 L2 L3 L4 L5 L6 L7         R,G,B --> drivers used for SW PWM modulation
      6       2      =>     R  x  x  x  x  x  x  x  x 
        5   3               G  x  x  x  x  x  x  x  x
          4                 B  x  x  x  x  x  x  x  x
"""

# LEDs on/off switch pin definitions
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
# PWM color driving  pin definitions
rgb_driver=RGBLED(13, 19, 26)

# Balancing  factors for better color accuracy
scale = {
    "R": 0.7,
    "G": 0.8,
    "B": 1
}


def light_standby():
    """Make the panel show a standby mode switching on all led and creating a pulse effect to """
    for led in leds:
        led.on()

    rgb_driver.pulse(on_color=(scale["R"], scale["G"], scale["B"]), off_color=(0,0,0))

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
    rgb_driver.off()
    #set the correct arrow configuration
    if direction == 0:
        # East direction
        for i in range (0, 5):
            leds[i].on()
    elif direction == 1:
        # North direction
        for i in range(6, 8):
            leds[i].on()
        for i in range (0, 3):
            leds[i].on()
    elif direction == 2:
        # West direction
        for i in range (4, 8):
              leds[i].on()
        leds[0].on()
    elif direction == 3:
        # South direction
        for i in range (2, 7):
            leds[i].on()

    # Set the event color and start blinking arrow
    rgb_driver.blink(on_color=(scale["R"]*red, scale["G"]*green, scale["B"]*blue), off_color=(0,0,0), n=5)

    # Sleep to watch Led's blink
    sleep(10)

