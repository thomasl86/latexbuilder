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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import com.icafe4j.image.png.Chunk;
import com.icafe4j.image.png.ChunkType;
import com.icafe4j.image.png.PNGTweaker;
import com.icafe4j.image.png.TextBuilder;

public class LaTeXService extends Thread implements Runnable {

	
	/* Members */
	
	private String mStrCode;
	private String mStrFileOut;
	private boolean mBoEmbed 				= false;
	private static String mStrDir			= "latex";
	private static String mStrFilePream 	= "";
	private static int mPngDensity 			= 500;
	private static int mPngQuality 			= 100;
	private static String mStrImgmgckPath   = " ";
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
	
	public void setDir(String strDir){
		mStrDir = strDir;
	}
	
	public static void setPreambleFile(String strFile){
		mStrFilePream = strFile;
	}
	
	public void setImagemagickParams(int intDensity, int intQuality, String path, String params){
		mPngDensity = intDensity;
		mPngQuality = intQuality;
		mStrImgmgckPath = path;
		mStrImgmgckParams = params;
	}
	
	public void buildLaTeX(String strCode, String strFilename, boolean boEmbed){
		
		mBoEmbed = boEmbed;
		mStrCode = strCode;
		mStrFileOut = strFilename;
		
		this.start();
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
		String strPreambleExt = 
				ReadWrite.readFile(
						mStrFilePream, 
						Charset.defaultCharset());
		if (strPreambleExt == null){
			Printing.error("Preamble file \'"+mStrFilePream+"\' loading failed (IOException).");
			strPreambleExt = "";
		}
		// --- Construct the standalone.tex file provided to pdflatex
		String strStandalone = 
				getPreamble() + "\n" 
				+ strPreambleExt 
				+ "\\begin{document}\n"
				+ mStrCode+"\n"
				+ "\\end{document}";
		
		// --- Write assembled contents to ascii file
		ReadWrite.writeFile(strStandalone, 
				new File(mStrDir + File.separator + "standalone.tex"));
		
		// --- Concatenate code to provide to class Runtime
		String[] cmdarray = new String[2];
		cmdarray[0] =  
				"pdflatex -output-directory " + mStrDir
				+ " -halt-on-error " + mStrDir + File.separator + "standalone.tex";
		if (OsDetection.getOS() == OsDetection.OS_WIN){
			cmdarray[1] = 
					"\""+mStrImgmgckPath+"convert.exe\" " + mStrImgmgckParams
					+ " -density " + mPngDensity + " -quality " + mPngQuality
					+ " "+mStrDir+File.separator+"standalone.pdf "+mStrFileOut;
		}
		else {
			cmdarray[1] = 
					"convert " + mStrImgmgckParams
					+ " -density " + mPngDensity + " -quality " + mPngQuality
					+ " "+mStrDir+File.separator+"standalone.pdf "+mStrFileOut;
		}
		Printing.debug(cmdarray[0]);
		Printing.info("Building...", 0);
		Process proc;
		// --- Build latex source 'standalone.tex' using pdflatex
		int resultLatex = -1;
		//TODO Automatically create latex folder before build and delete after build
		try {
			proc = Runtime.getRuntime().exec(cmdarray[0]);
			BufferedReader in = new BufferedReader(
                    new InputStreamReader(proc.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				// For some reason in Windows, while loop must be carried out even if isVerbose == false. 
				// Otherwise building won't complete (process won't end).
				// Linux is fine without it...I hate Windows...
				if (Printing.isVerbose())
					System.out.println(line);
			}
			resultLatex = proc.waitFor();
			if (resultLatex != 0){
				Printing.error("Build failed with return value "+resultLatex+". See log file for details.");
			}
			else{
				Printing.info("Success!", 0);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (resultLatex == 0){
			String strExtension = mStrFileOut.substring(mStrFileOut.length()-3, mStrFileOut.length());
			if(strExtension.equals("png")){
				// --- Convert standalone.pdf to png
				Printing.debug(cmdarray[1]);
				try {
					proc = Runtime.getRuntime().exec(cmdarray[1]);
					BufferedReader in = new BufferedReader(
		                    new InputStreamReader(proc.getErrorStream()));
					String line = null;
					while ((line = in.readLine()) != null) {
						System.out.println(line);
					}
					int result = proc.waitFor();
					if (result != 0)
						Printing.error("Conversion to PNG failed. ImageMagick returned "+result+".");
					else
						Printing.info("Conversion to PNG successful.", 0);
				} catch (IOException e1) {
					Printing.error("Conversion to PNG failed (IOException).");
					//e1.printStackTrace();
				} catch (InterruptedException e) {
					Printing.error("Conversion to PNG failed (InterruptedException).");
					//e.printStackTrace();
				}
			} else if(strExtension.equals("pdf")) {
				// --- Just rename standalone.pdf to desired filename and move to new location
				File fileStandalone = new File(mStrDir+File.separator+"standalone.pdf");
				fileStandalone.renameTo(new File(mStrFileOut));
			} else {
				Printing.error("Extension "+strExtension+" unknown.");
			}
			
			if(mBoEmbed){
				// --- Construct the latex code to include in the PNG image
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
	
	private String getPreamble(){
		String preamble = 
				"\\documentclass[%"
				+ "varwidth,"
				+ "\nfloat=true,"
				+ "\nclass=article,"
				+ "\npreview=false,"
				+ "\ncrop=true,"
				+ "\n10 pt,"
				+ "\nborder=5pt"
				+ "\n]{standalone}"
				+ "\n"
				+ "\n\\usepackage{tikz}"
				+ "\n\\usepackage{scalefnt}"
				+ "\n\\usepackage{transparent}"
				+ "\n\\usepackage{times} % assumes new font selection scheme installed"
				+ "\n\\usepackage[T1]{fontenc}"
				+ "\n\\usepackage{amsmath} % assumes amsmath package installed"
				+ "\n\\usepackage{amssymb}  % assumes amsmath package installed"
				+ "\n\\usepackage{mathtools}"
				+ "\n\\usepackage{mathptmx} % assumes new font selection scheme installed"
				+ "\n\\usepackage{import}"
				+ "\n\\usepackage{epstopdf}"
				+ "\n\\usepackage{pgfplots}"
				+ "\n\\pgfplotsset{compat=newest}"
				+ "\n%\\pgfplotsset{plot coordinates/math parser=true}"
				+ "\n\\newlength\\figureheight"
				+ "\n\\newlength\\figurewidth"
				+ "\n\\usepackage{paralist}"
				+ "\n\\usepackage{booktabs}"
				+ "\n\\usepackage{multirow}"
				+ "\n\\usepackage{sansmath}"
				+ "\n\\usetikzlibrary{positioning}"
				+ "\n\\usepackage{graphicx}";
		return preamble;
	}
}
