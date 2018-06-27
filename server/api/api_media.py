"""Media API"""

from flask import jsonify, request, Blueprint, abort

from .decorators import require_root
import database

media_bp = Blueprint("media_bp", __name__)

# Channels API

@media_bp.route("/media/channels", methods=["GET"])
def handler_get_channels():
    """Get the list of the channels.

    .. :quickref: Media; Get the list of the channels.

    :status 200: The list was correctly retrieved
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """

    with database.session.DatabaseSession() as db_session:
        return jsonify([c.to_dict() for c in db_session.get(database.model.media.Channel)])

@media_bp.route("/media/channels", methods=["POST"])
@require_root
def handler_add_channel():
    """Add a channel.

    .. :quickref: Media; Add a channel.
    
    :json string name: The name of the new channel
    :json string link: The link to fetch the channel from
    :status 200: The channel was correctly inserted
    :status 400: The provided JSON is invalid or there is a database error
    :status 401: The user has not logged in
    :return: The JSON-encoded newly created channel
    """

    with database.session.DatabaseSession() as db_session:
        try:
            new_channel = db_session.add(database.model.media.Channel.from_dict(request.json))
            return jsonify(new_channel.to_dict())
        except (database.exceptions.InvalidDictError, database.exceptions.DatabaseError) as e:
            return abort(400, str(e))

@media_bp.route("/media/channels/<int:channelID>", methods=["GET"])
def handler_get_channel_from_id(channelID):
    """Get the channel with the given ID.

    .. :quickref: Media; Get the channel with the given ID.
    
    :param int channelID: The ID of the channel to retrieve
    :status 200: The channel was correctly retrieved
    :status 400: The channel could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded channel
    """

    with database.session.DatabaseSession() as db_session:
        try:
            return jsonify(db_session.get(database.model.media.Channel, channelID)[0].to_dict())
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

@media_bp.route("/media/channels/<int:channelID>", methods=["PUT"])
@require_root
def handler_patch_channel_from_id(channelID):
    """Update the channel with the given ID.

    .. :quickref: Media; Update the channel with the given ID.
    
    :json string name: The name of the updated channel
    :json string link: The link to fetch the channel from
    :status 200: The channel was correctly updated
    :status 400: The channel could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded channel
    """

    with database.session.DatabaseSession() as db_session:
        try:
            upd_channel = db_session.get(database.model.media.Channel, channelID)[0]
            return jsonify(db_session.upd(upd_channel, request.json).to_dict())
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

@media_bp.route("/media/channels/<int:channelID>", methods=["DELETE"])
@require_root
def handler_delete_channel_from_id(channelID):
    """Delete the channel with the given ID.

    .. :quickref: Media; Delete the channel with the given ID.
    
    :param int channelID: The ID of the channel to delete
    :status 200: The channel was correctly deleted
    :status 400: The channel could not be found
    :status 401: The user has not logged in
    :return: Empty response
    """

    with database.session.DatabaseSession() as db_session:
        try:
            del_channel = db_session.get(database.model.media.Channel, channelID)[0]
            db_session.rem(del_channel)
            return ""
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

# Playlists API

@media_bp.route("/media/playlists", methods=["GET"])
def handler_get_playlists():
    """Get the list of the playlists.

    .. :quickref: Media; Get the list of the playlists.

    :status 200: The list was correctly retrieved
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """

    with database.session.DatabaseSession() as db_session:
        return jsonify([p.to_dict() for p in db_session.get(database.model.media.Playlist)])

@media_bp.route("/media/playlists", methods=["POST"])
@require_root
def handler_add_playlist():
    """Add a playlist.

    .. :quickref: Media; Add a playlist.
    
    :json string name: The name of the new playlist
    :json string link: The link to fetch the playlist from
    :status 200: The playlist was correctly inserted
    :status 400: The provided JSON is invalid or there is a database error
    :status 401: The user has not logged in
    :return: The JSON-encoded newly created playlist
    """

    with database.session.DatabaseSession() as db_session:
        try:
            new_channel = db_session.add(database.model.media.Playlist.from_dict(request.json))
            return jsonify(new_channel.to_dict())
        except (database.exceptions.InvalidDictError, database.exceptions.DatabaseError) as e:
            return abort(400, str(e))

@media_bp.route("/media/playlists/<int:playlistID>", methods=["GET"])
def handler_get_playlist_from_id(playlistID):
    """Get the playlist with the given ID.

    .. :quickref: Media; Get the playlist with the given ID.
    
    :param int playlistID: The ID of the playlist to retrieve
    :status 200: The playlist was correctly retrieved
    :status 400: The playlist could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded playlist
    """

    with database.session.DatabaseSession() as db_session:
        try:
            return jsonify(db_session.get(database.model.media.Playlist, playlistID)[0].to_dict())
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

@media_bp.route("/media/playlists/<int:playlistID>", methods=["PUT"])
@require_root
def handler_patch_playlist_from_id(playlistID):
    """Update the playlist with the given ID.

    .. :quickref: Media; Update the playlist with the given ID.
    
    :json string name: The name of the updated playlist
    :json string link: The link to fetch the playlist from
    :status 200: The playlist was correctly updated
    :status 400: The playlist could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded playlist
    """

    with database.session.DatabaseSession() as db_session:
        try:
            upd_playlist = db_session.get(database.model.media.Playlist, playlistID)[0]
            return jsonify(db_session.upd(upd_playlist, request.json).to_dict())
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

@media_bp.route("/media/playlists/<int:playlistID>", methods=["DELETE"])
@require_root
def handler_delete_playlist_from_id(playlistID):
    """Delete the playlist with the given ID.

    .. :quickref: Media; Delete the playlist with the given ID.
    
    :param int playlistID: The ID of the playlist to delete
    :status 200: The playlist was correctly deleted
    :status 400: The playlist could not be found
    :status 401: The user has not logged in
    :return: Empty response
    """

    with database.session.DatabaseSession() as db_session:
        try:
            del_playlist = db_session.get(database.model.media.Playlist, playlistID)[0]
            db_session.rem(del_playlist)
            return ""
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))