/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SbgnLayout;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import java.awt.Dimension;
import javax.swing.JFrame;
import SbgnLayout.Network.Node;
import SbgnLayout.Network.Edge;

/**
 *
 * @author mschu
 */
public class JUNGLayout {
    DirectedSparseMultigraph<Node, Edge> graph;
    Layout<Integer, String> layout;
    
    public JUNGLayout(Network net) {
        graph = new DirectedSparseMultigraph<Node, Edge>();
        
        for (Edge e : net.getEdges()) {
            graph.addEdge(e, e.getSrc(), e.getDest());
        }
    }

    public JUNGLayout() { // for testing purposes
        graph = new DirectedSparseMultigraph<Node, Edge>();
        // Create some MyNode objects to use as vertices
        Node n1 = new Node("1");
        Node n2 = new Node("2");
        Node n3 = new Node("3");
        Node n4 = new Node("4");
        Node n5 = new Node("5"); // note n1-n5 declared elsewhere.
        // Add some directed edges along with the vertices to the graph
        graph.addEdge(new Edge(n1, n2, "") ,n1, n2, EdgeType.DIRECTED);
        graph.addEdge(new Edge(n2, n3, ""), n2, n3, EdgeType.DIRECTED);
        graph.addEdge(new Edge(n3, n5, ""), n3, n5, EdgeType.DIRECTED);
        graph.addEdge(new Edge(n5, n4, ""), n5, n4, EdgeType.DIRECTED);
        graph.addEdge(new Edge(n4, n2, ""), n4, n2); // from, to
        graph.addEdge(new Edge(n3, n1, ""), n3, n1); // DIRECTED is implicit
        graph.addEdge(new Edge(n2, n5, ""), n2, n5);
    }
    
    public void applyCircle() {
        layout = new CircleLayout(graph);
        
        // TODO: write positional information back to network
    }
    
    public void renderGraph() {
        // The BasicVisualizationServer<V,E> is parameterized by the edge types
        layout.setSize(new Dimension(300,300)); // sets the initial size of the space
        BasicVisualizationServer<Integer,String> vv = new BasicVisualizationServer<Integer,String>(layout);
        vv.setPreferredSize(new Dimension(350,350)); //Sets the viewing area size
        
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);
    }
}
