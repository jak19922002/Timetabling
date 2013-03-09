package myTimetabling;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class WriteXML 
{
	
	public WriteXML()
	{
		FileInputStream ss;
		FileInputStream subject;
		FileInputStream student;
		FileInputStream teacher;
		FileInputStream canTeach;
		
		try {
			subject = new FileInputStream("Brampton_Data/subjects.xlsx");
			lessons(subject);
			
			subject = new FileInputStream("Brampton_Data/subjects.xlsx");
			teaching_group_size(subject);

			subject = new FileInputStream("Brampton_Data/subjects.xlsx");
			subjects(subject);
			
			student = new FileInputStream("Brampton_Data/students.xlsx");
			students(student);

			ss = new FileInputStream("Brampton_Data/Student_Subjects.xlsx");
			student_Subjects(ss);
			
			teacher = new FileInputStream("Brampton_Data/teachers_hours.xlsx");
			teachers(teacher);
			
			canTeach = new FileInputStream("Brampton_Data/can_teach.xlsx");
			can_teach(canTeach);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	private void lessons(FileInputStream subject)
	{
		try 
		{
			Workbook wb = WorkbookFactory.create(subject);
			Sheet sheet = wb.getSheetAt(0);
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("LessonsTable");
			doc.appendChild(rootElement);
			
			for(int i = 1; sheet.getRow(i) != null; i++)
			{
				Row row = sheet.getRow(i);

				Cell lName = row.getCell(0);
				Cell numGroups = row.getCell(1);
				Cell numLessons = row.getCell(2);
				
				char tGroup = 'a';
			
				for(int numTeachingGroups = 1; numTeachingGroups <= (int) numGroups.getNumericCellValue(); numTeachingGroups++)
				{
					for(int numberOfLessons = 1; numberOfLessons <= (int) numLessons.getNumericCellValue(); numberOfLessons++)
					{
						if(lName == null)
							break;
						
						Element lessons = doc.createElement("Lessons");
						rootElement.appendChild(lessons);
						
						// lessonID elements
						Element lessonID = doc.createElement("LessonID");
						lessonID.appendChild(doc.createTextNode("next value for lesson_seq"));
						lessons.appendChild(lessonID);
			
						
						// lesson elements
						Element lesson = doc.createElement("Lesson");
						lesson.appendChild(doc.createTextNode(lName.getStringCellValue() + "/" + tGroup + numberOfLessons));
						lessons.appendChild(lesson);
						
					}
					tGroup++;
				}
			}
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("xml_files/lessons.xml"));
	 
			transformer.transform(source, result);
	 
			System.out.println("File saved!");
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	private void teaching_group_size(FileInputStream subject)
	{
		try 
		{
			Workbook wb = WorkbookFactory.create(subject);
			Sheet sheet = wb.getSheetAt(0);
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Teaching_Group_Size");
			doc.appendChild(rootElement);
			
			for(int i = 1; sheet.getRow(i) != null; i++)
			{
				Row row = sheet.getRow(i);
 
				Cell tName = row.getCell(0);
				Cell numGroups = row.getCell(1);
				
				char tGroup = 'a';
			
				for(int numTeachingGroups = 1; numTeachingGroups <= (int) numGroups.getNumericCellValue(); numTeachingGroups++)
				{

						if(tName == null)
							break;
						
						Element teaching_groups = doc.createElement("Teaching_Groups");
						rootElement.appendChild(teaching_groups);
						
						// lessonID elements
						Element tgID = doc.createElement("Teaching_GroupID");
						tgID.appendChild(doc.createTextNode("next value for teaching_group_seq"));
						teaching_groups.appendChild(tgID);
			
						
						// lesson elements
						Element teachingGroup = doc.createElement("Teaching_Group");
						teachingGroup.appendChild(doc.createTextNode(tName.getStringCellValue() + "/" + tGroup));
						teaching_groups.appendChild(teachingGroup);
						
					tGroup++;
				}
			}
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("xml_files/teaching_group_size.xml"));
	 
			transformer.transform(source, result);
	 
			System.out.println("File saved!");
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	private void subjects(FileInputStream subject)
	{
		try 
		{
			Workbook wb = WorkbookFactory.create(subject);
			Sheet sheet = wb.getSheetAt(0);
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Subjects");
			doc.appendChild(rootElement);
			
			for(int i = 1; sheet.getRow(i) != null; i++)
			{
				Row row = sheet.getRow(i);

				Cell sName = row.getCell(0);

				if(sName == null)
					break;
				
				Element subjects = doc.createElement("Subject");
				rootElement.appendChild(subjects);
				
				// subjectID elements
				Element sID = doc.createElement("SubjectID");
				sID.appendChild(doc.createTextNode("next value for subject_seq"));
				subjects.appendChild(sID);
	
				
				// subject elements
				Element subName = doc.createElement("Subject_Name");
				subName.appendChild(doc.createTextNode(sName.getStringCellValue()));
				subjects.appendChild(subName);
				
				
				//------ WHAT MIGHT DO IS ASK BRAMPTON FOR DIFFERENT MAX SIZES FOR DIFFERENT 
				//SUBJECTS SO THAT NOT ALL TEACHING GROUPS ARE THE SAME SIZE----------
				// max size elements
				Element max = doc.createElement("Max_Size");
				max.appendChild(doc.createTextNode("10"));
				subjects.appendChild(max);
				
			}
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("xml_files/subjects.xml"));
	 
			transformer.transform(source, result);
	 
			System.out.println("File saved!");
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void students(FileInputStream student)
	{
		try 
		{
			Workbook wb = WorkbookFactory.create(student);
			Sheet sheet = wb.getSheetAt(0);
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Students");
			doc.appendChild(rootElement);
			
			for(int i = 1; i <= 50; i++)  //sheet.getRow(i) != null
			{
				Row row = sheet.getRow(i);

				// student subjects elements
		 
				Cell sName = row.getCell(0);

				if(sName == null)
					break;
				
				Element students = doc.createElement("Student");
				rootElement.appendChild(students);
				
				// subjectID elements
				Element sID = doc.createElement("StudentID");
				sID.appendChild(doc.createTextNode("next value for student_seq"));
				students.appendChild(sID);
	
				
				// subject elements
				Element studentName = doc.createElement("Student_Name");
				studentName.appendChild(doc.createTextNode(sName.getStringCellValue()));
				students.appendChild(studentName);
				
			}
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("xml_files/students.xml"));
	 
			transformer.transform(source, result);
	 
			System.out.println("File saved!");
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void student_Subjects(FileInputStream fis)
	{
		try 
		{
			Workbook wb = WorkbookFactory.create(fis);
			Sheet sheet = wb.getSheetAt(0);
					
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Student_Subjects");
			doc.appendChild(rootElement);
			
			for(int i = 1; sheet.getRow(i) != null; i++)
			{
				Row row = sheet.getRow(i);

				Cell studentID = row.getCell(1);
				Cell subjectChoice = row.getCell(2);
	
				if(studentID.getNumericCellValue() == 51)
					break;
				
				if(studentID == null || subjectChoice == null)
					break;
				
				Element student_subjects = doc.createElement("student_subjects");
				rootElement.appendChild(student_subjects);
				
				// studentID elements
				Element student = doc.createElement("StudentID");
				student.appendChild(doc.createTextNode(String.valueOf((int) studentID.getNumericCellValue())));
				student_subjects.appendChild(student);
	
				// subject elements
				Element subject = doc.createElement("Subject");
				subject.appendChild(doc.createTextNode(subjectChoice.getStringCellValue()));
				student_subjects.appendChild(subject);
				
				
			}
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("xml_files/student_subjects.xml"));
	 
			transformer.transform(source, result);
	 
			System.out.println("File saved!");
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void teachers(FileInputStream teacher)
	{
		try 
		{
			Workbook wb = WorkbookFactory.create(teacher);
			Sheet sheet = wb.getSheetAt(0);
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Teachers");
			doc.appendChild(rootElement);
			
			for(int i = 1; sheet.getRow(i) != null; i++)
			{
				Row row = sheet.getRow(i);

				// student subjects elements
		 
				Cell tName = row.getCell(1);
				Cell minMon = row.getCell(5);
				Cell maxMon = row.getCell(6);
				Cell minTues = row.getCell(7);
				Cell maxTues = row.getCell(8);				
				Cell minWed = row.getCell(9);
				Cell maxWed = row.getCell(10);
				Cell minThurs = row.getCell(11);
				Cell maxThurs = row.getCell(12);
				Cell minFri = row.getCell(13);
				Cell maxFri = row.getCell(14);

				if(tName == null)
					break;
				
				Element teachers = doc.createElement("Teacher");
				rootElement.appendChild(teachers);
				
				// teacherID elements
				Element tID = doc.createElement("TeacherID");
				tID.appendChild(doc.createTextNode("next value for teacher_seq"));
				teachers.appendChild(tID);
	
				
				// teacher elements
				Element teacherName = doc.createElement("Teacher_Name");
				teacherName.appendChild(doc.createTextNode(tName.getStringCellValue()));
				teachers.appendChild(teacherName);
				
				// min hours elements
				Element minHoursMon = doc.createElement("Min_Hours_Mon");
				minHoursMon.appendChild(doc.createTextNode(String.valueOf(minMon.getNumericCellValue())));
				teachers.appendChild(minHoursMon);
	
				
				// max hours elements
				Element maxHoursMon = doc.createElement("Max_Hours_Mon");
				maxHoursMon.appendChild(doc.createTextNode(String.valueOf(maxMon.getNumericCellValue())));
				teachers.appendChild(maxHoursMon);
				
				// min hours elements
				Element minHoursTues = doc.createElement("Min_Hours_Tues");
				minHoursTues.appendChild(doc.createTextNode(String.valueOf(minTues.getNumericCellValue())));
				teachers.appendChild(minHoursTues);
	
				
				// max hours elements
				Element maxHoursTues = doc.createElement("Max_Hours_Tues");
				maxHoursTues.appendChild(doc.createTextNode(String.valueOf(maxTues.getNumericCellValue())));
				teachers.appendChild(maxHoursTues);
				
				// min hours elements
				Element minHoursWed = doc.createElement("Min_Hours_Wed");
				minHoursWed.appendChild(doc.createTextNode(String.valueOf(minWed.getNumericCellValue())));
				teachers.appendChild(minHoursWed);
	
				
				// max hours elements
				Element maxHoursWed = doc.createElement("Max_Hours_Wed");
				maxHoursWed.appendChild(doc.createTextNode(String.valueOf(maxWed.getNumericCellValue())));
				teachers.appendChild(maxHoursWed);
				
				// min hours elements
				Element minHoursThurs = doc.createElement("Min_Hours_Thurs");
				minHoursThurs.appendChild(doc.createTextNode(String.valueOf(minThurs.getNumericCellValue())));
				teachers.appendChild(minHoursThurs);
	
				
				// max hours elements
				Element maxHoursThurs = doc.createElement("Max_Hours_Thurs");
				maxHoursThurs.appendChild(doc.createTextNode(String.valueOf(maxThurs.getNumericCellValue())));
				teachers.appendChild(maxHoursThurs);
				
				// min hours elements
				Element minHoursFri = doc.createElement("Min_Hours_Fri");
				minHoursFri.appendChild(doc.createTextNode(String.valueOf(minFri.getNumericCellValue())));
				teachers.appendChild(minHoursFri);
	
				
				// max hours elements
				Element maxHoursFri = doc.createElement("Max_Hours_Fri");
				maxHoursFri.appendChild(doc.createTextNode(String.valueOf(maxFri.getNumericCellValue())));
				teachers.appendChild(maxHoursFri);
				
				
			}
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("xml_files/teachers.xml"));
	 
			transformer.transform(source, result);
	 
			System.out.println("File saved!");
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void can_teach(FileInputStream canTeach)
	{
		try 
		{
			Workbook wb = WorkbookFactory.create(canTeach);
			Sheet sheet = wb.getSheetAt(0);
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Can_Teach");
			doc.appendChild(rootElement);
			
			for(int i = 1; sheet.getRow(i) != null; i++)
			{
				Row row = sheet.getRow(i);

				// student subjects elements
				Element teachers = doc.createElement("CanTeach");
				rootElement.appendChild(teachers);
		 
				Cell teacherID = row.getCell(0);
				Cell can = row.getCell(1);

				if(teacherID == null || can == null)
					break;
				
				// teacherID elements
				Element tID = doc.createElement("TeacherID");
				tID.appendChild(doc.createTextNode(String.valueOf((int) teacherID.getNumericCellValue())));
				teachers.appendChild(tID);
	
				
				// teacher elements
				Element can_Teach = doc.createElement("Subject");
				can_Teach.appendChild(doc.createTextNode(can.getStringCellValue()));
				teachers.appendChild(can_Teach);
				
				
			}
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("xml_files/can_teach.xml"));
	 
			transformer.transform(source, result);
	 
			System.out.println("File saved!");
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
} 
