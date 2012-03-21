#include <Qt/qcoreapplication.h>
#include <Qt/qxml.h>
#include <Qt/qdebug.h>
#include <QtXmlPatterns/qxmlquery.h>
#include <Qt/qregexp.h>
#include <string>
#include <vector>
#include <set>
#include <limits>
#include "graphlayouttest.h"

#define _DEBUG_SBML_
//#define _DEBUG_TRANSLATION_
#define _DEBUG_LAYOUT_


using namespace std;
using namespace cola;
using namespace vpsc;

void removeoverlaps(vpsc::Rectangles &rs, bool bothaxes) {
	double xBorder=10, yBorder=10;
    static const double EXTRA_GAP=1e-5;
	unsigned n=rs.size();
	try {
		// The extra gap avoids numerical imprecision problems
		Rectangle::setXBorder(xBorder+EXTRA_GAP);
		Rectangle::setYBorder(yBorder+EXTRA_GAP);
        vpsc::Variables vs(n);
		unsigned i=0;
		for(Variables::iterator v=vs.begin();v!=vs.end();++v,++i) {
			*v=new Variable(i,0,1);
		}
        vpsc::Constraints cs;
        vpsc::generateXConstraints(rs,vs,cs,bothaxes);
        vpsc::IncSolver vpsc_x(vs,cs);
		vpsc_x.solve();
        vpsc::Rectangles::iterator r=rs.begin();
		for(Variables::iterator v=vs.begin();v!=vs.end();++v,++r) {
			assert((*v)->finalPosition==(*v)->finalPosition);
			(*r)->moveCentreX((*v)->finalPosition);
		}
		assert(r==rs.end());
		for_each(cs.begin(),cs.end(),vpsc::delete_object());
		cs.clear();
        if(bothaxes) {
            // Removing the extra gap here ensures things that were moved to be adjacent to one another above are not considered overlapping
            Rectangle::setXBorder(Rectangle::xBorder-EXTRA_GAP);
            vpsc::generateYConstraints(rs,vs,cs);
            vpsc::IncSolver vpsc_y(vs,cs);
            vpsc_y.solve();
            r=rs.begin();
            for(Variables::iterator v=vs.begin();v!=vs.end();++v,++r) {
                (*r)->moveCentreY((*v)->finalPosition);
            }
            for_each(cs.begin(),cs.end(),vpsc::delete_object());
            cs.clear();
            Rectangle::setYBorder(Rectangle::yBorder-EXTRA_GAP);
            vpsc::generateXConstraints(rs,vs,cs,false);
            vpsc::IncSolver vpsc_x2(vs,cs);
            vpsc_x2.solve();
            r=rs.begin();
            for(Variables::iterator v=vs.begin();v!=vs.end();++v,++r) {
                (*r)->moveCentreX((*v)->finalPosition);
            }
            for_each(cs.begin(),cs.end(),vpsc::delete_object());
        }
		for_each(vs.begin(),vs.end(),vpsc::delete_object());
	} catch (char *str) {
		std::cerr<<str<<std::endl;
		for(vpsc::Rectangles::iterator r=rs.begin();r!=rs.end();++r) {
			std::cerr << **r <<std::endl;
		}
	}
    Rectangle::setXBorder(xBorder);
    Rectangle::setYBorder(yBorder);
}





namespace SBMLQual {
    struct Species {
        Species(QString qid, QString qname, QString qx, QString qy, QString qw, QString qh):
            id(qid.toAscii().data()), name(qname.toAscii().data()), 
            x(qx.toInt()), y(qy.toInt()), w(qw.toInt()), h(qh.toInt())
        {
            #ifdef _DEBUG_SBML_
            qDebug() << "Node created: " << qid << qname << qx << qy << qw << qh;
            #endif
        }
        const bool operator==(const string s) const { return s.compare(id) == 0; }
        string id, name;
        int x, y, w, h;
    };

    enum transitionType {
        POSITIVE,
        NEGATIVE,
        UNKNOWN,
        NECESSARY
    };

    struct Transition {
        Transition(QString qfrom, QString qto, transitionType qtype):
            from(qfrom.toAscii().data()), to(qto.toAscii().data()),
            type(qtype)
        {
            #ifdef _DEBUG_SBML_
            qDebug() << "Edge created: " << qfrom << qto << qtype;
            #endif
        }
        string from, to;
        transitionType type;
    };
};

using namespace SBMLQual;


QStringList executeQuery(QXmlQuery *query, QString qstring) {
    query->setQuery(
        "declare default element namespace 'http://www.sbml.org/"
            "sbml/level3/version1/core';"
        "declare namespace l = 'http://www.sbml.org/"
            "sbml/level3/version1/layout/version1';"
        "declare namespace q = 'http://www.sbml.org/"
            "sbml/level3/version1/qual/version1';"
        "declare variable $BASE := doc($path)/sbml/model;"
        + qstring
    );

    if(!query->isValid())
        throw QString("Invalid query.");

    QStringList result;
    if(!query->evaluateTo(&result))
        throw QString("Unable to evaluate...");

    return result;
}

int main(int argc, char *argv[]) {
    QCoreApplication app(argc, argv);

    QXmlQuery query;
    query.bindVariable("path", QVariant("hsa04210.sbml.xml"));

    QStringList layout = executeQuery(&query,
        "$BASE/l:listOfLayouts/l:layout/l:listOfSpeciesGlyphs"
        "/l:speciesGlyph/string-join(("
            "data(@l:species),"
            "data(l:boundingBox/l:position/@l:x),"
            "data(l:boundingBox/l:position/@l:y),"
            "data(l:boundingBox/l:dimensions/@l:width),"
            "data(l:boundingBox/l:dimensions/@l:height)), '|')");

    QStringList qual = executeQuery(&query,
        "$BASE/q:listOfQualitativeSpecies/q:qualitativeSpecies/"
        "string-join(("
            "data(@q:id),"
            "data(@q:name)), '|')");

    QStringList trans = executeQuery(&query,
        "$BASE/q:listOfTransitions/q:transition/"
        "string-join(("
            "data(q:listOfInputs/q:input/@q:qualitativeSpecies),"
            "data(q:listOfOutputs/q:output/@q:qualitativeSpecies),"
            "data(q:listOfInputs/q:input/@sboTerm)), '|')");

    vector<Species> sp;
    vector<Transition> tr;

    // handle the qualitative species
    QList<QStringList> ambiguousMatches = QList<QStringList>();
    foreach(QString line, qual) {
        QStringList items = line.split("|");
        QString id = items[0], name = items[1];

        QStringList match = layout.filter(QRegExp("^"+id+"\\|"));

        QStringList l = match[0].split("|");
        sp.push_back(Species(id, name, l[1], l[2], l[3], l[4]));

        if (match.size() > 1) {
            ambiguousMatches.append(match);
        }
    }

    // handle the qualitative transitions
    foreach(QString line, trans) {
        QStringList items = line.split("|");
        QString from = items[0], to = items[1];

        transitionType type = UNKNOWN;
        if (items.length() == 3) { // sboTerm is set
            QRegExp re = QRegExp("[0-9]+$");
            if (items[2].contains(re)) {
                switch(re.cap().toInt()) {
                    case 170:
                        type = POSITIVE; break;
                    case 169:
                        type = NEGATIVE; break;
                    case 171:
                        type = NECESSARY; break;
                }
            }
        }

        tr.push_back(Transition(from, to, type));
    }

    // construct adaptagrams network
    // (1) rectangles
    vector<Rectangle*> rs;
    foreach(Species s, sp) {
        rs.push_back(new Rectangle(s.x, s.x+s.w, s.y, s.y+s.h));
        #ifdef _DEBUG_TRANSLATION_
        qDebug() << "Rectangle(" << s.x << "," << s.y << ")";
        #endif
    }

    // (2) edges
    vector<Edge> es;
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
        #ifdef _DEBUG_TRANSLATION_
        qDebug() << "Edge(" << from << "," << to << ")";
        #endif
    }

    OutputFile before(rs, es, NULL, "hsa04210.before.svg");
    before.rects = true;
    before.generate();

    // handle nodes with ambiguous positions
    foreach(QStringList match, ambiguousMatches) {
        QStringList l = match[0].split("|");
        assert(l[0] == match[1].split("|")[0]); // 2nd match should have same id
        // find corresponding entry in rs
        int idx = find(sp.begin(), sp.end(), l[0].toAscii().data())-sp.begin();
        Rectangle *r = rs[idx];
        double oldStress = numeric_limits<double>::max();

        foreach(QString m, match) {
            QStringList pos = m.split("|");
            double oldX = r->getMinX(), oldY = r->getMinY();
            r->moveMinX(pos[1].toInt()); // size should stay constant
            r->moveMinY(pos[2].toInt());

            ConstrainedFDLayout alg_prelim(rs,es,70,true,NULL);
            double newStress = alg_prelim.computeStress();
            if (newStress > oldStress) {
                r->moveMinX(oldX);
                r->moveMinY(oldY);
            }
            else {
                oldStress = newStress;
                #ifdef _DEBUG_LAYOUT_
                qDebug() << l[0] << "moved to (" << r->getMinX() << "," << r->getMinY() << "); new stress:" << newStress;
                #endif
            }
        }
    }

    OutputFile during(rs, es, NULL, "hsa04210.during.svg");
    during.rects = true;
    during.generate();

    // apply constraints
    CompoundConstraints ccs;
/*    foreach(Edge e, es) {
        if (sp[e.first].x == sp[e.second].x)
            ccs.push_back(new OrthogonalEdgeConstraint(XDIM, e.first, e.second));
        if (sp[e.first].y == sp[e.second].y)
            ccs.push_back(new OrthogonalEdgeConstraint(YDIM, e.first, e.second));
    }
*/
    // 4 or more with same x -> align
/*    set<int> align;
    for(int i=0; i<sp.size(); i++) {
        int count = 0;
        for(int j=i+1; j<sp.size(); j++) {
            if (sp[i].x == sp[j].x)
                count++;
        }
        if (count >= 4) {
            //qDebug() << ">4:" << sp[i].x;
            align.insert(sp[i].x);
        }
    }
    foreach(int xval, align) {
        AlignmentConstraint *ac = new AlignmentConstraint(YDIM,1);
        ccs.push_back(ac);
        for (int i; i<sp.size(); i++) {
            if (sp[i].x == xval) {
                ac->addShape(i, 0); // xval
            }
        }
    }
*/
/*
    AlignmentConstraint *ac = new AlignmentConstraint(XDIM);
    ccs.push_back(ac);
    ac->addShape(46,0);
    ac->addShape(47,0);
    ac->addShape(48,0);
    ac->addShape(49,0);
    ac->addShape(51,0);
    ac->addShape(52,0);
*/
    CheckProgress test(1e-9,100);

    ConstrainedMajorizationLayout alg(rs, es, 0, 120);
    alg.setScaling(true);
    alg.setConstraints(&ccs);
    alg.run();

//    ConstrainedFDLayout alg(rs,es,70,true,NULL);
//    alg.setConstraints(&ccs);
//    alg.makeFeasable();

    removeoverlaps(rs, true);

    OutputFile after(rs, es, NULL, "hsa04210.after.svg");
    after.rects = true;
    after.generate();
    
    foreach(Rectangle *r, rs) {
        delete r;
    }
//    foreach(CompoundConstraint c, ccs) {
//        delete c;
//    }
}
