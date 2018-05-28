"""Media API"""

from flask import jsonify, request, Blueprint, abort
import database

media_bp = Blueprint("media_bp", __name__)

# Channels API

@media_bp.route("/media/channels")
def handler_get_channels():
    """Get the list of the channels.

    .. :quickref: Media; Get the list of the channels.

    :status 200: The list was correctly retrieved
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """
    return jsonify([c.to_dict() for c in database.functions.get(database.model.media.Channel)])

@media_bp.route("/media/channels/<int:channelID>", methods=["GET"])
def handler_get_channel_from_id(channelID):
    """Get the channel with the given ID.

    .. :quickref: Media; Get the chennel with the given ID.
    
    :param int channelID: The ID of the channel to retrieve
    :status 200: The channel was correctly retrieved
    :status 400: The channel could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded channel
    """
    try:
        return jsonify(database.functions.get(database.model.media.Channel, channelID)[0].to_dict())
    except database.exceptions.DatabaseError as e:
        return abort(400, str(e))

# Playlists API

@media_bp.route("/media/playlists")
def handler_get_playlists():
    """Get the list of the playlists.

    .. :quickref: Map; Get the list of the playlists.

    :status 200: The list was correctly retrieved
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """
    return jsonify([p.to_dict() for p in database.functions.get(database.model.media.Playlist)])

@media_bp.route("/media/playlists/<int:playlistID>", methods=["GET"])
def handler_get_playlist_from_id(playlistID):
    """Get the playlist with the given ID.

    .. :quickref: Media; Get the chennel with the given ID.
    
    :param int playlistID: The ID of the playlist to retrieve
    :status 200: The playlist was correctly retrieved
    :status 400: The playlist could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded playlist
    """
    try:
        return jsonify(database.functions.get(database.model.media.Playlist, playlistID)[0].to_dict())
    except database.exceptions.DatabaseError as e:
        return abort(400, str(e))