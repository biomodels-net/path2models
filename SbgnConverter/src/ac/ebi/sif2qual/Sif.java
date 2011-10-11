package uk.ac.ebi.sif2qual;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.sif2qual.Network.Edge;
import uk.ac.ebi.sif2qual.Network.Node;

public class Sif
{	
	public  Network readFromFile(File f) throws IOException
	{
		Network result = new Network();
		
		int lineNo = 0;
		BufferedReader reader = new BufferedReader(new FileReader (f));
		String line;
		while ((line = reader.readLine()) != null)
		{
			lineNo ++;
			String [] fields = line.split("\\s+");
			if (fields.length < 3) throw new IOException("Not valid SIF at line " + lineNo);
			
			Node src = result.createOrGetNode(fields[0]);
			Node dest = result.createOrGetNode(fields[2]);
			result.createEdge (src, dest, fields[1]);
		}
		reader.close();
		
		return result;
	}	
}
