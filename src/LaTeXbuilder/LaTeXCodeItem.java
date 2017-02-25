package LaTeXbuilder;

public class LaTeXCodeItem {
	public String 	label;
	public String 	code;
	public int 		line;
	
	public LaTeXCodeItem(String filename, String code){
		this.label = filename;
		this.code = code;
	}
	
	public LaTeXCodeItem(String filename, String code, int line){
		this.label = filename;
		this.code = code;
		this.line = line;
	}
}
