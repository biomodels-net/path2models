/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SbgnLayout;

import SbgnLayout.ChisioLayout.LayoutAlgorithm;

/**
 *
 * @author mschu
 */
public class Main {
    public static void main(String[] args) {
        try {
            SbgnIO sbgnIO = SbgnIO.fromSbmlQual("hsa04210.sbml.xml");
            
            KeggLayout kl = new KeggLayout(sbgnIO.getNetwork());
        //    kl.applyCoordFile("hsa04210.coords.txt");
        //    kl.applyKeggFile("hsa04210.xml");
            sbgnIO.writeToFile("kegg.sbgn");
            
            ChisioLayout cl5 = new ChisioLayout(sbgnIO.getNetwork(), LayoutAlgorithm.Sugiyama);
            sbgnIO.writeToFile("chi_sugiyama.sbgn");

        //    JGraphLayout jgf = new JGraphLayout(sbgnIO.getNetwork());
        //    jgf.applyHierarchical();
        //    sbgnIO.writeToFile("jg_hierarchical.sbgn");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
