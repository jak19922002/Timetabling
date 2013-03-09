package myTimetabling;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.table.AbstractTableModel;

public class TeacherModel extends AbstractTableModel
{
	DatabaseAccess access = new DatabaseAccess();
	String[] lessons;

	String[] rows = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
	String[] columnNames = {"","1","2","3","4","5","6","7"};
	
	TeacherModel(int teacherID)
	{
		try {
		access.createConnection();
		
		lessons = new String[access.getNumLessons()];
		
		access.stmt = access.conn.createStatement();
		String query = "select lesson, periodID from teacher_assignment " +
				"where teacherID = " + teacherID;
		ResultSet lessonsCanTeach = access.stmt.executeQuery(query);
				
		while(lessonsCanTeach.next())
		{
			lessons[lessonsCanTeach.getInt(2)] = lessonsCanTeach.getString(1);
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
	    	if(col == 0) //row==0 ||
	    		return rows[row];
	    	
	    	int periodID = (row)*(getColumnCount()-1) + col;
	    	
	    	if(lessons[periodID] == null)
	    		return "";
	    	else
	    		return lessons[periodID];
	    }
	    public boolean isCellEditable(int row, int col)
	    { 
	    	return false; 
	    }
	    
        public void setValueAt(String value, int row, int col) 
        {
        	int periodID = (row - 1)*getColumnCount() + col;

            lessons[periodID] = value;
            fireTableCellUpdated(row, col);
 
        }

    

}
