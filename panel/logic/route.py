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


def generate_direction(actual_node, destination):
    """
    :param actual_node: the node where the panel is setted
    :param destination: user's destination
    :return: index of the direction (cardinal point)
    """
    next_node = get_next_node(G.Graph,actual_node,destination,G.edge_weight_name)
    port = generate_port(actual_node,next_node)
    return port


def generate_port(node, next):
    """ This function receives the source node and the next node
        and returns the direction [ 0 - E; 1 - N; 2 - O; 3 - S]

        :param node: Source node
        :type node: dict
        :param next: Next node in the path
        :type node: dict
        :return: the index corresponding to the direction
        :rtype: int

    """
    """
    since some nodes may have the edges not aligned with the cardinal points
    for each of them is stored the orientation betw 

    """
    setting = node['orientation']
    x = node['X'] - next['X']
    y = node['Y'] - next['Y']

    angle = math.degrees(math.atan2(y, x) + math.pi) + setting
    port = round(angle / 90)

    return port


def get_shortest_path(source, dest):
    """Compute shortest path between pair of nodes in a graph."""

    path = nx.dijkstra_path(G.Graph, source, dest, weight=G.edge_weight_name)
    return path


def get_next_node(source, dest):
    """Return the next node from the shortest path to the destination."""

    path = get_shortest_path(source['id'], dest['id'])
    return path[1]


def search_node(node_id):
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

    try:
        node = search_node(node_id)
    except InvalidNodeId:
        print("Invalid Node ID! : returned None")
        node = None
        pass

    return node
