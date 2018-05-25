from colorsys import rgb_to_hls

import requests

class LightManager:
    def __init__(self, lightsuri, username):
        self.lightsuri = lightsuri
        self.username = username
    
    @staticmethod
    def rgb_to_hue(red, green, blue):
        color = rgb_to_hls(red, green, blue)[0] * 360 * 182
        return color
    
    def get_lights(self):
        try:
            return requests.get("{}/api/{}/lights".format(self.lightsuri, self.username)).json()
        except ValueError:
            return None
    
    def set_color_all(self, red, green, blue, sat, bri):
        if self.get_lights() is not None:
            for light in self.get_lights():
                requests.put("{}/api/{}/lights/{}/state".format(self.lightsuri, self.username, light), json={
                    "on": True,
                    "hue": self.rgb_to_hue(red, green, blue),
                    "sat": sat,
                    "bri": bri
                })