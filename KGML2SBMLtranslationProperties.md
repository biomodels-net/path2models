#Properties of initial KGML2SBML translation

_NOTE: Written by Clemens Wrzodek, I just can not create wiki pages with my own google-account :-(_

**Please also E-Mail changes and suggestions to me in person, because I can't check this page frequently enough.**

Basically every property may be changed/ discussed; Properties have been modified to match discussed solution C'.

# Properties of initial KGML2SBML translation #

## List of SBO terms used in KEGGtranslator ##
| **SBML object** | **Condition** | **SBO** | **SBO name** |
|:----------------|:--------------|:--------|:-------------|
|Reaction         |All reactions  |176      |biochemical reaction|
|                 | | | |
|ModifierSpeciesReference|Dependent on KEGG entry type of corresponding species: enzyme, gene, group (spec says "MOSTLY a protein complex"), ortholog (same as gene, but in orthologous pathways)|460      |enzymatic catalyst|
|ModifierSpeciesReference|All other entry types (compound (is "small molecule"), map ("pathway references"), other) => definively no enzyme|13       |catalyst      |
|                 | | | |
|SpeciesReference |Substrates for irreversible reactions|15       |substrate     |
|SpeciesReference |Products for irreversible reactions|11       |product       |
|SpeciesReference |Substrates and products for reversible reactions|10       |reactant      |
|                 | | | |
|Species          |Dependent on KEGG entry type:|See below:|              |
|Species          |compound (is "small molecule")|247      |simple chemical|
|Species          |enzyme         |14 NLN: should be 245|enzyme NLN: should be "macromolecule"|
|Species          |gene           |243 NLN: should be 354 CW: Set to 252, because these are proteins (I know they are names gene, but KEGG says: "the node is a gene product (mostly a protein)")|gene NLN: should be "informational molecule segment" CW: "polypeptide chain"|
|Species          |group (spec says "MOSTLY a protein complex")|253      |non-covalent complex|
|Species          |map ("pathway references")|291 NLN: should be 552|empty set NLN: Should be "reference annotation" (see below)|
|Species          |ortholog (same as gene, but in orthologous pathways)|243NLN: should be 354|gene NLN: should be "informational molecule segment"|
|Species          |other          |285      |material entity of unspecified nature|

NB: SBO:0000552 "reference annotation" must be analysed within the context to generated the SBGN. Within an sboTerm attribute, it should be converted into a "tag", within a MIRIAM annotation, it should be converted into an annotation glyph. We are going to create a proper term for tag.

## List of MIRIAM URNs that are added to SPECIES ##
_Note: We think of all species as proteins or final chemical compounds. Qualifiers are choosen from this point of view._

| **Identifier Type** | **Biological type (meaning of identifier, not contained in the SBML)** | **Qualifier Type** | **BiologicalQualifierType** | **Notes** |
|:--------------------|:-----------------------------------------------------------------------|:-------------------|:----------------------------|:----------|
|KEGG ID(s)           |Anything                                                                |BIOLOGICAL\_QUALIFIER|BQB\_IS / BQB\_HAS\_VERSION  |If species corresponds to ONE id => Is, for MULTIPLE ids => has version; Actual miriam URN depdens on entry type (gene, enzyme, pathway, compound, etc.)|
|Entrez Gene          |Gene                                                                    |BIOLOGICAL\_QUALIFIER|BQB\_IS\_ENCODED\_BY         |           |
|Omim                 |"OMIM focuses on the relationship between phenotype and genotype"       |BIOLOGICAL\_QUALIFIER|BQB\_HAS\_PROPERTY for ID prefixed by # and %,m BQB\_IS\_ENCODED\_BY for ID prefixed by asterisk and + |           |
|Ensembl (Ensembl Gene)|Gene                                                                    |BIOLOGICAL\_QUALIFIER|BQB\_IS\_ENCODED\_BY         |           |
|Uniprot              |Protein                                                                 |BIOLOGICAL\_QUALIFIER|BQB\_IS / BQB\_HAS\_VERSION  |If species corresponds to ONE protein => Is, for MULTIPLE proteins => has version|
|Chebi                |Chemical Entities                                                       |BIOLOGICAL\_QUALIFIER|BQB\_IS / BQB\_HAS\_VERSION  |If species corresponds to ONE id => Is, for MULTIPLE ids => has version|
|Drugbank             |"The DrugBank database is a unique bioinformatics and cheminformatics resource that combines detailed drug (i.e. chemical, pharmacological and pharmaceutical) data with comprehensive drug target (i.e. sequence, structure, and pathway) information. "|BIOLOGICAL\_QUALIFIER|BQB\_IS / BQB\_HAS\_VERSION  |If species corresponds to ONE id => Is, for MULTIPLE ids => has version|
|GO                   |Gene ontology… describing terms for the gene or protein               |BIOLOGICAL\_QUALIFIER|BQB\_IS\_DESCRIBED\_BY       |           |
|HGNC                 |HGNC gene symbol                                                        |BIOLOGICAL\_QUALIFIER|BQB\_IS / BQB\_HAS\_VERSION  |The Gene Symbol is not only used for a gene, but also for proteins and all hierarchies. Thus, "IS" is probably the best Qualifier. NLN: I disagree. Even in the cases where gene and protein have the same spelling, the case is different. HGNC is a gene symbol. If people misuse them, we are not forced to do so |
|PubChem              |Chemical Entities (structures)                                          |BIOLOGICAL\_QUALIFIER|BQB\_HAS\_PROPERTY           |           |
|3DMet                |Chemical Entities (3D structures)                                       |BIOLOGICAL\_QUALIFIER|BQB\_HAS\_PROPERTY           |           |
|Reaction ID (KEGG)   |Takes part in the named reaction                                        |BIOLOGICAL\_QUALIFIER|BQB\_OCCURS\_IN              |I'm very unsure about this one. Actually "TAKES\_PART\_IN" would be nice… NLN: I do not think we need those annotations at all. They are already part of the SBML structure. If we keep them, we must create a new qualifier |
|Taxonomy ID          |NCBI Taxon ID of the parent organism                                    |BIOLOGICAL\_QUALIFIER|BQB\_OCCURS\_IN              |Is not set always! Only if kegg api returns an explicit and specific taxonomy field.|
|PDBeChem             |Chemical Entities                                                       |BIOLOGICAL\_QUALIFIER|BQB\_IS / BQB\_HAS\_VERSION  |If species corresponds to ONE id => Is, for MULTIPLE ids => has version|
|GlycomeDB            |Certain Chemical Entities (structures)                                  |BIOLOGICAL\_QUALIFIER|BQB\_HAS\_PROPERTY           |           |
|LipidBank            |Certain Chemical Entities (structures)                                  |BIOLOGICAL\_QUALIFIER|BQB\_HAS\_PROPERTY           |           |
|ECNumbers            |Enzymes                                                                 |BIOLOGICAL\_QUALIFIER|BQB\_HAS\_PROPERTY           |           |
|ECO\_CODE            |Static reference to an ECO Code                                         |BIOLOGICAL\_QUALIFIER|BQM\_IS\_DESCRIBED\_BY       |Fixed to ECO:0000313 ("imported information used in automatic assertion")|


## List of MIRIAM URNs that are added to SBML objects other than species ##

| **SBML object** | **Identifier Type** | **Biological type (meaning of identifier, not contained in the SBML)** | **Qualifier Type** | **BiologicalQualifierType** |
|:----------------|:--------------------|:-----------------------------------------------------------------------|:-------------------|:----------------------------|
|Model            |KEGG PATHWAY ID      |Pathway                                                                 |MODEL\_QUALIFIER    |BQM\_IS                      |
|Model            |NCBI Taxonomy        |Organism                                                                |BIOLOGICAL\_QUALIFIER|BQB\_OCCURS\_IN              |
|Model            |Gene ontology        |Describing terms for the pathway                                        |BIOLOGICAL\_QUALIFIER|BQB\_IS\_DESCRIBED\_BY       |
|                 | | | | |
|Reaction         |KEGG reaction ID     |reaction                                                                |BIOLOGICAL\_QUALIFIER|BQB\_IS                      |
|Reaction         |KEGG PATHWAY ID      |reaction occurs in …                                                  |BIOLOGICAL\_QUALIFIER|BQB\_OCCURS\_IN              |

## Group nodes ##
Group nodes are a construct that is currently not supported by SBML. Example for group nodes:

![http://www.ra.cs.uni-tuebingen.de/mitarb/wrzodek/free/group_node.jpg](http://www.ra.cs.uni-tuebingen.de/mitarb/wrzodek/free/group_node.jpg)

KGML-Code, covering the group node and the shown relation:
```
    <entry id="137" name="undefined" type="group">
        <graphics fgcolor="#000000" bgcolor="#FFFFFF"
             type="rectangle" x="946" y="311" width="110" height="39"/>
        <component id="130"/>
        <component id="132"/>
        <component id="133"/>
    </entry>

    <relation entry1="137" entry2="129" type="GErel">
        <subtype name="expression" value="--&gt;"/>
    </relation>
```

Solution: besides adding the CellDesigner annotation complexSpeciesAlias (I don't like CD-Tags) and writing the contained genes into human-readable notes, a MIRIAM identifier "BQB\_HAS\_PART" is created and all KEGG IDs of contained elements are added. However, I'm happy for better suggestions and want to leave this property for further discussion.

Translated SBML for this group nodes looks like this:
```
      <species id="undefined" initialAmount="1" name="undefined" metaid="meta_undefined" sboTerm="SBO:0000253" substanceUnits="substance" compartment="default">
<notes><body xmlns="http://www.w3.org/1999/xhtml"><a href="">Original Kegg Entry</a><br/>
<p>This species is a group, consisting of 3 components:<br/><ul><li>Srf</li><li>Elk4</li><li>Elk1</li></ul></p></body></notes>
        <annotation>
          <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:bqbiol="http://biomodels.net/biology-qualifiers/">
            <rdf:Description rdf:about="#meta_undefined">
              <bqbiol:hasPart>
                <rdf:Bag>
                  <rdf:li rdf:resource="urn:miriam:kegg.genes:mmu%3A20807"/>
                  <rdf:li rdf:resource="urn:miriam:kegg.genes:mmu%3A13714"/>
                  <rdf:li rdf:resource="urn:miriam:kegg.genes:mmu%3A13712"/>
                </rdf:Bag>
              </bqbiol:hasPart>
              <bqbiol:unknownQualifier>
                <rdf:Bag>
                  <rdf:li rdf:resource="urn:miriam:obo.eco:ECO%3A0000313"/>
                </rdf:Bag>
              </bqbiol:unknownQualifier>
            </rdf:Description>
          </rdf:RDF>
        </annotation>
      </species>
```

NOTE: After discussion with NLN (about november/december 2011) the Qualifier has been changed to "IS\_ENCODED\_BY".

# Translation Properties #

Initially, we wanted to create signaling maps for the signaling pathways (with a partial focus on creating nice-visualizable graphs) and metabolic maps (with the focus on kinetics and simulation, not visualization).

### The following applies for both models: ###
We create SBML L3V1 code. Both contain the layout extension with layout information for all species for which we can provide layout information. Since KEGG/ KGML does not provide more layout information, we can't add more here.

### Therefore, the following applies to the metabolic models: ###

1. If an entity (can be compound, enzyme, gene, etc.) occurs multiple times, we just create one instance. (For simulation, we don't need multiple instances of the same thing).

2. Removal of all references to other pathways (This is nothing one can simulate or annotate with kinetics).

3. Autocompletion of reactions. If enzymes, substrates or products are missing, they are being added to the PW (In some pathways, up to 70% of all species was not originally contained in the PW!)

4. Do NOT remove orphans (this is something though were nobody really took a decision, so in doubt, we decided to keep them, because kegg hopefully put them there for a reason).

5. Remove enzymes that have no real instance in the current organism (Kegg makes orthologous pathways. Therefore, some species-specific pathways contain nodes for enzymes, whos existence is not even proven for the species).

### For signalling maps, the following applies: ###

1. We keep the pathway as-it-is (don't merge duplicate nodes, keep pathway-reference nodes, etc).

2. Number 4) and 5) from the metabolic maps are also valid for the signalling maps.

Clemens