#include <fstream>
#include "SBGNWriter.h"

SBGNWriter::SBGNWriter(vector<Rectangle*> r, vector<Edge> e, vector<Species> s,
    vector<Transition> t): rs(r), es(e), sp(s), tr(t) {
}

void SBGNWriter::writeFile(char* fname) {
    using namespace libsbgn::sn_0_2;

    // add glyphs
    assert(rs.size() == sp.size());
    glyph_sequence gs = glyph_sequence();
    for(unsigned i=0; i<rs.size(); i++) {
        Rectangle *rec = rs[i];
        Species species = sp[i];

        bbox _bbox = bbox(rec->getMinX(), rec->getMinY(), rec->width(), rec->height());
        class_ _class = class_("nucleic acid feature"); // TODO: get proper class somewhere
        glyph _glyph = glyph(_bbox, _class, species.id);
        _glyph.label(label(species.name));

        gs.push_back(_glyph);
    }

    // add arcs
    assert(es.size() == tr.size());
    arc_sequence as = arc_sequence();
    int i = 0;
    foreach(Transition trans, tr) {
        int npts = trans.line.size();
        start _start = start(trans.line[0].x(), trans.line[0].y());
        end _end = end(trans.line[npts-1].x(), trans.line[npts-1].y());

        class_ _class = class_(trans.type);
        source _source = source(trans.from);
        target _target= target(trans.to);
        std::stringstream _id;
        _id << "tr_" << i++;
        arc _arc = arc(_start, _end, _class, _id.str(), _source, _target);

        as.push_back(_arc);
    }

    // create object
    language l = language(language::value(2)); // activity_flow
    libsbgn::sn_0_2::map m = libsbgn::sn_0_2::map(l);
    m.glyph(gs);
    m.arc(as);
    sbgn s = sbgn(m);

    // write the xml file
    xml_schema::namespace_infomap map;
//    map[""].name = "test"; // xmlns
//    map[""].schema = "http://sbgn.org/libsbgn/0.2"; // xsi:noNamespaceSchemaLocation
    std::ofstream ofs(fname);
    sbgn_(ofs, s, map);
    ofs.close();
}

