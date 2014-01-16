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

public class MqlRead {
	  
	public static Properties properties = new Properties();
	  
	public static void main(String[] args) {
	    
		try {
	    	File directory = new File (".");
	    	properties.load(new FileInputStream(directory.getAbsolutePath()+"/src/main/resources/freebase.properties"));
	     
	      HttpTransport httpTransport = new NetHttpTransport();
	      HttpRequestFactory requestFactory = httpTransport.createRequestFactory();

	      
	      String query = "[{\"/people/person/date_of_birth>=\":\"1940\",\"limit\":5000,\"id\":null,\"type\":\"/government/politician\"}]";
	      GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/mqlread");
	      url.put("query", query);
	      url.put("key", properties.get("API_KEY"));
	      HttpRequest request = requestFactory.buildGetRequest(url);
	      HttpResponse httpResponse = request.execute();

	      JSONObject s = new JSONObject(httpResponse.parseAsString());

	      JSONArray array_result = s.getJSONArray("result");
	      
	      BufferedWriter bw;
		  bw = new BufferedWriter(new FileWriter(new File(directory.getAbsolutePath()+"/src/main/resources/"+"ls_potiticians.csv")));
		  
	      if(array_result!=null){
	      for (int i = 0; i < array_result.length(); i++) {
	    	   array_result.getJSONObject(i).get("id");
	    	   	bw.write(array_result.getJSONObject(i).get("id")+"\n" );
	      }
	      }
		  bw.flush();
		  bw.close();
		  } catch (Exception ex) {
		     ex.printStackTrace();
		  }
		  
		}
	
}

