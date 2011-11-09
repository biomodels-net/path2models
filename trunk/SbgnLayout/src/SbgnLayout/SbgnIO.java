/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SbgnLayout;

import SbgnLayout.Network.Edge;
import java.io.File;
import javax.xml.bind.JAXBException;
import SbgnLayout.Network.Node;
import java.util.HashMap;
import org.sbgn.SbgnUtil;
import org.sbgn.bindings.Arc;
import org.sbgn.bindings.Arc.End;
import org.sbgn.bindings.Arc.Start;
import org.sbgn.bindings.Bbox;
import org.sbgn.bindings.Glyph;
import org.sbgn.bindings.Sbgn;

/**
 *
 * @author mschu
 */
public class SbgnIO {
    private org.sbgn.bindings.Map map;
    private Network net;
    
    public Network getNetwork() { return net; }
    public org.sbgn.bindings.Map getMap() { return map; }
    
    public static SbgnIO fromSbgn(File in) throws JAXBException {
        Sbgn sbgn = SbgnUtil.readFromFile(in);
        
        SbgnIO sbgnIO = new SbgnIO();
        sbgnIO.map = sbgn.getMap();
        sbgnIO.createNetworkFromMap();
        
        return sbgnIO;
    }
    
    public static SbgnIO fromSbmlQual(File in) throws JAXBException {
        return new SbgnIO();
        // TODO
    }
    
    public void applyNetworkToMap() {
        // get coordinates from network object
        // and set them to the map object // TODO
        
        HashMap<String, Node> lookupNode = new HashMap<String, Node>();
        for (Node n : net.getNodes()) {
            lookupNode.put(n.getId(), n);
        }
        for (Glyph g : map.getGlyph()) {
            Bbox box = g.getBbox();
            Node node = lookupNode.get(g.getId());
            box.setX(node.getX());
            box.setY(node.getY());
        }
        
        // needs to be done via src+tgt bc no ids
        // or: ids addded to edges
        // maybe: calculate (x,y) by (source, target) [this would help with subglyphs]
        //        [would this be redundant w/ layout algos? -> likely only in part]
        /*
        HashMap<String, Edge> lookupEdge = new HashMap<String, Edge>();
        for (Edge e : net.getEdges()) {
            lookupEdge.put(e., e); // ??
        }
        for (Arc a : map.getArc()) {
            Start st = a.getStart();
            st.setX(0);
            st.setY(0);
            
            End en = a.getEnd();
            en.setX(0);
            en.setY(0);
        }*/
    }
    
    private void createNetworkFromMap() {
        net = new Network();
        HashMap<String, Node> lookupNode = new HashMap<String, Node>();
        
        for (Glyph glyph : map.getGlyph()) {
            Node node = net.createOrGetNode(glyph.getId());
            lookupNode.put(node.getId(), node);
        }
        
        for (Arc arc : map.getArc()) {
            Glyph source = (Glyph)arc.getSource();
            Glyph target = (Glyph)arc.getTarget();
            net.createEdge(arc.getId(),
                           lookupNode.get(source.getId()),
                           lookupNode.get(target.getId()), "0");
        }
    }
    
/*    public void applyLayoutAndWrite(File out) throws JAXBException {
        int autoid = 0;

        Sbgn sbgn = new Sbgn();
        sbgn.setMap(map);
        map.setLanguage(Language.AF.getName());

        Map<Node, Glyph> glyphMap = new HashMap <Node, Glyph>();

        for (Node n : net.getNodes())
        {
            Glyph g = new Glyph();
            Bbox b = new Bbox();
            b.setW(30);
            b.setH(30);
            b.setX((float)n.getX());
            b.setY((float)n.getY());
            g.setBbox(b);
            g.setClazz(GlyphClazz.BIOLOGICAL_ACTIVITY.getClazz());
            Label lab = new Label();
            lab.setText(n.getId());
            g.setLabel(lab);
            g.setId(n.getId());
            map.getGlyph().add(g);
            glyphMap.put (n, g);
        }

        for (Edge e : net.getEdges())
        {
            Arc a = new Arc();
            a.setClazz(e.getPredictate().equals ("-1") ? 
                       ArcClazz.NEGATIVE_INFLUENCE.getClazz() :
                       ArcClazz.POSITIVE_INFLUENCE.getClazz());
            a.setSource(glyphMap.get(e.getSrc()));
            a.setTarget(glyphMap.get(e.getDest()));
            Start start = new Start();
            start.setX((float)e.getSrc().getX());
            start.setY((float)e.getSrc().getY());
            a.setStart(start);
            End end = new End();
            end.setX((float)e.getDest().getX());
            end.setY((float)e.getDest().getY());
            a.setEnd(end);
            a.setId("id" + autoid++);
            map.getArc().add(a);
        }

        SbgnUtil.writeToFile(sbgn, out);
    }*/
}
