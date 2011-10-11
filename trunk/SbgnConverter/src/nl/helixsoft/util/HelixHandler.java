package nl.helixsoft.util;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class HelixHandler extends DefaultHandler 
{
	Xml root = null;
	Xml current = null;
	Stack<Xml> stack = new Stack<Xml>();
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		String s = new String(ch, start, length).trim();
		if (s.length() > 0)
			current.add(s);
	}

	@Override
	public void endElement(String arg0, String arg1, String arg2) throws SAXException
	{
		current = stack.pop();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes arg3)
			throws SAXException
	{
		Xml x = Xml.elt(localName);
		if (current != null)
		{
			current.add(x);	
		}
		else
		{
			root = x;
		}
		stack.push(current);
		current = x;

		for (int i = 0; i < arg3.getLength(); ++i)
		{
			x.setAttr(arg3.getLocalName(i), arg3.getValue(i));
		}

	}

	public Xml getRoot()
	{
		return root;
	}
}
