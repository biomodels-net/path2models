//#define QT_NO_DEBUG_OUTPUT

#include "SBMLQualParser.h"
#include "AdaptagramsLayout.h"
#include "SBGNWriter.h"

using namespace SBMLQual;

int main(int argc, char* argv[]) {
    Parser parser = Parser(argc, argv);
    vector<Species> sp = parser.getSpecies();
    vector<Transition> tr = parser.getTransitions();

    AdaptagramsLayout layout = AdaptagramsLayout();
    layout.constructFromSBMLQual(sp, tr);
    layout.iterateAmbiguousPositions(sp, parser.getAmbiguousPositions());

    vector<Rectangle*> rs = layout.getRectangles();
    vector<Edge> es = layout.getEdges();

    tr = layout.anchorEdges(tr); // TODO: pointers
    SBGNWriter writer = SBGNWriter(rs, es, sp, tr);
    writer.writeFile(argv[2]);
}

