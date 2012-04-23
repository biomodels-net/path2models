#include <fstream>
#include <sbgn.hxx>
#include "SBGNWriter.h"

void SBGNWriter::writeFile(vector<Rectangle*> rs, vector<Edge> es, vector<Species> sp,
        vector<Transition> tr, string fname) {
    using namespace libsbgn::sn_0_2;

    // add glyphs
    assert(rs.size() == sp.size());
    glyph_sequence gs = glyph_sequence();
    for(int i=0; i<rs.size(); i++) {
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
    for(int i=0; i<es.size(); i++) { // so far, i don't need the edges at all
        Edge edge = es[i];
        Transition trans = tr[i];

        start _start = start(0,0); //TODO: libavoid?
        end _end = end(1,1);
        class_ _class = class_(trans.type);
        source _source = source(trans.from);
        target _target= target(trans.to);
        stringstream _id;
        _id << "tr_" << i;
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
    std::ofstream ofs(fname.c_str());
    sbgn_(ofs, s, map);
    ofs.close();
}

