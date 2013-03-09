package myTimetabling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ConvertFromIntToVar
{
    DatabaseAccess access = new DatabaseAccess();   
	
	public void convertIntToVar(int L, int T, int P, int n) throws SQLException
	{	
		access.createConnection();
		
		int l = 0;
		int t = 0;
		int p = 0;
		
		int PT = P*T;
		
		n = n + P + PT;
				
		while((n-PT) > P)
		{
			n = n - PT;
			l++;
		}
		
		while(n > P)
		{
			n = n - P;
			t++;
		}
		
		p = n;
		
		access.stmt = access.conn.createStatement(); 
		String part = "insert into teacher_assignment " +
				"select teacherID, teacher_name, lesson, periodID " +
				"from teachers, lessons, periods " +
				"where teacherID = " + t + " and lessonID = " + l + " and periodID = " + p;
		access.stmt.execute(part);
					
	}
		
	public void convertIntToVarStudents(int S,int B,int G,int P,int T,int L, int n) throws SQLException
	{			
		access.createConnection();

		int s = 0;
		int b = 0;
		int g = 0;
		
		int PTL = P*T*L;
		int GB = G*B;
		
		n = n + G + GB - PTL;
				
		while((n-GB) > G)
		{
			n = n - GB;
			s++;
		}
		
		while(n > G)
		{
			n = n - G;
			b++;
		}
		
		g = n;
		
		access.stmt = access.conn.createStatement(); 
		
		for(int lesson : getLessonsInTeachingGroup(g))
		{
			String part = "insert into student_assignment " +
					"select studentID, student_name, lessons.lesson, teachers.teacher_name, periodID " +
					"from students, lessons, teachers, teacher_assignment " +
					"where studentID = " + s + " and lessonID = " + lesson + " and periodID = teacher_assignment.periodID " +
					"and lessons.lesson = teacher_assignment.lesson and teachers.teacher_name = teacher_assignment.teacher_name";
			access.stmt.addBatch(part);
		}
		access.stmt.executeBatch();
		
		//shutdown();
			
	}
	
	//gets all lessons in a teaching group
	private ArrayList<Integer> getLessonsInTeachingGroup(int teachingGroupID) throws SQLException
	{
		access.stmt = access.conn.createStatement();
		String query = "select lessonID from lessons, teaching_groups, teaching_group_size " +
				"where lessons.lesson = teaching_groups.lesson and " +
				"teaching_groups.teaching_group = teaching_group_size.teaching_group " +
				"and teaching_groupID = " + teachingGroupID;
		ResultSet lessonsInTeachingGroup = access.stmt.executeQuery(query);
		
		ArrayList<Integer> lessons = new ArrayList<Integer>();
		while(lessonsInTeachingGroup.next())
		{
			lessons.add(lessonsInTeachingGroup.getInt(1));
		}
		//stmt.close();
		return lessons;
	}
	
	
}
