package com.mxgraph.io.vdx;

import com.mxgraph.util.mxPoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * General utilities for .vdx format support
 */
public class mxVdxUtils
{
	private static double screenCoordinatesPerCm = 40;

	private static final double CENTIMETERS_PER_INCHES = 2.54;

	/**
	 * Checks if the NodeList has a Node with name = tag.
	 * @param nl NodeList
	 * @param tag Name of the node.
	 * @return Returns <code>true</code> if the Node List has a Node with name = tag.
	 */
	public static boolean nodeListHasTag(NodeList nl, String tag)
	{
		boolean has = false;

		if (nl != null)
		{
			int length = nl.getLength();

			for (int i = 0; (i < length) && !has; i++)
			{
				has = (nl.item(i)).getNodeName().equals(tag);
			}
		}
		return has;
	}

	/**
	 * Returns the first Element that has name = tag in Node List.
	 * @param nl NodeList
	 * @param tag Name of the Element
	 * @return Element with name = 'tag'.
	 */
	public static Element nodeListTag(NodeList nl, String tag)
	{
		if (nl != null)
		{
			int length = nl.getLength();
			boolean has = false;

			for (int i = 0; (i < length) && !has; i++)
			{
				has = (nl.item(i)).getNodeName().equals(tag);

				if (has)
				{
					return (Element) nl.item(i);
				}
			}
		}
		return null;
	}

	/**
	 * Returns the  Element that has name = tag and Index = ix in Node List.
	 * @param nl NodeList
	 * @param tag name of the Element
	 * @return Element that has name = tag and Index = ix in Node List..
	 */
	public static Element nodeListTagIndexed(NodeList nl, String tag, String ix)
	{
		if (nl != null)
		{
			int length = nl.getLength();
			boolean has = false;

			for (int i = 0; (i < length) && !has; i++)
			{
				has = (nl.item(i)).getNodeName().equals(tag)
						&& ((Element) (nl.item(i))).getAttribute("IX").equals(
								ix);

				if (has)
				{
					return (Element) nl.item(i);
				}
			}
		}
		return null;
	}

	/**
	 * Returns a list with the elements included in the Node List that have name = tag.
	 * @param nl NodeList
	 * @param tag name of the Element.
	 * @return List with the indicated elements.
	 */
	public static List<Element> nodeListTags(NodeList nl, String tag)
	{
		ArrayList<Element> ret = new ArrayList<Element>();
		if (nl != null)
		{
			int length = nl.getLength();

			for (int i = 0; i < length; i++)
			{
				if (tag.equals((nl.item(i)).getNodeName()))
				{
					ret.add((Element) nl.item(i));
				}
			}
		}
		return ret;
	}

	/**
	 * Copy a given NodeList into a List<Element>
	 * @param nodeList Node List.
	 * @return List with the elements of nodeList.
	 */
	public static List<Node> copyNodeList(NodeList nodeList)
	{
		ArrayList<Node> copy = new ArrayList<Node>();
		int length = nodeList.getLength();

		for (int i = 0; i < length; i++)
		{
			copy.add((Node) nodeList.item(i));
		}
		return copy;
	}

	/**
	 * Returns the conversion factor from inches to pixels.
	 * @return converison factor.
	 */
	public static double conversionFactor()
	{
		double ret = 0;
		ret = screenCoordinatesPerCm * CENTIMETERS_PER_INCHES;
		return ret;
	}

	/**
	 * Adjusts a constraint in the range of 0..1
	 * @param constraint Point with the constraint.
	 */
	public static mxPoint adjustConstraint(mxPoint constraint)
	{
		constraint.setX(Math.max(0, constraint.getX()));
		constraint.setY(Math.max(0, constraint.getY()));
		constraint.setX(Math.min(1, constraint.getX()));
		constraint.setY(Math.min(1, constraint.getY()));

		return constraint;
	}

	/**
	 * Returns the string that represents the content of a given style map.
	 * @param styleMap Map with the styles values
	 * @return string that represents the style.
	 */
	public static String getStyleString(Map<String, Object> styleMap,
			String asig)
	{
		String style = "";
		Iterator<Object> it = styleMap.values().iterator();
		Iterator<String> kit = styleMap.keySet().iterator();

		while (kit.hasNext())
		{
			String key = kit.next();
			Object value = it.next();
			style = style + key + asig + value + ";";
		}
		return style;
	}

	/**
	 * Returns a text surrounded by tags html.
	 * @param text Text to be surrounded.
	 * @param tag Name of the tag.
	 * @return &lt tag &gt text &lt /tag &gt
	 */
	public static String surroundedByTags(String text, String tag)
	{
		return "<" + tag + ">" + text + "</" + tag + ">";
	}

	/**
	 * Converts the initial letter  of each word in text to uppercase
	 * @param text Text to be transformed.
	 * @return Text with initial capitals.
	 */
	public static String toInitialCapital(String text)
	{
		String[] words = text.split(" ");
		String ret = "";
		for (String word : words)
		{
			String begin = word.substring(0, 1);
			word = word.substring(1);
			begin = begin.toUpperCase();
			ret += begin + word;
		}
		return ret.substring(0, ret.length());
	}

	/**
	 * Trnsforms each lower case letter in text to small capital.
	 * @param text Text to be transformed.
	 * @param size Size of the original text.
	 * @return Text in small capitals.
	 */
	public static String toSmallCaps(String text, String size)
	{
		String ret = "";
		if (!size.equals(ret))
		{
			char a = 'a';
			char z = 'z';
			char[] letters = text.toCharArray();
			for (char c : letters)
			{
				if (c >= a && c <= z)
				{
					String s = String.valueOf(c);
					s = s.toUpperCase();
					ret += "<font style=\"font-size:" + Double.valueOf(size)
							/ 1.28 + "px\">" + s + "</font>";
				}
				else
				{
					ret += c;
				}
			}
		}
		else
		{
			ret = text;
		}
		return ret;
	}

	/**
	 * Create a style map from a String with style definitions.
	 * @param style Definition of the style.
	 * @param asig Asignation simbol used in 'style'.
	 * @return Map with the style properties.
	 */
	public static HashMap<String, Object> getStyleMap(String style, String asig)
	{
		HashMap<String, Object> styleMap = new HashMap<String, Object>();

		String[] entries = style.split(";");
		for (String entry : entries)
		{
			int index = entry.indexOf(asig);
			String key = entry.substring(0, index);
			String value = entry.substring(index + 1);
			styleMap.put(key, value);
		}

		return styleMap;
	}

	/**
	 * Print a list of mxPoint in the standard output.
	 * @param list Lis of mxPoint.
	 */
	public static void printPointList(List<mxPoint> list)
	{
		int i = 0;
		for (mxPoint p : list)
		{
			i++;
			System.out.println("Point " + i + " X=" + p.getX() + ", Y="
					+ p.getY());
		}
	}

	/**
	 * Creates an array with the cells contained in the map, ordered according
	 * the order of the keys in orderList.
	 * @param orderList List of keys in the order desired.
	 * @param map Map with the object to be put in the array.
	 * @return Array with the cells.
	 */
	public static Object[] getOrderArray(List<PageShapeIDKey> orderList,
			HashMap<PageShapeIDKey, Object> map)
	{
		int length = orderList.size();
		Object[] array = new Object[length];
		int i = 0;
		for (PageShapeIDKey key : orderList)
		{
			array[i] = map.get(key);
			i++;
		}
		return array;
	}
}
