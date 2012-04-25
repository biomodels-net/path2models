#ifndef _util_h_
#define _util_h_

#include <string>
#include <vector>
#include <boost/geometry/geometries/linestring.hpp>
#include <boost/geometry/geometries/point_xy.hpp>

using std::string;
using std::vector;

typedef boost::geometry::model::d2::point_xy<double> point_type;
typedef boost::geometry::model::linestring<point_type> linestring_type;


template<class T> int getIndexById(T iterable, string id) {
    return find(iterable.begin(), iterable.end(), id.c_str())-iterable.begin();
}

template<class T> T getObjectById(std::vector<T> iterable, string id) {
    return find(iterable.begin(), iterable.end(), id.c_str());
}

#endif // _util_h_
