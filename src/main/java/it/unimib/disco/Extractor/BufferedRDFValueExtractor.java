package it.unimib.disco.Extractor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BufferedRDFValueExtractor {
	private int BUFFER_LENGHT = 1000000;	
	private int fileBufferLength;
	
	private byte[] fileBuffer;
	private int byteRead;
	private BufferedInputStream bfis;
	private static Map<String, SoccerPlayer> playersDumps;
	
	public BufferedRDFValueExtractor(){
		fileBufferLength = BUFFER_LENGHT;
		fileBuffer = new byte[fileBufferLength];
		byteRead = 0;
		bfis = null;
		playersDumps = new HashMap<String, SoccerPlayer>();
	}
	

	public static void  main(String[] args) throws IOException, ClassNotFoundException{		
		//String filename = "G:\\DBpedia_Dump\\dbpedia_2012_05_31.nt\\dbpedia_2012_05_31.nt";
		String playersFilename = "D:\\tmp\\urilist\\Serie_A_footballers_ridotta.txt";
		//String sourceDir = "D:\\tmp\\prova";
		String sourceDir = "D:\\tmp\\clubPlayers\\dump";
		String outFileName = "D:\\tmp\\urilist\\SoccerPlayerGoals.csv";
		String outFileName2 = "D:\\tmp\\urilist\\SoccerPlayerGoalsTotalVar.csv";
		
		FileOutputStream fOut = new FileOutputStream(new File(outFileName));
		FileOutputStream fOut2 = new FileOutputStream(new File(outFileName2));
		BufferedRDFValueExtractor brve = new BufferedRDFValueExtractor();
		brve.extract(sourceDir, playersFilename);
		
		for(SoccerPlayer p : playersDumps.values()){
			p.serialize(fOut);
			p.serialize2(fOut2);
		}
		fOut.close();
		fOut2.close();
	}
	//public?
	public void extract(String sourceDir, String playersFilename){
		int notNumericOnGoals = 0;
		try{			
			DirectoryStream<Path> dumpDir = Files.newDirectoryStream(Paths.get(sourceDir));
			List<String> matchStrings = Files.readAllLines(Paths.get(playersFilename), Charset.defaultCharset());
			for(Path dumpPath : dumpDir){
				if(Files.isDirectory(dumpPath)) continue;
				
				System.out.println(dumpPath.toString());

				int playCount = 0;
				for(String str : matchStrings){
					FileInputStream fis = new FileInputStream(dumpPath.toString());
					bfis = new BufferedInputStream(fis);
					
					String matchStr = "<" + str + ">";
					byte[] matchBytes = matchStr.getBytes();
					//String matchStr = "<http://dbpedia.org/resource/Ronaldinho>";
					String matchStrGoals = "<http://dbpedia.org/property/goals>";
					String matchStrPosition = "<http://dbpedia.org/ontology/position>";
					int length = matchStr.length();
					byte[] matchBuffer = new byte[length];										
					
					System.out.println(matchStr + " " + playCount++);
					SoccerPlayer player = null;
					if(playersDumps.containsKey(matchStr)) player = playersDumps.get(matchStr);
					else{
						player = new SoccerPlayer(matchStr, dumpPath.toString());
						playersDumps.put(matchStr, player);
					}
					player.newDump();
					
					player.addDumpNames(dumpPath.toString());
					
					int b = 0;
					int i = 0;
					while(b != 26 && b != -1){
						i++;
						if(i == 581) 
							i = 581;
						b = 0;
						b = fillBuffer(matchBuffer, 0, length);							
						if(b != -1 && Arrays.equals(matchBytes, matchBuffer)){							
							int g = 0;
							int p = 0;
							b = fillBuffer();
							while(b == 32 || b == 9 || 
								 (b != 62 && (
								    ( g < matchStrGoals.length()    && b == (int)matchStrGoals.charAt(g)   ) ||
								    ( p < matchStrPosition.length() && b == (int)matchStrPosition.charAt(p)) 
								 ) ) ){
								if(b == (int)matchStrGoals.charAt(g)) g++;
								if(b == (int)matchStrPosition.charAt(p)) p++;
								b = fillBuffer();
							}
							if(b == 62){
								int k = Math.max(p, g) + 1;
								b = fillBuffer();
								if(k == matchStrGoals.length()){
									b = fillBuffer();
									char[] buf = {' ', ' ',' ',' ',' '};
									b = 0;
									int h = 0;
									do{ 
										b = fillBuffer();
										if(b >= 48 && b <= 57) buf[h++] = (char)b;
									}while(b >= 48 && b <= 57);
									char[] buf2 = new char[h];
									for(int x = 0; x < h; x++) buf2[x] = buf[x];
									String bufStr = new String(buf2);
									if(!bufStr.equals("")){
										int nGoals = Integer.parseInt(bufStr);
										//System.out.println("Player: " + matchStr + " line " + i + ": goals = " + nGoals + ", dump file: " + dumpPath.toString());
										player.addGoalNum(nGoals);
									}
									else{
										notNumericOnGoals++;
										//System.out.println("Not numeric on goals!!! " + matchStr + " line " + i + " " + notNumericOnGoals);
									}
								}
								else if(k == matchStrPosition.length()){
									char[] buf = new char[200];
									Arrays.fill(buf, ' ');
									int h = 0;
									do{ 
										b = fillBuffer();
										if(b != 64)buf[h++] = (char)b;
									}while(b != 64 && b != 62); // 64 = @
									String position = new String(buf);
									player.addRole(position);
									//System.out.println("match POSITION " + position + "at line " + i + " for player " + matchStr);
								}
							}/*else{
								System.out.println("NOT match goals at line " + i + " for player " + matchStr);
							}*/
						}
						while(b != 10 && b != 26 && b != -1){
							b = fillBuffer();
						}
					}
					bfis.close();
					fis.close();
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		System.out.println("Not numeric goals properties: " + notNumericOnGoals);
	}
	
	private int fillBuffer() throws IOException{
		int hasNext = 0;		
		if(byteRead == 0 || byteRead >= fileBufferLength) {
			fileBufferLength = BUFFER_LENGHT;
			hasNext = bfis.read(fileBuffer, 0, fileBufferLength);
			if(hasNext < fileBufferLength && hasNext > 0) 
				fileBufferLength = hasNext;
			byteRead = 0;
			if(hasNext < 0) return hasNext;
		}
		
		return fileBuffer[byteRead++];
	}
	
	private int fillBuffer(byte[] buffer, int offset, int length) throws IOException{
		int toRead = Math.min(fileBufferLength - byteRead, length);
		int read = 0;
		if(toRead > 0 && byteRead > 0){
			read = Math.min(length, toRead);
			System.arraycopy(fileBuffer, byteRead, buffer, 0, read);
			byteRead += read;
			toRead = length - toRead;
		}
		int hasNext = fillBuffer();
		if(hasNext < 0) return hasNext;
		if(toRead > 0){
			System.arraycopy(fileBuffer, --byteRead, buffer, read, toRead);
			byteRead += toRead;
		}
		
		return buffer[0];
	}
}
