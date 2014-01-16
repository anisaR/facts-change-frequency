package it.unimib.disco.PredictionDocumentChange;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadWrite {
	
public static List<String> readNQFile(File rsListFile) {
		
		ArrayList<String> list = new ArrayList<String>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(rsListFile));
			
			String line = br.readLine();
			
			while(line!=null){
				if(line.charAt(0)!='#'){
					list.add(line);
				}
				line=br.readLine();
			}
			
			br.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;

	}

	public static List<ArrayList<String>> readDocument(List<String> file) {
	
		List<ArrayList<String>> fileArray = new ArrayList<ArrayList<String>>(); 
		for(String record:file){
			ArrayList<String> list = new ArrayList<String>();
			String subj=record.substring(0,record.indexOf(" "));
			list.add(subj);
			String remainingRecord= record.substring(record.indexOf(" ")+1);
			//remainingRecord=remainingRecord.substring(remainingRecord.indexOf('[')+1,remainingRecord.indexOf(']'));
			
			int begin;
			do{
				
				begin= remainingRecord.indexOf(" ");
				
				if(begin<0){
					
					list.add(remainingRecord.substring(0,remainingRecord.length()));
				}
				else{
					list.add(remainingRecord.substring(0, begin));

					remainingRecord=remainingRecord.substring(begin+1);
				}
				
			}while (begin>0);
			
			fileArray.add(list);
		}
		return fileArray;

	}
	
	
}
