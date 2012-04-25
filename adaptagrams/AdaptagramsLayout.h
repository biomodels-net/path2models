#ifndef _AdaptagramsLayout_h_
#define _AdaptagramsLayout_h_

#include <vector>
#include <libcola/cola.h>
#include <libvpsc/rectangle.h>
#include "SBMLQualParser.h"
#include "util.h"

using std::vector;
using vpsc::Rectangle;
using cola::Edge;
using SBMLQual::Species;
using SBMLQual::Transition;
using SBMLQual::AmbiguousMatch;


class AdaptagramsLayout {
public:
    AdaptagramsLayout() {}
    AdaptagramsLayout(vector<Species> sp, vector<Transition> tr) {
        constructFromSBMLQual(sp, tr);
    }
    ~AdaptagramsLayout() {}

    void constructFromSBMLQual(vector<Species>, vector<Transition>);
    double computeStress();

    void setNodePositionByIndex(int idx, double x, double y);
    std::pair<double, double> getNodePositionByIndex(int idx);

    vector<Rectangle*> getRectangles() const { return rs; }
    vector<Edge> getEdges() const { return es; }

    Rectangle* getRectangleByIndex(int idx) { return rs[idx]; }
    Edge getEdgeByIndex(int idx) { return es[idx]; }

    void iterateAmbiguousPositions(vector<Species>, vector<AmbiguousMatch>);
    vector<Transition> anchorEdges(vector<Transition> tr);

private:
    point_type getClosestPointToLine(linestring_type points, linestring_type centerLine);

    vector<Rectangle*> rs;
    vector<Edge> es;
};

#endif // _AdaptagramsLayout_h_
