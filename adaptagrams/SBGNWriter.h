#ifndef _SBGNWriter_h_
#define _SBGNWriter_h_

#include <string>
#include <libcola/cola.h>
#include <libvpsc/rectangle.h>
#include "SBMLQualParser.h"

using std::string;
using vpsc::Rectangle;
using cola::Edge;
using SBMLQual::Species;
using SBMLQual::Transition;

class SBGNWriter {
public:
    SBGNWriter();
    ~SBGNWriter();

    void writeFile(vector<Rectangle*> rs, vector<Edge> es, vector<Species> sp,
        vector<Transition> tr, string fname);

private:
    typedef xsd::cxx::tree::sequence<libsbgn::sn_0_2::glyph> glyph_sequence;
    typedef xsd::cxx::tree::sequence<libsbgn::sn_0_2::arc> arc_sequence;
    typedef xml_schema::idref source;
    typedef xml_schema::idref target;
    
};

#endif // _SBGNWriter_h_
