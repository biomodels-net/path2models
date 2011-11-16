/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SbgnLayout;

import SbgnLayout.Network.Edge;
import java.io.File;
import javax.xml.bind.JAXBException;
import SbgnLayout.Network.Node;
import java.io.IOException;
import java.util.HashMap;
import javax.xml.stream.XMLStreamException;
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
    
    public static SbgnIO fromSbmlQual(String in) throws IOException, XMLStreamException {
        SbmlQualIO qualIO = new SbmlQualIO(in);
        Sbgn sbgn = qualIO.createSBGN();
        
        SbgnIO sbgnIO = new SbgnIO();
        sbgnIO.map = sbgn.getMap();
        sbgnIO.createNetworkFromMap();
        
        return sbgnIO;
    }
    
    public void writeToFile(File out) throws JAXBException {
        HashMap<String, Node> lookupNode = new HashMap<String, Node>();
        for (Node n : net.getNodes()) {
            lookupNode.put(n.getId(), n);
        }
        for (Glyph g : map.getGlyph()) {
            Bbox box = g.getBbox();
            Node node = lookupNode.get(g.getId());
            box.setX(node.getX());
            box.setY(node.getY());
            box.setW(node.getW());
            box.setH(node.getH());
        }
        
        HashMap<String, Edge> lookupEdge = new HashMap<String, Edge>();
        for (Edge e : net.getEdges()) {
            lookupEdge.put(e.getId(), e);
        }
        for (Arc a : map.getArc()) {
            Edge edge = lookupEdge.get(a.getId());
            
            Start st = a.getStart();
            st.setX(edge.getX(0));
            st.setY(edge.getY(0));
            
            End en = a.getEnd();
            en.setX(edge.getX(-1));
            en.setY(edge.getY(-1));
        }
        
        Sbgn sbgn = new Sbgn();
        sbgn.setMap(this.map);
        SbgnUtil.writeToFile(sbgn, out);
    }
    
    private void createNetworkFromMap() {
        net = new Network();
        HashMap<String, Node> lookupNode = new HashMap<String, Node>();
        
        for (Glyph glyph : map.getGlyph()) {
            Node node = net.createOrGetNode(glyph.getId());
            Bbox box = glyph.getBbox();
            
            node.setPos(box.getX(), box.getY());
            node.setSize(box.getW(), box.getH());
            
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
