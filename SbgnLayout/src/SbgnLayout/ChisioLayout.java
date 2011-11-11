/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SbgnLayout;

import SbgnLayout.Network.Edge;
import SbgnLayout.Network.Node;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;
import java.util.HashMap;
import org.ivis.layout.LEdge;
import org.ivis.layout.LGraph;
import org.ivis.layout.LGraphManager;
import org.ivis.layout.LGraphObject;
import org.ivis.layout.LNode;
import org.ivis.layout.Layout;
import org.ivis.layout.Updatable;
import org.ivis.layout.cose.CoSELayout;
import org.ivis.layout.sgym.SgymLayout;
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
    
    public ChisioLayout(Network net) {
        this.net = net;
        //layout = new CoSELayout();
        layout = new SgymLayout();
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
        
        for (Edge edge : net.getEdges()) {
            LEdge graphEdge = edgeLookup.get(edge.getId());
            edge.addPoint(edge.getSrc().getX(), edge.getSrc().getY());
            for (PointD pt : graphEdge.getBendpoints()) {
                edge.addPoint((float)pt.x, (float)pt.y);
            }
            edge.addPoint(edge.getDest().getX(), edge.getDest().getY());
        }
    }
    
    public void renderGraph() {
        JGraphLayout jgl = new JGraphLayout(net);
        jgl.renderGraph();
    }
}
