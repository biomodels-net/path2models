//#define QT_NO_DEBUG_OUTPUT

#include <Qt/qcoreapplication.h>
#include <Qt/qdebug.h>
#include "SBMLQualParser.h"
#include "AdaptagramsLayout.h"
#include "SBGNWriter.h"

using namespace SBMLQual;

template<class T> int getIndexById(T iterable, string id) {
    return find(iterable.begin(), iterable.end(), id.c_str())-iterable.begin();
}

template<class T> T getObjectById(std::vector<T> iterable, string id) {
    return find(iterable.begin(), iterable.end(), id.c_str());
}

int main(int argc, char* argv[]) {
    Parser parser = Parser(argc, argv);
    vector<Species> sp = parser.getSpecies();
    vector<Transition> tr = parser.getTransitions();

    AdaptagramsLayout layout = AdaptagramsLayout();
    layout.constructFromSBMLQual(sp, tr);

    foreach(AmbiguousMatch am, parser.getAmbiguousPositions()) {
        int idx = getIndexById(sp, am.id);
        Rectangle *r = layout.getRectangleByIndex(idx);

        double oldStress = layout.computeStress();
        assert(am.x.size()==am.y.size());
        for(int i=1; i<am.x.size(); i++) {
            layout.setNodePositionByIndex(idx, am.x[i], am.y[i]);
            double newStress = layout.computeStress();
            if (newStress > oldStress) {
                layout.setNodePositionByIndex(idx, am.x[i-1], am.y[i-1]);
            }
            else {
                oldStress = newStress;
                qDebug() << QString(am.id.c_str()) << "moved to (" << r->getMinX() \
                    << "," << r->getMinY() << "); new stress:" << newStress;
            }
        }
    }

    vector<Rectangle*> rs = layout.getRectangles();
    vector<Edge> es = layout.getEdges();

    SBGNWriter writer = SBGNWriter(rs, es, sp, tr);
    writer.writeFile(argv[2]);
}

