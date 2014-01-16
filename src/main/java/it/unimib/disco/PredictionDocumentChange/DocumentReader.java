package it.unimib.disco.PredictionDocumentChange;
import java.util.ArrayList;
import java.util.List;

public class DocumentReader {
	public static List<DocumentFeatures> readFact(List<ArrayList<String>> listofacts)
    {
		List<DocumentFeatures> list = new ArrayList<DocumentFeatures>();
		for (int i=1;i<listofacts.size();i++){
			for (int j=1; j<listofacts.get(i).size();j++){
				
			DocumentFeatures r = new DocumentFeatures();
			System.out.println(listofacts.get(i).get(0));
			
			r.add(DocumentFeatures.Entry.URI, listofacts.get(i).get(0));
			System.out.println(listofacts.get(0).get(j));
			r.add(DocumentFeatures.Entry.YEAR, listofacts.get(0).get(j));
			System.out.println(listofacts.get(i).get(j));
			
			r.add(DocumentFeatures.Entry.CHANGE, listofacts.get(i).get(j));
			r.add(DocumentFeatures.Entry.DOMAIN, null);
			r.add(DocumentFeatures.Entry.PUBLISHER, null);
			list.add(r);
			}
		}
        return list;
    }
}
