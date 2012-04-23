#ifndef _AdaptagramsLayout_h_
#define _AdaptagramsLayout_h_

#include <vector>
#include <libcola/cola.h>
#include <libvpsc/rectangle.h>
#include "SBMLQualParser.h"

using std::vector;
using vpsc::Rectangle;
using cola::Edge;
using SBMLQual::Species;
using SBMLQual::Transition;

class AdaptagramsLayout {
public:
    AdaptagramsLayout();
    ~AdaptagramsLayout();

    void constructFromSBMLQual(vector<Species>, vector<Transition>);
    double computeStress();

    void setNodePositionByIndex(int idx, double x, double y);

    vector<Rectangle*> getRectangles() const { return rs; }
    vector<Edge> getEdges() const { return es; }

    Rectangle* getRectangleByIndex(int idx) { return rs[idx]; }
    Edge getEdgeByIndex(int idx) { return es[idx]; }

private:
    vector<Rectangle*> rs;
    vector<Edge> es;
};

#endif // _AdaptagramsLayout_h_
