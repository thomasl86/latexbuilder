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
	private static String mStrDirLaTeX 	 = "../latex/";
	private static String mStrFileOut	 = "out.png";
	private static String mStrDirWorking = null;
	private static String mStrDirApp 	 = null;
	private static boolean mIsDebug 	 = false;
	private static boolean mBoEmbed 	 = false;

	
	/* Methods */
	
	public static void main(String[] args) {
		
		// When program is run in eclipse, only do so in debug mode
		mIsDebug = ManagementFactory.getRuntimeMXBean().
		    getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
		
		//Path to the current working directory
		mStrDirWorking = System.getProperty("user.dir");
		//Path to the executable
		if (mIsDebug)
			mStrDirApp = mStrDirWorking;
		else
			mStrDirApp = getPath(LaTeXbuilder.class);
		
		/*
		 *  Parse the config file
		 */
		Wini config = null;
		LaTeXService laTeXService = new LaTeXService();
		
		try {
			config = new Wini(new File(mStrDirApp+"/config.ini"));
			Printing.info("Config file reading successful.", 0);
		} catch (IOException e1) {
			Printing.error("Could not read config file (IOException).");
			Printing.info("Using standard parameters.", 0);
			Printing.info("Path: "+mStrDirApp, 0);
		}
		if (config != null){
			int waitBuild = config.get("build", "wait", int.class);
			mStrDirLaTeX = config.get("build", "latexDir", String.class);
			String strFile = config.get("build", "latexPreFile", String.class);
			int intPngQuality = config.get("imagemagick", "quality", int.class);
			int intPngDensity = config.get("imagemagick", "density", int.class);
			String strImgmgckParams = config.get("imagemagick", "params", String.class);

			laTeXService.setWaitBuild(waitBuild);
			laTeXService.setDir(mStrDirApp+"/"+mStrDirLaTeX);
			laTeXService.setPreambleFile(strFile);
			laTeXService.setImagemagickParams(intPngDensity, intPngQuality, strImgmgckParams);
		}
		
		/* 
		 * Parse commandline options array
		 */
		// Setup commandline parser
		OptionParser parser = new OptionParser();
		parser.acceptsAll(asList("r","read"), "Read code embedded in PNG file.")
				.withRequiredArg();
		parser.acceptsAll(
				asList("b", "build"), 
				"Build latex code. [/dir/source.tex]")
				.withRequiredArg();
		parser.acceptsAll(asList("o", "output"), "Output file [/dir/file.ext]").withRequiredArg();
		parser.acceptsAll( asList("v", "verbose"), "Be more chatty." );
		parser.acceptsAll( asList("?", "h", "help"), "Show help and exit." );
		parser.acceptsAll(asList("e", "embed"), "Embed the latex source code supplied with parameter -s in output file.");
		parser.accepts("gui", "Start the program with GUI");
		
		// Parse arguments and conditionally execute code
		OptionSet optionSet = parser.parse( args );
		if (optionSet.hasOptions()){
			if (optionSet.has("v") || optionSet.has("verbose"))
				Printing.setVerbosity(true);
			if (optionSet.has("gui")) new GUI().setVisible(true);
			/*
			if (optionSet.has("s") || optionSet.has("source")){
				mStrFileCode = (String) optionSet.valueOf("s");
			}
			*/
			if (optionSet.has("r") || optionSet.has("read")){
				
				//TODO debug the below
				String strFilename = (String) optionSet.valueOf("r");
				
				String strCode = null;
				try {
					strCode = laTeXService.readLaTeXCodeFromFile(strFilename);
					if (strCode != null){
						ReadWrite.writeFile(strCode, mStrDirLaTeX + mStrFileCode);
					}
				} catch (IOException e) {
					Printing.error("Could not read file (IOException)");
				}
				
			}
			if (optionSet.has("o") || optionSet.has("output")){
				mStrFileOut = (String) optionSet.valueOf("o");
			}
			if (optionSet.has("e") || optionSet.has("embed")){
				mBoEmbed = true;
			}
			if (optionSet.has("b") || optionSet.has("build")){
				
				mStrFileCode = (String) optionSet.valueOf("b");
				
				// --- Get contents from code.tex
				String strCode;
				try {
					strCode = ReadWrite.readFile(mStrDirWorking+"/"+mStrFileCode, Charset.defaultCharset());
					boolean boSuccess = laTeXService.buildLaTeX(
							strCode, mStrDirWorking+"/"+mStrFileOut, mBoEmbed);
					//if(boSuccess) Printing.info("Build successful.", 0);
					//else Printing.info("Build failed.", 0);
				} catch (IOException e) {
					Printing.error("Could not read file "+mStrDirWorking+"/"+mStrFileCode+" (IOException)");
					Printing.info("Path: "+mStrDirWorking, 0);
					//e.printStackTrace();
				}
			}
			// Print the help and exit.
			if (optionSet.has("?") || optionSet.has("h") || optionSet.has("help")){
				try {
					parser.printHelpOn(System.out);
				} catch (IOException e) {
					Printing.error("Could not print the help (IOException)");
				}
			}
		}
		else{
			// No command line arguments given
			new GUI().setVisible(true);
		}
		
	}
	
	private static String getPath(Class cls) {
	    String cn = cls.getName();
	    String rn = cn.replace('.', '/') + ".jar";
	    String path = cls.getProtectionDomain().getCodeSource().getLocation().getPath();
	    int ix = path.indexOf("LaTeXbuilder.jar");
	    if(ix >= 0) {
	        return path.substring(0, ix);
	    } else {
	        return path;
	    }
	}
}
