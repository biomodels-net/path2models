#ifndef _SBMLQualParser_h_
#define _SBMLQualParser_h_

#include <Qt/qcoreapplication.h>
#include <Qt/qdebug.h>
#include <Qt/qstringlist.h>
#include <QtXmlPatterns/qxmlquery.h>
#include <vector>
#include <string>
#include "util.h"


namespace SBMLQual {
    struct Species {
        Species(QString qid, QString qname, QString qx, QString qy, QString qw, QString qh):
            id(qid.toAscii().data()), name(qname.toAscii().data()),
            x(qx.toInt()), y(qy.toInt()), w(qw.toInt()), h(qh.toInt())
        {
            #ifdef _DEBUG_SBML_
            qDebug() << "Node created: " << qid << qname << qx << qy << qw << qh;
            #endif

            if (w > 60 || h > 60)
                glyph = string("phenotype");
            else
                glyph = string("biological activity");

            if (w < 20) {
                w += 10;
                w += name.size() * 5;
                h += 5;
            }
        }
        const bool operator==(const string s) const { return s.compare(id) == 0; }
        string id, name, glyph;
        int x, y, w, h;
    };

    struct Transition {
        Transition(QString qfrom, QString qto, QString qtype):
            from(qfrom.toAscii().data()), to(qto.toAscii().data()),
            type(qtype.toAscii().data())
        {
            #ifdef _DEBUG_SBML_
            qDebug() << "Edge created: " << qfrom << qto << qtype;
            #endif
        }
        void addPoint(point_type p) { line.push_back(p); }
        string from, to;
        string type;
        linestring_type line;
    };

    struct AmbiguousMatch {
        string id;
        linestring_type pts;
    };

    class Parser {
    public:
        Parser(int, char**);
        ~Parser();

        vector<Species> getSpecies() const { return sp; }
        vector<Transition> getTransitions() const { return tr; }

        vector<AmbiguousMatch> getAmbiguousPositions() const { return ambiguousMatches; }

    private:
        void readFile(char*);
        QStringList executeQuery(QXmlQuery*, QString);

        vector<Species> sp;
        vector<Transition> tr;

        vector<AmbiguousMatch> ambiguousMatches;
    };
};

#endif //  _SBMLQualParser_h_
