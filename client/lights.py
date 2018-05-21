import requests
import time

class LightManager:
    def __init__(self, lightsuri, username):
        self.lightsuri = "http://localhost:8080"
        self.username = username
    
    @staticmethod
    def rgb_to_cie(red, green, blue):
        # Adapted from https://github.com/usolved/cie-rgb-converter
        red = ((red + 0.055) / (1.0 + 0.055))**2.4 if red > 0.04045 else red / 12.92
        green = ((green + 0.055) / (1.0 + 0.055))**2.4 if green > 0.04045 else green / 12.92
        blue = ((blue + 0.055) / (1.0 + 0.055))**2.4 if blue > 0.04045 else blue / 12.92

        X = red * 0.664511 + green * 0.154324 + blue * 0.162028
        Y = red * 0.283881 + green * 0.668433 + blue * 0.047685
        Z = red * 0.000088 + green * 0.072310 + blue * 0.986039

        x = X / (X + Y + Z)
        y = Y / (X + Y + Z)

        return [round(x, 4), round(y, 4)]
    
    def get_lights(self):
        try:
            return requests.get("{}/api/{}/lights".format(self.lightsuri, self.username)).json()
        except ValueError:
            return None
    
    def set_color_all(self, red, green, blue, bri):
        if self.get_lights() is not None:
            for light in self.get_lights():
                try:
                    requests.put("{}/api/{}/lights/{}/state".format(self.lightsuri, self.username, light), json={
                        "on": True,
                        "xy": self.rgb_to_cie(red, green, blue),
                        "bri": bri
                    })
                except requests.exceptions.ChunkedEncodingError:
                    continue