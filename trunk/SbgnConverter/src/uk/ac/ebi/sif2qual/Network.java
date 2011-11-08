package uk.ac.ebi.sif2qual;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import org.sbgn.ArcClazz;
import org.sbgn.GlyphClazz;
import org.sbgn.Language;
import org.sbgn.SbgnUtil;
import org.sbgn.bindings.Arc;
import org.sbgn.bindings.Arc.End;
import org.sbgn.bindings.Arc.Start;
import org.sbgn.bindings.Bbox;
import org.sbgn.bindings.Glyph;
import org.sbgn.bindings.Label;
import org.sbgn.bindings.Sbgn;

public class Network
{
        public void toSbgn(File out) throws JAXBException
	{
                int autoid = 0;
                
		HierarchicalLayout layout = new HierarchicalLayout();
		layout.doLayout(this);
		
		Sbgn sbgn = new Sbgn();
		org.sbgn.bindings.Map map = new org.sbgn.bindings.Map();
		sbgn.setMap(map);
		map.setLanguage(Language.AF.getName());
		
		Map<Node, Glyph> glyphMap = new HashMap <Node, Glyph>();
		
		for (Node n : this.getNodes())
		{
			Glyph g = new Glyph();
			Bbox b = new Bbox();
			b.setW(30);
			b.setH(30);
			b.setX((float)n.getX());
			b.setY((float)n.getY());
			g.setBbox(b);
			g.setClazz(GlyphClazz.BIOLOGICAL_ACTIVITY.getClazz());
			Label lab = new Label();
			lab.setText(n.getId());
			g.setLabel(lab);
			g.setId(n.getId());
			map.getGlyph().add(g);
			glyphMap.put (n, g);
		}
		
		for (Edge e : this.getEdges())
		{
			Arc a = new Arc();
			a.setClazz(
				e.getPredictate().equals ("-1") ? 
					ArcClazz.NEGATIVE_INFLUENCE.getClazz() :
					ArcClazz.POSITIVE_INFLUENCE.getClazz()
				);
			a.setSource(glyphMap.get(e.getSrc()));
			a.setTarget(glyphMap.get(e.getDest()));
			Start start = new Start();
			start.setX((float)e.getSrc().getX());
			start.setY((float)e.getSrc().getY());
			a.setStart(start);
			End end = new End();
			end.setX((float)e.getDest().getX());
			end.setY((float)e.getDest().getY());
			a.setEnd(end);
			a.setId("id" + autoid++);
			map.getArc().add(a);
		}
		
		SbgnUtil.writeToFile(sbgn, out);
	}
    
    
	public static class Node
	{
		private String id;
		double x = 0;
		double y = 0;
		private List<Edge> outgoing = new ArrayList<Edge>();
		private List<Edge> incoming = new ArrayList<Edge>();
		
		public List<Edge> getOutgoing() { return outgoing; }
		public List<Edge> getIncoming() { return incoming; }

		public void setPos (double x, double y)
		{
			this.x = x;
			this.y = y;
		}
		
		public Iterable<Node> getOutgoingNodes() 
		{
			List<Node> nodes = new ArrayList<Node>();
			for (Edge e : getOutgoing())
			{
				nodes.add(e.dest);
			}
			return nodes;
		}
		
		public Iterable<Node> getIncomingNodes()
		{
			List<Node> nodes = new ArrayList<Node>();
			for (Edge e : getIncoming())
			{
				nodes.add(e.src);
			}
			return nodes;
		}
		
		public Node (String id)
		{
			this.id = id;
		}

		public String getId()
		{
			return id;
		}
		
		public double getX()
		{
			return x;
		}
		
		public double getY()
		{
			return y;
		}
	}
	
	public static class Edge
	{
		private String predicate;
		
		private Node src;
		private Node dest;
		
		public Node getDest()
		{
			return dest;
		}
		
		public Node getSrc()
		{
			return src;
		}
		
		public Edge (Node src, Node dest, String predicate)
		{
			this.src = src;
			this.dest = dest;
			this.predicate = predicate;
		}
		
		@Override
		public String toString()
		{
			return  "Edge: " + src.getId() + " " + predicate + " " + dest.getId();
		}

		public String getPredictate()
		{
			return predicate;
		}
	}
	
	Map<String, Node> nodes = new HashMap<String, Node>();
	List<Edge> edges = new ArrayList<Edge>();

	public void createEdge (Node src, Node dest, String predicate)
	{
		Edge e = new Edge (src, dest, predicate);
		edges.add(e);
		dest.incoming.add(e);
		src.outgoing.add(e);		
	}
	
	public Node createOrGetNode (String name)
	{
		if (nodes.containsKey(name))
		{
			return nodes.get(name);
		}
		else
		{
			Node n = new Node(name);
			nodes.put (name, n);
			return n;
		}
	}

	public Collection<Node> getNodes()
	{
		return nodes.values();
	}

	public Collection<Edge> getEdges()
	{
		return edges;
	}

}
