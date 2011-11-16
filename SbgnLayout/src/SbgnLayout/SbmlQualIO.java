/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SbgnLayout;

import java.awt.Font;
import java.awt.FontMetrics;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.xml.stream.XMLStreamException;
import org.sbgn.ArcClazz;
import org.sbgn.GlyphClazz;
import org.sbgn.bindings.Arc;
import org.sbgn.bindings.Arc.End;
import org.sbgn.bindings.Arc.Start;
import org.sbgn.bindings.Bbox;
import org.sbgn.bindings.Glyph;
import org.sbgn.bindings.Label;
import org.sbgn.bindings.Map;
import org.sbgn.bindings.Point;
import org.sbgn.bindings.Sbgn;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.ext.qual.Input;
import org.sbml.jsbml.ext.qual.Output;
import org.sbml.jsbml.ext.qual.QualitativeModel;
import org.sbml.jsbml.ext.qual.QualitativeSpecies;
import org.sbml.jsbml.ext.qual.Transition;

/**
 *
 * @author mschu
 */
public class SbmlQualIO {
    
    private QualitativeModel model;
    
    public SbmlQualIO(String filename) throws XMLStreamException, IOException {
        SBMLReader sbmlRead = new SBMLReader();
        SBMLDocument sbmlDoc = sbmlRead.readSBMLFromFile(filename);
        model = new QualitativeModel(sbmlDoc.getModel());
    }
    
    public Sbgn createSBGN() {
        Sbgn sbgn = new Sbgn();		
        Map map = new Map();
        sbgn.setMap(map);
        List<Glyph> glyphs = map.getGlyph();
        
        HashMap<String, String> qualId2Name = new HashMap<String, String>();
        for (QualitativeSpecies qs : model.getListOfQualitativeSpecies()) {
            qualId2Name.put(qs.getId(), qs.getName());
            
            Glyph glyph = new Glyph();
            glyph.setId(qs.getId());
            glyph.setClazz(sbo2GlyphClazz(qs.getSBOTerm()));
            
            Label label = new Label();
            label.setText(qs.getName());
            glyph.setLabel(label);
            
            Bbox bbox = new Bbox();
            bbox.setX(0);
            bbox.setY(0);
            Point pt = label2Size(qs.getName());
            bbox.setW(pt.getX());
            bbox.setH(pt.getY());
            glyph.setBbox(bbox);
            
            glyphs.add(glyph);
        }
        
        for (Transition trans : model.getListOfTransitions()) {
            for (Output output : trans.getListOfOutputs()) {
                for (Input input : trans.getListOfInputs()) {
                    Arc arc = new Arc();
                    arc.setSource(input.getId());
                    arc.setTarget(output.getId());
                    arc.setClazz(sign2ArcClazz(input.getSign().toString()));
                    
                    Start start = new Start();
                    start.setX(0);
                    start.setY(0);
                    arc.setStart(start);
                    End end = new End();
                    end.setX(0);
                    end.setY(0);
                    arc.setEnd(end);
                    
                    arc.setId(trans.getId() + "_" + input.getId() + "_" + output.getId());
                    map.getArc().add(arc);
		/*	a.setClazz(
				e.getPredictate().equals ("-1") ? 
					ArcClazz.NEGATIVE_INFLUENCE.getClazz() :
					ArcClazz.POSITIVE_INFLUENCE.getClazz()
				);
			a.setSource(input.getId());
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
			map.getArc().add(a);*/
                }
            }
        }
        
        return sbgn;
    }
    
    private Point label2Size(String label) {
        String lines[] = label.split("&#xA;");
        int width = 0, height = lines.length*10 + 4;
        
        Font font = new Font("Verdana", Font.PLAIN, 10);  
        FontMetrics metrics = new FontMetrics(font) {};
        
        for (String line : lines) {
            int cur = SwingUtilities.computeStringWidth(metrics, line) + 6;
            if (cur > width) {
                width = cur;
            }
        }
        
        Point pt = new Point();
        pt.setX(width + 20 - (width % 20));
        pt.setY(height + 10 - (height % 10));
        return pt;
    }
    
    private String sbo2GlyphClazz(int id) {
        switch (id) {
            case 253:
                return GlyphClazz.COMPLEX.getClazz();
            case 245:
                return GlyphClazz.MACROMOLECULE.getClazz();
            case 354:
                return GlyphClazz.NUCLEIC_ACID_FEATURE.getClazz();
            case 247:
                return GlyphClazz.SIMPLE_CHEMICAL.getClazz();
            default:
                return GlyphClazz.UNSPECIFIED_ENTITY.getClazz();
        }
    }
    
    private String sign2ArcClazz(String sign) {
        if (sign.equals("positive")) {
            return ArcClazz.POSITIVE_INFLUENCE.getClazz();
        }
        else if (sign.equals("negative")) {
            return ArcClazz.NEGATIVE_INFLUENCE.getClazz();
        }
        else {
            return ArcClazz.UNKNOWN_INFLUENCE.getClazz();
        }
    }
}
