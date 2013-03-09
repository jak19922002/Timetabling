package myTimetabling;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

public class VisualTimetable extends JFrame implements ActionListener
{
	DatabaseAccess access = new DatabaseAccess();
	
	JPanel main = new JPanel();
	JPanel studentTimetables = new JPanel();
	
	JButton teachers = new JButton("Teacher Timetables");
	JButton students = new JButton("Student Timetables"); 	

	JTable sTT = new JTable();
	JTable mTT = new JTable();


	public VisualTimetable()
	{
		//GUI of main page
		setBounds(100,100,300,200);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    Container con = this.getContentPane(); // inherit main frame
	    con.add(main);    // JPanel containers default to FlowLayout
	    main.add(teachers); 
	    teachers.requestFocusInWindow();
	    teachers.addActionListener(this);
	    main.add(students); 
	    students.requestFocusInWindow();
	    students.addActionListener(this);
	    setVisible(true); // make frame visible
	    
	}
	
	public static void main(String args[])
	{
		new VisualTimetable();
	}
	
	  public void actionPerformed(ActionEvent event)
	  {
	    Object source = event.getSource();
	    if (source == teachers)
	    {
	    	new TeacherTimetables();
	    }
	    else if(source == students)
	    {
	    	new StudentTimetables();      
	    }
	  }
}
