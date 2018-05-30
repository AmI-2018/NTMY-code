from threading import Thread
from time import sleep

import fake_useragent
import omxplayer
import pafy
import requests

class Player():
    def __init__(self):
        self.media_list = []
        self.media_player = None
        self.media_player_thread = Thread(target=self.runner)
        self.stop_playing = False
    
    def add_media(self, path):
        self.media_list.append(path)
    
    def runner(self):
        for media in self.media_list:
            if self.media_player is None:
                self.media_player = omxplayer.OMXPlayer(media)
            else:
                self.media_player.load(media)

            print("Now playing '{}'".format(media))
            self.media_player.play()
            while self.media_player.is_playing():
                sleep(1)
                if self.stop_playing:
                    self.media_player.quit()
                    return
    
    def play(self):
        self.stop_playing = False
        self.media_player_thread.start()
    
    def stop(self):
        self.stop_playing = True
        self.media_player_thread.join()
        self.media_player_thread = Thread(target=self.runner)
    
    def empty(self):
        self.media_list = []
        self.media_player = None
    
    def fetch_channel(self, channel):
        print("Fetching channel '{}'...".format(channel["name"]))
        url = requests.get(channel["link"], headers={"User-Agent": fake_useragent.UserAgent().chrome}).text
        self.add_media(url)
        print("Fetch completed.")
    
    def fetch_playlist(self, playlist):
        print("Fetching playlist '{}'...".format(playlist["name"]))
        playlist = pafy.get_playlist(playlist["link"])
        for song in playlist["items"]:
            try:
                print("Adding song '{}'".format(song["pafy"].title))
                self.add_media(song["pafy"].getbestaudio().url)
            except Exception:
                continue
        print("Fetch completed.")
