/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SbgnLayout;

import SbgnLayout.Network.Edge;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import javax.swing.JFrame;

/**
 *
 * @author mschu
 */
public class JGraphLayout {
    private mxGraph graph;
    
    public JGraphLayout(Network net) {
        graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        try {
            for (Edge e : net.getEdges()) {
                graph.insertEdge(parent, null, e, e.getSrc(), e.getDest());
            }
        }
        finally {
            graph.getModel().endUpdate();
        }  
    }
    
    public JGraphLayout() { // for testing purposes
        graph = new mxGraph();
        Object parent = graph.getDefaultParent();
        
        graph.getModel().beginUpdate();
        try { // insert: x, y, width, height
            Object v1 = graph.insertVertex(parent, null, "Hello", 20, 20, 80, 30);
            Object v2 = graph.insertVertex(parent, null, "World!", 240, 150, 80, 30);
            graph.insertEdge(parent, null, "", v1, v2);
        }
        finally {
            graph.getModel().endUpdate();
        }        
    }
    
    public void applyHierarchical() {
        mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
        layout.execute(graph.getDefaultParent());
        
        // TODO: write positional information back to network
    }
    
    public void renderGraph() {
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        JFrame frame = new JFrame();
        frame.add(graphComponent);
        frame.pack();
        frame.setVisible(true);
    }
}
