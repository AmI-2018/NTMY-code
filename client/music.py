import pafy

from player import Player

# Available playlists

playlists = {
    "rock": pafy.get_playlist("https://www.youtube.com/playlist?list=PLhd1HyMTk3f7KwWMyGRfyTE12IahrHk52"),
    "pop": pafy.get_playlist("https://www.youtube.com/playlist?list=PLDcnymzs18LXHh9m-CyGtzC-6MiPrE2JV"),
    "latin": pafy.get_playlist("https://www.youtube.com/playlist?list=PLcfQmtiAG0X_3a1RP-bcjWImDwdYaOW4b"),
    "classic": pafy.get_playlist("https://www.youtube.com/playlist?list=PLVXq77mXV53-Np39jM456si2PeTrEm9Mj"),
    "house": pafy.get_playlist("https://www.youtube.com/playlist?list=PLhInz4M-OzRUDjZYmK62_k2xAMwTXiTup")
}

# Add all the songs to the given player

def prepare_music_player(player: Player, playlist_name: str):
    try:
        playlist = playlists[playlist_name]
    except KeyError:
        return
    
    print("Using playlist '{}'.".format(playlist_name))
    for song in playlist["items"]:
        try:
            print("Adding song '{}'".format(song["pafy"].title))
            player.add_media(song["pafy"].getbestaudio().url)
        except Exception:
            continue