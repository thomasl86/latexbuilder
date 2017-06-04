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



public class Printing {

	
	/* Members */
	
	private static boolean mBoVerbose = false;
	private static boolean mBoDebug = false;
	private static GUI	   mGui;
	private static String  mStrLineSep = "\n";

	
	/* Methods */
	
	public static void init(GUI gui){
		mGui = gui;
	}
	
	public static void error(String msg)
   	{
		String strMessage = "[ERROR] " + msg;
		System.out.println(strMessage);
		if(mGui!=null)
			mGui.printToLog(strMessage + mStrLineSep);
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
		String strMessage = "[INFO ] " + msg;
		if ((verbosity > 0) && mBoVerbose){
			System.out.println(strMessage);
			if(mGui!=null)
				mGui.printToLog(strMessage + mStrLineSep);
		}
		else if(verbosity == 0){
			System.out.println(strMessage);
			if(mGui!=null){
				String dbg = strMessage + mStrLineSep;
				mGui.printToLog(dbg);
			}
		}
	}
	
	public static void debug(String msg){
		
		String strMessage = "[DEBUG] " + msg;
		if(mBoDebug){
			System.out.println(strMessage);
			if(mGui!=null)
				mGui.printToLog(strMessage + mStrLineSep);
		}
	} 
	
	public static void setVerbosity(boolean boVerbose){ mBoVerbose = boVerbose; }
	
	public static void setDebug(boolean boDebug){ mBoDebug = boDebug; }
	
	public static boolean isVerbose(){ return mBoVerbose; }
}
