#include "AdaptagramsLayout.h"

AdaptagramsLayout::AdaptagramsLayout() {
}

AdaptagramsLayout::~AdaptagramsLayout() {
}

void AdaptagramsLayout::constructFromSBMLQual(vector<Species> sp, vector<Transition> tr) {
    // (1) rectangles
    foreach(Species s, sp) {
        rs.push_back(new Rectangle(s.x, s.x+s.w, s.y, s.y+s.h));
        qDebug() << "Rectangle(" << s.x << "," << s.y << ")";
    }

    // (2) edges
    foreach(Transition t, tr) {
        int from = -1, to = -1;
        for(int i=0; i<sp.size(); i++) {
            if (sp[i].id.compare(t.from) == 0) { // inefficient
                from = i;
            }
            if (sp[i].id.compare(t.to) == 0) {
                to = i;
            }
        }
        es.push_back(Edge(from, to));
        qDebug() << "Edge(" << from << "," << to << ")";
    }
}

double AdaptagramsLayout::computeStress() {
    cola::ConstrainedFDLayout alg_prelim(rs,es,70,true,NULL);
    return alg_prelim.computeStress();
}

void AdaptagramsLayout::setNodePositionByIndex(int idx, double x, double y) {
    Rectangle *r = rs[idx];
    r->moveMinX(x);
    r->moveMinY(y);
}
