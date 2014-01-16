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

public class MqlRead_InternationalAppear {
	  
	public static Properties properties = new Properties();
	  
	public static void main(String[] args) {
	    
		try {
	    	File directory = new File (".");
	    	properties.load(new FileInputStream(directory.getAbsolutePath()+"/src/main/resources/freebase.properties"));
	     
	      HttpTransport httpTransport = new NetHttpTransport();
	      HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
	      int id=1, id_ref = 1;
	      BufferedWriter bw;
		  bw = new BufferedWriter(new FileWriter(new File(directory.getAbsolutePath()+"/src/main/resources/"+"data_collector_statistics.csv")));
		  bw.write("id"+","+"referenc_id"+","+"subject"+","+"predicate"+","+"object"+","+"timestamp"+","+"operation"+"\n" );
	      List<String> ls_resources = ReadFiles.getURIs(new File(args[0]));
	      
	      for(int z=0;z<ls_resources.size();z++){
	      String query = "[{\"/type/object/id\":\""+ls_resources.get(z)+"\",\"/soccer/football_player/total_international_appearances\":[{\"link\":{\"*\":null}}],\"/soccer/football_player/total_international_goals\":[{\"link\":{\"*\":null}}]}]";
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
	    	    JSONArray array_apper = res.getJSONArray("/soccer/football_player/total_international_appearances");
	    	    JSONArray array_goal = res.getJSONArray("/soccer/football_player/total_international_goals");
	    	    
	    	    if(array_apper!=null){
	    	    for(int j = 0; j < array_apper.length(); j++){
	    	    	JSONObject rec = array_apper.getJSONObject(j);
    	    		JSONObject link = rec.getJSONObject("link");   	    	
		    	    bw.write(id+","+id_ref+","+ls_resources.get(z)+","+link.get("master_property")+","+link.get("target_value")+","+link.get("timestamp")+","+link.get("operation")+"\n" );
		    	    id++;
	    	    }
	    	    }
    	    
	    	    if(array_goal!=null){
		    	for(int k = 0; k < array_goal.length(); k++){
		    		JSONObject rec = array_goal.getJSONObject(k);
    	    		JSONObject link = rec.getJSONObject("link");   	    	
		    	    bw.write(id+","+id_ref+","+ls_resources.get(z)+","+link.get("master_property")+","+link.get("target_value")+","+link.get("timestamp")+","+link.get("operation")+"\n" );
		    	    id++;
	    	    }
	    	    }
	    	    id_ref=id;	
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

