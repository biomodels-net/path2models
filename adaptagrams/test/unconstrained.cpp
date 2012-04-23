/** \file unconstrained.cpp
 *
 * Unconstrained graph layout test.  Simple graph with 4 nodes and 4 edges,
 * a triangle with a dangle.  Final stress checked.
 *
 *
 * Authors:
 *   Tim Dwyer <tgdwyer@gmail.com>
 */
#include <iostream>
#include <fstream>

#include <vector>
#include <valarray>
#include <algorithm>
#include <float.h>
#include "graphlayouttest.h"

using namespace std;
using namespace cola;

int main() {
	const unsigned V = 4;
	Edge edge_array[] = { Edge(0, 1), Edge(1, 2), Edge(2, 3), Edge(1, 3) };
	const std::size_t E = sizeof(edge_array) / sizeof(Edge);
	vector<Edge> es(E);
	copy(edge_array,edge_array+E,es.begin());
	double width=100;
	double height=100;
	vector<vpsc::Rectangle*> rs;
	for(unsigned i=0;i<V;i++) {
		double x=getRand(width), y=getRand(height);
		rs.push_back(new vpsc::Rectangle(x,x+5,y,y+5));
	}
	CheckProgress test(1e-9,100);
	ConstrainedFDLayout alg(rs,es,width/2,false,NULL,test);
	alg.run();
	double stress = alg.computeStress();
  	assert(stress < 0.0013);
	OutputFile output(rs,es,NULL,"unconstrained.svg");
	output.generate();
	for(unsigned i=0;i<V;i++) {
		delete rs[i];
	}
}
