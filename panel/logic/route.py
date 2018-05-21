import graph as G
from graph import nx
import matplotlib.pyplot as plt
import math
from exceptions import InvalidNodeId


def plot_graph():
    """ plot the graph """
    plt.figure(figsize=(8, 6))
    G.nx.draw(G.Graph, pos=G.node_positions, edge_color=G.edge_colors, node_size=10, node_color='black')
    plt.title("Graph Representation of NTMY College's roads map ", size=15)
    plt.show()


def generate_direction(actual_node_id, destination_id):
    """
    Returns the port ID from the nodes IDs

    :param actual_node_id: panel's node id
    :type actual_node_id: str
    :param destination_id: user's destination id
    :type destination_id: str
    :return: index of the direction (cardinal point)
    """
    """ Obtain the Nodes objects """
    source_node = get_node_from_id(actual_node_id)
    dest_node = get_node_from_id(destination_id)

    """ Generate the next node """
    next_node = get_next_node(source_node,dest_node)

    """ Generate the exit_point """
    port = generate_port(source_node,next_node)

    return port


def generate_port(node, next):
    """ This function calculate the angle between two nodes and then
        return the coordinate [ 0 - E; 1 - N; 2 - O; 3 - S]

        :param node: Source node
        :type node: dict
        :param next: Next node in the path
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
    x = node['X'] - next['X']
    y = node['Y'] - next['Y']

    angle = math.degrees(math.atan2(y, x) + math.pi) + setting
    """ I added 180° because atan2() return an angle in [-180; 180] """
    """ Since the exits' ids are in [0; 3] I need an angle in [0; 360]"""

    port = round(angle / 90)

    return port


def get_shortest_path(source, dest):
    """Compute shortest path between pair of nodes in a graph."""

    path = nx.dijkstra_path(G.Graph, source, dest, weight=G.edge_weight_name)
    return path


def get_next_node(source, dest):
    """ Returns the next node receiving the shortest path to the destination."""

    path = get_shortest_path(source['id'], dest['id'])
    return path[1]


def search_node(node_id):
    """ Try to obtain a node by its ID """
    result = None
    for i,x in G.nodelist.iterrows():
        if x['id'] == node_id:
            result = x
            break

    if result is None:
        raise InvalidNodeId
    else:
        return result


def get_node_from_id(node_id):
    """ Provides the Node object receiving the ID """
    try:
        node = search_node(node_id)
    except InvalidNodeId:
        print("Invalid Node ID! : returned None")
        node = None
        pass

    return node
