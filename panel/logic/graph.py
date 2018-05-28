""" This module initializes the graph """
import networkx as nx
import pandas as pd
import sqlite3


edge_weight_name = "distance"

"""create the graph with networkx"""
Graph = nx.Graph()

"""
create a connection with sqlite database "graph.db" where is stored
all the data concerning the nodes and the edges

"""
if __name__ == "__main__":
    conn = sqlite3.connect("../database/graph.db")
else:
    conn = sqlite3.connect("./database/graph.db")

"""
store the data in two container, is used pandas in order 
to handle all this data in a better way

"""

nodelist = pd.read_sql("SELECT * FROM nodes",conn)
edgelist = pd.read_sql("SELECT * FROM edges",conn)


""" 
first add the nodes to the graph, each of them with its attributes,
then add the edges 

"""
for i, nlrow in nodelist.iterrows():
    Graph.add_node(nlrow[0], X=nlrow[1], Y=nlrow[2], orientation=nlrow[3])

for i, elrow in edgelist.iterrows():
    Graph.add_edge(elrow[0], elrow[1], distance=elrow[2], color=elrow[3])


""" set the position of each node and the color of each edge """

node_positions = {node[0]: (node[1]['X'], node[1]['Y']) for node in Graph.nodes(data=True)}
edge_colors = [e[2]['color'] for e in Graph.edges(data=True)]
