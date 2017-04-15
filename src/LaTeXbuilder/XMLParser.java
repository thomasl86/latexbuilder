package LaTeXbuilder;

import java.util.ArrayList;
import java.util.Scanner;

public class XMLParser {
	
	//TODO add capability to deal with comment symbols for different languages (detect via file extension)
	/* Members */
	
	public static final String STR_TAG_ITEM = "latex";
	public static final String STR_TAG_CODE = "code";
	public static final String STR_TAG_FILE = "file";
	
	
	/* Methods */
	
	public static ArrayList<String> getItems(String strLabel, String strText){
		
		StringBuffer strBufContents = new StringBuffer();
		Scanner scanner = new Scanner(strText);
		ArrayList<String> items = new ArrayList<String>();
		
		boolean itemStart = false; 
		while(scanner.hasNextLine()){
			String strLine = scanner.nextLine();
			if (itemStart && strLine.contains("</"+strLabel+">")){
				itemStart = false;
				items.add(strBufContents.toString());
				strBufContents = new StringBuffer();
			}
			if (itemStart){
				strBufContents.append(strLine+"\n");
			}
			if (strLine.contains("<"+strLabel+">")){
				itemStart = true;
			}
			if (strLine.contains("<"+strLabel+">") && strLine.contains("</"+strLabel+">")){
				// Filter out content between start & end tag
				strLine = strLine.trim();
				int iStart = strLine.lastIndexOf("<"+strLabel+">")-1;
				int iEnd = strLine.indexOf("</"+strLabel+">");
				String strTagEnd = "</"+strLabel+">";
				String strContent = strLine.substring(iStart+strTagEnd.length(), iEnd);
				//Write content to buffer & array list
				items.add(strContent);
				itemStart = false;
			}
		}
		scanner.close();
		
		return items;
	}
	
	public static ArrayList<LaTeXCodeItem> getItems(String strText){
		
		ArrayList<LaTeXCodeItem> itemsList = new ArrayList<>();
		ArrayList<String> strItems = getItems(STR_TAG_ITEM, strText);
		
		for(int i=0; i<strItems.size(); i++){
			String curItem = strItems.get(i);
			ArrayList<String> listFilename = getItems(STR_TAG_FILE, curItem);
			String strFilename = null;
			if (listFilename.size() != 0){
				strFilename = listFilename.get(0).toString();
				strFilename = strFilename.replaceAll("\n", "").trim();
			} else {
				strFilename = "item_"+i+".pdf";
			}
			String strCode = getItems(STR_TAG_CODE, curItem).toString();
			strCode = strCode.substring(1, strCode.length()-1);
			itemsList.add(new LaTeXCodeItem(strFilename, strCode));
		}
		
		return itemsList;
	}

}