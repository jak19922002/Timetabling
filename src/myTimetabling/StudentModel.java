package myTimetabling;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.table.AbstractTableModel;

public class StudentModel extends AbstractTableModel
{
	DatabaseAccess access = new DatabaseAccess();
	String[] lessons;
	String[] teachers;
	Object[][] data;

	String[] rows = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
	String[] columnNames = {"","1","2","3","4","5","6","7"};
	
	StudentModel(int studentID)
	{
		try {
		access.createConnection();
		
		lessons = new String[access.getNumLessons()];
		teachers = new String[access.getNumTeachers()];
		data = new Object[rows.length+1][columnNames.length];
		
		access.stmt = access.conn.createStatement();
		String query = "select lesson, teacher_name, periodID from student_assignment " +
				"where studentID = " + studentID;
		ResultSet lessonsCanTeach = access.stmt.executeQuery(query);
				
		while(lessonsCanTeach.next())
		{

			int row = (int) Math.ceil(lessonsCanTeach.getInt(3)/(double) (columnNames.length - 1));
			int col = lessonsCanTeach.getInt(3) - ((columnNames.length-1)*(row-1));
		
			//lessons[lessonsCanTeach.getInt(3)] = lessonsCanTeach.getString(1);
			//teachers[lessonsCanTeach.getInt(3)] = lessonsCanTeach.getString(2); 
			String[] temp = new String[2];
			temp[0] = lessonsCanTeach.getString(1);
			temp[1] = lessonsCanTeach.getString(2);
			data[row-1][col] = temp;
		}
		access.stmt.close();
		} catch(SQLException e) {}

	}
	
	    public String getColumnName(int col) 
	    {
	        return columnNames[col].toString();
	    }
	    public String getRowName(int row)
	    {
	    	return rows[row].toString();
	    }
	    public int getRowCount() 
	    { 
	    	return rows.length; 
	    }
	    public int getColumnCount() 
	    { 
	    	return columnNames.length; 
	    }
	    public Object getValueAt(int row, int col) 
	    {
	    	if(col == 0)
	    		return rows[row];
	    	
	    	if(data[row][col] == null)
	    		return "";
	    	
	    	return data[row][col];
	    }
	    public boolean isCellEditable(int row, int col)
	    { 
	    	return false; 
	    }
	    

}
