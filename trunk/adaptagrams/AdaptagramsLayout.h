#ifndef _AdaptagramsLayout_h_
#define _AdaptagramsLayout_h_

#include <vector>
#include "graphlayouttest.h"
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

private:
    vector<Rectangle*> rs;
    vector<Edge> es;
};

#endif // _AdaptagramsLayout_h_
