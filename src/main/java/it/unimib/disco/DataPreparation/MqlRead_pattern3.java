package it.unimib.disco.DataPreparation;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

public class MqlRead_pattern3 {
	  
	public static Properties properties = new Properties();
	  
	public static void main(String[] args) {
	    
		try {
	    	File directory = new File (".");
	    	properties.load(new FileInputStream(directory.getAbsolutePath()+"/src/main/resources/freebase.properties"));
	     
	      HttpTransport httpTransport = new NetHttpTransport();
	      HttpRequestFactory requestFactory = httpTransport.createRequestFactory();

	      BufferedWriter bw;
		  bw = new BufferedWriter(new FileWriter(new File(directory.getAbsolutePath()+"/src/main/resources/dataset_for_each_object_type/"+"music_musical_group.csv")));
	      List<String> ls_resources = ReadFiles.getURIs(new File(args[0]));

	      for(int z=0;z<ls_resources.size();z++)
	      {
	    	
	      String query = "[{\"type\":\"/music/musical_group\",\"mid\":\""+ls_resources.get(z)+"\",\"member\":[{\"member\":null,\"group\":null,\"role\":[{}],\"start\":null,\"end\":null}]}]";
	      GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/mqlread");
	      
	      url.put("query", query);
	      url.put("key", properties.get("API_KEY"));
	      HttpRequest request = requestFactory.buildGetRequest(url);
	      HttpResponse httpResponse = request.execute();

	      JSONObject s = new JSONObject(httpResponse.parseAsString());

	      JSONArray array_result = s.getJSONArray("result");
	      
		  //Record r = new Record();
	      System.out.println(ls_resources.get(z)+" "+array_result.length());
	      if(array_result!=null){
	      for (int i = 0; i < array_result.length(); i++) {
	    	    JSONObject res = array_result.getJSONObject(i);
	    	    JSONArray array_statistics = res.getJSONArray("member");
	    	   // System.out.println(array_statistics.length());
	    	    if(array_statistics!=null){
	    	    for(int j = 0; j < array_statistics.length(); j++){
	    	    	JSONObject rec = array_statistics.getJSONObject(j);
	    	    	
	    	    	String from = rec.get("start").toString();
	    	    	String to = rec.get("end").toString();
	    	    	long frequencyOfChange;
	    	    	
	    	    	if(!from.contains("null") && !to.contains("null")){
	    	    		
		    	    	Date fromD = stringToLong(from);
						Date toD = stringToLong(to);
						long millisecDistance = toD.getTime()-fromD.getTime();
						long dayDistance= millisecDistance/(1000 * 60 * 60 * 24);
						frequencyOfChange= (dayDistance/365)+1;
	    	    	}
	    	    	else{
	    	    		frequencyOfChange = -1;
	    	    	}
	    	    	String role =null;
	    	    	JSONArray array_position = rec.getJSONArray("role");

	    	    	if (array_position.length()>0){
	    	    		
	    	    		role =array_position.getJSONObject(0).get("name").toString();
	    	    	}
		    	   bw.write(rec.get("member").toString()+","+rec.get("group").toString()+","+role+","+from+","+to+","+frequencyOfChange+"\n" );

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
	
	public static Date stringToLong(String datestr){
		Date date = null;
		SimpleDateFormat[] FORMATS = {
				new SimpleDateFormat("yyyy-MM-dd"),
				new SimpleDateFormat("yyyy-MM"),
				new SimpleDateFormat("yyyy")
				};
		for (DateFormat formatter : FORMATS) {
	        try {	
	        	
	        	date = formatter.parse(datestr);
				break;
	        
	        } catch (java.text.ParseException ex) {
				continue;
	        }
	    }

		return date;
		
	}
	
}

