package myTimetabling;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

public class TestData 
{

	private static String dbURL = "jdbc:derby:TimetableInput;create=false;";
	private static String students = "students";
	private static String teachers = "teachers";
	
    // jdbc Connection
    private static Connection conn = null;
    private static Statement stmt = null; 

	public TestData() throws SQLException, IOException 
	{
		createConnection();
		
	    ArrayList<String> subjects = new ArrayList<String>();	    
	    subjects.add("ArtAS");		subjects.add("ArtA2");
	    subjects.add("BioAS");		subjects.add("BioA2");
	    subjects.add("BusnAS");		subjects.add("BusnA2");
	    subjects.add("ChemAS");		subjects.add("ChemA2");
	    subjects.add("ClasAS");		subjects.add("ClasA2");
	    subjects.add("CompAS");	    subjects.add("CompA2");
	    subjects.add("EconAS");	    subjects.add("EconA2");
	    subjects.add("EnglAS");	    subjects.add("EnglA2");
	    subjects.add("FrenAS");	    subjects.add("FrenA2");
	    subjects.add("FMathsAS");	subjects.add("FMathsA2");
	    subjects.add("GeogAS");	    subjects.add("GeogA2");
	    subjects.add("HistAS");	    subjects.add("HistA2");
	    subjects.add("InftAS");	    subjects.add("InftA2");
	    subjects.add("LawAS");		subjects.add("LawA2");
		subjects.add("MathsAS");	subjects.add("MathsA2");
		subjects.add("PhysAS");		subjects.add("PhysA2");
		subjects.add("PsycAS");		subjects.add("PsycA2");
		subjects.add("RelsAS");		subjects.add("RelsA2");
		subjects.add("SoclAS");		subjects.add("SoclA2");
		subjects.add("SpanAS");		subjects.add("SpanA2"); 

		
		
		ArrayList<String> groups = new ArrayList<String>();
		groups.add("a");
		groups.add("b");
		groups.add("c");

		
		
		ArrayList<String> students = new ArrayList<String>();
		FileInputStream fstream = new FileInputStream("students.txt");
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;		
		while ((strLine = br.readLine()) != null)   
		{
			students.add(strLine);
		}

		
		
		ArrayList<String> teachers = new ArrayList<String>();
		FileInputStream fistream = new FileInputStream("teachers.txt");
		DataInputStream dis = new DataInputStream(fistream);
		BufferedReader bir = new BufferedReader(new InputStreamReader(dis));
		String line;		
		while ((line = bir.readLine()) != null)   
		{
			teachers.add(line);
		}		
		
		int max_size = 8;
		
		stmt = conn.createStatement(); 

		stmt.executeUpdate("delete from teacher_assignment");
		stmt.executeUpdate("delete from student_assignment");
		stmt.executeUpdate("delete from periods");
		stmt.executeUpdate("delete from teaching_groups");
		stmt.executeUpdate("delete from lessons");
		stmt.executeUpdate("delete from subject_groups");
		stmt.executeUpdate("delete from teaching_group_size");
		stmt.executeUpdate("delete from student_subjects");
		stmt.executeUpdate("delete from can_teach");
		stmt.executeUpdate("delete from subjects");
		stmt.executeUpdate("delete from teachers");
		stmt.executeUpdate("delete from students");
		stmt.executeUpdate("delete from teacher_unavailable");

		
		stmt.executeUpdate("drop sequence period_seq restrict");
		stmt.executeUpdate("create sequence period_seq start with 1");
		
		stmt.executeUpdate("drop sequence lesson_seq restrict");
		stmt.executeUpdate("create sequence lesson_seq start with 1");
		
		stmt.executeUpdate("drop sequence teaching_group_seq restrict");
		stmt.executeUpdate("create sequence teaching_group_seq start with 1");
		
		stmt.executeUpdate("drop sequence subject_seq restrict");
		stmt.executeUpdate("create sequence subject_seq start with 1");
		
		stmt.executeUpdate("drop sequence subject_seq restrict");
		stmt.executeUpdate("create sequence subject_seq start with 1");
		
		stmt.executeUpdate("drop sequence student_seq restrict");
		stmt.executeUpdate("create sequence student_seq start with 1");
		
		stmt.executeUpdate("drop sequence teacher_seq restrict");
		stmt.executeUpdate("create sequence teacher_seq start with 1");
		 
		
		periods(5);
		lessons(subjects, groups);
		teaching_group_size(subjects, groups);
		teaching_groups(subjects, groups);
		subjects(subjects, max_size);
		subject_groups(subjects);
		students(students);
		student_subjects(subjects);
		teachers(teachers);
		can_teach(subjects);

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


	public void periods(int d) throws SQLException
	{
		stmt = conn.createStatement(); 
		

		for(int i = 1; i <= d; i++)
		{
			String part = "insert into periods values(next value for period_seq, 'Monday'," + i + ")";
			stmt.addBatch(part);				
		}
		for(int i = 1; i <= d; i++)
		{
			String part = "insert into periods values(next value for period_seq, 'Tuesday'," + i + ")";
			stmt.addBatch(part); 
		}
		for(int i = 1; i <= d; i++)
		{
			String part = "insert into periods values(next value for period_seq, 'Wednesday'," + i + ")";
			stmt.addBatch(part); 
		}
		for(int i = 1; i <= d; i++)
		{
			String part = "insert into periods values(next value for period_seq, 'Thursday'," + i + ")";
			stmt.addBatch(part); 
		}
		for(int i = 1; i <= d; i++)
		{
			String part = "insert into periods values(next value for period_seq, 'Friday'," + i + ")";
			stmt.addBatch(part);
		}
		stmt.executeBatch();	
	    stmt.close();
	}
	
	public void lessons(ArrayList<String> subjects, ArrayList<String> groups) throws SQLException
	{
		stmt = conn.createStatement(); 
			
		for(String subject : subjects)
		{
			for(String group : groups)
			{
				for(int i = 1; i <= 4; i++)
				{
					String part = "insert into lessons values(next value for lesson_seq, '" + subject + "/" + group + i + "')";
	
					stmt.addBatch(part);
				}
			}
		}
		
		stmt.executeBatch();
	    stmt.close();

	}
	
	public void teaching_group_size(ArrayList<String> subjects, ArrayList<String> groups) throws SQLException
	{
		stmt = conn.createStatement(); 
		
		for(String subject : subjects)
		{
			for(String group: groups)
			{
				String part = "insert into teaching_group_size values(next value for teaching_group_seq, '" + subject + "/" + group + "')";
	
				stmt.addBatch(part);
			}
		}
		
		stmt.executeBatch();
	    stmt.close();

	}
	
	public void teaching_groups(ArrayList<String> subjects, ArrayList<String> groups) throws SQLException
	{
		stmt = conn.createStatement();
		
		for(String subject : subjects)
		{
			for(String group : groups)
			{
				String part = "insert into teaching_groups select teaching_group, lesson " +
						"from teaching_group_size join lessons on teaching_group like '" + 
						subject + "/" + group + "%' and lesson like '" + subject + "/" + group + "%'";
				
				stmt.addBatch(part);
			}
		}
		
		stmt.executeBatch();
		stmt.close();
	}
	
	public void subjects(ArrayList<String> subjects, int max) throws SQLException
	{
		stmt = conn.createStatement();
	
		for(String subject : subjects)
		{
			String part = "insert into subjects values(next value for subject_seq, '" + subject + "', " + max + ")";
			stmt.addBatch(part);
		}
		
		stmt.executeBatch();
	    stmt.close();

	}
	
	public void subject_groups(ArrayList<String> subjects) throws SQLException
	{
		stmt = conn.createStatement();
		
		for(String subject : subjects)
		{
			String part = "insert into subject_groups select subject, teaching_group " +
					"from subjects join teaching_group_size on teaching_group like '" + 
					subject + "%' and subject like '" + subject + "%'";
			
			stmt.addBatch(part);		
		}
		
		stmt.executeBatch();
		stmt.close();
	}
	
	public void students(ArrayList <String> students) throws SQLException
	{
		stmt = conn.createStatement();
		
		for(String student : students)
		{
			String part = "insert into students values(next value for student_seq, '" + student + "')";
			
			stmt.addBatch(part);
		}
		
		stmt.executeBatch();
		stmt.close();
	}
	
	private int getNumStudents() throws SQLException
	{
		stmt = conn.createStatement(); 
		ResultSet numStudents = stmt.executeQuery("select count(StudentID) AS total from " + students); //query that counts the number of rows from table teachers
		numStudents.next(); //moves cursor to first row
		int numOfStudents = numStudents.getInt("total");  //gets value of first row from column total, which is the total number of teachers
		stmt.close();
		return numOfStudents;
	}
	
	
	public void student_subjects(ArrayList<String> subjects) throws SQLException
	{
		int numStudents = getNumStudents();

		ArrayList<Integer> temp = new ArrayList<Integer>();
		Random random = new Random();
		int index = random.nextInt(subjects.size());

		//WILL NEED TO THINK ABOUT HOW TO DO THIS
		stmt = conn.createStatement();

		for(int student = 1; student <= numStudents; student++)
		{
			for(int i = 1; i <= 4; i++)
			{			
				while(temp.contains(index))
				{
					index = random.nextInt(subjects.size());
				}
				temp.add(index);
				
				String part = "insert into student_subjects values(" + student + ", '" + subjects.get(index) + "')";
				stmt.addBatch(part);
			}
			temp.clear();
		}
		stmt.executeBatch();
		stmt.close();
	}
	
	public void teachers(ArrayList <String> teachers) throws SQLException
	{
		// WILL NEED TO MAKE THIS MORE FLEXIBLE, MIGHT BE DIFFERENT FOR DIFFERENT TEACHERS
		int min = 3;
		int max = 22;
		
		stmt = conn.createStatement();
		
		for(String teacher : teachers)
		{
			String part = "insert into teachers values(next value for teacher_seq, '" + 
					teacher + "', " + min + ", " + max +")";
			
			stmt.addBatch(part);
		}
		
		stmt.executeBatch();
		stmt.close();
	}
	
	private int getNumTeachers() throws SQLException
	{
		stmt = conn.createStatement(); 
		ResultSet numTeachers = stmt.executeQuery("select count(teacherID) AS total from " + teachers); //query that counts the number of rows from table teachers
		numTeachers.next(); //moves cursor to first row
		int numOfTeachers = numTeachers.getInt("total");  //gets value of first row from column total, which is the total number of teachers
	    stmt.close();
		return numOfTeachers;
		
	}
	
	public void can_teach(ArrayList<String> subjects) throws SQLException
	{
		int numTeachers = getNumTeachers();
		int temp = 0;
		stmt = conn.createStatement();

		int count = 0;
		int teacher = 1;
		
		for(String subject : subjects)
		{
					
			String part1 = "insert into can_teach values(" + teacher + ", '" + 
					subject + "')";
			temp = teacher + 1;
			String part2 = "insert into can_teach values(" + temp + ", '" + 
					subject + "')";
			
			count++;
			
			if(count % 2 == 0)
				teacher = teacher + 2;
			
			stmt.addBatch(part1);
			stmt.addBatch(part2);
			
			if(teacher > numTeachers)
				break;
			
		}
		
		stmt.executeBatch();
		stmt.close();
		
	}
	
	/*STILL TO DO - BUT HAVEN'T TESTED WITH PART TIME TEACHERS YET */
	public void teacher_unavailable() throws SQLException
	{
		stmt = conn.createStatement();
		
		stmt.executeBatch();
		stmt.close();

	}

	
}
