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

/*
 * Source: http://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/
 */

package LaTeXbuilder;

public class OsDetection {
	
	
	/* Members */
	
	private static String OS = System.getProperty("os.name").toLowerCase();
	public static final int OS_NIX = 0;
	public static final int OS_WIN = 1;
	public static final int OS_OSX = 2;
	public static final int OS_SOL = 3;

	
	/* Methods */
	
	public static boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}

	public static boolean isMac() {
		return (OS.indexOf("mac") >= 0);
	}

	public static boolean isUnix() {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
	}

	public static boolean isSolaris() {
		return (OS.indexOf("sunos") >= 0);
	}
	
	/**
	 * Determines the operating system
	 * @return Returns identifiers for the detectable operating systems (see public members).
	 */
	public static int getOS(){
		if (isWindows()) {
			return OS_WIN;
		} else if (isMac()) {
			return OS_OSX;
        } else if (isUnix()) {
            return OS_NIX;
        } else if (isSolaris()) {
            return OS_SOL;
        } else {
            return -1;
        }
    }
	
	public static String getOsString(){
		return OS;
	}
}

