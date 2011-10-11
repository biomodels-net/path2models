package uk.ac.ebi.sif2qual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Network
{
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
