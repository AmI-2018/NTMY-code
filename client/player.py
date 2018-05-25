import vlc

class Player():
    def __init__(self):
        # VLC instantiation
        self.instance = vlc.Instance()
        self.media_list = self.instance.media_list_new()
        self.media_player = self.instance.media_list_player_new()

        # VLC options
        self.media_player.get_media_player().set_fullscreen(True)
        self.media_player.set_media_list(self.media_list)
    
    def add_media(self, path):
        self.media_list.add_media(path)

    def play(self):
        self.media_player.play()
    
    def stop(self):
        self.media_player.stop()
    
    def empty(self):
        self.media_list.release()
        self.media_list = self.instance.media_list_new()
        self.media_player.set_media_list(self.media_list)