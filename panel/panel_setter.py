from gpiozero import LED, PWMLED
from time import sleep

"""
    RGB positions              LED SCANNING CONFIG
          0                       MATRIX 3X8
        7   1                 L0 L1 L2 L3 L4 L5 L6 L7
      6       2      =>      R
        5   3                G
          4                  B
"""

# definizione LED -> da mettere pin
leds ={
    "L0": LED(18), # 0
    "L1": LED(23), # 1
    "L2": LED(24), # 2
    "L3": LED(25), # 3
    "L4": LED(12), # 4
    "L5": LED(16), # 5
    "L6": LED(20), # 6
    "L7": LED(21)  # 7
}

color={
    "red": PWMLED(13),   # REDs
    "green": PWMLED(19), # GREENs
    "blue": PWMLED(26)   # BLUEs
}


color_set = [0, 0.6, 1]
direction=0

while 1:
# if(direction==0): #EST
     leds["L0"].on()
     leds["L1"].on()
     leds["L2"].on()
     leds["L3"].on()
     leds["L4"].on()
     leds["L5"].off()
     leds["L6"].off()
     leds["L7"].off()

     color["red"].value=color_set[0]
     color["green"].value=color_set[1]
     color["blue"].value = color_set[2]
     sleep(3)
     direction=2
     
     leds["L0"].off()
     leds["L1"].off()
     leds["L2"].off()
     leds["L3"].off()
     leds["L4"].off()
     leds["L5"].off()
     leds["L6"].off()
     leds["L7"].off()
     sleep(1)

 #if(direction==1):  SUD
     color["red"].value=0.3
     color["green"].value=0
     color["blue"].value =1

     leds["L2"].on()
     leds["L3"].on()
     leds["L4"].on()
     leds["L5"].on()
     leds["L6"].on()
     leds["L7"].off()
     leds["L0"].off()
     leds["L1"].off()

     sleep(3)
     direction=3
     leds["L2"].off()
     leds["L3"].off()
     leds["L4"].off()
     leds["L5"].off()
     leds["L6"].off()
     leds["L7"].off()
     leds["L0"].off()
     leds["L1"].off()
     sleep(1)
# if(direction==2):  OVEST
     color["red"].value=0.3
     color["green"].value=0.5
     color["blue"].value =1

     leds["L4"].on()
     leds["L5"].on()
     leds["L6"].on()
     leds["L7"].on()
     leds["L0"].on()
     leds["L1"].off()
     leds["L2"].off()
     leds["L3"].off()
     
     sleep(3)
     direction=1
     leds["L4"].off()
     leds["L5"].off()
     leds["L6"].off()
     leds["L7"].off()
     leds["L0"].off()
     leds["L1"].off()
     leds["L2"].off()
     leds["L3"].off()

     sleep(1)

#if(direction==3): NORD
     color["red"].value=0.3
     color["green"].value=1
     color["blue"].value =0

     leds["L6"].on()
     leds["L7"].on()
     leds["L0"].on()
     leds["L1"].on()
     leds["L2"].on()
     leds["L3"].off()
     leds["L4"].off()
     leds["L5"].off()
     sleep(3)
     direction=0

     leds["L6"].off()
     leds["L7"].off()
     leds["L0"].off()
     leds["L1"].off()
     leds["L2"].off()
     leds["L3"].off()
     leds["L4"].off()
     leds["L5"].off()
     sleep(1)

