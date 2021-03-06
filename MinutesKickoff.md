# Minutes from the KickOff meeting #

## Monday 10th metabolic session ##

The session initially focused on the differences between the KEGG to SBML translations of Subliminal and KEGGTranslator. The resulting files have a different number of species. It turns out that the tools use the information imported in KEGG Pathways from KEGG Enzyme differently. Another difference is the use of qualifiers.

The discussion then moved on two opposite constraints: The possibility to develop kinetic models once the conversion is done, and the need to avoid creating insufficiently supported knowledge (e.g. creating non-existing reactions).

## Monday 10th qual session ##

## Tuesday 11th metabolic session ##

We revisited the issue of representations of metabolic models from KEGG. This followed a
[discussion thread](http://groups.google.com/group/path2models/browse_thread/thread/f760aca9b85ab145?hl=en) started with a [summary of the possibilities](http://code.google.com/p/path2models/source/browse/trunk/documents/AlternativeK2S.png) discussed the previous day. Based on the responses by Andreas Draeger and Neil Swainston, and long discussions in the morning sessions, the
[remaining options](http://code.google.com/p/path2models/source/browse/trunk/documents/AlternativeK2S-2nd.png) are proposed:

  * The only curated information we find in the KEGG map correspond to solution A, i.e. no pools for the catalysts, only an annotation of the reaction mentioning the catalytic activity. All the remaining information in the KGML is coming from external resources. However, the solution A causes problems if we want to derive a quantitative model, except maybe if we add adequate SBO terms. Also we would like to make the most of the information available, even if not super-reliable.

  * Solution D', i.e. creating species for all the proteins and listing all the possible enzyme in the listOfModifiers, was eliminated because the semantics is different. The only thing we know is that proteins displaying a certain catalytic activity are modifying the reaction. Putting all of them as modifiers means they are all involved in all instances of the reaction.

  * Solution B, was just solution C without annotation.

  * We decided that solution C' would be the best by default. A pool is created, with a certain name. It represents the instances of all the proteins catalysing the reaction. It contains three annotations.
  1. hasProperty EC number; This says that all the instances of the pool have this catalytic activity. Note that some reactions have several EC code. ONLY ONE POOL IS CREATED.
  1. hasVersion a list of proteins, or isEncodedBy a list of genes. Of course, we do not know for sure, and ideally would have a qualifier "hasPossiblyVersion". But see below.
  1. hasProperty a term from the evidence code ontology. At the moment, the most suitable seems to be ECO:0000313 "imported information used in automatic assertion".

  * When we have independent information, for instance coming from MetaCyc, confirming the existence of particular proteins catalysing one process, we create the proteins and split the generic process, i.e. solution E. The EC code then annotate the reaction (isVersion qualifier).

## Tuesday 11th qual session ##

**Table of conversion between KEGG relations and SBO**:

| **KEGG**              | **KEGG value**                                            | **ECrel** | **PPrel** | **GErel**  | **Explanation**                                                                                     | **SBO ID**    | **SBO name**  | **Comments** |
|:----------------------|:----------------------------------------------------------|:----------|:----------|:-----------|:----------------------------------------------------------------------------------------------------|:--------------|:--------------|:-------------|
| compound              | Entry element id attribute value for compound.            | `*`       | `*`       |            | shared with two successive reactions (ECrel) or intermediate of two interacting proteins (PPrel)    |               |               | Those are relations |
| hidden compound       | Entry element id attribute value for hidden compound.	    | `*`	      |           |	           | shared with two successive reactions but not displayed in the pathway map                           |               |               | between processes |
|                       |                                                           |             |            |
| activation            | -->	                                                      | 	         | `*`       |            | positive and negative effects which may be associated with molecular information below              | SBO:0000170   | stimulation   |              |
| inhibition            | --|	 						                                               |	          | `*`	      |	           | positive and negative effects which may be associated with molecular information below              | SBO:0000169   | inhibition    |              |
| expression            | -->							                                                |	          |	          | `*`        | interactions via DNA binding                                                                        | SBO:0000170   | stimulation   |              |
| repression            | --|							                                                |	          |	          | `*`        | interactions via DNA binding                                                                        | SBO:0000169   | inhibition    |              |
| indirect effect       | ..>							                                                |	          | `*`	      | `*`	       | indirect effect without molecular details                                                           |       SBO:0000344      |      molecular interaction (also indirect)       | Examples are in hsa05146 ("Amoebiasis") |
| state change          | ...							                                                |	          | `*`	      |            | state transition                                                                                    | SBO:0000168   | control       |              |
|                       |                                                           |             |            |
| binding/association   | ---							                                                |	          | `*`	      |            | association and dissociation                                                                        | SBO:0000177   | non-covalent binding | process, not relation |
| dissociation          | -+-							                                                |	          | `*`	      |            | association and dissociation                                                                        | SBO:0000177   | non-covalent binding | process, not relation |
| missing interaction   | -/-							                                                |	          | `*`	      | `*`        | missing interaction due to mutation, etc.                                                           |       SBO:0000396      |       Uncertain process               |               |
| phosphorylation       | +p							                                                 |	          | `*`	      |            | molecular events                                                                                    | SBO:0000216   | phosphorylation | process, not relation |
| dephosphorylation     | -p							                                                 |	          | `*`	      |            | molecular events                                                                                    | SBO:0000330   | dephosphorylation | process, not relation |
| glycosylation         | +g							                                                 |	          | `*`	      |            | molecular events                                                                                    | SBO:0000217   | glycosylation | process, not relation |
| ubiquitination        | +u							                                                 |	          | `*`	      |            | molecular events                                                                                    | SBO:0000224   | ubiquitination | process, not relation |
| methylation           | +m							                                                 |	          | `*`	      |            | molecular events                                                                                    | SBO:0000214   | methylation   | process, not relation |


For the qual package activation, inhibition, expression, repression, indirect effect, and state change should be included. Activation and expression should be transformed into sign="positive" while inhibition and repression should be transformed into sign="negative". Indirect effect and state change should be transformed into sigb="unknown". The processual relations should be used to generated MIRIAM annotations using SBO (in the form of Identifiers.org URIs). The Maprel could be used to add annotations on QualitativeSpecies in the form of "PartOf" KEGG pathways.