/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SbgnLayout;

import SbgnLayout.Network.Node;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author mschu
 */
public class KeggLayout {
    
    Network net;
    
    public KeggLayout(Network net) {
        this.net = net;
    }
    
    void applyCoordFile(String filename) throws FileNotFoundException, IOException {
        BufferedReader readbuffer = new BufferedReader(new FileReader(filename));
        String line;
        while ((line=readbuffer.readLine())!=null){
            String elm[] = line.split("\t");
            
            try {
                Node node = net.getNodeByName("qual_" + elm[0]);
                node.setPos(Float.parseFloat(elm[1]), Float.parseFloat(elm[2]));
                node.setSize(Float.parseFloat(elm[3]), Float.parseFloat(elm[4]));
            }
            catch (NullPointerException e) {}
        }
        readbuffer.close();
        net.updateEdges();
    }
    
    void applyKeggFile(String filename) throws ParserConfigurationException, 
            SAXException, IOException, XPathExpressionException {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true); // never forget this!
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder.parse(filename);

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr = xpath.compile("//pathway/entry/graphics");

        Object result = expr.evaluate(doc, XPathConstants.NODESET);
        NodeList nodes = (NodeList)result;
        for (int i = 0; i < nodes.getLength(); i++) {
            NamedNodeMap nnm = nodes.item(i).getAttributes();
            try {
                String name = nnm.getNamedItem("name").getNodeValue();
                name = "qual_" + name.split(",")[0];

                Node node = net.getNodeByName(name);
                node.setPos(Float.parseFloat(nnm.getNamedItem("x").getNodeValue()), 
                            Float.parseFloat(nnm.getNamedItem("y").getNodeValue()));
                node.setSize(Float.parseFloat(nnm.getNamedItem("width").getNodeValue()), 
                            Float.parseFloat(nnm.getNamedItem("height").getNodeValue()));
            }
            catch (NullPointerException e) {}
        }
        net.updateEdges();
    }
    
    public void renderGraph() {
        JGraphLayout jgl = new JGraphLayout(net);
        jgl.renderGraph();
    }
}
