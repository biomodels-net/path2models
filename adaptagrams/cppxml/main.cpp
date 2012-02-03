#include <Qt/qcoreapplication.h>
#include <Qt/qxml.h>
#include <Qt/qdebug.h>
#include <QtXmlPatterns/qxmlquery.h>
#include <Qt/qregexp.h>
#include <string>

using std::string;


struct Node {
    Node(QString qid, QString qname, QString qx, QString qy, QString qw, QString qh):
        id(qid.toAscii().data()), name(qname.toAscii().data()), x(qx.toInt()),
        y(qy.toInt()), w(qw.toInt()), h(qh.toInt())
    {
        qDebug() << "Node created: " << qid << qname << qx << qy << qw << qh;
    }
    string id, name;
    int x, y, w, h;
};

enum edgeType {
    POSITIVE,
    NEGATIVE,
    UNKNOWN,
    NECESSARY
};

struct Edge {
   string from, to;
   edgeType type;
};


QStringList executeQuery(QXmlQuery *query) {
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

    query.setQuery(
        "declare default element namespace 'http://www.sbml.org/sbml/level3/version1/core';"
        "declare namespace l = 'http://www.sbml.org/sbml/level3/version1/layout/version1';"
        "declare namespace q = 'http://www.sbml.org/sbml/level3/version1/qual/version1';"
        "declare variable $BASE := doc($path)/sbml/model;"
        "$BASE/l:listOfLayouts/l:layout/l:listOfSpeciesGlyphs/l:speciesGlyph/"
        "string-join(("
            "data(@l:species),"
            "data(l:boundingBox/l:position/@l:x),"
            "data(l:boundingBox/l:position/@l:y),"
            "data(l:boundingBox/l:dimensions/@l:width),"
            "data(l:boundingBox/l:dimensions/@l:height)), '|')"
    );

    QStringList layout = executeQuery(&query);
//    foreach(QString string, layout)
//        qDebug() << "String: " << string;

    query.setQuery(
        "declare default element namespace 'http://www.sbml.org/sbml/level3/version1/core';"
        "declare namespace l = 'http://www.sbml.org/sbml/level3/version1/layout/version1';"
        "declare namespace q = 'http://www.sbml.org/sbml/level3/version1/qual/version1';"
        "declare variable $BASE := doc($path)/sbml/model;"
        "$BASE/q:listOfQualitativeSpecies/q:qualitativeSpecies/"
        "string-join(("
            "data(@q:id),"
            "data(@q:name)), '|')"
    );

    QStringList qual = executeQuery(&query);
    foreach(QString line, qual) {
        QStringList items = line.split("|");
        QString id = items[0], name = items[1];

        QStringList match = layout.filter(QRegExp("^"+id+"\\|"));
//        qDebug() << "LINE: " << id;
//        qDebug() << match;

        QStringList l = match[0].split("|"); // FIXME not only first but mix if more?
        Node(id, name, l[1], l[2], l[3], l[4]);
    }


// missing: edges
//
    query.setQuery(
        "declare default element namespace 'http://www.sbml.org/sbml/level3/version1/core';"
        "declare namespace l = 'http://www.sbml.org/sbml/level3/version1/layout/version1';"
        "declare namespace q = 'http://www.sbml.org/sbml/level3/version1/qual/version1';"
        "declare variable $BASE := doc($path)/sbml/model;"
        "$BASE/q:listOfTransitions/transition/"
        "string-join(("
            "data(q:listOfInputs/input/@qualitativeSpecies),"
            "data(q:listOfOutputs/output/@qualitativeSpecies),"
            "data(q:listOfInputs/input/@sboTerm)), '|')"
    );

}
