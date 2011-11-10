/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SbgnLayout;

import SbgnLayout.Network.Edge;
import SbgnLayout.Network.Node;
import com.mxgraph.layout.hierarchical.model.mxGraphHierarchyEdge;
import com.mxgraph.layout.hierarchical.model.mxGraphHierarchyNode;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import org.apache.commons.collections15.map.LinkedMap;
import org.apache.commons.collections15.map.MultiKeyMap;

/**
 *
 * @author mschu
 */
public class JGraphLayout {
    private mxGraph graph;
    private Network net;
    private HashMap<Node, Object> nodeLookup;
    private MultiKeyMap edgeLookup;
    
    public JGraphLayout(Network net) {
        this.net = net;
        this.graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        try {
            nodeLookup = new HashMap<Node, Object>();
            for (Node node : net.getNodes()) {
                Object graphNode = graph.insertVertex(parent, 
                                                      null, 
                                                      node.getId(), 
                                                      node.getX(),
                                                      node.getY(), 
                                                      node.getW(), 
                                                      node.getH());
                nodeLookup.put(node, graphNode);
            }
            
            edgeLookup = MultiKeyMap.decorate(new LinkedMap());
            for (Edge e : net.getEdges()) {
                Object sourceNode = nodeLookup.get(e.getSrc());
                Object targetNode = nodeLookup.get(e.getDest());
                Object graphEdge = graph.insertEdge(parent,
                                                    null, 
                                                    e.getId(), 
                                                    sourceNode, 
                                                    targetNode);
                edgeLookup.put(e.getSrc(), e.getDest(), graphEdge);
            }
        }
        finally {
            graph.getModel().endUpdate();
        }  
    }
    
    public void applyHierarchical() {
        mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
        layout.execute(graph.getDefaultParent());
        
        writeLayoutToNetwork(layout);
    }
    
    private void writeLayoutToNetwork(mxHierarchicalLayout layout) {
        Map<Object, mxGraphHierarchyNode> vertexMapper = layout.getModel().getVertexMapper();
        for (Node node : net.getNodes()) {
            Object graphNode = nodeLookup.get(node);
            mxGraphHierarchyNode hn = vertexMapper.get(graphNode);
            node.setPos((float)hn.x[0], (float)hn.y[0]);
        }
        
        Map<Object, mxGraphHierarchyEdge> edgeMapper = layout.getModel().getEdgeMapper();
        for (Edge edge : net.getEdges()) {
            Object graphEdge = edgeLookup.get(edge.getSrc(), edge.getDest());
            List<Object> edges = edgeMapper.get(graphEdge).edges;
            mxGeometry g = ((mxCell)edges.get(0)).getGeometry();
            List<mxPoint> pts = g.getPoints();
            for (mxPoint pt : pts) {
                edge.addPoint((float)pt.getX(), (float)pt.getY());
            }
        }
    }
    
    public void renderGraph() {
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        JFrame frame = new JFrame();
        frame.add(graphComponent);
        frame.pack();
        frame.setVisible(true);
    }
}
