#include <limits>
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

double AdaptagramsLayout::computeStress() {
    cola::ConstrainedFDLayout alg_prelim(rs,es,70,true,NULL);
    return alg_prelim.computeStress();
}

void AdaptagramsLayout::setNodePositionByIndex(int idx, double x, double y) {
    Rectangle *r = rs[idx];
    r->moveMinX(x);
    r->moveMinY(y);
}

/*
void AdaptagramsLayout::() { //TODO split this into parsing and layout logic part
    foreach(QStringList match, ambiguousMatches) {
        QStringList l = match[0].split("|");
        assert(l[0] == match[1].split("|")[0]); // 2nd match should have same id
        // find corresponding entry in rs
        int idx = find(sp.begin(), sp.end(), l[0].toAscii().data())-sp.begin();
        Rectangle *r = rs[idx];
        double oldStress = numeric_limits<double>::max();

        foreach(QString m, match) {
            QStringList pos = m.split("|");
            double oldX = r->getMinX(), oldY = r->getMinY();
            r->moveMinX(pos[1].toInt());
            r->moveMinY(pos[2].toInt());

            ConstrainedFDLayout alg_prelim(rs,es,70,true,NULL);
            double newStress = alg_prelim.computeStress();
            if (newStress > oldStress) {
                r->moveMinX(oldX);
                r->moveMinY(oldY);
            }
            else {
                oldStress = newStress;
                #ifdef _DEBUG_LAYOUT_
                qDebug() << l[0] << "moved to (" << r->getMinX() << "," << r->getMinY() << 
"); new stress:" << newStress;
                #endif
            }
        }
    }
}*/
