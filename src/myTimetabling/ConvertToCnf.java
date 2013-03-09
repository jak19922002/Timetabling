package myTimetabling;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class ConvertToCnf 
{

    private static int count = 0;
    private static int numPeriodsInDay = 0;
	DatabaseAccess access = new DatabaseAccess();

	private int numStudents = access.getNumStudents();
	private int numSubjects = access.getNumSubjects();
	private int numTeachingGroups = access.getNumTeachingGroups();
	private int numPeriods = access.getNumPeriods();
	private int numTeachers = access.getNumTeachers();
	private int numLessons = access.getNumLessons();
	private int PTL = numPeriods * numTeachers * numLessons;
	private int PT = numPeriods * numTeachers;
	private int GB = numTeachingGroups * numSubjects;



	
public ConvertToCnf(int numPeriodsInDay) throws IOException
{
	access.createConnection();
	
	try {			
		File tempFile = new File("TempInputFile.txt");
		FileWriter fstream = new FileWriter(tempFile);
	    BufferedWriter out = new BufferedWriter(fstream);

		File file = new File("InputFile.txt");
		FileWriter filestream = new FileWriter(file);
	    BufferedWriter output = new BufferedWriter(filestream);
	    
	    ConvertToCnf.numPeriodsInDay = numPeriodsInDay;
	    
		/*constraint3(out);
	    constraint4(out);
		constraint1(out); //Unfortunately have to run constraints twice, first time to work out
		constraint2(out); //the number of clauses.
	    constraint5(out);
	    constraint6(out); 
	    constraint7(out);
	    //constraint8(out);
	    studentConstraint1(out);
	    studentConstraint2(out);
	    studentConstraint3(out);
	    bigConstraint(out); */
	    
	    output.write("p cnf " + (numLessons*numTeachers*numPeriods + numStudents*numSubjects*numTeachingGroups) + " " + 25000000);
	    output.newLine();
	    count = 0;
		constraint3(output);
	    constraint4(output);
		constraint1(output);
		constraint2(output);
	    constraint5(output);
	    constraint6(output);	    
	    //constraint7(output);
	    //constraint8(output);
	    //updatedConstraint7(output);
	    //updatedConstraint8(output);
	    studentConstraint1(output);
	    studentConstraint2(output);
	    //studentConstraint3(output);
	    studentConstraint4(output);
	    bigConstraint(output);
	    //ilp1(output);
	    
		if (out != null && output != null) {
            out.flush();
            out.close();
            output.flush();
            output.close();
		}
		access.shutdown();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}


//-------------------------------- constraints  ---------------------------------

//Every teacher can have at most one lesson on the same period
public void constraint1(BufferedWriter out) throws SQLException, IOException
{	

	for(int teacher = 1; teacher <= numTeachers; teacher++)  //for every teacher
	{
		for(int lesson : access.getLessonsCanTeach(teacher)) 
		{
			for(int secondLesson : access.getLessonsCanTeach(teacher))  //compare every two lessons
			{
				if(lesson >= secondLesson) //such that the first lesson is always smaller than the second in terms of ID number
					continue;
				
				for(int period : access.getAvailablePeriods(teacher))  //for every teacher
				{
					int first = period + numPeriods*(teacher-1) + PT*(lesson-1);  //converts period,teacher,lesson into single integer
					int second = period + numPeriods*(teacher-1) + PT*(secondLesson-1); //converts period,teacher,secondlesson into single integer
					
					out.write("-" + first + " -" + second + " 0");	//writes negative of integer to InputFile.txt				
					out.newLine();
					count++;
				}
				
			}
		}
	}
	System.out.println(count);
}

//A lesson can only be taught on a period the teacher is available
public void constraint2(BufferedWriter out) throws SQLException, IOException
{
	
	for(int teacher = 1; teacher <= numTeachers; teacher++)  //for every teacher
	{
		for(int lesson : access.getLessonsCanTeach(teacher)) 
		{			
				for(int period : access.getUnavailablePeriods(teacher))  //for every teacher
				{
					int first = period + numPeriods*(teacher-1) + PT*(lesson-1);  //converts period,teacher,lesson into single integer
					
					out.write("-" + first + " 0");	//writes negative of integer to InputFile.txt				
					out.newLine();
					count++;
				}
							
		}
	}
	System.out.println(count);
}

//Every existing lesson does actually occur
public void constraint3(BufferedWriter out) throws SQLException, IOException
{
	
		for(int lesson = 1; lesson <= numLessons; lesson++) 
		{			
			for(int teacher : access.getTeachersTeachLessons(lesson))  //for every teacher
			{
				for(int period : access.getAvailablePeriods(teacher))  //for every teacher
				{
					int first = period + numPeriods*(teacher-1) + PT*(lesson-1);  //converts period,teacher,lesson into single integer
					
					out.write(first + " ");	//writes integer to InputFile.txt				
				}			
			}
			out.write("0");
			out.newLine();
			count++;
		}
	System.out.println(count);
}

//Every non-existing lesson does not occur. IS THIS DOING ANYTHING!!
public void constraint4(BufferedWriter out) throws SQLException, IOException
{
	
	for(int teacher = 1; teacher <= numTeachers; teacher++)  //for every teacher
	{
		for(int lesson : access.getLessonsNotTeach(teacher)) 
		{			
				for(int period = 1; period <= numPeriods; period++)  //for every teacher
				{
					int first = period + numPeriods*(teacher-1) + PT*(lesson-1);  //converts period,teacher,lesson into single integer
					
					out.write("-" + first + " 0");	//writes negative of integer to InputFile.txt
					out.newLine();
					count++;
				}
							
		}
	}	
	System.out.println(count);

}

//Every lesson is scheduled exactly once
public void constraint5(BufferedWriter out) throws SQLException, IOException
{	

	for(int lesson = 1; lesson <= numLessons; lesson++) 
	{
		for(int teacher : access.getTeachersTeachLessons(lesson))  //for every teacher
		{
			for(int secondTeacher : access.getTeachersTeachLessons(lesson))  //for every teacher
			{
				if(teacher > secondTeacher)
					continue;
				
				if(teacher == secondTeacher)  //if the teacher is the same, must have same available periods
				{
					for(int period : access.getAvailablePeriods(teacher))  //for every teacher
					{
						for(int secondPeriod : access.getAvailablePeriods(secondTeacher))  //for every teacher
						{
							if(period >= secondPeriod)
								continue;
							
							int first = period + numPeriods*(teacher-1) + PT*(lesson-1);  //converts period,teacher,lesson into single integer
							int second = secondPeriod + numPeriods*(secondTeacher-1) + PT*(lesson-1); //converts period,teacher,secondlesson into single integer
							
							out.write("-" + first + " -" + second + " 0");	//writes negative of integer to InputFile.txt				
							out.newLine();
							count++;
						}				
					}	
				}
				else  //teachers are different
				{				
				for(int period : access.getAvailablePeriods(teacher))  //for every teacher
				{
					for(int secondPeriod : access.getAvailablePeriods(secondTeacher))  //for every teacher
					{
						//if(period >= secondPeriod)
						//	continue;
						
						int first = period + numPeriods*(teacher-1) + PT*(lesson-1);  //converts period,teacher,lesson into single integer
						int second = secondPeriod + numPeriods*(secondTeacher-1) + PT*(lesson-1); //converts period,teacher,secondlesson into single integer
						
						out.write("-" + first + " -" + second + " 0");	//writes negative of integer to InputFile.txt				
						out.newLine();
						count++;
					}				
				}
				}
			}
		}
	}
	System.out.println(count);
}

//Lessons belonging to the same teaching group should be distributed s.t. no lesson appears on the same day
public void constraint6(BufferedWriter out) throws SQLException, IOException
{	

	for(int teachingGroup = 1; teachingGroup <= numTeachingGroups; teachingGroup++)
	{
		if(access.getLessonsInTeachingGroup(teachingGroup).size() == 0)
			continue;
		
		for(int lesson : access.getLessonsInTeachingGroup(teachingGroup)) 
		{
			for(int secondLesson : access.getLessonsInTeachingGroup(teachingGroup))  //compare every two lessons
			{
				if(lesson >= secondLesson) //such that the first lesson is always smaller than the second in terms of ID number
					continue;
				
				for(int teacher : access.getTeachersSubject(teachingGroup))  //for every teacher
				{  
					for(int secondTeacher : access.getTeachersSubject(teachingGroup))
					{
						if(teacher > secondTeacher)
							continue;
						
						for(int period : access.getAvailablePeriods(teacher))  //for every teacher
						{
							for(int secondPeriod : access.getAvailablePeriods(secondTeacher))  //for every teacher
							{
								if(teacher == secondTeacher && period >= secondPeriod) //preventing duplicates
									continue;
								
								if(teacher != secondTeacher && period > secondPeriod && access.getAvailablePeriods(teacher).contains(secondPeriod))
									continue;  //if the two different sets of periods have two matching periods, would cause a duplicate
								
								if(access.dist(period, secondPeriod, numPeriodsInDay) == 0 || period > secondPeriod)
								{			
									if(access.dist(period, secondPeriod, numPeriodsInDay) != 0)  //Need to do this since could have p=4 and p'=1, which dist!=0 
										continue;						 //so would skip rest but next could be p=4,p'=3, which dist=0
									
									/* 4 different group combinations can occur */
									int first = period + numPeriods*(teacher-1) + PT*(lesson-1);  //converts period,teacher,lesson into single integer
									int second = secondPeriod + numPeriods*(secondTeacher-1) + PT*(secondLesson-1); //converts period,teacher,secondlesson into single integer
									
									out.write("-" + first + " -" + second + " 0");	//writes negative of integer to InputFile.txt				
									out.newLine();
									count++;
									
									int third = secondPeriod + numPeriods*(secondTeacher-1) + PT*(lesson-1);  //converts period,teacher,lesson into single integer
									int fourth = period + numPeriods*(teacher-1) + PT*(secondLesson-1); //converts period,teacher,secondlesson into single integer
									
									out.write("-" + third + " -" + fourth + " 0");	//writes negative of integer to InputFile.txt				
									out.newLine();
									count++;
									
									if(teacher == secondTeacher || period == secondPeriod)  //prevent duplicates
										continue;
									
									int fifth = period + numPeriods*(secondTeacher-1) + PT*(lesson-1);  //converts period,teacher,lesson into single integer
									int sixth = secondPeriod + numPeriods*(teacher-1) + PT*(secondLesson-1); //converts period,teacher,secondlesson into single integer
									
									out.write("-" + fifth + " -" + sixth + " 0");	//writes negative of integer to InputFile.txt				
									out.newLine();
									count++;
									
									int seventh = secondPeriod + numPeriods*(teacher-1) + PT*(lesson-1);  //converts period,teacher,lesson into single integer
									int eighth = period + numPeriods*(secondTeacher-1) + PT*(secondLesson-1); //converts period,teacher,secondlesson into single integer
									
									out.write("-" + seventh + " -" + eighth + " 0");	//writes negative of integer to InputFile.txt				
									out.newLine();
									count++;
								}
								else
									break;  //since next period comparison will have a bigger difference than the previous comparison
							}				//clearly won't be on the same day hence skip entire loop.
						}
					}
				}
			}
		}
	}
	System.out.println(count);
}
/*
//Teachers teaching hours must be at least a minimum
public void constraint7(BufferedWriter out) throws SQLException, IOException
{
	ArrayList<Integer> availablePeriods;
	int[] indices;
	int minVar = 0;
	
	for(int teacher = 1; teacher <= numTeachers; teacher++)  //For every teacher
	{
		if(access.getLessonsCanTeach(teacher).size() == 0)
			continue;
		
		availablePeriods = new ArrayList<Integer>();
		availablePeriods = access.getAvailablePeriods(teacher);  //array of all available periods for teacher
		
		minVar = availablePeriods.size() - access.getMinHours(teacher) + 1;  //Number of variables in each clause for min hours
		
		if(minVar >= availablePeriods.size())
			minVar = availablePeriods.size();
		
		CombinationGenerator combMin = new CombinationGenerator(availablePeriods.size(), minVar);;  //gets every combination for min hours
					
		while(combMin.hasMore())  //while there still exist combinations
		{		
			indices = combMin.getNext();
			
			for(int lesson : access.getLessonsCanTeach(teacher))  //for every lesson teacher can take
			{
			
				for(int i = 0; i < indices.length; i++)
				{					
					int num = availablePeriods.get(indices[i]) + numPeriods*(teacher-1) + PT*(lesson-1);

					out.write(num + " ");				
				}
						
			}
						
			out.write("0");
			out.newLine();
			count++;
	
		}
	}
	System.out.println(count);	
}
*/

public void updatedConstraint7(BufferedWriter out) throws SQLException, IOException
{
	ArrayList<Integer> availablePeriods;
	CombinationGenerator combMin;
	int[] indices;
	int minVar = 0;
	
	for(int teacher = 1; teacher <= numTeachers; teacher++)  //For every teacher
	{
		if(access.getLessonsCanTeach(teacher).size() == 0)
			continue;
		
		/* ----------------------- Monday ------------------------------ */
		
		availablePeriods = new ArrayList<Integer>();
		availablePeriods = access.getAvailablePeriodsMon(teacher);  //array of all available periods for teacher
		
		minVar = availablePeriods.size() - access.getMinHoursMon(teacher) + 1;  //Number of variables in each clause for min hours
		
		if(minVar >= availablePeriods.size())
			minVar = availablePeriods.size();
		
		combMin = new CombinationGenerator(availablePeriods.size(), minVar);;  //gets every combination for min hours
					
		while(combMin.hasMore())  //while there still exist combinations
		{		
			if(minVar >= availablePeriods.size())
				break;
			
			indices = combMin.getNext();
			
			for(int lesson : access.getLessonsCanTeach(teacher))  //for every lesson teacher can take
			{
			
				for(int i = 0; i < indices.length; i++)
				{					
					int num = availablePeriods.get(indices[i]) + numPeriods*(teacher-1) + PT*(lesson-1);

					out.write(num + " ");				
				}
						
			}
						
			out.write("0");
			out.newLine();
			count++;
	
		}
		
		/* ----------------------- Tuesday ------------------------------ */
		
		availablePeriods = new ArrayList<Integer>();
		availablePeriods = access.getAvailablePeriodsTues(teacher);  //array of all available periods for teacher
		
		minVar = availablePeriods.size() - access.getMinHoursTues(teacher) + 1;  //Number of variables in each clause for min hours
		
		if(minVar >= availablePeriods.size())
			minVar = availablePeriods.size();
		
		combMin = new CombinationGenerator(availablePeriods.size(), minVar);;  //gets every combination for min hours
					
		while(combMin.hasMore())  //while there still exist combinations
		{		
			if(minVar >= availablePeriods.size())
				break;
			
			indices = combMin.getNext();
			
			for(int lesson : access.getLessonsCanTeach(teacher))  //for every lesson teacher can take
			{
			
				for(int i = 0; i < indices.length; i++)
				{					
					int num = availablePeriods.get(indices[i]) + numPeriods*(teacher-1) + PT*(lesson-1);

					out.write(num + " ");				
				}
						
			}
						
			out.write("0");
			out.newLine();
			count++;
	
		}
		
		/* ----------------------- Wednesday ------------------------------ */

		
		availablePeriods = new ArrayList<Integer>();
		availablePeriods = access.getAvailablePeriodsWed(teacher);  //array of all available periods for teacher
		
		minVar = availablePeriods.size() - access.getMinHoursWed(teacher) + 1;  //Number of variables in each clause for min hours
		
		if(minVar >= availablePeriods.size())
			minVar = availablePeriods.size();
		
		combMin = new CombinationGenerator(availablePeriods.size(), minVar);;  //gets every combination for min hours
					
		while(combMin.hasMore())  //while there still exist combinations
		{		
			if(minVar >= availablePeriods.size())
				break;
			
			indices = combMin.getNext();
			
			for(int lesson : access.getLessonsCanTeach(teacher))  //for every lesson teacher can take
			{
			
				for(int i = 0; i < indices.length; i++)
				{					
					int num = availablePeriods.get(indices[i]) + numPeriods*(teacher-1) + PT*(lesson-1);

					out.write(num + " ");				
				}
						
			}
						
			out.write("0");
			out.newLine();
			count++;
	
		}
		
		/* ----------------------- Thursday ------------------------------ */

		availablePeriods = new ArrayList<Integer>();
		availablePeriods = access.getAvailablePeriodsThurs(teacher);  //array of all available periods for teacher
		
		minVar = availablePeriods.size() - access.getMinHoursThurs(teacher) + 1;  //Number of variables in each clause for min hours
		
		if(minVar >= availablePeriods.size())
			minVar = availablePeriods.size();
		
		combMin = new CombinationGenerator(availablePeriods.size(), minVar);;  //gets every combination for min hours
					
		while(combMin.hasMore())  //while there still exist combinations
		{		
			if(minVar >= availablePeriods.size())
				break;
			
			indices = combMin.getNext();
			
			for(int lesson : access.getLessonsCanTeach(teacher))  //for every lesson teacher can take
			{
			
				for(int i = 0; i < indices.length; i++)
				{					
					int num = availablePeriods.get(indices[i]) + numPeriods*(teacher-1) + PT*(lesson-1);

					out.write(num + " ");				
				}
						
			}
						
			out.write("0");
			out.newLine();
			count++;
	
		}
		
		/* ----------------------- Friday ------------------------------ */

		availablePeriods = new ArrayList<Integer>();
		availablePeriods = access.getAvailablePeriodsFri(teacher);  //array of all available periods for teacher
		
		minVar = availablePeriods.size() - access.getMinHoursFri(teacher) + 1;  //Number of variables in each clause for min hours
		
		if(minVar >= availablePeriods.size())
			minVar = availablePeriods.size();
		
		combMin = new CombinationGenerator(availablePeriods.size(), minVar);;  //gets every combination for min hours
					
		while(combMin.hasMore())  //while there still exist combinations
		{		
			if(minVar >= availablePeriods.size())
				break;
			
			indices = combMin.getNext();
			
			for(int lesson : access.getLessonsCanTeach(teacher))  //for every lesson teacher can take
			{
			
				for(int i = 0; i < indices.length; i++)
				{					
					int num = availablePeriods.get(indices[i]) + numPeriods*(teacher-1) + PT*(lesson-1);

					out.write(num + " ");				
				}
						
			}
						
			out.write("0");
			out.newLine();
			count++;
	
		}
	}
	System.out.println(count);
}
/*
//Teachers must teach at most a maximum number of hours
public void constraint8(BufferedWriter out) throws SQLException, IOException
{
	ArrayList<Integer> availablePeriods;
	int[] indices;
	int maxVar = 0;
	
	for(int teacher = 1; teacher <= numTeachers; teacher++)  //For every teacher
	{
		if(access.getLessonsCanTeach(teacher).size() == 0)
			continue;
		
		availablePeriods = new ArrayList<Integer>();
		availablePeriods = access.getAvailablePeriods(teacher);  //array of all available periods for teacher
		
		maxVar = access.getMaxHours(teacher) + 1;  //Number of variables in each clause for max hours
		
		if(maxVar >= availablePeriods.size())
			maxVar = availablePeriods.size();
		
		CombinationGenerator combMax = new CombinationGenerator(availablePeriods.size(), maxVar);  //gets every combination for max hours
					
		while(combMax.hasMore())  //while there still exist combinations
		{		
			indices = combMax.getNext();
			
			for(int lesson : access.getLessonsCanTeach(teacher))  //for every lesson teacher can take
			{			
				for(int i = 0; i < indices.length; i++)
				{					
					int num = availablePeriods.get(indices[i]) + numPeriods*(teacher-1) + PT*(lesson-1);

					out.write("-" + num + " ");				
				}						
			}						
			out.write("0");
			out.newLine();
			count++;	
		}
	}	
	System.out.println(count);

}
*/
//Updated constraint: Teachers must teach at most a maximum number of hours on each day
public void updatedConstraint8(BufferedWriter out) throws SQLException, IOException
{
	ArrayList<Integer> availablePeriods;
	CombinationGenerator combMax;
	int[] indices;
	int maxVar = 0;

	
	for(int teacher = 1; teacher <= numTeachers; teacher++)  //For every teacher
	{
		if(access.getLessonsCanTeach(teacher).size() == 0)
			continue;
		
		/* ----------------------- Max Monday Hours ------------------------ */
		
		availablePeriods = new ArrayList<Integer>();
		availablePeriods = access.getAvailablePeriodsMon(teacher);  //array of all available periods for teacher
		
		maxVar = access.getMaxHoursMon(teacher) + 1;  //Number of variables in each clause for max hours
		
		if(maxVar >= availablePeriods.size())
			maxVar = availablePeriods.size();
		
		combMax = new CombinationGenerator(availablePeriods.size(), maxVar);  //gets every combination for max hours
					
		while(combMax.hasMore())  //while there still exist combinations
		{		
			indices = combMax.getNext();
			
			for(int lesson : access.getLessonsCanTeach(teacher))  //for every lesson teacher can take
			{			
				for(int i = 0; i < indices.length; i++)
				{					
					int num = availablePeriods.get(indices[i]) + numPeriods*(teacher-1) + PT*(lesson-1);
					
					out.write("-" + num + " ");
					
					if(maxVar == 1)
					{
						out.write("0");
						out.newLine();
						count++;
					}
				}						
			}
			if(maxVar != 1)
			{
				out.write("0");
				out.newLine();
				count++;	
			}
		}
		
		/* ----------------------- Max Tuesday Hours ------------------------ */

		availablePeriods = new ArrayList<Integer>();
		availablePeriods = access.getAvailablePeriodsTues(teacher);  //array of all available periods for teacher
		
		maxVar = access.getMaxHoursTues(teacher) + 1;  //Number of variables in each clause for max hours
		
		if(maxVar >= availablePeriods.size())
			maxVar = availablePeriods.size();
		
		combMax = new CombinationGenerator(availablePeriods.size(), maxVar);  //gets every combination for max hours
					
		while(combMax.hasMore())  //while there still exist combinations
		{		
			indices = combMax.getNext();
			
			for(int lesson : access.getLessonsCanTeach(teacher))  //for every lesson teacher can take
			{			
				for(int i = 0; i < indices.length; i++)
				{					
					int num = availablePeriods.get(indices[i]) + numPeriods*(teacher-1) + PT*(lesson-1);

					out.write("-" + num + " ");	
					
					if(maxVar == 1)
					{
						out.write("0");
						out.newLine();
						count++;	
					}
				}						
			}	
			if(maxVar != 1)
			{
				out.write("0");
				out.newLine();
				count++;	
			}
		}
		
		/* ----------------------- Max Wednesday Hours ------------------------ */
		
		availablePeriods = new ArrayList<Integer>();
		availablePeriods = access.getAvailablePeriodsWed(teacher);  //array of all available periods for teacher
		
		maxVar = access.getMaxHoursWed(teacher) + 1;  //Number of variables in each clause for max hours
		
		if(maxVar >= availablePeriods.size())
			maxVar = availablePeriods.size();
		
		combMax = new CombinationGenerator(availablePeriods.size(), maxVar);  //gets every combination for max hours
					
		while(combMax.hasMore())  //while there still exist combinations
		{		
			indices = combMax.getNext();
			
			for(int lesson : access.getLessonsCanTeach(teacher))  //for every lesson teacher can take
			{			
				for(int i = 0; i < indices.length; i++)
				{					
					int num = availablePeriods.get(indices[i]) + numPeriods*(teacher-1) + PT*(lesson-1);

					out.write("-" + num + " ");	
					
					if(maxVar == 1)
					{
						out.write("0");
						out.newLine();
						count++;	
					}
				}						
			}	
			if(maxVar != 1)
			{
				out.write("0");
				out.newLine();
				count++;
			}
		}
		
		/* ----------------------- Max Thursday Hours ------------------------ */
		
		availablePeriods = new ArrayList<Integer>();
		availablePeriods = access.getAvailablePeriodsThurs(teacher);  //array of all available periods for teacher
		
		maxVar = access.getMaxHoursThurs(teacher) + 1;  //Number of variables in each clause for max hours
		
		if(maxVar >= availablePeriods.size())
			maxVar = availablePeriods.size();
		
		combMax = new CombinationGenerator(availablePeriods.size(), maxVar);  //gets every combination for max hours
					
		while(combMax.hasMore())  //while there still exist combinations
		{		
			indices = combMax.getNext();
			
			for(int lesson : access.getLessonsCanTeach(teacher))  //for every lesson teacher can take
			{			
				for(int i = 0; i < indices.length; i++)
				{					
					int num = availablePeriods.get(indices[i]) + numPeriods*(teacher-1) + PT*(lesson-1);

					out.write("-" + num + " ");		
					
					if(maxVar == 1)
					{
						out.write("0");
						out.newLine();
						count++;	
					}
				}						
			}
			if(maxVar != 1)
			{
				out.write("0");
				out.newLine();
				count++;
			}
		}
		
		/* ----------------------- Max Friday Hours ------------------------ */
		
		availablePeriods = new ArrayList<Integer>();
		availablePeriods = access.getAvailablePeriodsFri(teacher);  //array of all available periods for teacher
		
		maxVar = access.getMaxHoursFri(teacher) + 1;  //Number of variables in each clause for max hours
		
		if(maxVar >= availablePeriods.size())
			maxVar = availablePeriods.size();
		
		combMax = new CombinationGenerator(availablePeriods.size(), maxVar);  //gets every combination for max hours
					
		while(combMax.hasMore())  //while there still exist combinations
		{		
			indices = combMax.getNext();
			
			for(int lesson : access.getLessonsCanTeach(teacher))  //for every lesson teacher can take
			{			
				for(int i = 0; i < indices.length; i++)
				{					
					int num = availablePeriods.get(indices[i]) + numPeriods*(teacher-1) + PT*(lesson-1);

					out.write("-" + num + " ");	
					
					if(maxVar == 1)
					{
						out.write("0");
						out.newLine();
						count++;	
					}
				}						
			}	
			if(maxVar != 1)
			{
				out.write("0");
				out.newLine();
				count++;
			}
		}
	}	
	System.out.println(count);
}


//Every student is assigned to an existing teaching group for each subject
public void studentConstraint1(BufferedWriter out) throws SQLException, IOException
{
	
	for(int student = 1; student <= numStudents; student++)
	{		
		for(int subject : access.getStudentSubjectChoice(student))
		{		
			for(int teachingGroup : access.getSubjectTeachingGroups(subject))
			{
				int num = teachingGroup + numTeachingGroups*(subject-1) + GB*(student-1) + PTL;
				
				out.write(num + " ");				
			}
			
			out.write("0");
			out.newLine();
			count++;
		}				
	}
	System.out.println(count);

}

//Every student is assigned to exactly one teaching group for each subject
public void studentConstraint2(BufferedWriter out) throws SQLException, IOException
{
	
	for(int student = 1; student <= numStudents; student++)
	{		
		for(int subject : access.getStudentSubjectChoice(student))
		{		
			for(int teachingGroup : access.getSubjectTeachingGroups(subject))
			{
				for(int secondTeachingGroup : access.getSubjectTeachingGroups(subject))
				{
					if(teachingGroup >= secondTeachingGroup)
						continue;
					
					int first = teachingGroup + numTeachingGroups*(subject-1) + GB*(student-1) + PTL;
					int second = secondTeachingGroup + numTeachingGroups*(subject-1) + GB*(student-1) + PTL;
					
					out.write("-" + first + " -" + second + " 0");	//writes negative of integer to InputFile.txt				
					out.newLine();
					count++;
				}
			}
		}				
	}
	System.out.println(count);

}

//Every teaching group has a maximum number of students
public void studentConstraint3(BufferedWriter out) throws SQLException, IOException
{
	ArrayList<Integer> students;
	int[] indices;
	int maxVar = 0;
	
	for(int subject = 1; subject <= numSubjects; subject++)  //For every teacher
	{
		
		students = new ArrayList<Integer>();
		students = access.getStudentsTakingSubject(subject);  //array of all available periods for teacher
		
		if(students.size() < 1)
			continue;
		
		maxVar = access.getMaxCapacity(subject) + 1;  //Number of variables in each clause for max hours
		
		if(maxVar >= students.size())
			maxVar = students.size();
		
		CombinationGenerator combMax = new CombinationGenerator(students.size(), maxVar);  //gets every combination for max hours
					
		while(combMax.hasMore())  //while there still exist combinations
		{		
			indices = combMax.getNext();
			
			for(int teachingGroup : access.getSubjectTeachingGroups(subject))  //for every lesson teacher can take
			{			
				for(int i = 0; i < indices.length; i++)
				{					
					int num = teachingGroup + numTeachingGroups*(subject-1) + GB*(students.get(indices[i])-1) + PTL;

					out.write("-" + num + " ");				
				}						
			}						
			out.write("0");
			out.newLine();
			count++;	
		}
	}	
	System.out.println(count);

}

//Every non-existing teaching group does not occur.
public void studentConstraint4(BufferedWriter out) throws SQLException, IOException
{
	
	for(int student = 1; student <= numStudents; student++)  //for every teacher
	{
		for(int subject : access.getStudentsNonSubjectChoice(student)) 
		{			
				for(int teachingGroup = 1; teachingGroup <= numTeachingGroups; teachingGroup++)  //for every teacher
				{
					int first = teachingGroup + numTeachingGroups*(subject-1) + GB*(student-1) + PTL;  //converts period,teacher,lesson into single integer
					
					out.write("-" + first + " 0");	//writes negative of integer to InputFile.txt
					out.newLine();
					count++;
				}
							
		}
		for(int subject : access.getStudentSubjectChoice(student))
		{
			for(int teachingGroup : access.getNonSubjectTeachingGroups(subject))
			{
				int first = teachingGroup + numTeachingGroups*(subject-1) + GB*(student-1) + PTL;  //converts period,teacher,lesson into single integer
				
				out.write("-" + first + " 0");	//writes negative of integer to InputFile.txt
				out.newLine();
				count++;
			}
		}
	}	
	System.out.println(count);

}

//Every student can have at most one lesson on the same period
public void bigConstraint(BufferedWriter out) throws SQLException, IOException
{
	
	for(int student = 1; student <= numStudents; student++)  //for every student
	{
		for(int subject : access.getStudentSubjectChoice(student))  //for every subject the student is taking
		{
			for(int secondSubject : access.getStudentSubjectChoice(student))  //again for every subject the student is taking
			{
				if(subject >= secondSubject) //prevents duplicates since other constraints already prevent lessons of
					continue;				 //same subject being on the same period 
				
				for(int teachingGroup : access.getSubjectTeachingGroups(subject))					//the two different teaching groups are in different subjects
				{																					//so the teaching groups must be different and so no  
					for(int secondTeachingGroup : access.getSubjectTeachingGroups(secondSubject)) 	//duplicates could occur. Same for lessons.
					{
						for(int lesson : access.getLessonsInTeachingGroup(teachingGroup))
						{
							for(int secondLesson : access.getLessonsInTeachingGroup(secondTeachingGroup))
							{
								for(int teacher : access.getTeachersTeachLessons(lesson))
								{
									for(int secondTeacher : access.getTeachersTeachLessons(secondLesson))
									{
										for(int period : access.getAvailablePeriods(teacher, secondTeacher))
										{
											int one = teachingGroup + numTeachingGroups*(subject-1) + GB*(student-1) + PTL;
											int two = secondTeachingGroup + numTeachingGroups*(secondSubject-1) + GB*(student-1) + PTL;
											int three = period + numPeriods*(teacher-1) + PT*(lesson-1);
											int four = period + numPeriods*(secondTeacher-1) + PT*(secondLesson-1);
											
											/*This works because if one and two are true, i.e. the student has been assigned to both 
											 * teaching groups, which is acceptable, then at most one of the lessons, which occur
											 * on the same period, one belonging to one teaching group and the other to the other 
											 * teaching group must be true. Both can't be true else the clause evaluates to false
											 * and this means that these two lessons which will be taken by this student can't occur
											 * on the same period. This is then done for every lesson the student is taking. Since 
											 * the assignment of student to teaching group is done simultaneously to the assignment 
											 * of lessons to teachers and periods, one and two must be present since we don't know
											 * which teaching group the student has been assigned to yet. That is the job of the
											 * SAT solver.
											 */
											out.write("-"+ one + " -" + two + " -" + three + " -" + four + " 0");
											out.newLine();
											count++;
										}
									}
								}
							}
						}
						
					}
				}
			}
		}		
		
	}
	System.out.println(count);
	
}

	
}
