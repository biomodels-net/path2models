/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SbgnLayout;

import SbgnLayout.Network.Edge;
import SbgnLayout.Network.Node;
import com.mxgraph.layout.hierarchical.model.mxGraphHierarchyNode;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;

/**
 *
 * @author mschu
 */
public class JGraphLayout {
    private mxGraph graph;
    private Network net;
    private HashMap<Node, Object> nodeLookup;
    
    public JGraphLayout(Network net) {
        this.net = net;
        this.graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        try {
            this.nodeLookup = new HashMap<Node, Object>();
            for (Node node : net.getNodes()) {
                // insert: name, x, y, w, h
                Object graphNode = graph.insertVertex(parent, null, node.getId(), 0, 0, 0, 0);
                nodeLookup.put(node, graphNode);
            }
            
            for (Edge e : net.getEdges()) {
                Object sourceNode = nodeLookup.get(e.getSrc());
                Object targetNode = nodeLookup.get(e.getDest());
                graph.insertEdge(parent, null, e.getId(), sourceNode, targetNode);
            }
        }
        finally {
            graph.getModel().endUpdate();
        }  
    }
    
    public void applyHierarchical() {
        mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
        layout.execute(graph.getDefaultParent());
        
        // write layout information back to network
        Map<Object, mxGraphHierarchyNode> vertexMapper = layout.getModel().getVertexMapper();
        for (Node node : this.net.getNodes()) {
            Object graphNode = nodeLookup.get(node);
            mxGraphHierarchyNode hn = vertexMapper.get(graphNode);
            node.setPos((float)hn.x[0], (float)hn.y[0]);
        }
        
        // TODO: do the same for the edges
    }
    
    public void renderGraph() {
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        JFrame frame = new JFrame();
        frame.add(graphComponent);
        frame.pack();
        frame.setVisible(true);
    }
}
