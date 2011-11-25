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
import org.sbgn.bindings.Point;

public class Network
{
    public static class Node
    {
        private String id;
        float x = 0;
        float y = 0;
        float w = 0;
        float h = 0;
        
        private List<Edge> outgoing = new ArrayList<Edge>();
        private List<Edge> incoming = new ArrayList<Edge>();

        public List<Edge> getOutgoing() { return outgoing; }
        public List<Edge> getIncoming() { return incoming; }

        public void setPos(float x, float y) {
            this.x = x;
            this.y = y;
        }
        
        public void setSize(float w, float h) {
            this.w = w;
            this.h = h;
        }

        public Iterable<Node> getOutgoingNodes() {
            List<Node> nodes = new ArrayList<Node>();
            for (Edge e : getOutgoing())
            {
                nodes.add(e.dest);
            }
            return nodes;
        }

        public Iterable<Node> getIncomingNodes() {
            List<Node> nodes = new ArrayList<Node>();
            for (Edge e : getIncoming()) {
                nodes.add(e.src);
            }
            return nodes;
        }

        public Node(String id) { this.id = id; }

        public String getId() { return id; }
        public float getX() { return x; }
        public float getY() { return y; }
        public float getW() { return w; }
        public float getH() { return h; }
    }

    public static class Edge
    {
        private String id;
        private String predicate;
        private Node src;
        private Node dest;
        private ArrayList<Point> pts;

        public String getId() { return id; }
        public Node getDest() { return dest; }
        public Node getSrc() { return src; }
        public String getPredictate() { return predicate; }
        
        public void addPoint(float x, float y) {
            Point pt = new Point();
            pt.setX(x);
            pt.setY(y);
            pts.add(pt);
        }
        public float getX(int index) {
            if (index == -1) {
                index = pts.size() - 1;
            }
            return (pts.get(index)).getX(); 
        }
        public float getY(int index) { 
            if (index == -1) {
                index = pts.size() - 1;
            }
            return (pts.get(index)).getY(); 
        }

        public Edge(String id, Node src, Node dest, String predicate) {
            this.id = id;
            this.src = src;
            this.dest = dest;
            this.predicate = predicate;
            this.pts = new ArrayList<Point>();
        }

        @Override
        public String toString() {
            return  "Edge: " + src.getId() + " " + predicate + " " + dest.getId();
        }
    }

    Map<String, Node> nodes = new HashMap<String, Node>();
    List<Edge> edges = new ArrayList<Edge>();

    public void createEdge(String id, Node src, Node dest, String predicate) {
        Edge e = new Edge (id, src, dest, predicate);
        edges.add(e);
        dest.incoming.add(e);
        src.outgoing.add(e);		
    }

    public Node createOrGetNode (String name) {
        if (nodes.containsKey(name)) {
            return nodes.get(name);
        }
        else {
            Node n = new Node(name);
            nodes.put (name, n);
            return n;
        }
    }

    public Collection<Node> getNodes() { return nodes.values(); }
    public Collection<Edge> getEdges() { return edges; }
    
    public Node getNodeByName(String name) {
        return nodes.get(name);
    }
    
    public void resetAllEdgePoints() {
        for (Edge edge : getEdges()) {
            edge.pts = new ArrayList<Point>();
        }
    }
    
    public void updateEdges() {
        // TODO: proper edge-node crossing routine
        for (Edge edge : getEdges()) {
            edge.pts = new ArrayList<Point>();
            Node src = edge.getSrc();
            Node dest = edge.getDest();
            edge.addPoint(src.getX()+src.getW()/2, src.getY()+src.getH()/2);
            edge.addPoint(dest.getX()+dest.getW()/2, dest.getY()+dest.getH()/2);
        }
    }
}
