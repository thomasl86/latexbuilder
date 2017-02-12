package LaTeXbuilder;

import java.util.ArrayList;
import java.util.Scanner;

//TODO Parser should be able to read tag that start and end in the same line
public class XMLParser {
	
	
	/* Members */
	
	public static final String STR_TAG_ITEM  = "latex";
	public static final String STR_TAG_CODE  = "code";
	public static final String STR_TAG_FILE = "file";
	
	
	/* Methods */
	
	public static ArrayList<String> getItems(String strLabel, String strText){
		
		StringBuffer strBufContents = new StringBuffer();
		Scanner scanner = new Scanner(strText);
		ArrayList<String> items = new ArrayList<String>();
		
		boolean itemStart = false; 
		int countLine = 0;
		int countLineStart = 0;
		while(scanner.hasNextLine()){
			countLine++;
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
				countLineStart = countLine;
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
			//TODO Change getContent to getItems such that one latex item can contain multiple code snippets
			String strFilename = getContent(STR_TAG_FILE, curItem);
			if (strFilename != null)
				strFilename = strFilename.replaceAll("\n", "").trim();
			else
				strFilename = "code_"+i+".tex";
			
			String strCode = getContent(STR_TAG_CODE, curItem);
			itemsList.add(new LaTeXCodeItem(strFilename, strCode));
		}
		
		return itemsList;
	}
	
	public static String getContent(String strLabel, String strText){
		
		StringBuffer strBufContents = new StringBuffer();
		Scanner scanner = new Scanner(strText);
		
		boolean itemStart = false;
		boolean hasItem = false;
		int countLine = 0;
		while(scanner.hasNextLine()){
			countLine++;
			String strLine = scanner.nextLine();
			if (itemStart && strLine.contains("</"+strLabel+">")){
				itemStart = false;
				hasItem = true;
			}
			if (itemStart){
				strBufContents.append(strLine+"\n");
			}
			if (strLine.contains("<"+strLabel+">")){
				itemStart = true;
			}
		}
		scanner.close();
		
		if (hasItem)
			return strBufContents.toString();
		else
			return null;
	}

}