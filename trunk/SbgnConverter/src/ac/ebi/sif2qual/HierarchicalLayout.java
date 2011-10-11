package uk.ac.ebi.sif2qual;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import uk.ac.ebi.sif2qual.Network.Edge;
import uk.ac.ebi.sif2qual.Network.Node;

public class HierarchicalLayout
{
	public void doLayout (Network net)
	{
		// determine root node: one with most downstream nodes.
		
		DownstreamMap maxMap = null;
		
		for (Node n : net.getNodes())
		{
			DownstreamMap map = countDownStream (net, n);
			if (
					maxMap == null ||
					map.size > maxMap.size ||
					(map.size == maxMap.size && map.weightedSize > maxMap.weightedSize)
				)
			{
				maxMap = map;
			}
		}
		
		horizontalLayout(net, maxMap);
	}

	private void horizontalLayout (Network net, DownstreamMap map)
	{
		Multimap <Integer, Node> levelMap = new HashMultimap <Integer, Node>();
		for (Node n : net.getNodes())
		{
			Integer dist = map.distances.get(n);
			if (dist == null) dist = 0; //TODO, check, why this is necessary
			levelMap.put(dist, n);
		}
		
		double SPACING = 60.0;
		
		int maxNodesInLevel = 0;
		int levelNum = 0;
		
		for (Integer i : levelMap.keySet())
		{
			int nodesInLevel = levelMap.get(i).size();
			if (nodesInLevel > maxNodesInLevel) 
				maxNodesInLevel = nodesInLevel;
			if (i > levelNum)
				levelNum = i;
		}
		
		double width = (maxNodesInLevel + 1) * SPACING;
		
		int zeroSize = levelMap.get(0).size();
		double dx = (width / zeroSize); 
		double x = dx / 2;
		double y = SPACING / 2;
		
		// level 0
		for (Node n : levelMap.get(0))
		{
			n.setPos(x, y);
			x += dx;
		}
		
		y += SPACING;
	
		// lower levels
		for (int i = 0; i < levelNum; ++i)
		{
			int levelSize = levelMap.get(i + 1).size();
			if (levelSize == 0) continue;

			List <Node> sortedNodes = new ArrayList<Node>();
			sortedNodes.addAll (levelMap.get(i));
			Collections.sort (sortedNodes, new Comparator<Node>()
			{
				@Override
				public int compare(Node n0, Node n1)
				{
					return Double.compare(n0.getX(), n1.getX());
				}
			});
			
			dx = width / levelSize;
			x = dx / 2;
			
			for (Node n : sortedNodes)
			{
				for (Edge e : n.getOutgoing())
				{
					Node m = e.getDest();
					m.setPos(x, y);
				}
				x += dx;
			}
			
			y += SPACING;
		}
		
	}
	
	private class DownstreamMap
	{
		int weightedSize = 0;
		int size = 0;
		Map<Node, Integer> distances = new HashMap<Node, Integer>();		
	}
	
	//TODO: optimization by pre-processing leaves possible.
	private DownstreamMap countDownStream(Network net, Node n)
	{
		Set<Node> downstreamNodes = new HashSet<Node>();
		
		DownstreamMap result = new DownstreamMap();
		
		Stack<Node> processing = new Stack<Node>();
		processing.add(n);
		result.distances.put(n, 0);
				
		while (!processing.isEmpty())
		{
			Node m = processing.pop();
			int dist = result.distances.get(m);
			
			downstreamNodes.add(m);
			for (Edge e : m.getOutgoing())
			{
				Node dest = e.getDest();
				
				int newSize = dist + 1; 
				if (result.distances.containsKey(dest))
				{
					int oldSize = result.distances.get(dest);
					newSize = Math.max (newSize, oldSize);
					result.weightedSize -= oldSize;
				}
				result.distances.put(dest, newSize);
				result.weightedSize += newSize;
				
				if (!downstreamNodes.contains(dest))
					processing.add(e.getDest());
			}
		}
		
		result.size = downstreamNodes.size();
		
		return result;
	}
}
