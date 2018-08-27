
public class Token {
	
	public final String strSeq;
	public int tokenType;
	
	public static final int END = 0;
	public static final int GT = 1;
	public static final int LT = 2;
	public static final int GTFS = 3;
	public static final int LTFS = 4;
	public static final int EQ = 5;
	public static final int NAME = 6;
	public static final int PNAME = 7;
	public static final int ANAME = 8;
	public static final int STRING = 9;
	public static final int DATA = 10;
	
	public Token(String strSeq, int tokenType)
	{
		this.strSeq = strSeq.trim();
		this.tokenType = tokenType;
	}
	
	public Token( Token cloneToken )
	{
		this.strSeq = cloneToken.strSeq;
		this.tokenType = cloneToken.tokenType;
	}
	
	public String toString()
	{
		return this.strSeq;
	}
	
	public void setType(int TYPE)
	{
		this.tokenType = TYPE;
	}
	
	public String getTypeStr()
	{
		switch(this.tokenType)
		{
			case (END): { return "END"; }
			case (GT): { return "GT"; }
			case (LT): { return "LT"; }
			case (GTFS): { return "GTFS"; }
			case (LTFS): { return "LTFS"; }
			case (EQ): { return "EQ"; }
			case (NAME): { return "NAME"; }
			case (PNAME): { return "PNAME"; }
			case (ANAME): { return "ANAME"; }
			case (STRING): { return "STRING"; }
			case (DATA): { return "DATA"; }
			
			default: return "err";
		}
	}
	
	public int getTypeVal()
	{
		return this.tokenType;
	}

}
