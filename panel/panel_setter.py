from gpiozero import PWMLED
from .app_functions import generate_arrow , reset_arrow




#definizione numero di rgb_led
rgb_led=["led0","led1","led2","led3","led4","led5","led6","led7"]

#definizione dei 3 pin per ogni rgb_led
for i in range(0,8):
    rgb_led[i]=["red_pin" , "green_pin", "blue_pin"]

#ASSEGNAZIONE PIN RASPBERRY CON RGB_leds
"""
rgb_led[0]=[]
rgb_led[1]=[]
rgb_led[2]=[]
rgb_led[3]=[]
rgb_led[4]=[]
rgb_led[5]=[]
rgb_led[6]=[]
rgb_led[7]=[]
"""

def rgb_setter (num_led, red_val, green_val, blue_val):
    {
        #association for software pwm modulation  
        red_led = PWMLED(rgb_led[num_led][0])   
        green_led = PWMLED(rgb_led[num_led] [1])
        blue_led = PWMLED(rgb_led[num_led] [2])
        
        #set for each pin of the led
        red_led.value=red_val
        green_led.value=green_val
        blue_led.value=blue_val
    }

"""
      posizione degli rgb:
                      0
                    7   1
                  6       2
                    5   3
                      4

"""
# direction returned by function "generate_arrow" from app_function
def set_arrow(direction):
        while not reset_arrow
            if (direction == left)
    
                for num in range(4, 7):  # accende i led per la freccia a sinistra
                    rgb_setter(num, red_val, green_val, blue_val)
                rgb_setter(0, red_val, green_val, blue_val)
    
                for num in range(1, 3):  # spegne gli altri led
                    rgb_setter(num, 0, 0, 0)
    
            elif (direction == right)
                for num in range(0, 4):
                    rgb_setter(num, red_val, green_val, blue_val)
    
                for num in range(5, 7)
                    rgb_setter(num, 0, 0, 0)
    
            elif (direction == front)
                for num in range(0, 2):
                    rgb_setter(num, red_val, green_val, blue_val)
                rgb_setter(6, red_val, green_val, blue_val)
                rgb_setter(7, red_val, green_val, blue_val)
    
                for num in range(3, 5):  # spegne gli altri led
                    rgb_setter(num, 0, 0, 0)
    
            elif (direction == behind)
                for num in range(2, 6):
                    rgb_setter(num, red_val, green_val, blue_val)
    
                rgb_setter(7, red_val, green_val, blue_val)
                rgb_setter(0, red_val, green_val, blue_val)
                rgb_setter(1, red_val, green_val, blue_val)
    
    return 1

