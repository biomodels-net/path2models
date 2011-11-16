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
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import java.util.HashMap;
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
        if (layout.getModel() == null) { // if layout on empty network
            return;
        }
        
        Map<Object, mxGraphHierarchyNode> vertexMapper = layout.getModel().getVertexMapper();
        for (Node node : net.getNodes()) {
            Object graphNode = nodeLookup.get(node);
            mxGraphHierarchyNode hn = vertexMapper.get(graphNode);
            node.setPos((float)hn.x[0]-node.getW()/2, (float)hn.y[0]-node.getH()/2);
        }
        
        Map<Object, mxGraphHierarchyEdge> edgeMapper = layout.getModel().getEdgeMapper();
        for (Edge edge : net.getEdges()) {
            Object graphEdge = edgeLookup.get(edge.getSrc(), edge.getDest());
            mxCellState state = (mxCellState)graph.getView().getState(graphEdge);
            for (mxPoint pt : state.getAbsolutePoints()) {
                edge.addPoint((float)pt.getX(), (float)pt.getY());
            }
        }
    }
    
    public void renderGraph() {
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(graphComponent);
        frame.pack();
        frame.setVisible(true);
    }
}
