""" This module initializes the graph """
import networkx as nx

edge_weight_name = "distance"
node_position = {}
edge_colors = []

"""create the graph with networkx"""
Graph = nx.Graph()

"""
create a connection with sqlite database "graph.db" where is stored
all the data concerning the nodes and the edges

"""
edgelist = []
nodelist = []


def load_graph(session,config):
    data = session.get(config["serveruri"]+"/map").json()
    global edge_colors
    global node_positions
    global Graph
    global edgelist
    global nodelist

    nodelist = data['nodes']
    edgelist = data['edges']

    """
    first add the nodes to the graph, each of them with its attributes,
    then add the edges 
    
    """

    for node in nodelist:
        Graph.add_node(node['nodeID'], name=node['name'], X=node['X'], Y=node['Y'], orientation=node['orientation'])

    for edge in edgelist:
        Graph.add_edge(edge['node1']['nodeID'], edge['node2']['nodeID'], distance=edge['distance'], color=edge['color'])

    # set the position of each node and the color of each edge
    node_positions = {node[0]: (node[1]['X'], node[1]['Y']) for node in Graph.nodes(data=True)}
    edge_colors = [e[2]['color'] for e in Graph.edges(data=True)]
