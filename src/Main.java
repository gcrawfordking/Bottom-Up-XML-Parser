import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

// CS575 PROJECT 2

public class Main {

	public static void main (String args[]) throws IOException
	{
		// read in the input file, strip whitespace, and put in String Buffer
		final String fileName = "input3.xml";
		BufferedReader buffReader = new BufferedReader(new FileReader(fileName));
		
		String inputLine = null;
		StringBuilder strBldr = new StringBuilder();
		
		while((inputLine = buffReader.readLine()) != null) {
			strBldr.append(" " + inputLine.trim()); }
		
		buffReader.close();
		
		ArrayList<Token> tokenList = XML_Scanner.scan(strBldr.toString());

		
		// basic error checking
		if (XML_Scanner.checkList(tokenList) == true)
		{
			XMLBottomUpParser myParser = new XMLBottomUpParser(tokenList);
			myParser.parseTokens(); // run parser
		}
		
	}
}
