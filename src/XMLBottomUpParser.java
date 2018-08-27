import java.util.ArrayList;
import java.util.Stack;


public class XMLBottomUpParser {
	
	// Object Variables

	private Stack<Integer> stateStack;
	private Stack<Integer> tokenStack;
	private Stack<String> tagMatchStack;
	private int currState;
	private int inputIndx;
	private int lookAhead;
	private ArrayList<Token> tokenList;
	private boolean reduceBool; 	// to simulate GOTO such that index in token string is not lost after reduce steps
	private Stack<Integer> rhsStack;
	private boolean errBool;
	private String traceStr;
	private String ruleStr;
	private String tagMatchStr;
	
	// *******************	Token and Variable Integer Codes ******************* //	
	
	// Terminals:
			static final int BOTTOM = -1; // bottom of stack symbol to indicate error
			static final int END = 0; // end of string symbol
			static final int GT = 1;
			static final int LT = 2;
			static final int GTFS = 3;
			static final int LTFS = 4;
			static final int EQ = 5;
			static final int NAME = 6; // push name tags on this one
			static final int PNAME = 7; // for popping tag names
			static final int ANAME = 8;
			static final int STRING = 9;
			static final int DATA = 10;
			
	// Variables:
			static final int VARIABLES = 11;
			static final int DOC = 12;
			static final int E = 13;
			static final int E0 = 14;
			static final int E1 = 15;
			static final int ED = 16;
			static final int A = 17;
			
			public static String getTypeStr(int val)
			{
				switch(val)
				{
					case (BOTTOM): { return "BOTTOM"; }
					case (END): { return "END"; }
					case (GT): { return "gt"; }
					case (LT): { return "lt"; }
					case (GTFS): { return "gtfs"; }
					case (LTFS): { return "ltfs"; }
					case (EQ): { return "eq"; }
					case (NAME): { return "name"; }
					case (PNAME): { return "name"; }
					case (ANAME): { return "name"; }
					case (STRING): { return "string"; }
					case (DATA): { return "data"; }
					
					case (DOC): { return "DOC"; }
					case (E): { return "E"; }
					case (E0): { return "E0"; }
					case (E1): { return "E1"; }
					case (ED): { return "ED"; }
					case (A): { return "A"; }
					
					default: return "err";
				}
			}
			
	public XMLBottomUpParser(ArrayList<Token> tokenList)
	{
		this.stateStack = new Stack<Integer>();
		this.tokenStack = new Stack<Integer>();
		this.rhsStack = new Stack<Integer>();
		this.tagMatchStack = new Stack<String>();
		this.tokenList = tokenList;

		// push -1 (error/bottom) and 0 on top of state stack
		tokenStack.push(BOTTOM);
		stateStack.push(BOTTOM);
		stateStack.push(0);
		this.currState = 0;
		this.inputIndx = 0;
		this.lookAhead = tokenList.get(0).getTypeVal();
		this.reduceBool = false;
		this.errBool = false;
		this.traceStr = "";
		this.ruleStr = "";
		this.tagMatchStr = "";
	}
	
	public void parseTokens()
	{
		
		// main parser loop
		// initializes with current state and token index set to 0
		
		System.out.println("State\tLook Ahead\tTerminals Consumed/Rule Applied");
		
		while (true)
		{
			
			// successful parse
			if (currState == 2) {
				System.out.println();
				System.out.println();
				System.out.println("Parse Successful!");
				return;
			}
			
			// parse error
			if (errBool) {
				printErr();
				return;
			}
			
			currState = stateStack.peek();
			System.out.println();
			traceStr = currState + "\t" + getTypeStr(lookAhead);
			System.out.print(traceStr);
			System.out.print("\t\t\t");
			//if (traceStr.length() < 8) System.out.print("\t");
			
			switch(currState)
			{
				case -1:
					System.out.println("State stack underflow!");
					errBool = true;
					break;
			
				case 0:
					if (lookAhead == LT) {
						stateStack.push(3);
						shiftParse();
					}
					
					else if (lookAhead == DOC) {
						stateStack.push(2);
						shiftParse();
					}
					
					else if (lookAhead == E) {
						stateStack.push(1);
						shiftParse();
					}
					
					else { errBool = true; }
					break;
					
				case 1:
					if (lookAhead == END)
					{
						rhsStack.clear();
						rhsStack.push(E);
						
						if (popHandle())
						{
							lookAhead = DOC;
							reduceBool = true;
							ruleStr = "DOC -> E";
						}
						else { errBool = true;  }
					}
					
					else { errBool = true;  }
					break;
					
				case 2: break;
					
				case 3:
					// Shift
					if (lookAhead == NAME || lookAhead == ANAME ) {
						stateStack.push(5);
						
						if (lookAhead == NAME) { // push to name check stack
							tagMatchStack.push(tokenList.get(inputIndx).toString().trim());
						}
						
						shiftParse();						
					}
					// Shift
					else if (lookAhead == E0 ) {
						stateStack.push(4);
						shiftParse();
					}
					else { errBool = true;  }
					break;
					
				case 4:
					// Non empty reduce
					if (lookAhead == END)
					{
						rhsStack.clear();
						rhsStack.push(LT);
						rhsStack.push(E0);
						
						if (popHandle())
						{
							lookAhead = E;
							reduceBool = true;
							ruleStr = "E -> lt E0";
						}
						else { errBool = true;  }
					}
					else { errBool = true;  }
					break;
					
				case 5:
					if (lookAhead == NAME || lookAhead == ANAME ) {
						stateStack.push(7);
						shiftParse();
					}
					
					else if (lookAhead == GTFS) {
						lookAhead = A;
						reduceBool = true;
						ruleStr = "A -> epsilon";
					}
					
					else if (lookAhead == GT) {
						lookAhead = A;
						reduceBool = true;
						ruleStr = "A -> epsilon";
					}
					
					else if (lookAhead == A) {
						stateStack.push(6);
						shiftParse();
					}
					else { errBool = true; }
					break;
					
				case 6:
					
					// Shift
					if (lookAhead == GTFS ) {
						stateStack.push(11);
						shiftParse();
					}
					
					// Shift
					else if (lookAhead == GT ) {
						stateStack.push(10);
						shiftParse();
					}
					
					// Shift
					else if (lookAhead == E1 ) {
						stateStack.push(9);
						shiftParse();
					}
					else { errBool = true; }
					break;
					
				case 7:
					
					// Shift
					if (lookAhead == EQ ) {
						stateStack.push(8);
						shiftParse();
					}
					else { errBool = true; }
					break;
					
				case 8:
					
					// Shift
					if (lookAhead == STRING ) {
						stateStack.push(15);
						shiftParse();
					}
					else { errBool = true; }
					break;
					
				case 9:
					
					// Non epsilon reduce
					if (lookAhead == END)
					{
						rhsStack.clear();
						rhsStack.push(NAME);
						rhsStack.push(A);
						rhsStack.push(E1);
						
						if (popHandle())
						{
							lookAhead = E0;
							reduceBool = true;
							ruleStr = "E0 -> name A E1";
						}
						else { errBool = true;  }
					}
					else { errBool = true; }
					break;
					
				case 10:
					
					// Shift
					if (lookAhead == DATA ) {
						stateStack.push(14);
						shiftParse();
					}
					
					// Shift
					else if (lookAhead == LT ) {
						stateStack.push(13);
						shiftParse();
					}
					
					// epsilon reduce
					else if (lookAhead == LTFS) {
						lookAhead = ED;
						reduceBool = true;
						ruleStr = "ED -> epsilon";
					}
					
					// Shift
					else if (lookAhead == ED ) {
						stateStack.push(12);
						shiftParse();
					}
					else { errBool = true; }
					break;
					
				case 11:
					
					// Non epsilon reduce
					if (lookAhead == END)
					{
						rhsStack.clear();
						rhsStack.push(GTFS);
						
						if (popHandle())
						{
							lookAhead = E1;
							reduceBool = true;
							ruleStr = "E1 -> gtfs";
						}
						else { errBool = true;  }
					}
					else { errBool = true; }
					break;
					
				case 12:
					
					// Shift
					if (lookAhead == LTFS ) {
						stateStack.push(20);
						shiftParse();
					}
					else { errBool = true; }
					break;
					
				case 13:
					
					// Shift
					if (lookAhead == NAME || lookAhead == ANAME  ) {
						
						stateStack.push(19);
						if (lookAhead == NAME) { // push to name check stack
							tagMatchStack.push(tokenList.get(inputIndx).toString().trim());
						}
						
						shiftParse();
					}
					
					// Shift
					else if (lookAhead == E0 ) {
						stateStack.push(18);
						shiftParse();
					}
					else { errBool = true; }
					break;
					
				case 14:
					
					// Shift
					if (lookAhead == DATA ) {
						stateStack.push(14);
						shiftParse();
					}
					
					// Shift
					else if (lookAhead == LT ) {
						stateStack.push(13);
						shiftParse();
					}
					
					// epsilon reduce
					else if (lookAhead == LTFS) {
						lookAhead = ED;
						reduceBool = true;
						ruleStr = "ED -> epsilon";
					}
					
					// Shift
					else if (lookAhead == ED ) {
						stateStack.push(17);
						shiftParse();
					}
					else { errBool = true; }
					break;
					
				case 15:
					
					// Shift
					if (lookAhead == NAME || lookAhead == ANAME  ) {
						stateStack.push(7);
						shiftParse();
					}
					
					// epsilon reduce
					else if (lookAhead == GTFS) {
						lookAhead = A;
						reduceBool = true;
						ruleStr = "A -> epsilon";
					}
					
					// epsilon reduce
					else if (lookAhead == GT ) {
						lookAhead = A;
						reduceBool = true;
						ruleStr = "A -> epsilon";
					}
					
					// Shift
					else if (lookAhead == A ) {
						stateStack.push(16);
						shiftParse();
					}
					else { errBool = true; }
					break;
					
				case 16:
					
					// Non epsilon reduce
					if (lookAhead == GTFS)
					{
						rhsStack.clear();
						rhsStack.push(ANAME);
						rhsStack.push(EQ);
						rhsStack.push(STRING);
						rhsStack.push(A);
						
						if (popHandle())
						{
							lookAhead = A;
							reduceBool = true;
							ruleStr = "A -> name eq string A";
						}
						else { errBool = true;  }
					}
					
					// Non epsilon reduce
					else if (lookAhead == GT)
					{
						rhsStack.clear();
						rhsStack.push(ANAME);
						rhsStack.push(EQ);
						rhsStack.push(STRING);
						rhsStack.push(A);
						
						if (popHandle())
						{
							lookAhead = A;
							reduceBool = true;
							ruleStr = "A -> name eq string A";
						}
						else { errBool = true;  }
					}
					else { errBool = true; }
					break;
					
				case 17:
					
					if (lookAhead == LTFS)
					{
						rhsStack.clear();
						rhsStack.push(DATA);
						rhsStack.push(ED);
						
						if (popHandle())
						{
							lookAhead = ED;
							reduceBool = true;
							ruleStr = "ED -> data ED";
						}
						else { errBool = true;  }
					}
					else { errBool = true; }
					break;
					
				case 18:
					
					// Shift
					if (lookAhead == DATA ) {
						stateStack.push(14);
						shiftParse();
					}
					
					// Shift
					else if (lookAhead == LT ) {
						stateStack.push(13);
						shiftParse();
					}
					
					// epsilon reduce
					else if (lookAhead == LTFS) {
						lookAhead = ED;
						reduceBool = true;
						ruleStr = "ED -> epsilon";
					}
					
					// Shift
					else if (lookAhead == ED ) {
						stateStack.push(23);
						shiftParse();
					}
					else { errBool = true; }
					break;
					
				case 19:
					
					// Shift
					if (lookAhead == NAME || lookAhead == ANAME  ) {
						stateStack.push(7);
						shiftParse();
					}
					
					// epsilon reduce
					else if (lookAhead == GTFS) {
						lookAhead = A;
						reduceBool = true;
						ruleStr = "A -> epsilon";
					}
					
					// epsilon reduce
					else if (lookAhead == GT) {
						lookAhead = A;
						reduceBool = true;
						ruleStr = "A -> epsilon";
					}
					
					// Shift
					else if (lookAhead == A ) {
						stateStack.push(22);
						shiftParse();
					}
					else { errBool = true; }
					break;
					
				case 20:
					
					// Shift
					if (lookAhead == NAME || lookAhead == ANAME  ) {
						stateStack.push(21);
						
						// pop from name tag check stack
						if (lookAhead == NAME) {
							tagMatchStr = tagMatchStack.pop();
							if (!(tokenList.get(inputIndx).toString().trim().equals(tagMatchStr))) {
								System.out.println();
								System.out.println("Tag name mismatch!");
								System.out.println("Expected: " + tagMatchStr);
								System.out.println("Got: " + tokenList.get(inputIndx).toString().trim());
								return;
							}
						}

						shiftParse();
					}
					else { errBool = true; }
					break;
					
				case 21:
					
					// Shift
					if (lookAhead == GT ) {
						stateStack.push(27);
						shiftParse();
					}
					else { errBool = true; }
					break;
					
				case 22:
					
					// Shift
					if (lookAhead == GTFS ) {
						stateStack.push(26);
						
						// pop from name tag check stack... this one pops empty tag name just pushed
							tagMatchStack.pop();
						
						shiftParse();
					}
					
					// Shift
					else if (lookAhead == GT ) {
						stateStack.push(25);
						shiftParse();
					}
					
					// Shift
					else if (lookAhead == E1 ) {
						stateStack.push(24);
						shiftParse();
					}
					else { errBool = true; }
					break;
					
				case 23:
					
					// Non epsilon reduce
					if (lookAhead == LTFS)
					{
						rhsStack.clear();
						rhsStack.push(LT);
						rhsStack.push(E0);
						rhsStack.push(ED);
						
						if (popHandle())
						{
							lookAhead = LTFS;
							reduceBool = true;
							ruleStr = "ED -> lt E0 ED";
						}
						else { errBool = true;  }
					}
					else { errBool = true; }
					break;
					
				case 24:
					
					// Non epsilon reduce
					if (lookAhead == DATA)
					{
						rhsStack.clear();
						rhsStack.push(NAME);
						rhsStack.push(A);
						rhsStack.push(E1);
						
						if (popHandle())
						{
							lookAhead = E0;
							reduceBool = true;
							ruleStr = "E0 -> name A E1";
						}
						else { errBool = true;  }
					}
					
					// Non epsilon reduce
					else if (lookAhead == LT)
					{
						rhsStack.clear();
						rhsStack.push(NAME);
						rhsStack.push(A);
						rhsStack.push(E1);
						
						if (popHandle())
						{
							lookAhead = E0;
							reduceBool = true;
							ruleStr = "E0 -> name A E1";
						}
						else { errBool = true;  }
					}
					
					// Non epsilon reduce
					else if (lookAhead == LTFS)
					{
						rhsStack.clear();
						rhsStack.push(NAME);
						rhsStack.push(A);
						rhsStack.push(E1);
						
						if (popHandle())
						{
							lookAhead = E0;
							reduceBool = true;
							ruleStr = "E0 -> name A E1";
						}
						else { errBool = true;  }
					}
					else { errBool = true; }
					break;
					
				case 25:
					
					// Shift
					if (lookAhead == DATA ) {
						stateStack.push(14);
						shiftParse();
					}
					
					// Shift
					else if (lookAhead == LT ) {
						stateStack.push(13);
						shiftParse();
					}
					
					// epsilon reduce
					else if (lookAhead == LTFS) {
						lookAhead = ED;
						reduceBool = true;
						ruleStr = "ED -> epsilon";
					}
					
					// Shift
					else if (lookAhead == ED ) {
						stateStack.push(28);
						shiftParse();
					}
					else { errBool = true; }
					break;
					
				case 26:
					
					// Non epsilon reduce
					if (lookAhead == DATA)
					{
						rhsStack.clear();
						rhsStack.push(GTFS);
						
						if (popHandle())
						{
							lookAhead = E1;
							reduceBool = true;
							ruleStr = "E1 -> gtfs";
						}
						else { errBool = true;  }
					}
					
					// Non epsilon reduce
					else if (lookAhead == LT)
					{
						rhsStack.clear();
						rhsStack.push(GTFS);
						
						if (popHandle())
						{
							lookAhead = E1;
							reduceBool = true;
							ruleStr = "E1 -> gtfs";
						}
						else { errBool = true;  }
					}
					
					// Non epsilon reduce
					else if (lookAhead == LTFS)
					{
						rhsStack.clear();
						rhsStack.push(GTFS);
						
						if (popHandle())
						{
							lookAhead = E1;
							reduceBool = true;
							ruleStr = "E1 -> gtfs";
						}
						else { errBool = true;  }
					}
					else { errBool = true; }
					break;
					
				case 27:
					
					// Non epsilon reduce
					if (lookAhead == END)
					{
						rhsStack.clear();
						rhsStack.push(GT);
						rhsStack.push(ED);
						rhsStack.push(LTFS);
						rhsStack.push(NAME);
						rhsStack.push(GT);
						
						if (popHandle())
						{
							lookAhead = E1;
							reduceBool = true;
							ruleStr = "E1 -> gt ED ltfs name gt";
						}
						else { errBool = true;  }
					}
					else { errBool = true; }
					break;
					
				case 28:
					
					// Shift
					if (lookAhead == LTFS ) {
						stateStack.push(29);
						shiftParse();
					}
					else { errBool = true; }
					break;
					
				case 29:
					
					// Shift
					if (lookAhead == NAME || lookAhead == ANAME ) {
						stateStack.push(30);
						
						// pop from name tag check stack
						if (lookAhead == NAME) {
							tagMatchStr = tagMatchStack.pop();
							if (!(tokenList.get(inputIndx).toString().trim().equals(tagMatchStr))) {
								System.out.println();
								System.out.println("Tag name mismatch!");
								System.out.println("Expected: " + tagMatchStr);
								System.out.println("Got: " + tokenList.get(inputIndx).toString().trim());
								return;
							}
						}
						
						shiftParse();
					}
					else { errBool = true; }
					break;
					
				case 30:
					
					// Shift
					if (lookAhead == GT ) {
						stateStack.push(31);
						shiftParse();
					}
					else { errBool = true; }
					break;
					
				case 31:
					
					// Non epsilon reduce
					if (lookAhead == DATA)
					{
						rhsStack.clear();
						rhsStack.push(GT);
						rhsStack.push(ED);
						rhsStack.push(LTFS);
						rhsStack.push(NAME);
						rhsStack.push(GT);
						
						if (popHandle())
						{
							lookAhead = E1;
							reduceBool = true;
							ruleStr = "E1 -> gt ED ltfs name gt";
						}
						else { errBool = true;  }
					}
					
					// Non epsilon reduce
					else if (lookAhead == LT)
					{
						rhsStack.clear();
						rhsStack.push(GT);
						rhsStack.push(ED);
						rhsStack.push(LTFS);
						rhsStack.push(NAME);
						rhsStack.push(GT);
						
						if (popHandle())
						{
							lookAhead = E1;
							reduceBool = true;
							ruleStr = "E1 -> gt ED ltfs name gt";
						}
						else { errBool = true;  }
					}
					
					// Non epsilon reduce
					else if (lookAhead == LTFS)
					{
						rhsStack.clear();
						rhsStack.push(GT);
						rhsStack.push(ED);
						rhsStack.push(LTFS);
						rhsStack.push(NAME);
						rhsStack.push(GT);
						
						if (popHandle())
						{
							lookAhead = E1;
							reduceBool = true;
							ruleStr = "E1 -> gt ED ltfs name gt";
						}
						else { errBool = true;  }
					}
					else { errBool = true; }
					break;
					
			
			
			} // end switch
			
			if (reduceBool) { System.out.print(ruleStr); }
		
		}
	}
	
	
	private void shiftParse()
	{
			tokenStack.push(lookAhead);
			
			if (!reduceBool)
				{
					System.out.print(tokenList.get(inputIndx).toString().trim());
				}
			//System.out.print(ruleStr);
			
			if (!reduceBool) inputIndx++;
			reduceBool = false;
			lookAhead = tokenList.get(inputIndx).getTypeVal();
	}
	
	private boolean popHandle()
	{
			while(!rhsStack.isEmpty())
			{
				if (rhsStack.pop() == tokenStack.pop())
				{
					stateStack.pop(); 	
				}
				else { return false; }
			}
			return true;
	}
	
	private void printErr()
	{
		System.out.println();
		System.out.println("-----Error!-----");
	}

}
