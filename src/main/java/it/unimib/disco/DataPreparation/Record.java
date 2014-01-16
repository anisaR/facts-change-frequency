package it.unimib.disco.DataPreparation;

import java.util.HashMap;
import java.util.Map;


public class Record {


	    Map<Entry, String> components;

	    public enum Entry {ID, REF_ID, SUBJECT, PREDICATE, OBJECT, TIMESTAMP, OPERATION_TYPE};
	    public Record() {
	        components = new HashMap<Entry, String>();
	    }

	    public String get(Entry e) {
	        if (components.containsKey(e)) {
	            return components.get(e);
	        } else {
	            return null;
	        }
	    }

	    public void add(Entry key, String s) {
	        components.put(key, s);
	    }

	    public void remove(Entry key) {
	        if (components.containsKey(key)) {
	            components.remove(key);
	        }
	    }

	    public Record copy() {
	        Record f = new Record();
	        for (Entry key : components.keySet()) {
	            f.add(key, components.get(key));
	        }
	        return f;
	    }
	    
	    public String toString()
	    {
	        return components.toString();
	    }
}
