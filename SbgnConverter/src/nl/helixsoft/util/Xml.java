package nl.helixsoft.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * Simple API for reading / writing Xml. The API was designed to be as brief as possible.
 * For example, to generate the following xml:
 * 
 * 
<pre>
	 &lt;book author="douglas adams"&gt;
	 	&lt;title&gt;Hitchhikers guide to the galaxy&lt;/title&gt;
	 &lt;/book&gt;
</pre>	 
<p>
	 All you have to do is this:
<p>
<code>	 
	 Xml.elt("book").setAttr("author", "douglas adams").add(Xml.elt("title", "Hitchhikers guide to the galaxy")).toString();
</code> 
 */
public class Xml
{
	private Map<String, String> attributes = new HashMap<String, String>();
	private List<String> attributeOrder = new ArrayList<String>();
	
	private String name;
	private String namespace;
	private List<Object> contents = new ArrayList<Object>();
	
	/**
	 * Implement this interface for use in the mapList method
	 */
	public interface XmlMapper<T> 
	{
		public Xml asXml (Object T);
	}

	/**
	 * Quickly add a list of items. Each item will be converted to Xml using the specified mapper.
	 */
	public <T> Xml mapList (final XmlMapper<T> mapper, final Collection<T> data)
	{
		for (T t : data)
		{
			addContents(mapper.asXml(t));
		}
		return this;
	}
	
	/**
	 * Add contents to an element. This contents can be either an Xml child element, or an arbitrary Object that is converted to String text. 
	 */
	private void addContents(Object o)
	{
		contents.add(o);
	}
	
	/** Private constructor, not for external use. Use the Xml.elt() static method instead */
	private Xml (String name, Object... os)
	{
		this.name = name;
		for (Object o : os)
			addContents (o);
	}
	
	/**
	 * Generate an Xml element with given name. Zero or more subelements or text contents (but not attributes) can be specified optionally.
	 */
	public static Xml elt (String name, Object... os)
	{
		return new Xml(name, os);
	}

	/**
	 * Set the XML namespace of this element
	 */
	public Xml setNs (String namespace)
	{
		this.namespace = namespace; 
		return this;
	}
	
	/**
	 * Get the XML namespace of this element
	 */
	public String getNs ()
	{
		return namespace;
	}

	/**
	 * Set an attribute on an element. Returns this, so that methods can be chained. For example:
	 * book.setAttr("author", "Douglas Adams").setAttr("genre", "science fiction comedy");
	 */
	public Xml setAttr (String key, String val)
	{
		attributes.put(key, val);
		attributeOrder.add(key);
		return this;
	}
	
	/**
	 * Add contents to a node. Contents can be either Xml children, or text content. Text content can be any object that implements toString().
	 */
	public Xml add (Object... os)
	{
		for (Object o : os)
			addContents(o);
		return this;
	}
		
	/**
	 * get the value of attribute with the given key.
	 */
	public String getAttr(String key)
	{
		return attributes.get(key);
	}
	
	/**
	 * Get the first child with the given element name.
	 */
	public Xml getFirst(String name)
	{
		 for (Object o : contents)
		 {
			 if (o instanceof Xml && name.equals (((Xml)o).name))
			 {
				 return ((Xml)o);
			 }
		 }
		 return null;
	}
	
	/**
	 * Get all childr elements with the given element name
	 */
	public Iterable<Xml> getChildren(String name)
	{
		List<Xml> result = new ArrayList<Xml>();
		for (Object o : contents)
		{
			if (o instanceof Xml && name.equals (((Xml)o).name))
			{
				result.add((Xml)o);
			}
		}
		return result;
	}

	/**
	 * Get all child elements.
	 */
	public Iterable<Xml> getChildren()
	{
		List<Xml> result = new ArrayList<Xml>();
		for (Object o : contents)
		{
			if (o instanceof Xml)
			{
				result.add((Xml)o);
			}
		}
		return result;
	}
	
	/**
	 * Write this node out to file.
	 */
	public void writeToFile(File out) throws IOException
	{
		FileWriter writer = new FileWriter (out);
		Context context = new Context();
		context.identation = 0;
		flush (writer, context);
		writer.close();
	}
	
	/**
	 * Convert this Xml to String.
	 */
	@Override
	public String toString()
	{
		StringWriter writer = new StringWriter();
		try
		{
			Context context = new Context();
			context.identation = 0;
			flush (writer, context);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return writer.toString();
	}
	
	/** Internal. Append this node and it's children to the writer */
	private void flush(Writer writer, Context context) throws IOException
	{
		if (context.identation == 0)
			writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		
		for (int i = 0; i < context.identation; ++i)
		{
			writer.write("  ");
		}
		
		writer.write('<');
		writer.write(name);
		for (String key : attributeOrder)
		{
			writer.write (' ');
			writer.write (key);
			writer.write ("=\"");
			writer.write (attributes.get(key));
			writer.write ('"');
		}
		if (contents.size() == 0)
		{
			writer.write ("/>\n");
		}
		else
		{
			writer.write (">\n");
			for (Object o : contents)
			{
				if (o instanceof Xml)
				{
					Context childContext = new Context();
					childContext.identation = context.identation + 1;
					((Xml) o).flush(writer, childContext);
				}
				else
				{
					writer.write ("" + o);
					writer.write ('\n');
				}
			}
			
			for (int i = 0; i < context.identation; ++i)
			{
				writer.write("  ");
			}
			
			writer.write("</");
			writer.write(name);
			writer.write (">\n");
		}
	}

	/** Internal. For use by flush */
	private static class Context
	{
		int identation;
	}
}
