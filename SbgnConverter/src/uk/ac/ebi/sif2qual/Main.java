package uk.ac.ebi.sif2qual;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

public class Main
{

	public static void main (String[] args) throws IOException, JAXBException
	{
		Sif sif = new Sif();
		Network network = sif.readFromFile (new File("/home/martijn/Dropbox/withSaezGroup/sifExample.sif"));
		
		QualModel qual = new QualModel();
		qual.fromSif (network);
		System.out.println (qual.toString());
		
                network.toSbgn(new File("/home/martijn/Desktop/sifExample.sbgn"));
	}
}
