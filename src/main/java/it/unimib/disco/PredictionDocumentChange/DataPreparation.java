package it.unimib.disco.PredictionDocumentChange;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataPreparation {
	public static void main(String[] args){
		List<String> records = ReadWrite.readNQFile(new File(args[0]));
		List<ArrayList<String>>  recordsArray = ReadWrite.readDocument(records);
		List<DocumentFeatures>  recordsFeatures = DocumentReader.readFact(recordsArray);
		
		for(DocumentFeatures d:recordsFeatures){
			System.out.println(d.get(DocumentFeatures.Entry.URI)+""+d.get(DocumentFeatures.Entry.YEAR)+" "+d.get(DocumentFeatures.Entry.CHANGE));
		}
	}
}
