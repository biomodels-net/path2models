/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SbgnLayout;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import SbgnLayout.Network.Node;
import SbgnLayout.Network.Edge;
import edu.uci.ics.jung.algorithms.layout.DAGLayout;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import java.awt.geom.Point2D;
import javax.swing.JFrame;

/**
 *
 * @author mschu
 */
public class JUNGLayout {
    private DirectedSparseMultigraph<Node, Edge> graph;
    private Layout<Node, Point2D> layout;
    private Network net;
    
    public JUNGLayout(Network net) {
        this.net = net;
        graph = new DirectedSparseMultigraph<Node, Edge>();
        
        for (Edge e : net.getEdges()) {
            graph.addEdge(e, e.getSrc(), e.getDest());
        }
    }
    
    public void applyDAG() {
        layout = new DAGLayout(graph);
        writeLayoutToNetwork();
    }
    
    private void writeLayoutToNetwork() {
        // TODO: write positional information back to network

        for (Node node : net.getNodes()) {
            Point2D coord = layout.transform(node);
            node.setPos((float)coord.getX(), (float)coord.getY());
        }
        
        // TODO: do the same for the edges
    }
    
    public void renderGraph() {
        //layout.setSize(new Dimension(300,300)); // sets the initial size of the space
        BasicVisualizationServer<Node, Point2D> vv = new BasicVisualizationServer<Node, Point2D>(layout);
        //vv.setPreferredSize(new Dimension(350,350)); //Sets the viewing area size
        
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);
    }
}
