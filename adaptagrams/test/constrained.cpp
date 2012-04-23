/** \file constrained.cpp
 *
 * runs constraint layout on a small graph.  nodes 0 and 3 are constrained
 * to a vertical line
 *
 *
 * Authors:
 *   Tim Dwyer <tgdwyer@gmail.com>
 */
#include <iostream>

#include <vector>
#include <algorithm>
#include <float.h>
#include <libcola/cola.h>
#include <libcola/output_svg.h>
inline double getRand(double range) {
	return range*rand()/RAND_MAX;
}

using namespace std;
using namespace cola;
/** 
* \brief Determines when to terminate layout of a particular graph based
* on a given relative tolerance. 
*/
int main() {

	const unsigned V = 4;
	typedef pair < unsigned, unsigned >Edge;
	Edge edge_array[] = { Edge(0, 1), Edge(1, 2), Edge(2, 3), Edge(1, 3) };
	unsigned E = sizeof(edge_array) / sizeof(Edge);
	vector<Edge> es(edge_array,edge_array+E);
	double width=100;
	double height=100;
	vector<vpsc::Rectangle*> rs;
	for(unsigned i=0;i<V;i++) {
		double x=getRand(width), y=getRand(height);
		rs.push_back(new vpsc::Rectangle(x,x+5,y,y+5));
	}
	CompoundConstraints ccs;
	AlignmentConstraint ac(vpsc::XDIM);
	ccs.push_back(&ac);
	ac.addShape(0,0);
	ac.addShape(3,0);
	// apply steepest descent layout
	ConstrainedFDLayout alg2(rs,es,width/2, false);
	alg2.setConstraints(ccs);
	alg2.run();
	assert(alg2.computeStress()<0.0013);
	// the following pair of nodes should line-up
	assert(fabs(rs[0]->getCentreX()-rs[3]->getCentreX())<0.001);
	// reset rectangles to random positions
	for(unsigned i=0;i<V;i++) {
		double x=getRand(width), y=getRand(height);
		rs[i]->moveCentre(x,y);
	}
	// apply scaled majorization layout
	ConstrainedMajorizationLayout alg(rs,es,NULL,width/2);
	alg.setConstraints(&ccs);
	alg.setScaling(true);
	alg.run();
	// the following pair of nodes should line-up
	assert(fabs(rs[0]->getCentreX()-rs[3]->getCentreX())<0.001);
	cout<<rs[0]->getCentreX()<<","<<rs[3]->getCentreX()<<endl;
	OutputFile output(rs,es,NULL,"constrained.svg");
	output.rects=true;
	output.generate();

	for(unsigned i=0;i<V;i++) {
		delete rs[i];
	}
}
