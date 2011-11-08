/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SbgnLayout;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBException;
import SbgnLayout.Network.Edge;
import SbgnLayout.Network.Node;
import org.sbgn.ArcClazz;
import org.sbgn.GlyphClazz;
import org.sbgn.Language;
import org.sbgn.SbgnUtil;
import org.sbgn.bindings.Arc;
import org.sbgn.bindings.Arc.End;
import org.sbgn.bindings.Arc.Start;
import org.sbgn.bindings.Bbox;
import org.sbgn.bindings.Glyph;
import org.sbgn.bindings.Label;
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
        // and set them to the map object
    }
    
    private void createNetworkFromMap() {
        net = new Network();
        
        for (Glyph glyph : map.getGlyph()) {
            net.createOrGetNode(glyph.getId());
        }
        
        for (Arc arc : map.getArc()) {
            net.createEdge((Node)arc.getSource(), (Node)arc.getTarget(), "0");
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
