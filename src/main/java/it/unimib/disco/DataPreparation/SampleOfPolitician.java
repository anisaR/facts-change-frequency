package it.unimib.disco.DataPreparation;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Properties;
import org.json.JSONArray;

import org.json.JSONObject;

public class SampleOfPolitician {
	  
	public static Properties properties = new Properties();
	  
	public static void main(String[] args) {
	    
		try {
	    	File directory = new File (".");
	    	properties.load(new FileInputStream(directory.getAbsolutePath()+"/src/main/resources/freebase.properties"));
	     
	      HttpTransport httpTransport = new NetHttpTransport();
	      HttpRequestFactory requestFactory = httpTransport.createRequestFactory();

	      
	      String soccerName="cristiano_ronaldo";
	      
	      String query = "[{\"/type/object/id\":\"/en/"+soccerName+"\",\"/soccer/football_player/transfers\":[{\"selling_team\":[{\"link\":{\"*\":null}}],\"purchasing_team\":[{\"link\":{\"*\":null}}]}]}]";
	      GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/mqlread");
	      url.put("query", query);
	      url.put("key", properties.get("API_KEY"));
	      HttpRequest request = requestFactory.buildGetRequest(url);
	      HttpResponse httpResponse = request.execute();

	      JSONObject s = new JSONObject(httpResponse.parseAsString());

	      JSONArray array_result = s.getJSONArray("result");
	      
	      BufferedWriter bw;
		  bw = new BufferedWriter(new FileWriter(new File(directory.getAbsolutePath()+"/src/main/resources/selection_object_type/"+"sample_government_politician_age1940.csv")));
		  bw.write("subject"+","+"predicate"+","+"object"+","+"timestamp"+","+"operation"+"\n" );
		  
	      if(array_result!=null){
	      for (int i = 0; i < array_result.length(); i++) {
	    	    JSONObject res = array_result.getJSONObject(i);
	    	    JSONArray array_transfers = res.getJSONArray("/soccer/football_player/transfers");
	    	    
	    	    if(array_transfers!=null){
	    	    for(int j = 0; j < array_transfers.length(); j++){
	    	    	JSONObject rec = array_transfers.getJSONObject(j);
	    	    	
	    	    	for(int k = 0; k < rec.getJSONArray("selling_team").length(); k++){
		    	    	JSONObject link = rec.getJSONArray("selling_team").getJSONObject(k).getJSONObject("link");
		    	    	bw.write(soccerName+","+link.get("master_property")+","+link.get("target")+","+link.get("timestamp")+","+link.get("operation")+"\n" );
	    	    	}
	    	    	for(int k = 0; k < rec.getJSONArray("purchasing_team").length(); k++){
		    	    	JSONObject link = rec.getJSONArray("purchasing_team").getJSONObject(k).getJSONObject("link");
		    	    	bw.write(soccerName+","+link.get("master_property")+","+link.get("target")+","+link.get("timestamp")+","+link.get("operation")+"\n" );
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

