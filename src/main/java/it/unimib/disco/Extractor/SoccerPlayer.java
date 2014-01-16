package it.unimib.disco.Extractor;

/* Classi-base: 
 * 		- InputStream. Per leggere stream di byte. (I byte non coincidono con i caratteri)
 * 		- OutputStream. Per scrivere stream di byte.
 * 		- Reader. Per leggere stream di caratteri.
 * 		- Writer. Per scrivere stream di caratteri.
 * 
 * Classi-concrete:
 * 		ognuna delle 4 classi-base elencate qui sopra d� origine a una variet� di classi concrete che leggono o scrivono tipi specifici 
 * 		di stream.
 * 
 * OutputStream definisce metodi per:
 * 		- scrivere uno o pi� byte;
 * 		- svuotare il buffer;
 * 		- chiudere lo stream;
 * 
 * Rispetto alla classe-base OutputStream, ci sono due classi sorgenti (specializzano le classi astratte rispetto alla sorgente/destinazione):
	- ByteArrayOutputStream;
	- FileOutputStream. Implementa questi metodi nel caso particolare in cui l'output � un file, in cui nome � passato al costruttore di 
	 					FileOutputStream; in alternativa si pu� passare al costruttore un oggetto File.

*/
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SoccerPlayer {
	private List<String> dumpNames;
	private String name;
	private List<String> roles;
	private List< List<Integer> > dump;
	private int[] histogram;
	
	public SoccerPlayer(String name, String fname) {
		dumpNames = new ArrayList<String>();
		this.name = name;
		dump = new ArrayList< List<Integer> >();
		roles = new ArrayList<String>();
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
		roles.add("");
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
				outStr += ";" + roles.get(k++);
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
			int prevSize = prev.size();
			if(prev.size() < maxCols) 
				for(int k = 0; k < maxCols - prevSize; k++) prev.add(-1);
			if(curr.size() < maxCols) 
				for(int k = 0; k < maxCols - size; k++) curr.add(-1);
			
			try{
				if(!roles.get(i).equals(roles.get(i - 1))) 
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
		// come lo vuole maurino
		//double[] histoVar = histoVar();
		//double histoVarSum = 0.0;
		//for(int i = 1; i < histoVar.length; i++){
		//	histoVarSum += histoVar[i];
		//}
		//return histoVarSum / ((double)(histoVar.length - 1));
		
		// vecchio codice
		//return ((double)histoSum()) / ((double)(histogram.length - 1));
		
		// nostro ragionamento
		return ((double)histoSum()) / ((double)((histogram.length - 1) * dump.size()));
	}
	
	public double[] histoVar(){
		double[] histoVar = new double[histogram.length];
		// come lo vuole maurino
		//histoVar[0] = ((double)histogram[0]) / dump.size(); 
		//for(int i = 1; i < histogram.length; i++) histoVar[i] = ((double)histogram[i]) / (histogram.length - 1);
		//return histoVar;
		
		// vecchio codice
		for(int i = 0; i < histogram.length; i++) histoVar[i] = ((double)histogram[i]) / dump.size();
		return histoVar;
	}
	
	public void addDumpNames(String dumpName){
		dumpNames.set(dumpNames.size() - 1, dumpName);
	}

	public void addRole(String role) {
		roles.set(roles.size() - 1, role);
	}
}
