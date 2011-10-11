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

public class Xml
{
	private Map<String, String> attributes = new HashMap<String, String>();
	private List<String> attributeOrder = new ArrayList<String>();
	
	private String name;
	private String namespace;
	private List<Object> contents = new ArrayList<Object>();
	
	public interface XmlMapper<T> 
	{
		public Xml asXml (Object T);
	}

	public <T> Xml mapList (final XmlMapper<T> mapper, final Collection<T> data)
	{
		for (T t : data)
		{
			addContents(mapper.asXml(t));
		}
		return this;
	}
	
	private void addContents(Object o)
	{
		contents.add(o);
	}
	
	private Xml (String name, Object... os)
	{
		this.name = name;
		for (Object o : os)
			addContents (o);
	}
	
	public static Xml elt (String name, Object... os)
	{
		return new Xml(name, os);
	}

	public Xml setNs (String namespace)
	{
		this.namespace = namespace; 
		return this;
	}
	
	public String getNs ()
	{
		return namespace;
	}

	public Xml setAttr (String key, String val)
	{
		attributes.put(key, val);
		attributeOrder.add(key);
		return this;
	}
	
	public Xml add (Object... os)
	{
		for (Object o : os)
			addContents(o);
		return this;
	}
		
	public String getAttr(String key)
	{
		return attributes.get(key);
	}
	
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
	
	public void writeToFile(File out) throws IOException
	{
		FileWriter writer = new FileWriter (out);
		Context context = new Context();
		context.identation = 0;
		flush (writer, context);
		writer.close();
	}
	
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

	private static class Context
	{
		int identation;
	}
}
