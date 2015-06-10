# DISCLAIMER:
**The initial phase of the Path2Models project is over.** People interested in the outcome can visit: <http://www.ebi.ac.uk/biomodels-main/path2models> and/or read the following publication:

Finja Büchel, Nicolas Rodriguez, Neil Swainston, Clemens Wrzodek, Tobias Czauderna, Roland Keller, Florian Mittag, Michael Schubert, Mihai Glont, Martin Golebiewski, Martijn van Iersel, Sarah Keating, Matthias Rall, Michael Wybrow, Henning Hermjakob, Michael Hucka, Douglas B Kell, Wolfgang Müller, Pedro Mendes, Andreas Zell, Claudine Chaouiya, Julio Saez-Rodriguez, Falk Schreiber, Laibe, Camille, Andreas Dräger and Nicolas Le Novère
**[Path2Models: large-scale generation of computational models from biochemical pathway maps](http://www.biomedcentral.com/1752-0509/7/116).** _BMC Systems Biology_ 2013, 7:116.

\[[BioMed Central link](http://www.biomedcentral.com/1752-0509/7/116)\] \[[Europe PMC link](http://europepmc.org/abstract/MED/24180668)\]


**You will find below a short description of the project.**

# Systematic creation of quantitative models from pathway databases

## Background ##

The amount of information gathered by high-throughput functional genomics and systems biology projects led to genome-wide knowledge about biochemical networks. In parallel to the “traditional” focussed bottom-up detailed mechanistic models developed in computational systems biology, the need is therefore growing to develop top-down models, starting from the pathways. Examples of such models are the constraint-based models of metabolic networks and the logical models of signalling pathways and gene regulatory networks.

In order to develop those models, a common starting point are qualitative “pathways”, such as those distributed by KEGG, Metacyc or Reactome. Because of its size and the ease of retrieving information, KEGG Pathways has been one of the main resources for such a purpose. Users generally convert the KEGG files into SBML ones, that are then used in one of the >230 software supporting the format. Several tools have been developed to convert KEGG format (KGML) to SBML, including KEGG2SBML, KEGGTranslator and Subliminal.

Unfortunately, the KEGG federation of databases became commercial on July 1st.2011. However, prior to this date, the content of KEGG Pathways had been downloaded by several of collaborators. Neil Swainston from the Manchester Centre of Integrative Systems Biology and Andreas Dräger from the University of Tübingen started to build pipeline to process and enrich KEGG files automatically. One of the problems with these conversions is that while they can convert adequately metabolic networks, the conversion of signalling pathways is lossy . Indeed KEGG signalling pathways are influence graphs (or activity flow diagrams) rather than process descriptions. There is no notion of “reaction”, “reactant” and “product” and the result is a list of nodes without arcs.

## Project ##

The purpose of the project is to systematically generate mathematical models corresponding to the entire KEGG pathways, augment them with information coming from other data resources, and submit them to BioModels Database (after publication).

All information coming from the KEGG pathways will be checked for consistency and corrected (for mass balance, duplications etc.). The network will be completed by information coming from MetaCyc. We will automatically produce SBML descriptions for all pathways, and enrich them with cross-references (using Identifiers.org). This will be performed with the software Subliminal (Swainston) and KEGGtranslator (Wrzodek et al 2011). In order to properly encode the signalling pathways in SBML, we will finalise the package “qual” of SBML Level 3, encoding qualitative models (Berenguier et al), and implement support in JSBML, the native Java SBML API (Dräger et al 2011).

SBGN maps will be generated, and enriched with data fetched using the cross-references.  In order to properly represent the signalling pathways in SBGN, we will finalise the support of the Activity Flow language in libSBGN, as well as support for dynamic annotations.

Mathematical models will then be generated for as many pathways as possible. For the metabolic networks, we will create constraint-based models using the methods developed by Manchester and Tuebingen (Dräger et al 2008). For the signalling pathways, we will create logical models using the CoLoMoTo tool suite coordinated by Claudine Chaouiya, and in particular CellNetOptimizer developed by Julio (Saez-Rodriguez et al 2009). Some previous (Saez-Rodriguez et al 2011) and ongoing studies for which Julio's group had experimental data will be re-run in CellNetOptimizer to evaluate the impact of the improved prior knowledge.

A new branch of BioModels Database will be deployed, serving the few thousands resulting models, as well as the enriched SBGN visual maps. We will also make the enriched visual maps available for community feedback through the collaborative WikiPathways website (Pico et al 2008). BioModels Database automatically produces BioPAX from SBML. This will allow us to compare the coverage and connectivity of our pathways compared to other resources such as Pathway Commons.

The resulting resource will have an important impact on the existing research projects from the group of Nicolas (Reconstruction of synaptic signalling for SynSys, phosphoinositide signalling with the Babraham) and Julio (virtually all projects, but in particular modelling phosphoinositol signalling and endocytosis, with C. Schultz at EMBL-HD; analysis of proteomic deregulation in diabetes with ETH-Zuerich; characterizing drug’s  mode of action in cancer with Sanger Institute), but also on any other project using pathways such as Janet's project on insulin signalling and ageing.

In summary this ‘unexpected’ funding opportunity is extremely timely to enable to develop a unique resource that will have profound impact on research at EBI and beyond.

## References ##

Berenguier D, Chaouiya C, Naldi A, Thieffry D. Qualitative models (qual). http://sbml.org/images/6/61/SBML-L3-qual-proposal_2.1.pdf

Dräger A, Hassis N, Supper J, Schröder A, Zell A. SBMLsqueezer: a CellDesigner plug-in to generate kinetic rate equations for biochemical networks. BMC Syst Biol. 2008, 2:39.

Dräger A, Rodriguez N, Dumousseau M, Dörr A, Wrzodek C, Le Novère N, Zell A, Hucka M. JSBML: a flexible Java library for working with SBML. Bioinformatics 2011, 27: 2167-2168.

Pico AR, Kelder T, van Iersel MP, Hanspers K, Conklin BR, Evelo C. WikiPathways: pathway editing for the people. PLoS Biol. 2008 Jul 22;6(7):e184.

Saez-Rodriguez J., Alexopoulos L.G., Epperlein J., Samaga R., Lauffenburger D.A., Klamt S., Sorger P.K. Discrete logic modeling as a means to link protein signaling networks with functional analysis of mammalian signal transduction. Mol Syst. Biol. 2009, 5: 331

J. Saez-Rodriguez, L. G. Alexopoulos, M. Zhang, M. K. Morris, D. A. Lauffenburger, P. K. Sorger. Comparing signaling networks between normal and transformed hepatocytes using discrete logical models. Cancer Res. 2011, 71(16): 1-12.

Smallbone K, Simeonidis E, Swainston N, Mendes P. Towards a genome-scale kinetic model of cellular metabolism. BMC Syst. Biol. 2010, 4:6.

Swainston N. The Subliminal Toolbox: automating steps in the reconstruction of metabolic networks. Slideshare http://tinyurl.com/3husu78

Wrzodek C., Dräger A., Zell A. KEGGtranslator: visualizing and converting the KEGG PATHWAY database to various formats. Bioinformatics. 2011, 27:2314-5.
