from threading import Thread
from time import sleep
from typing import Dict, Any

import fake_useragent
import omxplayer
import pafy
import requests

class Player():
    def __init__(self):
        """Instantiate the Player."""

        self.media_list = []
        self.media_player = None
        self.media_player_thread = Thread(target=self.runner)
        self.stop_playing = False
    
    def add_media(self, path: str):
        """Append the given media to the playlist
        
        :param path: The media to add
        :type path: str
        """

        self.media_list.append(path)
    
    def runner(self):
        """Thread function for playing the media list."""

        for media in self.media_list:
            if self.media_player is None:
                self.media_player = omxplayer.OMXPlayer(media, args=["-o", "both"])
            else:
                self.media_player.load(media)

            print("Now playing '{}'".format(media))
            self.media_player.play()
            sleep(30)
            while self.media_player.is_playing():
                sleep(1)
                if self.stop_playing:
                    self.media_player.quit()
                    return
    
    def play(self):
        """Start the player."""

        self.stop_playing = False
        self.media_player_thread.start()
    
    def stop(self):
        """Stop the player."""

        self.stop_playing = True
        self.media_player_thread.join()
        self.media_player_thread = Thread(target=self.runner)
    
    def empty(self):
        """Empty the media list."""

        self.media_list = []
        self.media_player = None
    
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
        for song in playlist["items"]:
            try:
                print("Adding song '{}'".format(song["pafy"].title))
                self.add_media(song["pafy"].getbestaudio().url)
            except Exception:
                continue
        print("Fetch completed.")
