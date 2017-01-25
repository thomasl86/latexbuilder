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
* along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
*/

package LaTeXbuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.icafe4j.image.png.Chunk;
import com.icafe4j.image.png.ChunkType;
import com.icafe4j.image.png.PNGTweaker;
import com.icafe4j.image.png.TextBuilder;

public class LaTeXService extends Thread implements Runnable {

	
	/* Members */
	
	private String mStrCode;
	private String mStrFileOut;
	private boolean mBoEmbed 				= false;
	private static int mWaitBuild 			= 10;
	private static String mStrDir			= "latex";
	private static String mStrFilePream 	= "standalone_pre";
	private static int mPngDensity 			= 500;
	private static int mPngQuality 			= 100;
	private static String mStrImgmgckParams	= " ";
	private static final String STR_CODE_KEYWORD 	= "Code";
	private static final String STR_DELIMITER 		= "latex_delim";
	
	
	/* Constructors */
	
	public LaTeXService(){
		
	}
	
	public LaTeXService(String code){
		mStrCode = code;
	}
	
	
	/* Methods */
	
	public void setEnteredCode(String code){
		mStrCode = code;
	}
	
	public void setWaitBuild(int waitBuild){
		mWaitBuild = waitBuild;
	}
	
	public void setDir(String strDir){
		mStrDir = strDir;
	}
	
	public void setPreambleFile(String strFile){
		mStrFilePream = strFile;
	}
	
	public void setImagemagickParams(int intDensity, int intQuality, String params){
		mPngDensity = intDensity;
		mPngQuality = intQuality;
		mStrImgmgckParams = params;
	}
	
	public void buildLaTeX(String strCode, String strFilename, boolean boEmbed){
		
		mBoEmbed = boEmbed;
		mStrCode = strCode;
		mStrFileOut = strFilename;
		
		this.start();
		Printing.info("Building...", 0);
	}
	
	public String readLaTeXCodeFromFile(String strFilename) throws IOException{
		String strCode = null;
		String strChunks = null;

		strChunks = PNGTweaker.readTextChunks(strFilename);
		Scanner sc = new Scanner(strChunks);
		try{
		sc.useDelimiter(STR_DELIMITER);
		sc.next();
		strCode = sc.next();
		}
		catch(NoSuchElementException e){
			Printing.error("No LaTeX code found in image file.");
		}
		sc.close();
		
		return strCode;
	}
	
	@Override
	public void run() {
		
		String mStrCodeInsert = STR_DELIMITER + mStrCode + STR_DELIMITER;

		// --- Load contents of latex preamble file
		ArrayList<String> standaloneLines = 
				ReadWrite.readFile(mStrDir + File.separator + mStrFilePream + ".tex");
		standaloneLines.add(mStrCode);
		standaloneLines.add("\\end{document}");
		
		// --- Write assembled contents to ascii file
		ReadWrite.writeFile(standaloneLines, mStrDir + File.separator + "standalone.tex");
		
		// --- Concatenate code to provide to class Runtime
		String[] cmdarray = new String[2];
		cmdarray[0] =  
				"pdflatex -output-directory " + mStrDir
				+ " " + mStrDir + File.separator + "standalone.tex";
		cmdarray[1] = 
				"convert " + mStrImgmgckParams
				+ " -density " + mPngDensity + " -quality " + mPngQuality
				+ " "+mStrDir+File.separator+"standalone.pdf "+mStrFileOut;
		Printing.info(cmdarray[0], 1);
		Printing.info(cmdarray[1], 1);
		Process proc;
		// --- Build latex source 'standalone.tex' using pdflatex
		try {
			proc = Runtime.getRuntime().exec(cmdarray[0]);
			if (!proc.waitFor(mWaitBuild, TimeUnit.SECONDS)){
				Printing.error("Waiting time of " + mWaitBuild +" seconds expired before building finished. Read log for more info.");
				Printing.info("Build failed.", 0);
			}
			else {
				int result = proc.exitValue();
				if (result != 0)
					Printing.info("Build failed. Return value: "+result+".", 0);
				else{
					Printing.info("Success!", 0);
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		String strExtension = mStrFileOut.substring(mStrFileOut.length()-3, mStrFileOut.length());
		if(strExtension.equals("png")){
			// --- Convert standalone.pdf to png
			try {
				proc = Runtime.getRuntime().exec(cmdarray[1]);
				int result = proc.waitFor();
				if (result != 0)
					Printing.error("Conversion to PNG failed. ImageMagick returned "+result+".");
				else
					Printing.info("Conversion to PNG successful.", 0);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else if(strExtension.equals("pdf")) {
			// --- Just rename standalone.pdf to desired filename and move to new location
			File fileStandalone = new File(mStrDir+File.separator+"standalone.pdf");
			fileStandalone.renameTo(new File(mStrFileOut));
		} else {
			Printing.error("Extension "+strExtension+" unknown.");
		}
		
		if(mBoEmbed){
			// --- Construct the latex code to include in the PNG image"Code"
	        TextBuilder builder = new TextBuilder(ChunkType.ITXT)
	        		.keyword("Author").text("https://github.com/thomasl86/latexbuilder");
	        Chunk authorChunk = builder.build();
	        builder.keyword("Software").text("LaTeXbuilder");
	        Chunk softwareChunk = builder.build();
	        builder.keyword(STR_CODE_KEYWORD).text(mStrCodeInsert);
	        Chunk codeChunk = builder.build();
	
	        ArrayList<Chunk> chunks = new ArrayList<Chunk>();
	        chunks.add(authorChunk);
	        chunks.add(softwareChunk);
	        chunks.add(codeChunk);
 
	        FileInputStream fi;
	        FileOutputStream fo;
			try {
				fi = new FileInputStream(mStrFileOut);
				String strFileOutTmp = mStrFileOut.substring(0, mStrFileOut.length()-4)+"_e.png";
				fo = new FileOutputStream(strFileOutTmp);
				
				PNGTweaker.insertChunks(chunks, fi, fo);
	
		        fi.close();
		        fo.close();
		        
		        File fileOutTemp = new File(strFileOutTmp);
		        File fileOut = new File(mStrFileOut);
		        fileOut.delete();
		        fileOutTemp.renameTo(new File(mStrFileOut));
	
			} catch (FileNotFoundException e) {
				Printing.error(mStrFileOut+" could not be found.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
