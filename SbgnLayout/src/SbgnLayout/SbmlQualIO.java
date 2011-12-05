/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SbgnLayout;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.ext.layout.BoundingBox;
import org.sbml.jsbml.ext.layout.Dimensions;
import org.sbml.jsbml.ext.layout.ExtendedLayoutModel;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;
import org.sbml.jsbml.ext.qual.Input;
import org.sbml.jsbml.ext.qual.Output;
import org.sbml.jsbml.ext.qual.QualConstant;
import org.sbml.jsbml.ext.qual.QualitativeModel;
import org.sbml.jsbml.ext.qual.QualitativeSpecies;
import org.sbml.jsbml.ext.qual.Transition;

/**
 *
 * @author mschu
 */
public class SbmlQualIO {
    private Model baseModel;
    private QualitativeModel qualModel;
    
    public SbmlQualIO(String filename) throws XMLStreamException, IOException {
        SBMLReader sbmlRead = new SBMLReader();
        SBMLDocument sbmlDoc = sbmlRead.readSBMLFromFile(filename);
        baseModel = sbmlDoc.getModel();
        qualModel = (QualitativeModel)baseModel.getExtension(QualConstant.namespaceURI);
    }
    
    public Sbgn createSBGN() {     
        Sbgn sbgn = new Sbgn();		
        Map map = new Map();
        sbgn.setMap(map);
        List<Glyph> glyphs = map.getGlyph();
        
        ListOf<QualitativeSpecies> qualSpeciesList = qualModel.getListOfQualitativeSpecies();
        ArrayList<String> qsNameList = new ArrayList<String>();
        for (QualitativeSpecies qs : qualSpeciesList) {
            qsNameList.add(qs.getName());
        }
        HashMap<String, BoundingBox> layoutLookup = getLayoutMapOrNull(qsNameList);
        
        HashMap<String, Glyph> glyphLookup = new HashMap<String, Glyph>();
        for (QualitativeSpecies qs : qualSpeciesList) {            
            Glyph glyph = new Glyph();
            glyph.setId(qs.getId());
            glyph.setClazz(sbo2GlyphClazz(qs.getSBOTerm()));
            
            Label label = new Label();
            label.setText(qs.getName());
            glyph.setLabel(label);
            
            Bbox bbox = new Bbox();
            if (layoutLookup != null) {
                BoundingBox box = layoutLookup.get(qs.getName());
                org.sbml.jsbml.ext.layout.Point pt = box.getPosition();
                bbox.setX((float)pt.getX());
                bbox.setY((float)pt.getY());
                Dimensions dim = box.getDimensions();
                bbox.setH((float)dim.getHeight());
                bbox.setW((float)dim.getWidth());
            } else {
                bbox.setX(0);
                bbox.setY(0);
                Point pt = label2Size(qs.getName());
                bbox.setW(pt.getX());
                bbox.setH(pt.getY());
            }
            glyph.setBbox(bbox);
            
            glyphLookup.put(glyph.getId(), glyph);
            glyphs.add(glyph);
        }
        
        for (Transition trans : qualModel.getListOfTransitions()) {
            for (Output output : trans.getListOfOutputs()) {
                for (Input input : trans.getListOfInputs()) {
                    Arc arc = new Arc();
                    arc.setSource(glyphLookup.get(input.getQualitativeSpecies()));
                    arc.setTarget(glyphLookup.get(output.getQualitativeSpecies()));
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
                }
            }
        }
        
        return sbgn;
    }
    
    private HashMap<String, BoundingBox> getLayoutMapOrNull(ArrayList<String> qsNameList) {
        final String LAYOUT_NS = "http://www.sbml.org/sbml/level3/version1/layout/version1";
        ExtendedLayoutModel layout = (ExtendedLayoutModel)baseModel.getExtension(LAYOUT_NS);
        if (!layout.isSetListOfLayouts()) {
            return null;
        }
        
        Layout l = layout.getLayout(0);
        HashMap<String, BoundingBox> layoutMap = new HashMap<String, BoundingBox>();
        for (SpeciesGlyph sg: l.getListOfSpeciesGlyphs()) {
            if (sg.isSetBoundingBox()) {
                layoutMap.put(sg.getSpecies(), sg.getBoundingBox());
            }
        }
        
        for (String qs : qsNameList) {
            if (!layoutMap.keySet().contains(qs)) {
                return null;
            }
        }
        return layoutMap;
    }
    
    private Point label2Size(String label) {
        Font font = new Font("Arial", Font.PLAIN, 10);
        BufferedImage bi = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        FontMetrics metrics = bi.getGraphics().getFontMetrics(font);

        
        String lines[] = label.split("&#xA;");
        int width = 0, height = lines.length*metrics.getHeight() + 4;
        
        for (String line : lines) {
            int cur = metrics.stringWidth(line);
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
