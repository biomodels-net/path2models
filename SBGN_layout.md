# Todo #

  * parser for nested logical expressions that introduce glyphs
  * parser for layout information w/ Finja's code example
    * how to handle partial layout information?
  * SBFC interfaces
    * w/ and w/o layout extension available
  * maybe: constrained-based layout using VANTED or adaptagrams

# Overview #

General-purpose Java libraries were tested for layout of simple biochemical networks that were already encoded in the SBGN AF test files. From the layouts, JGraph's hierarchical is the most promising but does not take into account compartments and may not suffice. Documentation of JGraph and JUNG is lacking important parts.

Review of scientific layout algorithms almost complete, original authors were in part able to provide their original implementations. Testing with these yields better results that will be used for the first conversion of SBML-qual to SBGN AF.

# Metabolic Part #

Code available

  * Gael's pipeline already available, SBML2SBGN will be used as-is
  * Currently using layout with GraphViz, which may not be optimal

Work subjects

  * Probably exchange layout algorithm with one found for signalling (cf. below)
  * Will concentrate on the signalling part for now

# Signalling Part #

## General purpose Java libraries ##

  * **JGraph** (Commercial software with open-source licensing)
    * Data structes for graph handling
    * Visulization with SWING
    * Hierarchical layout included (for signalling)
    * does not support compartments
    * _implementation with simple SBGN networks available_

  * <s><b>JUNG</b> (Java Universal Network/Graph Framework)</s>
    * Data structes for graph handling in 3rd party dependencies
    * Visulization with SWING
    * Only very basic layouts implemented, hierarchical fails when circles occur (Java Exception)

  * <s><b>GraphViz</b></s>
    * discouraged by Martijn because of non-Java solution

## Scientific implementations for biochemical networks ##


  * **Sugiyama (1981).** Hierarchical layout that minimises edge crossings
    * K. Sugiyama, S. Tagawa, and M. Toda, “Methods for visual understanding of hierarchical system structures,” Systems, Man and Cybernetics, IEEE Transactions on 11, no. 2 (1981): 109–125.
    * does not support compartments
    * _implementation tested, available in path2models code_

  * <s><b>Becker (2001).</b></s>
    * Moritz Y. Becker and Isabel Rojas, “A graph layout algorithm for drawing metabolic pathways,” Bioinformatics 17, no. 5 (May 1, 2001): 461 -467.
    * Hierarchical similar to Sugiyama
    * Uses largest circle as backbone, works well when there is a very limited number of connections to that circle
    * does not support compartments
    * _sent email to author, implementation no longer available_

  * **Wegner & Kummer (2004-2005).**
    * Katja Wegner and Ursula Kummer, “A new dynamical layout algorithm for complex biochemical reaction networks,” BMC Bioinformatics 6 (2005): 212-212.
    * U. Rost and U. Kummer, “Visualisation of biochemical network simulations with SimWiz,” Systems Biology 1 (2004): 184.
    * Implementation available in SimWiz, but download link broken
    * _wrote email to authors, no answer yet_

  * **Dogrusoz (2004-2009).**
    * Ugur Dogrusoz et al., “A layout algorithm for undirected compound graphs,” Information Sciences 179, no. 7 (March 15, 2009): 980-994.
    * B. Genc and U. Dogrusoz, “A layout algorithm for signaling pathways,” Information Sciences 176, no. 2 (January 20, 2006): 135-149.
    * Ugur Dogrusoz, “A Compound Graph Layout Algorithm for Biological Pathways”, 2005.
    * Burkay Genc and Ugur Dogrusoz, “A Constrained, Force-Directed Layout Algorithm for Biological Pathways”, 2004.
    * ChiLay (Java library) open source project with various algorithms, used in PATIKA and VISIOweb software
    * supports compartments
    * _implementation available in path2models code via ChiLay_

  * **Schreiber (2009).**
    * Falk Schreiber et al., “A generic algorithm for layout of biological networks” 10 (2009): 375-375.
    * Article results promising, could be used for both signalling and metabolic part
    * supports compartments
    * looks good for metabolic and signalling part
    * _library is in development, will be released ca. April 2012; alternatives: Dunnart (C++) and Vanted (Java) with partial implementations_

  * **Kojima (2008-2010).**
    * Kaname Kojima, Masao Nagasaki, and Satoru Miyano, “An efficient biological pathway layout algorithm combining grid-layout and spring embedder for complicated cellular location information” 11 (2010): 335-335.
    * Kaname Kojima, Masao Nagasaki, and Satoru Miyano, “Fast grid layout algorithm for biological networks with sweep calculation,” Bioinformatics 24, no. 12 (June 15, 2008): 1433 -1441.
    * supports compartments
    * grid layout may be more suitable for metabolic part
    * _sent email to author, no answer yet_

## Possible use of constrained-based layout on KEGG maps ##

  * **VANTED** (<a href='http://vanted.ipk-gatersleben.de/'>link</a>)
    * C. Klukas and F. Schreiber, “Integration of-omics data and networks for biomedical research with VANTED,” Journal of integrative bioinformatics 7, no. 2 (2010): 112.
    * B. Junker, C. Klukas, and F. Schreiber, “VANTED: A system for advanced data analysis and visualization in the context of biological networks,” BMC bioinformatics 7, no. 1 (2006): 109.
    * library for Falk Schreiber's article in development, will take until about April
    * will be integrated in VANTED framework

  * **adaptagrams** (<a href='http://adaptagrams.sourceforge.net/'>link</a>)
    * T. Dwyer, K. Marriott, and M. Wybrow, “Dunnart: A constraint-based network diagram authoring tool,” in Graph Drawing, 2009, 420–431.
    * T. Dwyer, K. Marriott, and M. Wybrow, “Interactive, Constraint-based Layout of Engineering Diagrams,” Electronic Communications of the EASST 13, no. 0 (2008).
    * best implementation of constrained-based layout available
    * is a C++ library, used by e.g. <a href='http://www.csse.monash.edu.au/~mwybrow/dunnart/'>Dunnart</a>
    * Demo video <a href='http://www.csse.monash.edu.au/~mwybrow/ContinuousUserGuidedNetworkLayout.mov'>here<a></li></ul>

## Implementation ##

  * Layout
    * Currently favoured: JGraph framework with ChiLay algorithms, on the path2models SVN for testing.
    * Implementation for subset of glyphs available
    * Continued work will use these for the conversions and may be switched at a later point in time if layout improvements can be observed.
    * SBML qual input works