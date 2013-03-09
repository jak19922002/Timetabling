package myTimetabling;

import java.io.*;
import java.sql.*;

import org.w3c.dom.*;
import javax.xml.parsers.*;

public class ReadXML 
{
	private final static int NUMPERIODSDAY = 7;
	DatabaseAccess access = new DatabaseAccess();

	public ReadXML()
	{
		
		try 
		{
			access.createConnection();
			access.stmt = access.conn.createStatement();

			access.stmt.executeUpdate("delete from teacher_assignment");
			access.stmt.executeUpdate("delete from student_assignment");			
			access.stmt.executeUpdate("delete from teacher_unavailable");
			access.stmt.executeUpdate("delete from periods");
			access.stmt.executeUpdate("delete from teaching_groups");
			access.stmt.executeUpdate("delete from lessons");
			access.stmt.executeUpdate("delete from subject_groups");
			access.stmt.executeUpdate("delete from teaching_group_size");
			access.stmt.executeUpdate("delete from student_subjects");
			access.stmt.executeUpdate("delete from can_teach");
			access.stmt.executeUpdate("delete from subjects");
			access.stmt.executeUpdate("delete from teachers");
			access.stmt.executeUpdate("delete from students");
			
			access.stmt.executeUpdate("drop sequence period_seq restrict");
			access.stmt.executeUpdate("create sequence period_seq start with 1");
			
			access.stmt.executeUpdate("drop sequence lesson_seq restrict");
			access.stmt.executeUpdate("create sequence lesson_seq start with 1");
			
			access.stmt.executeUpdate("drop sequence teaching_group_seq restrict");
			access.stmt.executeUpdate("create sequence teaching_group_seq start with 1");
			
			access.stmt.executeUpdate("drop sequence subject_seq restrict");
			access.stmt.executeUpdate("create sequence subject_seq start with 1");
			
			access.stmt.executeUpdate("drop sequence student_seq restrict");
			access.stmt.executeUpdate("create sequence student_seq start with 1");
			
			access.stmt.executeUpdate("drop sequence teacher_seq restrict");
			access.stmt.executeUpdate("create sequence teacher_seq start with 1");
			
			periods();
			lessons();
			teaching_Group_Size();
			teaching_Groups();
			subjects();
			subject_Groups();
			students();
			student_Subjects();
			teachers();
			can_Teach();
			
			
			access.shutdown();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	private void periods()
	{
		try 
		{
			access.stmt = access.conn.createStatement();
		
			for(int i = 1; i <= NUMPERIODSDAY; i++)
			{
				String part = "insert into periods values(next value for period_seq, 'Monday'," + i + ")";
				access.stmt.addBatch(part);				
			}
			for(int i = 1; i <= NUMPERIODSDAY; i++)
			{
				String part = "insert into periods values(next value for period_seq, 'Tuesday'," + i + ")";
				access.stmt.addBatch(part); 
			}
			for(int i = 1; i <= NUMPERIODSDAY; i++)
			{
				String part = "insert into periods values(next value for period_seq, 'Wednesday'," + i + ")";
				access.stmt.addBatch(part); 
			}
			for(int i = 1; i <= NUMPERIODSDAY; i++)
			{
				String part = "insert into periods values(next value for period_seq, 'Thursday'," + i + ")";
				access.stmt.addBatch(part); 
			}
			for(int i = 1; i <= NUMPERIODSDAY; i++)
			{
				String part = "insert into periods values(next value for period_seq, 'Friday'," + i + ")";
				access.stmt.addBatch(part);
			}
			access.stmt.executeBatch();	
		    access.stmt.close();
	    
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	private void lessons()
	{
		try {
			access.stmt = access.conn.createStatement();


			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			
			Document doc = docBuilder.parse (new File("xml_files/lessons.xml"));
			doc.getDocumentElement().normalize();
			
			NodeList listOfLessons = doc.getElementsByTagName("Lessons");
			
			
			for(int s=0; s < listOfLessons.getLength(); s++)
			{
				Node lessons = listOfLessons.item(s);
				
				if(lessons.getNodeType() == Node.ELEMENT_NODE)
				{
					Element lesson = (Element)lessons;
					
					NodeList lessonID = lesson.getElementsByTagName("LessonID");		
					String lID = lessonID.item(0).getChildNodes().item(0).getNodeValue();
		
					NodeList lessonList = lesson.getElementsByTagName("Lesson");
					String lessonName = lessonList.item(0).getChildNodes().item(0).getNodeValue();
		
					access.stmt.addBatch("insert into lessons values("+ lID +",'"+ lessonName +"')");
				}
			}
			access.stmt.executeBatch();
		    access.stmt.close();

		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void teaching_Group_Size()
	{
		try {
			access.stmt = access.conn.createStatement();


			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			
			Document doc = docBuilder.parse (new File("xml_files/teaching_group_size.xml"));
			doc.getDocumentElement().normalize();
			
			NodeList listOfTGroups = doc.getElementsByTagName("Teaching_Groups");
			
			
			for(int s=0; s < listOfTGroups.getLength(); s++)
			{
				Node tGroups = listOfTGroups.item(s);
				
				if(tGroups.getNodeType() == Node.ELEMENT_NODE)
				{
					Element tGroup = (Element)tGroups;
					
					NodeList teaching_groupID = tGroup.getElementsByTagName("Teaching_GroupID");		
					String tID = teaching_groupID.item(0).getChildNodes().item(0).getNodeValue();
		
					NodeList tGroupList = tGroup.getElementsByTagName("Teaching_Group");
					String teaching_group = tGroupList.item(0).getChildNodes().item(0).getNodeValue();
		
					access.stmt.addBatch("insert into teaching_group_size values("+ tID +",'"+ teaching_group +"')");
				}
			}
			access.stmt.executeBatch();
		    access.stmt.close();

		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void teaching_Groups()
	{
		try 
		{
			
			access.stmt = access.conn.createStatement();

			String part = "insert into teaching_groups select teaching_group, lesson " +
					"from teaching_group_size join lessons on teaching_group like " + 
					"substr(lesson,1,length(lesson)-1)";
			
			access.stmt.addBatch(part);
		
			access.stmt.executeBatch();
		    access.stmt.close();

		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void subjects()
	{
		try {

			access.stmt = access.conn.createStatement();

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			
			Document doc = docBuilder.parse (new File("xml_files/subjects.xml"));
			doc.getDocumentElement().normalize();
			
			NodeList listOfSubjects = doc.getElementsByTagName("Subject");
			
			
			for(int s=0; s < listOfSubjects.getLength(); s++)
			{
				Node subjects = listOfSubjects.item(s);
				
				if(subjects.getNodeType() == Node.ELEMENT_NODE)
				{
					Element subject = (Element)subjects;
					
					NodeList subjectID = subject.getElementsByTagName("SubjectID");		
					String sID = subjectID.item(0).getChildNodes().item(0).getNodeValue();
		
					NodeList subjectList = subject.getElementsByTagName("Subject_Name");
					String sub = subjectList.item(0).getChildNodes().item(0).getNodeValue();
					
					NodeList max_Size = subject.getElementsByTagName("Max_Size");
					String max = max_Size.item(0).getChildNodes().item(0).getNodeValue();
		
					access.stmt.addBatch("insert into subjects values("+ sID +",'"+ sub +"', " + max + ")");
				}
			}
			access.stmt.executeBatch();
		    access.stmt.close();

		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void subject_Groups()
	{
		try
		{
			access.stmt = access.conn.createStatement();
	
			String part = "insert into subject_groups select subject, teaching_group " +
					"from subjects join teaching_group_size on subject like " + 
					"substr(teaching_group,1,length(teaching_group)-2)";
			
			access.stmt.addBatch(part);		
			
			access.stmt.executeBatch();
		    access.stmt.close();

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void students()
	{
		try {

			access.stmt = access.conn.createStatement();

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			
			Document doc = docBuilder.parse (new File("xml_files/students.xml"));
			doc.getDocumentElement().normalize();
			
			NodeList listOfStudents = doc.getElementsByTagName("Student");
			
			
			for(int s=0; s < listOfStudents.getLength(); s++)
			{
				Node students = listOfStudents.item(s);
				
				if(students.getNodeType() == Node.ELEMENT_NODE)
				{
					Element student = (Element)students;
					
					NodeList studentID = student.getElementsByTagName("StudentID");		
					String sID = studentID.item(0).getChildNodes().item(0).getNodeValue();
		
					NodeList studentList = student.getElementsByTagName("Student_Name");
					String student_name = studentList.item(0).getChildNodes().item(0).getNodeValue();
		
					access.stmt.addBatch("insert into students values("+ sID +",'"+ student_name +"')");
				}
			}
			access.stmt.executeBatch();
		    access.stmt.close();

		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void student_Subjects()
	{
		try {
			access.stmt = access.conn.createStatement();


			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			
			Document doc = docBuilder.parse (new File("xml_files/student_subjects.xml"));
			doc.getDocumentElement().normalize();
			
			NodeList listOfStudentSubjects = doc.getElementsByTagName("student_subjects");
			
			
			for(int s=0; s < listOfStudentSubjects.getLength(); s++)
			{
				Node studentSubjects = listOfStudentSubjects.item(s);
				
				if(studentSubjects.getNodeType() == Node.ELEMENT_NODE)
				{
					Element studentSubject = (Element)studentSubjects;
					
					NodeList studentID = studentSubject.getElementsByTagName("StudentID");		
					String sID = studentID.item(0).getChildNodes().item(0).getNodeValue();
		
					NodeList subjectList = studentSubject.getElementsByTagName("Subject");
					String subject = subjectList.item(0).getChildNodes().item(0).getNodeValue();
		
					access.stmt.addBatch("insert into student_subjects values("+ sID +",'"+ subject +"')");
				}
			}
			access.stmt.executeBatch();
		    access.stmt.close();

		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void teachers()
	{
		try {
			access.stmt = access.conn.createStatement();


			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			
			Document doc = docBuilder.parse (new File("xml_files/teachers.xml"));
			doc.getDocumentElement().normalize();
			
			NodeList listOfTeachers = doc.getElementsByTagName("Teacher");
			
			
			for(int s=0; s < listOfTeachers.getLength(); s++)
			{
				Node teachers = listOfTeachers.item(s);
				
				if(teachers.getNodeType() == Node.ELEMENT_NODE)
				{
					Element teacher = (Element)teachers;
					
					NodeList teacherID = teacher.getElementsByTagName("TeacherID");		
					String tID = teacherID.item(0).getChildNodes().item(0).getNodeValue();
		
					NodeList teacherList = teacher.getElementsByTagName("Teacher_Name");
					String teacher_name = teacherList.item(0).getChildNodes().item(0).getNodeValue();
					
					NodeList min_Hours_Mon = teacher.getElementsByTagName("Min_Hours_Mon");		
					String minMon = min_Hours_Mon.item(0).getChildNodes().item(0).getNodeValue();
		
					NodeList max_Hours_Mon = teacher.getElementsByTagName("Max_Hours_Mon");
					String maxMon = max_Hours_Mon.item(0).getChildNodes().item(0).getNodeValue();
					
					NodeList min_Hours_Tues = teacher.getElementsByTagName("Min_Hours_Tues");		
					String minTues = min_Hours_Tues.item(0).getChildNodes().item(0).getNodeValue();
		
					NodeList max_Hours_Tues = teacher.getElementsByTagName("Max_Hours_Tues");
					String maxTues = max_Hours_Tues.item(0).getChildNodes().item(0).getNodeValue();
					
					NodeList min_Hours_Wed = teacher.getElementsByTagName("Min_Hours_Wed");		
					String minWed = min_Hours_Wed.item(0).getChildNodes().item(0).getNodeValue();
		
					NodeList max_Hours_Wed = teacher.getElementsByTagName("Max_Hours_Wed");
					String maxWed = max_Hours_Wed.item(0).getChildNodes().item(0).getNodeValue();
					
					NodeList min_Hours_Thurs = teacher.getElementsByTagName("Min_Hours_Thurs");		
					String minThurs = min_Hours_Thurs.item(0).getChildNodes().item(0).getNodeValue();
		
					NodeList max_Hours_Thurs = teacher.getElementsByTagName("Max_Hours_Thurs");
					String maxThurs = max_Hours_Thurs.item(0).getChildNodes().item(0).getNodeValue();
					
					NodeList min_Hours_Fri = teacher.getElementsByTagName("Min_Hours_Fri");		
					String minFri = min_Hours_Fri.item(0).getChildNodes().item(0).getNodeValue();
		
					NodeList max_Hours_Fri = teacher.getElementsByTagName("Max_Hours_Fri");
					String maxFri = max_Hours_Fri.item(0).getChildNodes().item(0).getNodeValue();
		
					access.stmt.addBatch("insert into teachers values("+ tID +",'"+ teacher_name +"', " + minMon + ", " + maxMon + ", " + minTues + ", " + maxTues + ", " + minWed + ", " + maxWed + ", " + minThurs 
							+ ", " + maxThurs + ", " + minFri + ", " + maxFri + ")");
				}
			}
			access.stmt.executeBatch();
		    access.stmt.close();

		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void can_Teach()
	{
		try {
			access.stmt = access.conn.createStatement();


			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			
			Document doc = docBuilder.parse (new File("xml_files/can_teach.xml"));
			doc.getDocumentElement().normalize();
			
			NodeList listOfTeachers = doc.getElementsByTagName("CanTeach");
			
			
			for(int s=0; s < listOfTeachers.getLength(); s++)
			{
				Node teachers = listOfTeachers.item(s);
				
				if(teachers.getNodeType() == Node.ELEMENT_NODE)
				{
					Element teacher = (Element)teachers;
					
					NodeList teacherID = teacher.getElementsByTagName("TeacherID");		
					String tID = teacherID.item(0).getChildNodes().item(0).getNodeValue();
		
					NodeList subjectList = teacher.getElementsByTagName("Subject");
					String subject = subjectList.item(0).getChildNodes().item(0).getNodeValue();
		
					access.stmt.addBatch("insert into can_teach values("+ tID +",'"+ subject +"')");
				}
			}
			access.stmt.executeBatch();
		    access.stmt.close();

		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void teacher_Unavailable()
	{
		
	}
	
	
}
