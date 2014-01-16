package it.unimib.disco.PredictionDocumentChange;

import java.util.HashMap;
import java.util.Map;


public class DocumentFeatures {
	
	Map<Entry, String> records;

    public enum Entry {URI, DOMAIN, PUBLISHER, YEAR, CHANGE};
	
    public DocumentFeatures(){
		records = new HashMap<Entry, String>();
	}
    
    public String get(Entry e) {
        if (records.containsKey(e)) {
            return records.get(e);
        } else {
            return null;
        }
    }
    
    public void add(Entry key, String s) {
        records.put(key, s);
    }

    public void remove(Entry key) {
        if (records.containsKey(key)) {
            records.remove(key);
        }
    }

    public DocumentFeatures copy() {
        DocumentFeatures f = new DocumentFeatures();
        for (Entry key : records.keySet()) {
            f.add(key, records.get(key));
        }
        return f;
    }
    
    public String toString()
    {
        return records.toString();
    }
}
