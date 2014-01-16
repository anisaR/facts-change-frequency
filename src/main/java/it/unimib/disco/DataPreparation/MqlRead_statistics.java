package it.unimib.disco.DataPreparation;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.List;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

public class MqlRead_statistics {
	  
	public static Properties properties = new Properties();
	  
	public static void main(String[] args) {
	    
		try {
	    	File directory = new File (".");
	    	properties.load(new FileInputStream(directory.getAbsolutePath()+"/src/main/resources/freebase.properties"));
	     
	      HttpTransport httpTransport = new NetHttpTransport();
	      HttpRequestFactory requestFactory = httpTransport.createRequestFactory();


	      int id=1, id_ref = 1;
	      BufferedWriter bw;
		  bw = new BufferedWriter(new FileWriter(new File(directory.getAbsolutePath()+"/src/main/resources/"+"data_collector_statistics1.csv")));
		  bw.write("id"+","+"referenc_id"+","+"subject"+","+"predicate"+","+"object"+","+"timestamp"+","+"operation"+"\n" );
	      List<String> ls_resources = ReadFiles.getURIs(new File(args[0]));
	      
	      for(int z=0;z<ls_resources.size();z++){
	      
	      String query = "[{\"/type/object/id\":\""+ls_resources.get(z)+"\",\"/soccer/football_player/statistics\":[{\"team\":[{\"link\":{\"*\":null}}],\"appearances\":[{\"link\":{\"*\":null}}],\"total_goals\":[{\"link\":{\"*\":null}}]}]}]";
	      GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/mqlread");
	      url.put("query", query);
	      url.put("key", properties.get("API_KEY"));
	      HttpRequest request = requestFactory.buildGetRequest(url);
	      HttpResponse httpResponse = request.execute();

	      JSONObject s = new JSONObject(httpResponse.parseAsString());

	      JSONArray array_result = s.getJSONArray("result");
	      
		  //Record r = new Record();
		  
	      if(array_result!=null){
	      for (int i = 0; i < array_result.length(); i++) {
	    	    JSONObject res = array_result.getJSONObject(i);
	    	    JSONArray array_statistics = res.getJSONArray("/soccer/football_player/statistics");
	    	    
	    	    if(array_statistics!=null){
	    	    for(int j = 0; j < array_statistics.length(); j++){
	    	    	JSONObject rec = array_statistics.getJSONObject(j);
	    	    	
	    	    	for(int k = 0; k < rec.getJSONArray("team").length(); k++){
		    	    	
	    	    		JSONObject link = rec.getJSONArray("team").getJSONObject(k).getJSONObject("link");
   	    	
	    	    		/*r.add(Record.Entry.ID, Integer.toString(id));
	    	    		r.add(Record.Entry.REF_ID, Integer.toString(id_ref));
		    	    	r.add(Record.Entry.SUBJECT, soccerName);
		    	    	r.add(Record.Entry.PREDICATE, link.get("master_property").toString());
		    	    	r.add(Record.Entry.OBJECT, link.get("target").toString());
		    	    	r.add(Record.Entry.TIMESTAMP, link.get("timestamp").toString());
		    	    	r.add(Record.Entry.OPERATION_TYPE, link.get("operation").toString());*/
		    	    	
		    	    	bw.write(id+","+id_ref+","+ls_resources.get(z)+","+link.get("master_property")+","+link.get("target")+","+link.get("timestamp")+","+link.get("operation")+"\n" );
		    	    	id++;
	    	    	}
	    	    	for(int k = 0; k < rec.getJSONArray("appearances").length(); k++){
		    	    	JSONObject link = rec.getJSONArray("appearances").getJSONObject(k).getJSONObject("link");
		    	    	bw.write(id+","+id_ref+","+ls_resources.get(z)+","+link.get("master_property")+","+link.get("target_value")+","+link.get("timestamp")+","+link.get("operation")+"\n" );
		    	    	id++;
	    	    	}
	    	    	
	    	    	for(int k = 0; k < rec.getJSONArray("total_goals").length(); k++){
		    	    	JSONObject link = rec.getJSONArray("total_goals").getJSONObject(k).getJSONObject("link");
		    	    	bw.write(id+","+id_ref+","+ls_resources.get(z)+","+link.get("master_property")+","+link.get("target_value")+","+link.get("timestamp")+","+link.get("operation")+"\n" );
		    	    	id++;
	    	    	}

		    	    id_ref=id;	
	    	    }
	    	    }

	      }
	      }
	      }
		  bw.flush();
		  bw.close();
		  } catch (Exception ex) {
		     ex.printStackTrace();
		  }
		  
		}
	
}

