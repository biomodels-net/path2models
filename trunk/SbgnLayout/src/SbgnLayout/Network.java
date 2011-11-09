/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SbgnLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Network
{
    public static class Node
    {
        private String id;
        float x = 0;
        float y = 0;
        private List<Edge> outgoing = new ArrayList<Edge>();
        private List<Edge> incoming = new ArrayList<Edge>();

        public List<Edge> getOutgoing() { return outgoing; }
        public List<Edge> getIncoming() { return incoming; }

        public void setPos(float x, float y)
        {
            this.x = x;
            this.y = y;
        }

        public Iterable<Node> getOutgoingNodes() 
        {
            List<Node> nodes = new ArrayList<Node>();
            for (Edge e : getOutgoing())
            {
                nodes.add(e.dest);
            }
            return nodes;
        }

        public Iterable<Node> getIncomingNodes()
        {
            List<Node> nodes = new ArrayList<Node>();
            for (Edge e : getIncoming())
            {
                nodes.add(e.src);
            }
            return nodes;
        }

        public Node(String id) { this.id = id; }

        public String getId() { return id; }
        public float getX() { return x; }
        public float getY() { return y; }
    }

    public static class Edge
    {
        private String id;
        private String predicate;
        private Node src;
        private Node dest;

        public String getId() { return id; }
        public Node getDest() { return dest; }
        public Node getSrc() { return src; }
        public String getPredictate() { return predicate; }

        public Edge(String id, Node src, Node dest, String predicate)
        {
            this.id = id;
            this.src = src;
            this.dest = dest;
            this.predicate = predicate;
        }

        @Override
        public String toString()
        {
            return  "Edge: " + src.getId() + " " + predicate + " " + dest.getId();
        }
    }

    Map<String, Node> nodes = new HashMap<String, Node>();
    List<Edge> edges = new ArrayList<Edge>();

    public void createEdge(String id, Node src, Node dest, String predicate)
    {
        Edge e = new Edge (id, src, dest, predicate);
        edges.add(e);
        dest.incoming.add(e);
        src.outgoing.add(e);		
    }

    public Node createOrGetNode (String name)
    {
        if (nodes.containsKey(name))
        {
            return nodes.get(name);
        }
        else
        {
            Node n = new Node(name);
            nodes.put (name, n);
            return n;
        }
    }

    public Collection<Node> getNodes() { return nodes.values(); }
    public Collection<Edge> getEdges() { return edges; }
}
