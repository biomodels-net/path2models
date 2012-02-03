#include <Qt/qcoreapplication.h>
#include <Qt/qxml.h>
#include <Qt/qdebug.h>
#include <QtXmlPatterns/qxmlquery.h>

int main(int argc, char *argv[]) {
    QCoreApplication app(argc, argv);

    QXmlQuery query;
    query.bindVariable("path", QVariant("my.xml"));

    query.setQuery(
        "declare default element namespace \""
        "http://www.newzbin.com/DTD/2003/nzb\";"
        "doc($path)/nzb/file/segments/segment/string()"
    );
    if(!query.isValid())
        throw QString("Invalid query.");

    QStringList segments;
    if(!query.evaluateTo(&segments))
        throw QString("Unable to evaluate...");

    QString string;
    foreach(string, segments)
        qDebug() << "String: " << string;
}
