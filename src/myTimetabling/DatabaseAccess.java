package myTimetabling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseAccess 
{
	protected String dbURL = "jdbc:derby:TimetableInput;create=false;";
	protected String teachers = "teachers";
	protected String periods = "periods";
	protected String lessons = "lessons";
	protected String teachingGroups = "teaching_group_size";
    protected String students = "students";
    protected String subjects = "subjects";
    protected int numPeriodsInDay = 0;
    // jdbc Connection
    protected Connection conn = null;
    protected Statement stmt = null;	
	
    protected void createConnection()
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

	protected void shutdown()
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
	protected int getNumTeachers()
	{
		try {
		stmt = conn.createStatement(); 
		ResultSet numTeachers = stmt.executeQuery("select count(teacherID) AS total from " + teachers); //query that counts the number of rows from table teachers
		numTeachers.next(); //moves cursor to first row
		int numOfTeachers = numTeachers.getInt("total");  //gets value of first row from column total, which is the total number of teachers
	    stmt.close();
		return numOfTeachers;
		
		} catch(SQLException e) {}
		
		return 0;
		
	}

	// gets the total number of periods
	protected int getNumPeriods() 
	{
		try {
		stmt = conn.createStatement(); 
		ResultSet numPeriods = stmt.executeQuery("select count(PeriodID) AS total from " + periods); //query that counts the number of rows from table teachers
		numPeriods.next(); //moves cursor to first row
		int numOfPeriods = numPeriods.getInt("total");  //gets value of first row from column total, which is the total number of teachers
	    stmt.close();
		return numOfPeriods;
		
		} catch(SQLException e) {}

		return 0;
	}

	// gets the total number of lessons
	protected int getNumLessons()
	{
		try {
		stmt = conn.createStatement(); 
		ResultSet numLessons = stmt.executeQuery("select count(LessonID) AS total from " + lessons); //query that counts the number of rows from table teachers
		numLessons.next(); //moves cursor to first row
		int numOfLessons = numLessons.getInt("total");  //gets value of first row from column total, which is the total number of teachers
	    stmt.close();
		return numOfLessons;
		
		} catch(SQLException e) {}
		
		return 0;
	}

	//gets the total number of teaching groups
	protected int getNumTeachingGroups()
	{
		try {
		stmt = conn.createStatement(); 
		ResultSet numTeachingGroups = stmt.executeQuery("select count(Teaching_GroupID) AS total from " + teachingGroups); //query that counts the number of rows from table teachers
		numTeachingGroups.next(); //moves cursor to first row
		int numOfTeachingGroups = numTeachingGroups.getInt("total");  //gets value of first row from column total, which is the total number of teachers
		stmt.close();
		return numOfTeachingGroups;
		
		} catch(SQLException e) {}
		
		return 0;
	}

	protected int getNumStudents()
	{
		createConnection();
		try {
		stmt = conn.createStatement(); 
		ResultSet numStudents = stmt.executeQuery("select count(StudentID) AS total from " + students); //query that counts the number of rows from table teachers
		numStudents.next(); //moves cursor to first row
		int numOfStudents = numStudents.getInt("total");  //gets value of first row from column total, which is the total number of teachers
		stmt.close();
		return numOfStudents;
		
		} catch(SQLException e) {}
		
		return 0;
	}

	protected int getNumSubjects()
	{
		try {
		stmt = conn.createStatement(); 
		ResultSet numStudents = stmt.executeQuery("select count(SubjectID) AS total from " + subjects); //query that counts the number of rows from table teachers
		numStudents.next(); //moves cursor to first row
		int numOfStudents = numStudents.getInt("total");  //gets value of first row from column total, which is the total number of teachers
		stmt.close();
		return numOfStudents;
		
		} catch(SQLException e) {}
		
		return 0;
	}


	// -------------------------------- get lessons  ---------------------------------

	// gets all the lessons of a teacher that the teacher is capable of teaching
	protected ArrayList<Integer> getLessonsCanTeach(int teacherID)
	{
		try {
		stmt = conn.createStatement();
		String query = "select lessons.lessonID as lesson " +
				"from lessons, teaching_groups, subject_groups, can_teach " +
				"where lessons.lesson = teaching_groups.lesson and teaching_groups.teaching_group = subject_groups.teaching_group " +
				"and subject_groups.subject = can_teach.subject and can_teach.teacherID = " + teacherID;
		ResultSet lessonsCanTeach = stmt.executeQuery(query);
		
		ArrayList<Integer> lessons = new ArrayList<Integer>();
		while(lessonsCanTeach.next())
		{
			lessons.add(lessonsCanTeach.getInt(1));
		}
		stmt.close();
		return lessons;
		
		} catch(SQLException e) {}
		
		return null;
		
	}

	//gets all the lessons of a teacher that the teacher is not capable of teaching
	protected ArrayList<Integer> getLessonsNotTeach(int teacherID)
	{
		try {
		stmt = conn.createStatement();
		String query = "select lessonID from lessons except " +
				"select lessons.lessonID " +
				"from lessons, teaching_groups, subject_groups, can_teach " +
				"where lessons.lesson = teaching_groups.lesson and teaching_groups.teaching_group = subject_groups.teaching_group " +
				"and subject_groups.subject = can_teach.subject and can_teach.teacherID = " + teacherID;
		ResultSet lessonsNotTeach = stmt.executeQuery(query);
		
		ArrayList<Integer> lessons = new ArrayList<Integer>();
		while(lessonsNotTeach.next())
		{
			lessons.add(lessonsNotTeach.getInt(1));
		}
		stmt.close();
		return lessons;
		
		} catch(SQLException e) {}
		
		return null;
	}

	//gets all lessons in a teaching group
	protected ArrayList<Integer> getLessonsInTeachingGroup(int teachingGroupID) 
	{
		try {
		stmt = conn.createStatement();
		String query = "select lessonID from lessons, teaching_groups, teaching_group_size " +
				"where lessons.lesson = teaching_groups.lesson and " +
				"teaching_groups.teaching_group = teaching_group_size.teaching_group " +
				"and teaching_groupID = " + teachingGroupID;
		ResultSet lessonsInTeachingGroup = stmt.executeQuery(query);
		
		ArrayList<Integer> lessons = new ArrayList<Integer>();
		while(lessonsInTeachingGroup.next())
		{
			lessons.add(lessonsInTeachingGroup.getInt(1));
		}
		stmt.close();
		return lessons;
		
		} catch(SQLException e) {}
		
		return null;
	}


	//-------------------------------- get periods  ---------------------------------

	// gets all the periods the teacher is available to teach
	protected ArrayList<Integer> getAvailablePeriods(int teacherID)
	{
		try {
	
		stmt = conn.createStatement();
		String query = "select periodID from periods except " +
				"select periodID from teacher_unavailable " +
				"where teacherID = " + teacherID;

		ResultSet periodsAvailable = stmt.executeQuery(query);
		
		ArrayList<Integer> periods = new ArrayList<Integer>();
		while(periodsAvailable.next())
		{
			periods.add(periodsAvailable.getInt(1));
		}
		stmt.close();
		return periods;
		
		} catch(SQLException e) {}
		
		return null;
	}
	
	//gets all the periods the teacher is available to teach on a Monday
	protected ArrayList<Integer> getAvailablePeriodsMon(int teacherID)
	{
		try {
			
		stmt = conn.createStatement();
		String query = "select periodID from periods where day = 'Monday' except " +
				"select periodID from teacher_unavailable " +
				"where teacherID = " + teacherID;

		ResultSet periodsAvailable = stmt.executeQuery(query);
		
		ArrayList<Integer> periods = new ArrayList<Integer>();
		while(periodsAvailable.next())
		{
			periods.add(periodsAvailable.getInt(1));
		}
		stmt.close();
		return periods;
		
		} catch(SQLException e) {}
		
		return null;
	}
	
	//gets all the periods the teacher is available to teach on a Tuesday
	protected ArrayList<Integer> getAvailablePeriodsTues(int teacherID)
	{
		try {
			
		stmt = conn.createStatement();
		String query = "select periodID from periods where day = 'Tuesday' except " +
				"select periodID from teacher_unavailable " +
				"where teacherID = " + teacherID;

		ResultSet periodsAvailable = stmt.executeQuery(query);
		
		ArrayList<Integer> periods = new ArrayList<Integer>();
		while(periodsAvailable.next())
		{
			periods.add(periodsAvailable.getInt(1));
		}
		stmt.close();
		return periods;
		
		} catch(SQLException e) {}
		
		return null;
	}
	
	//gets all the periods the teacher is available to teach on a Wednesday
	protected ArrayList<Integer> getAvailablePeriodsWed(int teacherID)
	{
		try {
			
		stmt = conn.createStatement();
		String query = "select periodID from periods where day = 'Wednesday' except " +
				"select periodID from teacher_unavailable " +
				"where teacherID = " + teacherID;

		ResultSet periodsAvailable = stmt.executeQuery(query);
		
		ArrayList<Integer> periods = new ArrayList<Integer>();
		while(periodsAvailable.next())
		{
			periods.add(periodsAvailable.getInt(1));
		}
		stmt.close();
		return periods;
		
		} catch(SQLException e) {}
		
		return null;
	}
	
	//gets all the periods the teacher is available to teach on a Thursday
	protected ArrayList<Integer> getAvailablePeriodsThurs(int teacherID)
	{
		try {
			
		stmt = conn.createStatement();
		String query = "select periodID from periods where day = 'Thursday' except " +
				"select periodID from teacher_unavailable " +
				"where teacherID = " + teacherID;

		ResultSet periodsAvailable = stmt.executeQuery(query);
		
		ArrayList<Integer> periods = new ArrayList<Integer>();
		while(periodsAvailable.next())
		{
			periods.add(periodsAvailable.getInt(1));
		}
		stmt.close();
		return periods;
		
		} catch(SQLException e) {}
		
		return null;
	}
	
	//gets all the periods the teacher is available to teach on a Friday
	protected ArrayList<Integer> getAvailablePeriodsFri(int teacherID)
	{
		try {
			
		stmt = conn.createStatement();
		String query = "select periodID from periods where day = 'Friday' except " +
				"select periodID from teacher_unavailable " +
				"where teacherID = " + teacherID;

		ResultSet periodsAvailable = stmt.executeQuery(query);
		
		ArrayList<Integer> periods = new ArrayList<Integer>();
		while(periodsAvailable.next())
		{
			periods.add(periodsAvailable.getInt(1));
		}
		stmt.close();
		return periods;
		
		} catch(SQLException e) {}
		
		return null;
	}

	//gets all the periods the teacher is unavailable to teach
	protected ArrayList<Integer> getUnavailablePeriods(int teacherID)
	{
		try {
		stmt = conn.createStatement();
		String query = "select periodID from teacher_unavailable " +
				"where teacherID = " + teacherID;

		ResultSet periodsUnavailable = stmt.executeQuery(query);
		
		ArrayList<Integer> periods = new ArrayList<Integer>();
		while(periodsUnavailable.next())
		{
			periods.add(periodsUnavailable.getInt(1));
		}
		stmt.close();
		return periods;
		
		} catch(SQLException e) {}
		
		return null;
	}

	//gets all the periods the both teachers are available to teach
	protected ArrayList<Integer> getAvailablePeriods(int teacherID, int secondTeacherID)
	{
		try {
		stmt = conn.createStatement();
		String query = "select periodID from periods except " +
				"select periodID from teacher_unavailable " +
				"where teacherID = " + teacherID + " or teacherID = " + secondTeacherID;

		ResultSet periodsAvailable = stmt.executeQuery(query);
		
		ArrayList<Integer> periods = new ArrayList<Integer>();
		while(periodsAvailable.next())
		{
			periods.add(periodsAvailable.getInt(1));
		}
		stmt.close();
		return periods;
		
		} catch(SQLException e) {}
		
		return null;
	}

	//-------------------------------- get teachers  ---------------------------------

	//gets all teachers who can teach this lesson
	protected ArrayList<Integer> getTeachersTeachLessons(int lessonID) 
	{
		try {
		stmt = conn.createStatement();
		String query = "select can_teach.teacherID from can_teach, lessons, teaching_groups, subject_groups " +
				"where teaching_groups.teaching_group = subject_groups.teaching_group and " +
				"subject_groups.subject = can_teach.subject and " +
				"lessons.lesson = teaching_groups.lesson and lessons.lessonID = " + lessonID;
		ResultSet teachersLessons = stmt.executeQuery(query);
		
		ArrayList<Integer> teachers = new ArrayList<Integer>();
		while(teachersLessons.next())
		{
			teachers.add(teachersLessons.getInt(1));
		}
		stmt.close();
		return teachers;
		
		} catch(SQLException e) {}
		
		return null;
	}


	//gets all teachers who can teach teaching group
	protected ArrayList<Integer> getTeachersSubject(int teachingGroupID) 
	{
		try {
		stmt = conn.createStatement();
		String query = "select teacherID from can_teach, subject_groups, teaching_group_size " +
				"where can_teach.subject = subject_groups.subject and " +
				"subject_groups.teaching_group = teaching_group_size.teaching_group and " +
				"teaching_group_size.teaching_groupID = " + teachingGroupID;
		ResultSet teachersSubject = stmt.executeQuery(query);
		
		ArrayList<Integer> teachers = new ArrayList<Integer>();
		while(teachersSubject.next())
		{
			teachers.add(teachersSubject.getInt(1));
		}
		stmt.close();
		return teachers;
		
		} catch(SQLException e) {}
		
		return null;
	}
	
	protected String getTeacher(int teacherID)
	{
		try {
		stmt = conn.createStatement(); 
		
		String query = "select teacher_name from teachers where teacherID = " + teacherID;
		ResultSet teacher = stmt.executeQuery(query);
		teacher.next();
		String teacher_name = teacher.getString("teacher_name");
		stmt.close();
		return teacher_name;
		
		} catch(SQLException e) {}
		
		return "";
		
	}

	//-------------------------------- get subjects  ---------------------------------

	//gets all the subjects that the student is taking 
	protected ArrayList<Integer> getStudentSubjectChoice(int studentID) 
	{
		try {
		stmt = conn.createStatement();
		String query = "select subjectID from subjects, student_subjects " +
				"where subjects.subject = student_subjects.subject and studentID = " + studentID;
		ResultSet studentsSubject = stmt.executeQuery(query);
		
		ArrayList<Integer> subjects = new ArrayList<Integer>();
		while(studentsSubject.next())
		{
			subjects.add(studentsSubject.getInt(1));
		}
		stmt.close();
		return subjects;
		
		} catch(SQLException e) {}
		
		return null;
	}

	//gets all the subjects that the student is not taking 
	protected ArrayList<Integer> getStudentsNonSubjectChoice(int studentID)
	{
		try {
		stmt = conn.createStatement();
		String query = "select subjectID from subjects except " +
				"select subjectID from subjects, student_subjects " +
				"where subjects.subject = student_subjects.subject and studentID = " + studentID;
		ResultSet studentsSubject = stmt.executeQuery(query);
		
		ArrayList<Integer> subjects = new ArrayList<Integer>();
		while(studentsSubject.next())
		{
			subjects.add(studentsSubject.getInt(1));
		}
		stmt.close();
		return subjects;
		
		} catch(SQLException e) {}
		
		return null;
	}


	//-------------------------------- get teaching groups  ---------------------------------

	//gets all the teaching groups of a particular subject
	protected ArrayList<Integer> getSubjectTeachingGroups(int subjectID)
	{
		try {
		stmt = conn.createStatement();
		String query = "select teaching_GroupID from teaching_group_size, subjects, subject_groups " +
				"where teaching_group_size.teaching_group = subject_groups.teaching_group and " +
				"subject_groups.subject = subjects.subject and subjectID = " + subjectID;
		ResultSet subjectTeachingGroups = stmt.executeQuery(query);
		
		ArrayList<Integer> teachingGroups = new ArrayList<Integer>();
		while(subjectTeachingGroups.next())
		{
			teachingGroups.add(subjectTeachingGroups.getInt(1));
		}
		stmt.close();
		return teachingGroups;
		
		} catch(SQLException e) {}
		
		return null;
	}

	//gets all the teaching groups not of a particular subject
	protected ArrayList<Integer> getNonSubjectTeachingGroups(int subjectID)
	{
		try {
		stmt = conn.createStatement();
		String query = "select teaching_GroupID from teaching_group_size except " +
				"select teaching_GroupID from teaching_group_size, subjects, subject_groups " +
				"where teaching_group_size.teaching_group = subject_groups.teaching_group and " +
				"subject_groups.subject = subjects.subject and subjectID = " + subjectID;
		ResultSet subjectTeachingGroups = stmt.executeQuery(query);
		
		ArrayList<Integer> teachingGroups = new ArrayList<Integer>();
		while(subjectTeachingGroups.next())
		{
			teachingGroups.add(subjectTeachingGroups.getInt(1));
		}
		stmt.close();
		return teachingGroups;
		
		} catch(SQLException e) {}
		
		return null;
	}

	//-------------------------------- get students  ---------------------------------

	protected ArrayList<Integer> getStudentsTakingSubject(int subjectID)
	{
		try {
		stmt = conn.createStatement();
		String query = "select studentID from student_subjects, subjects " +
				"where student_subjects.subject = subjects.subject and subjectID = " + subjectID;
		ResultSet studentsTakingSubject = stmt.executeQuery(query);
		
		ArrayList<Integer> students = new ArrayList<Integer>();
		while(studentsTakingSubject.next())
		{
			students.add(studentsTakingSubject.getInt(1));
		}
		stmt.close();
		return students;
		
		} catch(SQLException e) {}
		
		return null;
	}
	
	protected String getStudent(int studentID)
	{
		try {
		stmt = conn.createStatement(); 
		
		String query = "select student_name from students where studentID = " + studentID;
		ResultSet student = stmt.executeQuery(query);
		student.next();
		String student_name = student.getString("student_name");
		stmt.close();
		return student_name;
		
		} catch(SQLException e) {}
		
		return "";
		
	}



	//-------------------------------- specific functions  ---------------------------------


	protected int dist(double period1, double period2, int numPeriodsInDay)
	{
		int difference = (int) ((int) Math.ceil(period2/numPeriodsInDay) - Math.ceil(period1/numPeriodsInDay));
		
		return difference;
	}

	protected int getMinHoursMon(int teacherID)
	{
		try {
		stmt = conn.createStatement(); 
		ResultSet minHours = stmt.executeQuery("select Min_Hours_Mon as a from teachers where teacherID = " + teacherID); //query that counts the number of rows from table teachers
		minHours.next(); //moves cursor to first row
		int min = minHours.getInt("a");  //gets value of first row from column total, which is the total number of teachers
		stmt.close();
		return min;
		
		} catch(SQLException e) {}
		
		return 0;
	}

	protected int getMaxHoursMon(int teacherID)
	{
		try {
		stmt = conn.createStatement(); 
		ResultSet maxHours = stmt.executeQuery("select Max_Hours_Mon as b from teachers where teacherID = " + teacherID); //query that counts the number of rows from table teachers
		maxHours.next(); //moves cursor to first row
		int max = maxHours.getInt("b");  //gets value of first row from column total, which is the total number of teachers
		stmt.close();
		return max;
		
		} catch(SQLException e) {}
		
		return 0;
		
	}
	
	protected int getMinHoursTues(int teacherID)
	{
		try {
		stmt = conn.createStatement(); 
		ResultSet minHours = stmt.executeQuery("select Min_Hours_Tues as a from teachers where teacherID = " + teacherID); //query that counts the number of rows from table teachers
		minHours.next(); //moves cursor to first row
		int min = minHours.getInt("a");  //gets value of first row from column total, which is the total number of teachers
		stmt.close();
		return min;
		
		} catch(SQLException e) {}
		
		return 0;
	}

	protected int getMaxHoursTues(int teacherID)
	{
		try {
		stmt = conn.createStatement(); 
		ResultSet maxHours = stmt.executeQuery("select Max_Hours_Tues as b from teachers where teacherID = " + teacherID); //query that counts the number of rows from table teachers
		maxHours.next(); //moves cursor to first row
		int max = maxHours.getInt("b");  //gets value of first row from column total, which is the total number of teachers
		stmt.close();
		return max;
		
		} catch(SQLException e) {}
		
		return 0;
		
	}
	
	protected int getMinHoursWed(int teacherID)
	{
		try {
		stmt = conn.createStatement(); 
		ResultSet minHours = stmt.executeQuery("select Min_Hours_Wed as a from teachers where teacherID = " + teacherID); //query that counts the number of rows from table teachers
		minHours.next(); //moves cursor to first row
		int min = minHours.getInt("a");  //gets value of first row from column total, which is the total number of teachers
		stmt.close();
		return min;
		
		} catch(SQLException e) {}
		
		return 0;
	}

	protected int getMaxHoursWed(int teacherID)
	{
		try {
		stmt = conn.createStatement(); 
		ResultSet maxHours = stmt.executeQuery("select Max_Hours_Wed as b from teachers where teacherID = " + teacherID); //query that counts the number of rows from table teachers
		maxHours.next(); //moves cursor to first row
		int max = maxHours.getInt("b");  //gets value of first row from column total, which is the total number of teachers
		stmt.close();
		return max;
		
		} catch(SQLException e) {}
		
		return 0;
		
	}
	
	protected int getMinHoursThurs(int teacherID)
	{
		try {
		stmt = conn.createStatement(); 
		ResultSet minHours = stmt.executeQuery("select Min_Hours_Thurs as a from teachers where teacherID = " + teacherID); //query that counts the number of rows from table teachers
		minHours.next(); //moves cursor to first row
		int min = minHours.getInt("a");  //gets value of first row from column total, which is the total number of teachers
		stmt.close();
		return min;
		
		} catch(SQLException e) {}
		
		return 0;
	}

	protected int getMaxHoursThurs(int teacherID)
	{
		try {
		stmt = conn.createStatement(); 
		ResultSet maxHours = stmt.executeQuery("select Max_Hours_Thurs as b from teachers where teacherID = " + teacherID); //query that counts the number of rows from table teachers
		maxHours.next(); //moves cursor to first row
		int max = maxHours.getInt("b");  //gets value of first row from column total, which is the total number of teachers
		stmt.close();
		return max;
		
		} catch(SQLException e) {}
		
		return 0;
		
	}
	
	protected int getMinHoursFri(int teacherID)
	{
		try {
		stmt = conn.createStatement(); 
		ResultSet minHours = stmt.executeQuery("select Min_Hours_Fri as a from teachers where teacherID = " + teacherID); //query that counts the number of rows from table teachers
		minHours.next(); //moves cursor to first row
		int min = minHours.getInt("a");  //gets value of first row from column total, which is the total number of teachers
		stmt.close();
		return min;
		
		} catch(SQLException e) {}
		
		return 0;
	}

	protected int getMaxHoursFri(int teacherID)
	{
		try {
		stmt = conn.createStatement(); 
		ResultSet maxHours = stmt.executeQuery("select Max_Hours_Fri as b from teachers where teacherID = " + teacherID); //query that counts the number of rows from table teachers
		maxHours.next(); //moves cursor to first row
		int max = maxHours.getInt("b");  //gets value of first row from column total, which is the total number of teachers
		stmt.close();
		return max;
		
		} catch(SQLException e) {}
		
		return 0;
		
	}

	protected int getMaxCapacity(int subjectID)
	{
		try {
		stmt = conn.createStatement(); 
		ResultSet maxCapacity = stmt.executeQuery("select Max_Size as n from subjects where subjectID = " + subjectID); //query that counts the number of rows from table teachers
		maxCapacity.next(); //moves cursor to first row
		int max = maxCapacity.getInt("n");  //gets value of first row from column total, which is the total number of teachers
		stmt.close();
		return max;
		
		} catch(SQLException e) {}
		
		return 0;
		
	}
	
	
}
