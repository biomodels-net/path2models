/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SbgnLayout;

import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;

/**
 *
 * @author mschu
 */
public class Main {
    public static void main(String[] args) {
        try {
            // File f = new File("two_edges_between_two_activities.sbgn");
            // SbgnIO sbgnIO = SbgnIO.fromSbgn(f);
            
            SbgnIO sbgnIO = SbgnIO.fromSbmlQual("hsa04210.sbml.xml");
            
            KeggLayout kl = new KeggLayout(sbgnIO.getNetwork());
            kl.applyKeggFile("hsa04210.xml");
            kl.renderGraph();
            sbgnIO.writeToFile("layoutKegg.sbgn");
            
            ChisioLayout cl = new ChisioLayout(sbgnIO.getNetwork());
            cl.renderGraph();
            sbgnIO.writeToFile("layoutChiLay.sbgn");

            JGraphLayout jgf = new JGraphLayout(sbgnIO.getNetwork());
            jgf.applyHierarchical();
            jgf.renderGraph();
            sbgnIO.writeToFile("layoutJGraph.sbgn");

            JUNGLayout jung = new JUNGLayout(sbgnIO.getNetwork());
            jung.applyCircle();
            jung.renderGraph();
            sbgnIO.writeToFile("layoutJung.sbgn");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
