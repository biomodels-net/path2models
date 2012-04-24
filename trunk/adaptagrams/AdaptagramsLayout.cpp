#include "AdaptagramsLayout.h"

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
/*
vector<double> AdaptagramsLayout::computeEdgePoints(int i, int j) {
    vector<double> line();
    std::pair<double, double> pos;
    pos = getNodePositionByIndex(i);
    line.push_back(pos.first);
    line.push_back(pos.second);
    pos = getNodePositionByIndex(j);
    line.push_back(pos.first);
    line.push_back(pos.second);
    return line; //TODO: get proper intersections, not just corner
}*/

