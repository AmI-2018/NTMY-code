from fake_useragent import UserAgent
from requests import get

from player import Player

# Available channels

channels = {
    "rai1": "http://mediapolis.rai.it/relinker/relinkerServlet.htm?cont=2606803&output=20",
    "rai2": "http://mediapolis.rai.it/relinker/relinkerServlet.htm?cont=180116&output=20",
    "rai3": "http://mediapolis.rai.it/relinker/relinkerServlet.htm?cont=180117&output=20",
    "rai_sport": "http://mediapolis.rai.it/relinker/relinkerServlet.htm?cont=4145&output=20",
    "rai_news": "http://mediapolis.rai.it/relinker/relinkerServlet.htm?cont=1&output=20"
}

ua = UserAgent()
headers = {"User-Agent": ua.chrome} # I'm a browser!

def prepare_tv_player(player: Player, channel_name: str):
    print("Fetching channel '{}'...".format(channel_name))
    try:
        url = get(channels[channel_name], headers=headers).text
        player.add_media(url)
    except KeyError:
        pass