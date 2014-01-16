package it.unimib.disco.currentClub;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SoccerPlayer_02 {
	private List<String> dumpNames;
	private String name;
	private List<String> currentclubs;
	private List< List<Integer> > dump;
	private int[] histogram;
	
	public SoccerPlayer_02(String name, String fname) {
		dumpNames = new ArrayList<String>();
		this.name = name;
		dump = new ArrayList< List<Integer> >();
		currentclubs = new ArrayList<String>();
		histogram = null;
	}
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public void newDump(){
		dump.add(new ArrayList<Integer>());
		currentclubs.add("");
		dumpNames.add("");
	}
	
	public void addGoalNum(Integer goalNum){
		dump.get(dump.size() - 1).add(goalNum);
	}
	
	public void serialize2(FileOutputStream out) throws IOException {		
		if(histogram.length > 0){
			String outStr = name + ";" + String.format("%1$,.6f", totalVar()) + ";" + histogram[0] + "\n";
			if(!outStr.equals(""))out.write(outStr.getBytes());
		}
	}
	
	public void serialize(OutputStream out) throws IOException{
		createHistogram();
		
		String outStr = "";
		int k = 0;
		for(List<Integer> dumpline : dump){
			if(dumpline.size() > 0){
				outStr += dumpNames.get(k);
				outStr += ";" + name;
				outStr += ";" + currentclubs.get(k++);
				for(Integer i : dumpline){
					outStr += ";" + (i >= 0 ? i.toString() : "");
				}
				outStr += "\n";
			}
		}
		
		if(histogram.length > 1){
			outStr += ";" + name + " histogram";
			for(int histoVal : histogram){
				outStr += ";" + new Integer(histoVal).toString();
			}
			outStr += "\n";
		
			outStr += ";" + name + " histogram var";
			double[] histoVar = histoVar();
			for(double histoVal : histoVar){
				outStr += ";" + new Double(histoVal).toString();
			}
			outStr += "\n";
			
			outStr += ";" + name + " Total var;" + new Double(totalVar()).toString() + "\n";
		}
		
		if(!outStr.equals("")){
			outStr += "\n";
			out.write(outStr.getBytes());
		}
	}
	
	public void createHistogram() {
		int maxCols = 0;
		for(int i = 0; i < dump.size(); i++){
			int colsNum = dump.get(i).size();
			if(colsNum > maxCols) maxCols = colsNum;
		}
		
		histogram = new int[maxCols > 0 ? maxCols + 1 : 1];
		Arrays.fill(histogram, 0);
		for(int i = 1; i < dump.size(); i++){
			List<Integer> prev = dump.get(i - 1);
			List<Integer> curr = dump.get(i);
			int size = curr.size();
			if(curr.size() < maxCols) 
				for(int k = 0; k < maxCols - size; k++) curr.add(-1);
			
			try{
				if(!currentclubs.get(i).equals(currentclubs.get(i - 1))) 
					histogram[0]++;
			}catch(IndexOutOfBoundsException ex){
				histogram[0]++;
			}
			
			
			for(int j = 0; j < curr.size(); j++){
				try{
					
					if(prev.get(j) != curr.get(j)){
						histogram[j + 1]++;
					}
				}catch(IndexOutOfBoundsException ex){
					histogram[j + 1]++;
				}
			}
		}
	}
	
	public int histoSum(){
		if (histogram == null) return 0;
		
		int sum = 0;
		for(int i = 1; i < histogram.length; i++) sum += histogram[i];
		return sum;
	}
	
	public double totalVar(){
		return ((double)histoSum()) / ((double)(histogram.length - 1));
	}
	
	public double[] histoVar(){
		double[] histoVar = new double[histogram.length];
		for(int i = 0; i < histogram.length; i++) histoVar[i] = ((double)histogram[i]) / dump.size();
		return histoVar;
	}
	
	public void addDumpNames(String dumpName){
		dumpNames.set(dumpNames.size() - 1, dumpName);
	}

	public void addCurrentClub(String club) {
		currentclubs.set(currentclubs.size() - 1, club);
	}
}

