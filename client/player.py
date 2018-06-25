from subprocess import Popen, PIPE
from time import sleep
from typing import Dict, Any

import fake_useragent
import pafy
import requests

playlist_max_len = 15

class Player():
    def __init__(self):
        """Instantiate the Player."""

        self.media_list = []
        self.media_player = None
    
    def add_media(self, path: str, duration: int=None):
        """Append the given media to the playlist
        
        :param path: The media to add
        :type path: str
        :param duration: The duration of the media (None for streaming)
        :type duration: int
        """

        self.media_list.append({
            "path": path,
            "duration": duration
        })
    
    def play(self):
        """Start the player."""

        for media in self.media_list:
            self.media_player = Popen(["omxplayer", media["path"], "-o", "local"], stdin=PIPE, bufsize=0)
            print("Now playing '{}'".format(media))
            if media["duration"] is None:
                return
            else:
                sleep(media["duration"])
                self.stop()
    
    def stop(self):
        """Stop the player."""

        self.media_player.kill()
    
    def empty(self):
        """Empty the media list."""

        self.media_list = []
    
    def fetch_channel(self, channel: Dict[str, Any]):
        """Fetch the given TV channel.
        
        :param channel: The channel to fetch
        :type channel: Dict[str, Any]
        """

        print("Fetching channel '{}'...".format(channel["name"]))
        url = requests.get(channel["link"], headers={"User-Agent": fake_useragent.UserAgent().chrome}).text
        self.add_media(url)
        print("Fetch completed.")
    
    def fetch_playlist(self, playlist: Dict[str, Any]):
        """Fetch the given music playlist.
        
        :param playlist: The playlist to fetch.
        :type playlist: Dict[str, Any]
        """

        print("Fetching playlist '{}'...".format(playlist["name"]))
        playlist = pafy.get_playlist(playlist["link"])
        for i in range(min(len(playlist["items"]), playlist_max_len)):
            try:
                song = playlist["items"][i]
                print("Adding song '{}' (duration {})".format(song["pafy"].title, song["pafy"].duration))
                hh, mm, ss = song["pafy"].duration.split(":")
                duration = int(hh) * 3600 + int(mm) * 60 + int(ss)
                self.add_media(song["pafy"].getbestaudio().url, duration)
            except Exception:
                continue
        print("Fetch completed.")
