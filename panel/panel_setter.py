from gpiozero import LED, PWMLED
from time import pause
from app_functions import generate_arrow , reset_arrow

"""
    RGB positions              LED SCANNING CONFIG
          0                       MATRIX 4X6
        7   1                    A0 A1 A2 B0 B1 B2
      6       2      =>       C0 
        5   3                 C1
          4                   C2
                              C3
"""

# definizione LED -> da mettere pin
couple_led = [
    LED(12), # 0, 4   C0
    LED(16), # 1, 7   C1
    LED(20), # 2, 6   C2
    LED(21) # 3, 5    C3
]

color_A={
    "red": PWMLED(11),  #A0 RED
    "green": PWMLED(5),   #A1 GREEN
    "blue": PWMLED(6)    #A2 BLUE
}

color_B={
    "red": PWMLED(13), #A0 RED
    "green": PWMLED(19), #A1 GREEN
    "blue": PWMLED(26)  #A2 BLUE
}

color[0, 1, 1];

#if(direction==0)#EST
        couple_led[0].on()
        couple_led[1].on()
        couple_led[2].on()
        couple_led[3].on()
        couple_led[4].on()
        color_B["red"].value(color[0])
        color_B["green"].value(color[1])
        color_B["blue"].value(color[2])






