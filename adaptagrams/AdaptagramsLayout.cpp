#include <boost/geometry.hpp>
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

void AdaptagramsLayout::iterateAmbiguousPositions(vector<Species> sp, vector<AmbiguousMatch> ams) {
    foreach(AmbiguousMatch am, ams) {
        int idx = getIndexById(sp, am.id);
        Rectangle *r = getRectangleByIndex(idx);

        double oldStress = computeStress();
        for(int i=1; i<am.pts.size(); i++) {
            setNodePositionByIndex(idx, am.pts[i].x(), am.pts[i].y());
            double newStress = computeStress();
            if (newStress > oldStress) {
                setNodePositionByIndex(idx, am.pts[i-1].x(), am.pts[i-1].y());
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

    // construct anchor points for all rectangles
    vector<linestring_type> anchors;
    foreach(Rectangle *r, rs) {
        linestring_type line;

        for(double x=0.25; x<0.9; x+=0.25) {
            line.push_back(point_type(r->getMinX()+x*r->width(), r->getMinY()));
            line.push_back(point_type(r->getMinX()+x*r->width(), r->getMaxY()));
        }
        line.push_back(point_type(r->getMinX(), r->getMinY()+0.5*r->height()));
        line.push_back(point_type(r->getMaxX(), r->getMinY()+0.5*r->height()));

        anchors.push_back(line);
    }

    for(int i=0; i<es.size(); i++) {
        Rectangle *from = rs[es[i].first];
        Rectangle *to = rs[es[i].second];

        linestring_type centerLine;
        centerLine.push_back(point_type(from->getCentreX(), from->getCentreY()));
        centerLine.push_back(point_type(to->getCentreX(), to->getCentreY()));

        tr[i].addPoint(getClosestPointToLine(anchors[es[i].first], centerLine));
        tr[i].addPoint(getClosestPointToLine(anchors[es[i].second], centerLine));
    }

    return tr;
}

point_type AdaptagramsLayout::getClosestPointToLine(linestring_type points, linestring_type centerLine) {
    using boost::geometry::distance;
    point_type closest(0,0);
    double dist = 1e5;

    foreach(point_type pt, points) {
        double new_dist = distance(pt, centerLine);
        if (new_dist < dist) {
            dist = new_dist;
            closest = pt;
        }
    }

    return closest;
}

