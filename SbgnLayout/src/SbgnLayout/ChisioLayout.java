/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SbgnLayout;

import SbgnLayout.Network.Edge;
import SbgnLayout.Network.Node;
import java.util.HashMap;
import org.ivis.layout.LEdge;
import org.ivis.layout.LGraph;
import org.ivis.layout.LGraphManager;
import org.ivis.layout.LGraphObject;
import org.ivis.layout.LNode;
import org.ivis.layout.Layout;
import org.ivis.layout.Updatable;
import org.ivis.layout.cise.CiSELayout;
import org.ivis.layout.cluster.ClusterLayout;
import org.ivis.layout.cose.CoSELayout;
import org.ivis.layout.fd.FDLayout;
import org.ivis.layout.sgym.SgymLayout;
import org.ivis.layout.six.SixCircularLayout;
import org.ivis.layout.spring.SpringLayout;
import org.ivis.util.PointD;

/**
 *
 * @author mschu
 */
public class ChisioLayout {
    
    Network net;
    Layout layout;
    private HashMap<String, LNode> nodeLookup;
    private HashMap<String, LEdge> edgeLookup;
    
    public class Label implements Updatable {
        String label;
        Label(String label) {
            this.label = label;
        }
        @Override public void update(LGraphObject lGraphObj) {
            lGraphObj.label = label;
        }
    }
    
    public enum LayoutAlgorithm { CoSE, CiSE, Cluster, Sugiyama, SixCircular, Spring }
    
    public ChisioLayout(Network net, LayoutAlgorithm alg) {
        this.net = net;
        switch (alg) {
            case CoSE: layout = new CoSELayout();
            case CiSE: layout = new CiSELayout();
            case Cluster: layout = new ClusterLayout();
            case Sugiyama: layout = new SgymLayout();
            case SixCircular: layout = new SixCircularLayout();
            case Spring: layout = new SpringLayout();
        }
        LGraphManager gm = layout.getGraphManager();
        LGraph g1 = gm.addRoot();
        
        nodeLookup = new HashMap<String, LNode>();
        for (Node node : net.getNodes()) {
            String id = node.getId();
            LNode ln = g1.add(layout.newNode(new Label(id)));
            ln.setWidth(node.getW());
            ln.setHeight(node.getW());
            nodeLookup.put(id, ln);
        }

        edgeLookup = new HashMap<String, LEdge>();
        for (Edge edge : net.getEdges()) {
            LNode src = nodeLookup.get(edge.getSrc().getId());
            LNode dest = nodeLookup.get(edge.getDest().getId());
            
            String id = edge.getId();
            edgeLookup.put(id, g1.add(layout.newEdge(new Label(id)), src, dest));
        }
        
        layout.runLayout();
        writeLayoutToNetwork();
    }
    
    private void writeLayoutToNetwork() {
        for (Node node : net.getNodes()) {
            LNode graphNode = nodeLookup.get(node.getId());
            float x = (float)graphNode.getLeft();
            float y = (float)graphNode.getTop();
            node.setPos(x, y);
        }
        
        net.resetAllEdgePoints();
        for (Edge edge : net.getEdges()) {
            LEdge graphEdge = edgeLookup.get(edge.getId());
            edge.addPoint(edge.getSrc().getX(), edge.getSrc().getY());
            for (PointD pt : graphEdge.getBendpoints()) {
                edge.addPoint((float)pt.x, (float)pt.y);
            }
            edge.addPoint(edge.getDest().getX(), edge.getDest().getY());
        }
        
        JGraphLayout jgl = new JGraphLayout(net);
        jgl.writeEdgesToNetwork();
    }
    
    public void renderGraph() {
        JGraphLayout jgl = new JGraphLayout(net);
        jgl.renderGraph();
    }
}