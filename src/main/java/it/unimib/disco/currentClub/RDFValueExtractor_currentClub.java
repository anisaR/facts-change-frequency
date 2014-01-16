package it.unimib.disco.currentClub;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class RDFValueExtractor_currentClub {
	public static void  main(String[] args){		
		//String filename = "G:\\DBpedia_Dump\\dbpedia_2012_05_31.nt\\dbpedia_2012_05_31.nt";
		//String filename = "G:\\DBpedia_Dump\\dbpedia_2012_05_31\\prova.txt";
		String playersFilename = "D:\\tmp\\urilist\\SoccerPlayer.txt";
		//String sourceDir = "D:\\tmp\\prova";
		String sourceDir = "G:\\DBpedia_Dump";
		
		try{			
			DirectoryStream<Path> dumpDir = Files.newDirectoryStream(Paths.get(sourceDir));			
			List<String> matchStrings = Files.readAllLines(Paths.get(playersFilename), Charset.defaultCharset());
			for(Path dumpPath : dumpDir){
				if(Files.isDirectory(dumpPath)) continue;
				
				System.out.println(dumpPath.toString());
				
				for(String str : matchStrings){
					FileInputStream fis = new FileInputStream(dumpPath.toString());
					BufferedInputStream bfis = new BufferedInputStream(fis);
					
					String matchStr = "<" + str + ">";
					//String matchStr = "<http://dbpedia.org/resource/Ronaldinho>";
					String matchStrGoals = "<http://dbpedia.org/property/goals>";
					byte[] matchBuffer = new byte[matchStr.length()];
					int length = matchBuffer.length;
				
					//System.out.println(matchStr);
					
					int b = 0;
					int i = 0;
					while(b != 26 && b != -1){
						i++;
						b = 0;
						b = bfis.read(matchBuffer, 0, length);
						String readStr = "";
						if(b != -1)readStr = new String(matchBuffer);
						if(readStr.equals(matchStr)){							
							int k = 0;
							b = bfis.read();
							while(b == 32 || b == 9 || (b != 62 && b == (int)matchStrGoals.charAt(k++))){
								b = bfis.read();
							}
							if(b == 62){
								b = bfis.read();
								b = bfis.read();
								char[] buf = {' ', ' ',' ',' ',' '};
								b = 0;
								int h = 0;
								do{ 
									b = bfis.read();
									if(b >= 48 && b <= 57) buf[h++] = (char)b;
								}while(b >= 48 && b <= 57);
								char[] buf2 = new char[h];
								for(int x = 0; x < h; x++) buf2[x] = buf[x];
								String bufStr = new String(buf2);
								int nGoals = Integer.parseInt(bufStr);
								System.out.println("Player: " + matchStr + " line " + i + ": currentclub = " + nGoals + ", dump file: " + dumpPath.toString());
							}else{
								System.out.println("NOT match currentclub at line " + i + " for player " + matchStr);
							}
						}
						while(b != 10 && b != 26 && b != -1){
							b = bfis.read();
						}
					}

					fis.close();
					bfis.close();
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
