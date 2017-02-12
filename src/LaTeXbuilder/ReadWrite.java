/*
* This file is part of LaTeXbuilder.
* 
* LaTeXbuilder is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* LaTeXbuilder is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with LaTeXbuilder.  If not, see <http://www.gnu.org/licenses/>.
*/

package LaTeXbuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ReadWrite {

	//TODO add a method for serialization
	
	public static String readFile(String strFile){
		BufferedReader br;
		String strContent = null;
	    try {
	        br = new BufferedReader(new FileReader(strFile));
	        try {
	            String x;
	            while ( (x = br.readLine()) != null ) {
            		strContent = strContent +"\n"+x;
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    } catch (FileNotFoundException e) {
	        System.out.println(e);
	        e.printStackTrace();
	    }
	    
	    return strContent;
	}
	
	public static ArrayList<String> readFileLines(String strFile){
		
		BufferedReader br;
		ArrayList<String> lines = new ArrayList<String>();
	    try {
	        br = new BufferedReader(new FileReader(strFile));
	        try {
	            String x;
	            while ( (x = br.readLine()) != null ) {
            		lines.add(x);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    } catch (FileNotFoundException e) {
	        System.out.println(e);
	        e.printStackTrace();
	    }
	    
	    return lines;
	}
	
	public static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	public static boolean writeFile(ArrayList<String> lines, String filename){

		boolean boSuccess = false;
		BufferedWriter bw;
		try {
			File file = new File(filename);
			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			for(String element : lines){
				bw.write(element);
				bw.newLine();
			}
			bw.close();
			boSuccess = true;
		}
		catch(IOException e){
			boSuccess = false;
		}
		
		return boSuccess;
	}
	
	public static boolean writeFile(String content, String filename){

		boolean boSuccess = false;
		BufferedWriter bw;
		try {
			File file = new File(filename);
			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
			boSuccess = true;
		}
		catch(IOException e){
			e.printStackTrace();
			boSuccess = false;
		}
		
		return boSuccess;
	}
}
