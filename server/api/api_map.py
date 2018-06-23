"""Map API"""

from flask import jsonify, request, Blueprint, abort

from .decorators import require_root
import database

map_bp = Blueprint("map_bp", __name__)

@map_bp.route("/map", methods=["GET"])
def handler_get_map():
    """Get the list of the nodes and edges.

    .. :quickref: Map; Get the list of the nodes and edges.

    :status 200: The list was correctly retrieved
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """

    with database.session.DatabaseSession() as db_session:
        return jsonify({
            "nodes": [n.to_dict() for n in db_session.get(database.model.map.Node)],
            "edges": [e.to_dict() for e in db_session.get(database.model.map.Edge)]
        })

# Nodes API

@map_bp.route("/map/nodes", methods=["GET"])
def handler_get_nodes():
    """Get the list of the nodes.

    .. :quickref: Map; Get the list of the nodes.

    :status 200: The list was correctly retrieved
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """

    with database.session.DatabaseSession() as db_session:
        return jsonify([n.to_dict() for n in db_session.get(database.model.map.Node)])

@map_bp.route("/map/nodes", methods=["POST"])
@require_root
def handler_add_node():
    """Add a node.

    .. :quickref: Map; Add a node.
    
    :json string name: The name of the new node
    :json float x: The x value of the new node
    :json float y: The y value of the new node
    :json float orientation: The orientation of the new node
    :json int roomID: The ID of the room the node refers to
    :status 200: The node was correctly inserted
    :status 400: The provided JSON is invalid or there is a database error
    :status 401: The user has not logged in
    :return: The JSON-encoded newly created node
    """

    with database.session.DatabaseSession() as db_session:
        try:
            new_node = db_session.add(database.model.map.Node.from_dict(request.json))
            return jsonify(new_node.to_dict())
        except (database.exceptions.InvalidDictError, database.exceptions.DatabaseError) as e:
            return abort(400, str(e))

@map_bp.route("/map/nodes/<int:nodeID>", methods=["GET"])
def handler_get_node_from_id(nodeID):
    """Get the node with the given ID.

    .. :quickref: Map; Get the node with the given ID.
    
    :param int nodeID: The ID of the node to retrieve
    :status 200: The node was correctly retrieved
    :status 400: The node could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded node
    """

    with database.session.DatabaseSession() as db_session:
        try:
            return jsonify(db_session.get(database.model.map.Node, nodeID)[0].to_dict())
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

@map_bp.route("/map/nodes/<int:nodeID>", methods=["PUT"])
@require_root
def handler_patch_node_from_id(nodeID):
    """Update the node with the given ID.

    .. :quickref: Map; Update the node with the given ID.
    
    :json string name: The name of the updated node
    :json float x: The x value of the updated node
    :json float y: The y value of the updated node
    :json float orientation: The orientation of the updated node
    :json int roomID: The ID of the room the node refers to
    :param int nodeID: The ID of the node to update
    :status 200: The node was correctly updated
    :status 400: The node could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded node
    """

    with database.session.DatabaseSession() as db_session:
        try:
            upd_node = db_session.get(database.model.map.Node, nodeID)[0]
            return jsonify(db_session.upd(upd_node, request.json).to_dict())
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

@map_bp.route("/map/nodes/<int:nodeID>", methods=["DELETE"])
@require_root
def handler_delete_node_from_id(nodeID):
    """Delete the node with the given ID.

    .. :quickref: Map; Delete the node with the given ID.
    
    :param int nodeID: The ID of the node to delete
    :status 200: The node was correctly deleted
    :status 400: The node could not be found
    :status 401: The user has not logged in
    :return: Empty response
    """

    with database.session.DatabaseSession() as db_session:
        try:
            del_node = db_session.get(database.model.map.Node, nodeID)[0]
            db_session.rem(del_node)
            return ""
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

# Edges API

@map_bp.route("/map/edges", methods=["GET"])
def handler_get_edges():
    """Get the list of the edges.

    .. :quickref: Map; Get the list of the edges.

    :status 200: The list was correctly retrieved
    :status 401: The user has not logged in
    :return: The JSON-encoded list
    """

    with database.session.DatabaseSession() as db_session:
        return jsonify([e.to_dict() for e in db_session.get(database.model.map.Edge)])

@map_bp.route("/map/edges", methods=["POST"])
@require_root
def handler_add_edge():
    """Add an edge.

    .. :quickref: Map; Add an edge.
    
    :json int nodeID1: The ID of the first node
    :json int nodeID2: The ID of the second node
    :json float distance: The distance between the two nodes
    :json string color: The color of the new edge
    :status 200: The edge was correctly inserted
    :status 400: The provided JSON is invalid or there is a database error
    :status 401: The user has not logged in
    :return: The JSON-encoded newly created edge
    """
    
    with database.session.DatabaseSession() as db_session:
        try:
            new_edge = db_session.add(database.model.map.Edge.from_dict(request.json))
            return jsonify(new_edge.to_dict())
        except (database.exceptions.InvalidDictError, database.exceptions.DatabaseError) as e:
            return abort(400, str(e))

@map_bp.route("/map/edges/<int:nodeID1>/<int:nodeID2>", methods=["GET"])
def handler_get_edge_from_id(nodeID1, nodeID2):
    """Get the edge with the given IDs.

    .. :quickref: Map; Get the edge with the given IDs.
    
    :param int nodeID1: The ID of the first node
    :param int nodeID2: The ID of the second node
    :status 200: The edge was correctly retrieved
    :status 400: The edge could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded edge
    """

    with database.session.DatabaseSession() as db_session:
        try:
            return jsonify(db_session.get(database.model.map.Edge, (nodeID1, nodeID2))[0].to_dict())
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

@map_bp.route("/map/edges/<int:nodeID1>/<int:nodeID2>", methods=["PUT"])
@require_root
def handler_patch_edge_from_id(nodeID1, nodeID2):
    """Update the edge with the given IDs.

    .. :quickref: Map; Update the edge with the given IDs.
    
    :json float distance: The distance between the two nodes
    :json string color: The color of the updated edge
    :param int nodeID1: The ID of the first node
    :param int nodeID2: The ID of the second node
    :status 200: The edge was correctly updated
    :status 400: The edge could not be found
    :status 401: The user has not logged in
    :return: The JSON-encoded edge
    """

    with database.session.DatabaseSession() as db_session:
        try:
            upd_edge = db_session.get(database.model.map.Node, (nodeID1, nodeID2))[0]
            return jsonify(db_session.upd(upd_edge, request.json).to_dict())
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))

@map_bp.route("/map/edges/<int:nodeID1>/<int:nodeID2>", methods=["DELETE"])
@require_root
def handler_delete_edge_from_id(nodeID1, nodeID2):
    """Delete the edge with the given IDs.

    .. :quickref: Map; Delete the edge with the given IDs.
    
    :param int nodeID1: The ID of the first node
    :param int nodeID2: The ID of the second node
    :status 200: The edge was correctly deleted
    :status 400: The edge could not be found
    :status 401: The user has not logged in
    :return: Empty response
    """

    with database.session.DatabaseSession() as db_session:
        try:
            del_edge = db_session.get(database.model.map.Node, (nodeID1, nodeID2))[0]
            db_session.rem(del_edge)
            return ""
        except database.exceptions.DatabaseError as e:
            return abort(400, str(e))