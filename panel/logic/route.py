from . import graph as G
from .graph import nx
import math
from .exceptions import InvalidNodeId


def generate_direction(actual_node_id: int, destination_id: int):
    """
    Returns the port ID from the nodes IDs

    :param actual_node_id: panel's node id
    :type actual_node_id: int
    :param destination_id: user's destination id
    :type destination_id: int
    :return: index of the direction (cardinal point)
    """
    """ Generate the next node """
    next_node = get_next_node(actual_node_id, destination_id)

    """ Generate the exit_point """
    port = generate_port(get_node_from_id(actual_node_id),next_node)

    return port


def generate_port(node, next_node):
    """ This function calculate the angle between two nodes and then
        return the coordinate [ 0 - E; 1 - N; 2 - O; 3 - S]

        :param node: Source node
        :type node: dict
        :param next_node: Next node in the path
        :type node: dict
        :return: the index corresponding to the direction
        :rtype: int

    """
    """
    since some nodes may have the edges not aligned with the cardinal points
    for each of them is stored the angle between Graph's East and Node's East
       
    """

    """ Retrieve orientation  """
    setting = node['orientation']

    """ Calculate the angle """
    x = node['X'] - next_node['X']
    y = node['Y'] - next_node['Y']

    angle = math.degrees(math.atan2(y, x) + math.pi) + setting
    """ I added 180 degrees because atan2() return an angle in [-180; 180] """
    """ Since the exits' ids are in [0; 3] I need an angle in [0; 360]"""

    port = round(angle / 90) % 4

    return port


def get_shortest_path(source, dest):
    """Compute shortest path between pair of nodes in a graph."""

    path = nx.dijkstra_path(G.Graph, source, dest, weight=G.edge_weight_name)
    print(path)
    return path


def get_next_node(source, dest):
    """ Returns the next node receiving the shortest path to the destination."""

    path = get_shortest_path(source, dest)
    node = get_node_from_id(path[1])
    return node


def get_node_from_id(node_id):
    """ Provides the Node object receiving the ID """
    try:
        result = None
        for node in G.nodelist:
            if node['nodeID'] == node_id:
                result = node
                break

        if result is None:
            raise InvalidNodeId
        else:
            return result
    except InvalidNodeId:
        print("Invalid Node ID! : returned None")
        node = None
        pass

    return node
