/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SbgnLayout;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
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
    Network net;
    
    public JUNGLayout(Network net) {
        this.net = net;
        graph = new DirectedSparseMultigraph<Node, Edge>();
        
        for (Edge e : net.getEdges()) {
            graph.addEdge(e, e.getSrc(), e.getDest());
        }
    }
    
    public void applyCircle() {
        layout = new CircleLayout(graph);
        
   //     for (Node node : net.getNodes()) {
   //         layout.transform(0);
   //         layout.
   //     }
        
        // TODO: write positional information back to network!!!
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
