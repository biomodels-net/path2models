/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SbgnLayout;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import SbgnLayout.Network.Node;
import SbgnLayout.Network.Edge;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import java.awt.Dimension;
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
    
    public void applySpring() {
        layout = new SpringLayout(graph);
        layout.setSize(new Dimension(500,500));
        writeLayoutToNetwork();
    }
    
    private void writeLayoutToNetwork() {
        for (Node node : net.getNodes()) {
            Point2D coord = layout.transform(node);
            node.setPos((float)coord.getX(), (float)coord.getY());
        }
        
        net.resetAllEdgePoints();
        for (Edge edge : net.getEdges()) {
            Pair<Node> pts = graph.getEndpoints(edge);
            Node start = pts.getFirst();
            edge.addPoint(start.getX(), start.getY());
            Node end = pts.getSecond();
            edge.addPoint(start.getX(), start.getY());
        }
    }
    
    public void renderGraph() {
        BasicVisualizationServer<Node, Point2D> vv = new BasicVisualizationServer<Node, Point2D>(layout);
        vv.setPreferredSize(layout.getSize());
        
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);
    }
}
