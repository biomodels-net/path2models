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
            kl.applyCoordFile("hsa04210.coords.txt");
        //    kl.applyKeggFile("hsa04210.xml");
            sbgnIO.writeToFile("kegg.sbgn");
            
            ChisioLayout cl1 = new ChisioLayout(sbgnIO.getNetwork(), LayoutAlgorithm.CoSE);
            sbgnIO.writeToFile("chi_cose.sbgn");
            
            ChisioLayout cl2 = new ChisioLayout(sbgnIO.getNetwork(), LayoutAlgorithm.CiSE);
            sbgnIO.writeToFile("chi_cise.sbgn");

            ChisioLayout cl3 = new ChisioLayout(sbgnIO.getNetwork(), LayoutAlgorithm.SixCircular);
            sbgnIO.writeToFile("chi_sixcircular.sbgn");
            
            ChisioLayout cl4 = new ChisioLayout(sbgnIO.getNetwork(), LayoutAlgorithm.Cluster);
            sbgnIO.writeToFile("chi_cluster.sbgn");

            ChisioLayout cl5 = new ChisioLayout(sbgnIO.getNetwork(), LayoutAlgorithm.Sugiyama);
            sbgnIO.writeToFile("chi_sugiyama.sbgn");
            
            ChisioLayout cl6 = new ChisioLayout(sbgnIO.getNetwork(), LayoutAlgorithm.Spring);
            sbgnIO.writeToFile("chi_spring.sbgn");

            JGraphLayout jgf = new JGraphLayout(sbgnIO.getNetwork());
            jgf.applyHierarchical();
            sbgnIO.writeToFile("jg_hierarchical.sbgn");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
