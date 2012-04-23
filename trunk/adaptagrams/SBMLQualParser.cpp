#include <Qt/qregexp.h>
#include "SBMLQualParser.h"


namespace SBMLQual {

    Parser::Parser(int argc, char *argv[]) {
        QCoreApplication app(argc, argv);
        readFile(argv[1]);
    }

    Parser::~Parser() {
    }

    QStringList Parser::executeQuery(QXmlQuery *query, QString qstring) {
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

    void Parser::readFile(char fname[]) {
        QXmlQuery query;
        query.bindVariable("path", QVariant(fname));

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

        // handle the qualitative species
        ambiguousMatches = QList<QStringList>();
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

            QString type = "unknown influence";
            if (items.length() == 3) { // sboTerm is set
                QRegExp re = QRegExp("[0-9]+$");
                if (items[2].contains(re)) {
                    switch(re.cap().toInt()) {
                        case 170:
                            type = "positive influence"; break;
                        case 169:
                            type = "negative influence"; break;
                        case 171:
                            type = "necessary stimulation"; break; //FIXME is this right?
                    }
                }
            }

            tr.push_back(Transition(from, to, type));
        }
    }

}; // SBMLQual
