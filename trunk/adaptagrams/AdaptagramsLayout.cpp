#include "AdaptagramsLayout.h"
#include "util.h"

void AdaptagramsLayout::constructFromSBMLQual(vector<Species> sp, vector<Transition> tr) {
    foreach(Species s, sp) {
        rs.push_back(new Rectangle(s.x, s.x+s.w, s.y, s.y+s.h));
        qDebug() << "Rectangle(" << s.x << "," << s.y << ")";
    }

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

std::pair<double, double> AdaptagramsLayout::getNodePositionByIndex(int idx) {
    std::pair<double, double> pos;
    pos.first = rs[idx]->getMinX();
    pos.second = rs[idx]->getMinY();
    return pos;
}

void AdaptagramsLayout::iterateAmbiguousPositions(vector<Species> sp, vector<AmbiguousMatch> ams) {
    foreach(AmbiguousMatch am, ams) {
        int idx = getIndexById(sp, am.id);
        Rectangle *r = getRectangleByIndex(idx);

        double oldStress = computeStress();
        assert(am.x.size()==am.y.size());
        for(int i=1; i<am.x.size(); i++) {
            setNodePositionByIndex(idx, am.x[i], am.y[i]);
            double newStress = computeStress();
            if (newStress > oldStress) {
                setNodePositionByIndex(idx, am.x[i-1], am.y[i-1]);
            }
            else {
                oldStress = newStress;
                qDebug() << QString(am.id.c_str()) << "moved to (" << r->getMinX() \
                    << "," << r->getMinY() << "); new stress:" << newStress;
            }
        }
    }
}

vector<Transition> AdaptagramsLayout::anchorEdges(vector<Transition> tr) {
    assert(es.size()==tr.size());
    for(int i=0; i<es.size(); i++) { //TODO: boost dist stuff
        Edge e = es[i];
        Rectangle* from = rs[e.first];
        Rectangle* to = rs[e.second];
        tr[i].addPoint(from->getMinX(), from->getMinY());
        tr[i].addPoint(to->getMinX(), to->getMinY());
    }
    return tr;
}

