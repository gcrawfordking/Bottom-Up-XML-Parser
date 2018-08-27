import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.*;

public class XML_Scanner {

	public static ArrayList<Token> scan(String inputStr)
	{
		int i = 0 ; String token; int indx = 0; boolean tagBounds = true;
		ArrayList<Token> tokenList = new ArrayList<Token>();

		inputStr = inputStr.replaceAll("<!--[\\s\\S]*?-->", ""); // strip comments
		
		while (i < inputStr.length())
		{
			token = inputStr.substring(i, i + 1);
			
			switch(token)
			{
			
			case " ":
				i++;
				break;
			
			case "<":
				tagBounds = true;
				if (inputStr.substring(i + 1, i + 2).equals("/")) { // adds "</" token
					tokenList.add( new Token(inputStr.substring(i, i + 2), Token.LTFS) );
					i = i + 2;
				}
				
				else {
					tokenList.add( new Token(inputStr.substring(i, i + 1), Token.LT) ); // adds "<" token
					i = i + 1;
				}
				
				while (inputStr.substring(i, i + 1).equals(" ")) {
					i++;
				}
				
				indx = i;
				while (indx < inputStr.length() && !(inputStr.substring(indx, indx + 1).equals(" ")
						| inputStr.substring(indx, indx + 1).equals("<") | inputStr.substring(indx, indx + 1).equals(">")
						| inputStr.substring(indx, indx + 1).equals("=") | inputStr.substring(indx, indx + 1).equals("/"))) {
					indx++;
				}
				
				tokenList.add( new Token (inputStr.substring(i, indx), Token.NAME ));
				i = indx;
				
				break;
				
			case ">":
				tagBounds = false;
				if (inputStr.substring(i - 1, i).equals("/")) { // adds "/>" token
					tokenList.add( new Token(inputStr.substring(i - 1, i + 1), Token.GTFS));
				}
				else {
					tokenList.add( new Token(inputStr.substring(i, i + 1), Token.GT)); // adds ">" token
				}
				i++;
				break;
				
			case "=": // adds "=" token
					tokenList.add( new Token(inputStr.substring(i, i + 1), Token.EQ));
					i++;
				break;
				
			case "\'": // for single quote strings
				indx = i + 1;
				while (!(inputStr.substring(indx, indx + 1).equals("\'"))) {
					indx++;
				}
				
				tokenList.add( new Token(inputStr.substring(i, indx + 1), Token.STRING));
				i = indx + 1;
				break;
				
			case "\"":	// for double quote strings	
				indx = i + 1;
				while (!(inputStr.substring(indx, indx + 1).equals("\""))) {
					indx++;
				}
				
				tokenList.add( new Token(inputStr.substring(i, indx + 1), Token.STRING));
				i = indx + 1;
				break;
	
			default: // for data not enclosed in strings (will need to further sanitize)
				while (inputStr.substring(i, i + 1).equals(" ")) {
					i++;
				}
				
				if (inputStr.substring(i, i + 1).equals("/")) { // forward slashes handled with "<" and ">"
					i++;
					break;
				}
				
				indx = i;
				while (!(inputStr.substring(indx, indx + 1).equals("<") | inputStr.substring(indx, indx + 1).equals(">")
						| inputStr.substring(indx, indx + 1).equals("="))) {
					indx++;
				}
				
				if (tagBounds){
				tokenList.add( new Token(inputStr.substring(i, indx), Token.NAME)); }
				else { tokenList.add( new Token(inputStr.substring(i, indx), Token.DATA)); }
				
				
				i = indx;
				break;
			}
			
		}
		
		// add end of string symbol
		tokenList.add( new Token("", Token.END));
		
		
		//kludge to differentiate Attribute Names (ANAME) from regular NAMES... 'real' names must follow an LT or LTFS
		for (int j = 1; j < tokenList.size(); j++)
		{
			if ((tokenList.get(j).getTypeVal() == Token.NAME) && ((tokenList.get(j-1).getTypeVal() != Token.LT)) && (tokenList.get(j-1).getTypeVal() != Token.LTFS))
			{
				tokenList.get(j).setType(Token.ANAME);
			}
		}
		
		return tokenList;
		
	}

	public static boolean checkList(ArrayList<Token> tokenList) {
		
		// check for uniqueness of attribute names
    	Set<String> foundNames = new HashSet<>();
	    for(Token t : tokenList){
	    	if (t.getTypeVal() == Token.ANAME){
			        if(foundNames.contains(t.toString())){
			            System.out.println("Attribute Names not unique!");
			            System.out.println("Already have: " + foundNames.toString());
			            System.out.println("Got: " + t.toString());
			        	return false;
			        }
			        foundNames.add(t.toString());
		    	}
		    	if ((t.getTypeVal() == Token.GT) || (t.getTypeVal() == Token.GTFS))
		    	{
		    		foundNames.clear();
		    	}
		    }
		    
		   
	  // check for legal characters
		    
		    // char == Ordinary|Special|Reference
		    
		    String charRegexStr = "([^<>\"\'&]|&lt;|&gt;|&quot;|&apos;|&amp;|&#\\d+|&#x[a-fA-F0-9]+)";
		    Pattern stringPattern = Pattern.compile("(\'(" + charRegexStr + "|\")*\')|" + "(\"(" + charRegexStr + "|\')*\")");
		    Pattern dataPattern = Pattern.compile(charRegexStr + "+");
		    Pattern namePattern = Pattern.compile("[a-zA-Z|_|:][a-zA-Z|_|:|\\d|\\-|\\.]*");
		    
		    for(Token t : tokenList){
		    	
		    	// check names
		    	if (t.getTypeVal() == Token.ANAME || t.getTypeVal() == Token.NAME ){
		    		
		    		Matcher tempNameMatcher = namePattern.matcher(t.toString());
		    		if (tempNameMatcher.matches() == false){
		    			System.out.println("Invalid Name token: " + t.toString());
		    			return false;
		    		}
		    		
		    	}
		    	
			    // check strings for legal characters
		    	if (t.getTypeVal() == Token.STRING){
		    		
		    		Matcher tempStringMatcher = stringPattern.matcher(t.toString());
		    		if (tempStringMatcher.matches() == false) {
		    			System.out.println("Invalid String token: " + t.toString());
		    			return false;
		    		}
			    	
		    	}
		    	
			    // check data for legal characters
		    	if (t.getTypeVal() == Token.DATA ){
			    	
		    		Matcher tempDataMatcher = dataPattern.matcher(t.toString());
		    		if (tempDataMatcher.matches() == false) {
		    			System.out.println("Invalid Data token: " + t.toString());
		    			return false;
		    		}
		    		
		    	}
		    	
		    }
		    
		    
		return true;
	}
}
