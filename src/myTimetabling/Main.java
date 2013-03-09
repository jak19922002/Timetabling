package myTimetabling;

import java.io.IOException;

public class Main 
{

	
	public static void main(String[] args) throws IOException 
	{	
		//new TestData();
		new WriteXML();
	    new ReadXML();
		new ConvertToCnf(7);
		

	}

}
