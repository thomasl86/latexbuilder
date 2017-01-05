package LaTeXbuilder;



public class Printing {

	
	/* Members */
	
	private static boolean mBoVerbose = false;

	
	/* Methods */
	
	public static void error(String msg)
   	{
		String stMessage = "ERROR: " + msg;
		//TODO implement method 'print() in the GUI class 
		//ServerGUI.print(stMessage);
		System.out.println(stMessage);
   	}
	

   	/**
	* Simple function to echo info to terminal. 
	* Verbose parameter can be set to 0 or >0 to indicate different verbose levels.
	* 0 means always print, 1 means only print if verbose is set to on. 
	* @param msg The message to print.
	* @param chVerbosity Verbose message or not
	*/
	public static void info(String msg, int verbosity)
	{
		String stMessage = "INFO: " + msg;
		if ((verbosity > 0) && mBoVerbose){
			//TODO call 'print() in GUI class
			//ServerGUI.print(stMessage);
			System.out.println(stMessage);
		}
		else if(verbosity == 0){
			//TODO call 'print() in GUI class
			//ServerGUI.print(stMessage);
			System.out.println(stMessage);
		}
	}
	
	public static void setVerbosity(boolean boVerbose){ mBoVerbose = boVerbose; }
}
