/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SbgnLayout;

import java.io.File;
import org.sbgn.SbgnUtil;

/**
 *
 * @author mschu
 */
public class Main {
    public static void main(String[] args) {
        try {
            File f = new File("two_edges_between_two_activities.sbgn");
            SbgnUtil.readFromFile(f);
            
            SbgnIO sbgnIO = SbgnIO.fromSbgn(f);
            
            ChisioLayout cl = new ChisioLayout(sbgnIO.getNetwork());
            cl.renderGraph();
            sbgnIO.writeToFile(new File("layoutChiLay.sbgn"));

            JGraphLayout jgf = new JGraphLayout(sbgnIO.getNetwork());
            jgf.applyHierarchical();
            jgf.renderGraph();
            sbgnIO.writeToFile(new File("layoutJGraph.sbgn"));

            JUNGLayout jung = new JUNGLayout(sbgnIO.getNetwork());
            jung.applyCircle();
            jung.renderGraph();
            sbgnIO.writeToFile(new File("layoutJung.sbgn"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
