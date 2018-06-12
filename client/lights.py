from colorsys import rgb_to_hls
from typing import Dict, Any

import requests

class LightManager:
    def __init__(self, lightsuri: str, username: str):
        """Instantiate the LightManager.
        
        :param lightsuri: The URL of the lights controller
        :type lightsuri: str
        :param username: The username for the lights controller
        :type username: str
        """

        self.lightsuri = lightsuri
        self.username = username
    
    @staticmethod
    def rgb_to_hue(red: float, green: float, blue: float) -> int:
        """Converts RGB components to Hue value.
        
        :param red: The red component
        :type red: float
        :param green: The green component
        :type green: float
        :param blue: The blue component
        :type blue: float
        :return: The hue value
        :rtype: int
        """

        color = round(rgb_to_hls(red, green, blue)[0] * 360 * 182)
        return color
    
    @staticmethod
    def create_user(lightsuri: str, device: str) -> str:
        """Create an user on the lights manager.
        
        :param lightsuri: The URL of the lights controller 
        :type lightsuri: str
        :param device: The device description
        :type device: str
        :return: The ID of the new user
        :rtype: str
        """

        try:
            resp = requests.post("{}/api".format(lightsuri), json={"devicetype": device}).json()[0]
            if "success" in resp:
                return resp["success"]["username"]
            else:
                return None
        except Exception:
            return None
    
    def get_lights(self) -> Dict[str, Any]:
        """Get the lights dict.
        
        :return: The lights dict, or None
        :rtype: Dict[str, Any]
        """

        try:
            return requests.get("{}/api/{}/lights".format(self.lightsuri, self.username)).json()
        except Exception:
            return None
    
    def set_color(self, light: str, red: float, green: float, blue: float, sat: float, bri: float):
        requests.put("{}/api/{}/lights/{}/state".format(self.lightsuri, self.username, light), json={
            "on": True,
            "hue": self.rgb_to_hue(red, green, blue),
            "sat": sat,
            "bri": bri
        })

    def set_color_all(self, red: float, green: float, blue: float, sat: float, bri: float):
        """Set all the lights with the given RGB color, sat and bri.
        
        :param red: The red component
        :type red: float
        :param green: The green component
        :type green: float
        :param blue: The blue component
        :type blue: float
        :param sat: The sat value
        :type sat: float
        :param bri: The bri value
        :type bri: float
        """

        if self.get_lights() is not None:
            for light in self.get_lights():
                self.set_color(light, red, green, blue, sat, bri)