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

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import static java.util.Arrays.*;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;

import org.ini4j.Wini;

public class LaTeXbuilder {
	
	
	/* Members */
	
	private static String mStrFileCode 	 = "code.tex";
	private static String mStrDirLaTeX 	 = ".."+File.separator+"latex";
	private static String mStrFileOut	 = "out.png";
	private static String mStrFileRead 	 = null;
	private static String mStrDirWorking = null;
	private static String mStrDirApp 	 = null;
	private static boolean mIsDebug 	 = false;
	private static boolean mDoEmbedCode	 = false;
	static Wini mConfig 		 		 = null;
	static Wini mConfigStore 		 	 = null;

	
	/* Methods */
	
	public static void main(String[] args) {
		
		// When program is run in eclipse, only do so in debug mode
		mIsDebug = ManagementFactory.getRuntimeMXBean().
		    getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
		
		// Path to the current working directory
		mStrDirWorking = System.getProperty("user.dir");
		//Path to the executable
		if (mIsDebug)
			mStrDirApp = mStrDirWorking;
		else
			mStrDirApp = getPath(LaTeXbuilder.class);
		
		/* 
		 * Parse command line options array
		 */
		// Setup command line parser
		OptionParser parser = new OptionParser();
		parser.acceptsAll(asList("r","read"), "Read code embedded in PNG file.")
				.withRequiredArg();
		parser.acceptsAll(
				asList("b", "build"), 
				"Build latex code. ["+File.separator+"dir"+File.separator+"source.tex]")
				.withRequiredArg();
		parser.acceptsAll(asList("o", "output"), "Output file ["+File.separator+"dir"+File.separator+"file.ext]").withRequiredArg();
		parser.acceptsAll(asList("v", "verbose"), "Be more chatty." );
		parser.acceptsAll(asList("?", "h", "help"), "Show help and exit." );
		parser.acceptsAll(asList("e", "embed"), "Embed the latex source code in output file.");
		
		// --- Parse command line arguments
		OptionSet optionSet = parser.parse( args );
		boolean hasCmdArgs = optionSet.hasOptions();
		boolean doReadFile = false;
		boolean doBuild = false;
		boolean doPrintHelp = false;
		boolean doReadConfig = true;
		if (hasCmdArgs){
			if (optionSet.has("v") || optionSet.has("verbose"))
				Printing.setVerbosity(true);
			if (optionSet.has("r") || optionSet.has("read")){
				doReadFile = true;				
				//TODO debug the below
				mStrFileRead = (String) optionSet.valueOf("r");
				
			}
			if (optionSet.has("o") || optionSet.has("output")){
				mStrFileOut = (String) optionSet.valueOf("o");
			}
			if (optionSet.has("e") || optionSet.has("embed")){
				mDoEmbedCode = true;
			}
			if (optionSet.has("b") || optionSet.has("build")){
				doBuild = true;
				
				mStrFileCode = (String) optionSet.valueOf("b");
			}
			// Print the help and exit.
			if (optionSet.has("?") || optionSet.has("h") || optionSet.has("help")){
				doPrintHelp = true;
				doReadConfig = false;
			}
			else{
			}
		}
		
		// --- Parse the config file
		LaTeXService laTeXService = new LaTeXService();
		
		if(doReadConfig){			
			try {
				mConfig = new Wini(new File(mStrDirApp+File.separator+"config.ini"));
				Printing.info("Config file reading successful.", 0);
			} catch (IOException e1) {
				Printing.error("Could not read config file (IOException).");
				Printing.info("Using standard parameters.", 0);
				Printing.info("Path: "+mStrDirApp, 0);
			}
			File fileCfgStore = new File(mStrDirApp+File.separator+"config_store.inf");
			try {
				fileCfgStore.createNewFile();
				mConfigStore = new Wini(fileCfgStore);
			} catch (IOException e) {
				Printing.error("Could not create file 'config_store.inf' (IOException).");
				e.printStackTrace();
			}
			if (mConfigStore != null) {
				String temp = mConfigStore.get("build", "workingdir", String.class);
				if (temp != null) GUI.setWorkingDir(temp);
			}
			if (mConfig != null){
				int waitBuild = mConfig.get("build", "wait", int.class);
				mStrDirLaTeX = mConfig.get("build", "latexDir", String.class);
				String strFile = mConfig.get("build", "latexPreFile", String.class);
				int intPngQuality = mConfig.get("imagemagick", "quality", int.class);
				int intPngDensity = mConfig.get("imagemagick", "density", int.class);
				String strImgmgckParams = mConfig.get("imagemagick", "params", String.class);
	
				laTeXService.setWaitBuild(waitBuild);
				laTeXService.setDir(mStrDirApp+File.separator+mStrDirLaTeX);
				laTeXService.setPreambleFile(strFile);
				laTeXService.setImagemagickParams(intPngDensity, intPngQuality, strImgmgckParams);
			}
		}		

		if(hasCmdArgs){
			// --- Execute code based on command line arguments given
			if(doPrintHelp){
				try {
					parser.printHelpOn(System.out);
				} catch (IOException e) {
					Printing.error("Could not print the help (IOException)");
				}
			} else {
				if(doBuild){
					// --- Get contents from code.tex
					String strCode;
					try {
						strCode = ReadWrite.readFile(mStrDirWorking+File.separator+mStrFileCode, Charset.defaultCharset());
						laTeXService.buildLaTeX(
								strCode, mStrDirWorking+File.separator+mStrFileOut, mDoEmbedCode);
						//if(boSuccess) Printing.info("Build successful.", 0);
						//else Printing.info("Build failed.", 0);
					} catch (IOException e) {
						Printing.error("Could not read file "+mStrDirWorking+File.separator+mStrFileCode+" (IOException)");
						Printing.info("Path: "+mStrDirWorking, 0);
					}
				}
				
				else if(doReadFile){
					String strCode = null;
					try {
						strCode = laTeXService.readLaTeXCodeFromFile(mStrFileRead);
						if (strCode != null){
							ReadWrite.writeFile(strCode, mStrDirLaTeX + mStrFileCode);
						}
					} catch (IOException e) {
						Printing.error("Could not read file (IOException)");
					}
				}
			}
		} else {
			// --- No command line arguments given
			new GUI().setVisible(true);
		}
	}
	
	private static String getPath(Class<LaTeXbuilder> cls) {
	    String path = cls.getProtectionDomain().getCodeSource().getLocation().getPath();
	    int ix = path.indexOf("LaTeXbuilder.jar");
	    if(ix >= 0) {
	        return path.substring(0, ix);
	    } else {
	        return path;
	    }
	}
	
	public static void putConfigArg(String section, String option, Object value){
		if (mConfigStore != null){
			mConfigStore.put(section, option, value);
			try {
				mConfigStore.store();
			} catch (IOException e) {
				Printing.error("Could not write to config file (IOException)");
			}
		}
	}
}
