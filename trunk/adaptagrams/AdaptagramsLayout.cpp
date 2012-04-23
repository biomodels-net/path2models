#include "AdaptagramsLayout.h"

AdaptagramsLayout::AdaptagramsLayout() {
}

AdaptagramsLayout::~AdaptagramsLayout() {
}

void AdaptagramsLayout::constructFromSBMLQual(vector<Species> sp, vector<Transition> tr) {
    // (1) rectangles
    foreach(Species s, sp) {
        rs.push_back(new Rectangle(s.x, s.x+s.w, s.y, s.y+s.h));
        #ifdef _DEBUG_TRANSLATION_
        qDebug() << "Rectangle(" << s.x << "," << s.y << ")";
        #endif
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
        #ifdef _DEBUG_TRANSLATION_
        qDebug() << "Edge(" << from << "," << to << ")";
        #endif
    }
}
