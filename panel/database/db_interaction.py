import sqlite3
import math


def get_nodes():
    nodes = []
    sql = "SELECT * FROM nodes"
    conn = sqlite3.connect("graph.db")

    # to remove u from sqlite3 cursor.fetchall() results
    conn.text_factory = sqlite3.OptimizedUnicode

    cursor = conn.cursor()
    cursor.execute(sql)

    results = cursor.fetchall()

    for node in results:
        nodes.append({'id': node[0], 'X': node[1], 'Y': node[2], 'orientation' : node[3]})

    conn.close()

    return nodes


def get_edges():
    edges = []
    sql = "SELECT * FROM edges"
    conn = sqlite3.connect("graph.db")

    # to remove u from sqlite3 cursor.fetchall() results
    conn.text_factory = sqlite3.OptimizedUnicode

    cursor = conn.cursor()
    cursor.execute(sql)

    results = cursor.fetchall()

    for edge in results:
        edges.append({'node1': edge[0],
                      'node2': edge[1],
                      'trail': edge[2],
                      'distance': edge[3],
                      'color': edge[4],
                      'estimate': edge[5],
                      })

    conn.close()

    return edges


def insert_edge(node1,node2,distance):
    sql = "INSERT INTO edges(node1,node2,trail,distance,color,estimate) values (?,?,?,?,?,?)"
    conn = sqlite3.connect("graph.db")

    # to remove u from sqlite3 cursor.fetchall() results
    conn.text_factory = sqlite3.OptimizedUnicode

    cursor = conn.cursor()
    cursor.execute(sql,(node1,node2,"",0,"",0))

    conn.commit()
    cursor.close()
    conn.close()


def update_dist(node1,node2,new_dist):
    sql = "UPDATE edges SET distance = ? WHERE node1 = ? AND node2 = ?"
    conn = sqlite3.connect("graph.db")

    cursor = conn.cursor()
    cursor.execute(sql, (new_dist,node1,node2))

    conn.commit()
    cursor.close()
    conn.close()


def edges_distances_update(edges,nodes):
    for edge in edges:
        for no1 in nodes:
            for no2 in nodes:
                if(no1['id'] == edge['node1'] and no2['id'] == edge['node2']):
                    x = no1['X'] - no2['X']
                    y = no1['Y'] - no2['Y']
                    dist = math.pow(x,2) + math.pow(y,2)
                    dist = math.sqrt(dist)
                    update_dist(no1['id'],no2['id'],dist)
