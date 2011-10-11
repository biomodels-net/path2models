package uk.ac.ebi.sif2qual;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import nl.helixsoft.util.Xml;

import uk.ac.ebi.sif2qual.Network.Edge;
import uk.ac.ebi.sif2qual.Network.Node;

public class QualModel
{
	Xml sbml;
	
	private interface Op
	{
		abstract String getOperation();
		abstract List<Op> getChildren();		
		abstract Xml asXml();
	}
	
	private static Op makeTree (Node n)
	{
		if (n.getIncoming().size() == 1)
		{
			return makeOp (n.getIncoming().get(0).getSrc());
		}
		else
		{
			return new OrGate (n.getIncoming());
		}
		
	}
	
	private static Op makeOp(Node n)
	{
		if (isGate(n))
		{
			return new AndGate(n);
		}
		else
		{
			return new Leaf(n);
		}
	}

	private static abstract class Gate implements Op
	{
		public Xml asXml()
		{
			Xml result = Xml.elt (getOperation());
			for (Op o : getChildren())
			{
				result.add (o.asXml());
			}
			return result;
		}
	}
	
	private static class OrGate extends Gate
	{
		List<Op> children = new ArrayList<Op>();
		
		OrGate (List<Edge> incoming)
		{
			for (Edge e : incoming)
			{
				children.add(makeOp(e.getSrc()));
			}			
		}

		@Override
		public String getOperation()
		{
			return "or";
		}

		@Override
		public List<Op> getChildren()
		{
			return children;
		}

	}

	private static class AndGate extends Gate
	{
		private Node gate;
		List<Op> children = new ArrayList<Op>();
		
		AndGate (Node n)
		{
			this.gate = n;
			
			for (Edge e : n.getIncoming())
			{
				children.add(makeOp(e.getSrc()));
			}
		}
		
		@Override
		public String getOperation()
		{
			return "and";
		}

		@Override
		public List<Op> getChildren()
		{
			return children;
		}
	}
	
	private static class Leaf implements Op
	{
		Node n;
		
		Leaf (Node n)
		{
			this.n = n;
		}

		@Override
		public String getOperation()
		{
			return "apply";
		}

		@Override
		public List<Op> getChildren()
		{
			return Collections.emptyList();
		}
		
		public Xml asXml()
		{
			return Xml.elt (getOperation(), n.getId());
		}

	}
	
	private static class Transition
	{
		Set<Node> inputs = new HashSet<Node>();
		Node output;
		Op operation;
	}
	
	Stack <Node> remainingGates = new Stack<Node>();
	
	List <Node> species = new ArrayList<Node>();
	List <Transition> transitions = new ArrayList<Transition>();
	Map <Node, Transition> transitionByOutput = new HashMap<Node, Transition>();
	Map <Node, Transition> transitionByGate = new HashMap<Node, Transition>();
	
	private void createSbml()
	{
		Xml loQualitativeSpecies = Xml.elt ("listOfQualitativeSpecies");		
		for (Node n : species)
		{
			Xml qs = Xml.elt ("qualitativeSpecies").setAttr("id", n.getId());
			loQualitativeSpecies.add(qs);
		}

		Xml loTransitions = Xml.elt ("listOfTransitions");
		for (Transition t : transitions)
		{
			Xml loInputs = Xml.elt("listOfInputs");
			for (Node n : t.inputs)
			{
				loInputs.add (Xml.elt ("input").setAttr("ref", n.getId()));
			}			
			Xml loOutputs = Xml.elt("listOfOutputs");
			{
				Node n = t.output;
				loOutputs.add (Xml.elt ("output").setAttr("ref", n.getId()));
			}
			loTransitions.add(Xml.elt ("transition",
					loInputs, loOutputs,
					Xml.elt ("math", t.operation.asXml())
					));
		}

		sbml = Xml.elt("sbml", loQualitativeSpecies, loTransitions);
	}
	
	public void fromSif(Network sif)
	{
		transformNetwork (sif);
		createSbml();
	}

	private static boolean isGate (Node n)
	{
		return n.getId().startsWith("and");
	}
	
	private void transformNetwork(Network sif)
	{
		for (Node n : sif.getNodes())
		{
			if (!n.getId().startsWith("and"))
			{
				species.add(n);
			}
		}

		for (Edge e : sif.getEdges())
		{
			Node output = e.getDest();
			System.out.println (output.getId());
			if (!isGate(output))
			{
				if (transitionByOutput.containsKey(output))
				{
					continue;
				}
				else
				{
					Transition t = new Transition();
					t.output = output;
					transitionByOutput.put(output, t);
					transitions.add (t);
					t.operation = makeTree(output);
					t.inputs = new HashSet<Node>();
					addLeaves(t.inputs, t.operation);
				}
			}
		}		
	}

	private void addLeaves(Set<Node> set, Op operation)
	{
		if (operation instanceof Leaf)
		{
			set.add(((Leaf)operation).n);
		}
		else
		{
			for (Op i : operation.getChildren())
				addLeaves (set, i);
		}
	}

	public String toString()
	{
		return sbml.toString();
	}
}
