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

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import static java.util.Arrays.*;

import java.util.ArrayList;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.net.URLDecoder;
import java.nio.charset.Charset;

import org.ini4j.Wini;

import javafx.application.Application;

public class LaTeXbuilder {
	
	
	/* Members */
	
	private static String mStrFileCode 	 		= "code.tex";
	private static String mStrDirLaTeX 	 		= null;
	private static String mStrFileOut	 		= "out.png";
	private static String mStrFileRead 	 		= null;
	private static String mStrDirWorking 		= null;
	private static String mStrDirApp 	 		= null;
	private static String mStrFilePream			= null;
	private static boolean mIsDebug 	 		= false;
	private static boolean mDoEmbedCode	 		= false;
	private static Wini mIniConfig 		 		= null;
	private static Wini mIniBackup 		 	    = null;
	private static final String STR_CONFIG_NAME = "config.ini";
	private static final String STR_BCKP_NAME   = "backup.ini";

	
	/* Methods */
	
	public static void main(String[] args) {
		
		// When program is run in eclipse, only do so in debug mode
		mIsDebug = ManagementFactory.getRuntimeMXBean().
		    getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
		
		/* 
		 * Parse command line options array
		 */
		// Setup command line parser
		//TODO Add possibility to type in simple formula directly into command line
		OptionParser parser = new OptionParser();
		parser.acceptsAll(asList("r","read"), 
				"Read code embedded in PNG file.")
				.withRequiredArg();
		parser.acceptsAll(asList("b", "build"), 
				"Build latex code. ["+File.separator+"dir"+File.separator+"source.tex]")
				.withRequiredArg();
		parser.acceptsAll(asList("o", "output"), 
				"Output file ["+File.separator+"dir"+File.separator+"file.ext]")
				.withRequiredArg();
		parser.acceptsAll(asList("v", "verbose"), 
				"Be more chatty." );
		parser.acceptsAll(asList("d", "debug"), 
				"Enable printing of debug information." );
		parser.acceptsAll(asList("?", "h", "help"), 
				"Show help and exit." );
		parser.acceptsAll(asList("e", "embed"), 
				"Embed the latex source code in output file.");
		parser.acceptsAll(asList("s", "scan"), 
				"[Experimental] Scans an ASCII file for latex code enclosed in the appropriate XML tags and builds the code.")
				.withRequiredArg();
		parser.accepts("open-config", "Opens the config file with the standard editor and exits.");
		parser.accepts("latex-preamble", "The preamble file (including path).")
				.withRequiredArg();
		
		// --- Parse command line arguments
		OptionSet optionSet   = parser.parse( args );
		boolean hasCmdArgs    = optionSet.hasOptions();
		boolean doReadFile    = false;
		boolean doBuild       = false;
		boolean doScan        = false;
		boolean doPrintHelp   = false;
		boolean doReadConfig  = true;
		boolean doOpenConfig  = false;
		boolean hasLaTeXPream = false;
		if (hasCmdArgs){
			if (optionSet.has("v") || optionSet.has("verbose"))
				Printing.setVerbosity(true);
			if (optionSet.has("d") || optionSet.has("debug"))
				Printing.setDebug(true);
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
			if(optionSet.has("s") || optionSet.has("scan")){
				doScan = true;
				mStrFileCode = (String) optionSet.valueOf("s");
			}
			// Print the help and exit.
			if (optionSet.has("?") || optionSet.has("h") || optionSet.has("help")){
				doPrintHelp = true;
				doReadConfig = false;
			}
			if (optionSet.has("open-config")){
				doOpenConfig = true;
			}
			if(optionSet.has("latex-preamble")){
				hasLaTeXPream = true;
				mStrFilePream = (String) optionSet.valueOf("latex-preamble");
			}
			else{
			}
		}
		
		// --- Path to the current working directory
		mStrDirWorking = System.getProperty("user.dir");
		//Path to the executable
		if (mIsDebug){
			mStrDirApp = mStrDirWorking;
			mStrDirLaTeX = "latex";
		} else {
			mStrDirApp = getPath(LaTeXbuilder.class);
			mStrDirLaTeX = mStrDirApp+File.separator+"latex";
		}
		Printing.debug("App dir: "+mStrDirApp);
		Printing.debug("Working dir: "+mStrDirWorking);
		Printing.debug("Operating system: "+OsDetection.getOsString());
		
		// --- Parse the config file
		LaTeXService laTeXService = new LaTeXService();
		
		if(doReadConfig){			
			try {
				mIniConfig = new Wini(new File(mStrDirApp+File.separator+STR_CONFIG_NAME));
				Printing.info("Config file reading successful.", 1);
			} catch (IOException e1) {
				Printing.error("Could not read config file (IOException).");
				Printing.info("Using standard parameters.", 0);
				Printing.info("Path: "+mStrDirApp, 0);
			}
			File fileCfgStore = new File(mStrDirApp+File.separator+STR_BCKP_NAME);
			try {
				fileCfgStore.createNewFile();
				mIniBackup = new Wini(fileCfgStore);
			} catch (IOException e) {
				Printing.error("Could not create file 'config_store.inf' (IOException).");
				e.printStackTrace();
			}
			if (mIniBackup != null) {
				String temp = mIniBackup.get("gui", "workingdir", String.class);
				if (temp != null) GUI.setWorkingDir(temp);
			}
			if (mIniConfig != null){
				if(!hasLaTeXPream){
					mStrFilePream = mIniConfig.get("build", "latexPreambleFile", String.class);
				}
				String strBorder = mIniConfig.get("build", "border");
				int intPngQuality = mIniConfig.get("imagemagick", "quality", int.class);
				int intPngDensity = mIniConfig.get("imagemagick", "density", int.class);
				String strImgmgckParams = mIniConfig.get("imagemagick", "params", String.class); 
				String strImgmgckPath = null;
				if(OsDetection.getOS() == OsDetection.OS_WIN){
					strImgmgckPath = mIniConfig.get("imagemagick", "path", String.class);
				}
				laTeXService.setImagemagickParams(intPngDensity, intPngQuality, strImgmgckPath, strImgmgckParams);
	
				laTeXService.setDir(mStrDirLaTeX);
				LaTeXService.setLaTeXBuildParams(strBorder);
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
					LaTeXService.setPreambleFile(mStrFilePream);
					String strCode = ReadWrite.readFile(
							mStrDirWorking+File.separator+mStrFileCode, 
							Charset.defaultCharset());
					if (strCode != null)
						laTeXService.buildLaTeX(
								strCode, mStrDirWorking+File.separator+mStrFileOut, mDoEmbedCode);
					else {
						Printing.error("Could not read file "+mStrDirWorking+File.separator+mStrFileCode+" (IOException)");
					}
				}
				else if(doScan){
					//TODO Print better information
					//TODO add capability to only extract latex code but not build it (e.g. for inclusion in paper)
					String strCode = ReadWrite.readFile(mStrDirWorking+File.separator+mStrFileCode);
					ArrayList<LaTeXCodeItem> itemsList = XMLParser.getItems(strCode);
					for(int i=0; i<itemsList.size(); i++){
						laTeXService.buildLaTeX(
								itemsList.get(i).code, 
								mStrDirWorking+File.separator+itemsList.get(i).label, 
								mDoEmbedCode);
					}
					
				}
				else if(doReadFile){
					//TODO Read the file, create *.tex file in same folder and open an editor in same folder
					String strCode = null;
					try {
						strCode = laTeXService.readLaTeXCodeFromFile(mStrFileRead);
						if (strCode != null){
							ReadWrite.writeFile(strCode, new File(mStrDirLaTeX + mStrFileCode));
						}
					} catch (IOException e) {
						Printing.error("Could not read file (IOException)");
					}
				}
				else if (doOpenConfig){
					if (mIniConfig != null) {
						try {
							String strCmd = null;
							if (OsDetection.getOS() == OsDetection.OS_NIX)
								strCmd = "xdg-open "+mStrDirApp+File.separator+STR_CONFIG_NAME;
							else if (OsDetection.getOS() == OsDetection.OS_WIN)
								strCmd = "start "+mStrDirApp+File.separator+STR_CONFIG_NAME;
							if (strCmd != null){
								Runtime.getRuntime().exec(strCmd);	
							}
						} catch (IOException e) {
							Printing.error("Could not open confg file (IOException).");
						}
					}
					else {
						Printing.error("Could not open config file (not available?).");
					}
				}
			}
		} else { // --- No command line arguments given
			
			Application.launch(GUI.class, args);
		}
	}
	
	private static String getPath(Class<LaTeXbuilder> cls) {
	    String path = null;
		try {
			path = cls.getProtectionDomain().getCodeSource().getLocation().getPath();
			path = URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Printing.error("Exception in getPath(): UnsupportedEncodingException");
		}
		if(path != null){
		    int ix = path.indexOf("LaTeXbuilder.jar");
		    if(ix >= 0) {
		    	path = path.substring(0, ix);
		    }
			path = new File(path).getAbsolutePath();
		}
		return path;
	}
	
	/**
	 * Saves the given value Object to the given option, which lives in the given section.
	 * @param section The section name in the INI file in which the option lives.
	 * @param option The option name in the INI file.
	 * @param value The value to store.
	 * @param switchFile Two possible values: 0 for writing to the config file and 1 for writing to the backup file.
	 */
	public static void putIniArg(String section, String option, Object value, int switchFile){
		Wini ini = null;
		switch (switchFile) {
		case 0:
			ini = mIniConfig;
			break;
		case 1:
			ini = mIniBackup;
		default:
			Printing.debug(
					"In putBackupArg(): Invalid value "
					+switchFile+" for parameter \'switchFile\'.");
			break;
		}
		if (ini != null){
			ini.put(section, option, value);
			try {
				ini.store();
				Printing.debug("Stored value \'"+value+"\' in \'"+STR_BCKP_NAME+"\'.");
			} catch (IOException e) {
				Printing.error("Could not write to backup file \'"+STR_BCKP_NAME+"\' (IOException).");
			}
		}
	}
}
