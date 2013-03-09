package myTimetabling;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class OutputTimetable 
{
	private static String dbURL = "jdbc:derby:TimetableInput;create=false;";
    private static String teachers = "teachers";
    private static String periods = "periods";
    private static String lessons = "lessons";
    private static String teachingGroups = "teaching_group_size";
    private static String students = "students";
    private static String subjects = "subjects";

    // jdbc Connection
    private static Connection conn = null;
    private static Statement stmt = null;

	public static void main(String[] args) throws FileNotFoundException, SQLException 
	{
		createConnection();

		File f = null;
		Scanner scan = null;
		try{
		   f = new File("OutputFile.txt");
		   scan = new Scanner(f);
		}
		catch(Exception e){
		   System.exit(0);
		}
		int next = 0;
		ArrayList<Integer> x = new ArrayList<Integer>();
		scan.nextLine();
		while(scan.hasNext())
		{
			next = scan.nextInt();
			if(next > 0)
				x.add(next);
		}
		
		int numStudents = getNumStudents();
		int numSubjects = getNumSubjects();
		int numTeachingGroups = getNumTeachingGroups();
		int numPeriods = getNumPeriods();
		int numTeachers = getNumTeachers();
		int numLessons = getNumLessons();
		int PTL = numPeriods * numTeachers * numLessons;
		
		ConvertFromIntToVar convert = new ConvertFromIntToVar();
		
		stmt = conn.createStatement();
		stmt.executeUpdate("delete from teacher_assignment");
		stmt.executeUpdate("delete from student_assignment");
		stmt.close();
		
		for(int i = 0; i < x.size(); i++)
		{
			if(x.get(i) <= PTL)
			{
				convert.convertIntToVar(numLessons, numTeachers, numPeriods, x.get(i));
			}
			else
			{
				convert.convertIntToVarStudents(numStudents, numSubjects, numTeachingGroups, numPeriods, numTeachers, numLessons, x.get(i));
			}
		}
		
		shutdown();

	}
	
	private static void createConnection()
	{
	    try
	    {
	    	
	    	Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
	        //Get a connection
	        conn = DriverManager.getConnection(dbURL); 
	    }
	    catch (Exception except)
	    {
	        except.printStackTrace();
	    }
	}

	private static void shutdown()
	{
	    try
	    {
	        if (stmt != null)
	        {
	            stmt.close();
	        }
	        if (conn != null)
	        {
	            DriverManager.getConnection(dbURL + ";shutdown=true");
	            conn.close();
	        }           
	    }
	    catch (SQLException sqlExcept)
	    {
	        
	    }

	}

	
	// ------------------------------------- Totals ---------------------------------------------- 

	// gets the total number of teachers 
	private static int getNumTeachers() throws SQLException
	{
		stmt = conn.createStatement(); 
		ResultSet numTeachers = stmt.executeQuery("select count(teacherID) AS total from " + teachers); //query that counts the number of rows from table teachers
		numTeachers.next(); //moves cursor to first row
		int numOfTeachers = numTeachers.getInt("total");  //gets value of first row from column total, which is the total number of teachers
	    stmt.close();
		return numOfTeachers;
		
	}

	// gets the total number of periods
	private static int getNumPeriods() throws SQLException
	{
		stmt = conn.createStatement(); 
		ResultSet numPeriods = stmt.executeQuery("select count(PeriodID) AS total from " + periods); //query that counts the number of rows from table teachers
		numPeriods.next(); //moves cursor to first row
		int numOfPeriods = numPeriods.getInt("total");  //gets value of first row from column total, which is the total number of teachers
	    stmt.close();
		return numOfPeriods;
	}

	// gets the total number of lessons
	private static int getNumLessons() throws SQLException
	{
		stmt = conn.createStatement(); 
		ResultSet numLessons = stmt.executeQuery("select count(LessonID) AS total from " + lessons); //query that counts the number of rows from table teachers
		numLessons.next(); //moves cursor to first row
		int numOfLessons = numLessons.getInt("total");  //gets value of first row from column total, which is the total number of teachers
	    stmt.close();
		return numOfLessons;
	}

	//gets the total number of teaching groups
	private static int getNumTeachingGroups() throws SQLException
	{
		stmt = conn.createStatement(); 
		ResultSet numTeachingGroups = stmt.executeQuery("select count(Teaching_GroupID) AS total from " + teachingGroups); //query that counts the number of rows from table teachers
		numTeachingGroups.next(); //moves cursor to first row
		int numOfTeachingGroups = numTeachingGroups.getInt("total");  //gets value of first row from column total, which is the total number of teachers
		stmt.close();
		return numOfTeachingGroups;
	}

	private static int getNumStudents() throws SQLException
	{
		stmt = conn.createStatement(); 
		ResultSet numStudents = stmt.executeQuery("select count(StudentID) AS total from " + students); //query that counts the number of rows from table teachers
		numStudents.next(); //moves cursor to first row
		int numOfStudents = numStudents.getInt("total");  //gets value of first row from column total, which is the total number of teachers
		stmt.close();
		return numOfStudents;
	}

	private static int getNumSubjects() throws SQLException
	{
		stmt = conn.createStatement(); 
		ResultSet numStudents = stmt.executeQuery("select count(SubjectID) AS total from " + subjects); //query that counts the number of rows from table teachers
		numStudents.next(); //moves cursor to first row
		int numOfStudents = numStudents.getInt("total");  //gets value of first row from column total, which is the total number of teachers
		stmt.close();
		return numOfStudents;
	}

}
