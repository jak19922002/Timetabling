package myTimetabling;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class TeacherTimetables extends JFrame implements ActionListener
{
	DatabaseAccess access = new DatabaseAccess();
	JFrame frame;
	JPanel backNext;
	JPanel content;
	
	private static int count = 0;

	JButton nextButton;
	JButton backButton;
	//ArrayList<TeacherTimetables> tt = new ArrayList<TeacherTimetables>();
	ArrayList<JScrollPane> tt = new ArrayList<JScrollPane>();

	public TeacherTimetables()
	{			
		access.createConnection();

		backNext = new JPanel();
		content = new JPanel();
		frame = new JFrame(access.getTeacher(1));
		nextButton = new JButton("Next");
		backButton = new JButton("Back");
		
		content.setLayout(new BorderLayout());
		content.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
		
		backNext.setLayout(new FlowLayout(FlowLayout.LEFT));
		backNext.add(backButton, BorderLayout.CENTER); 
		backButton.requestFocusInWindow();
		backButton.addActionListener(this);
		backNext.add(nextButton, BorderLayout.CENTER); 
	    nextButton.requestFocusInWindow();
	    nextButton.addActionListener(this);
	    
		
		for(int teacher = 1; teacher <= access.getNumTeachers(); teacher++)
		{		
			tt.add(createTimetable(teacher));
		}
		content.add(tt.get(count), BorderLayout.CENTER);
		content.add(backNext, BorderLayout.PAGE_START);
		frame.getContentPane().add(content);
		frame.pack();
		frame.setVisible(true);
		

		
	}
	
	private JScrollPane createTimetable(int teacher)
	{
		JTable table = new JTable(new TeacherModel(teacher));
		table.setDefaultRenderer(String.class, new MultiLineCellRenderer());
	    table.setPreferredScrollableViewportSize(new Dimension(800, 90));
	    table.setFillsViewportHeight(true);
		
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);
		return scrollPane;

	}
	
	public void actionPerformed(ActionEvent event)
	{
	    Object source = event.getSource();

		if(source == nextButton)
		{
			next();
		}
		else if (source == backButton)
		{
			back();
		}
	}
	
	private void next()
	{
		if(count+1 != tt.size())
		{
			content.remove(tt.get(count));
			
			count++;
			content.add(tt.get(count));
			frame.getContentPane().add(content);
			frame.setTitle(access.getTeacher(count+1));
			content.getRootPane().revalidate();
			content.updateUI();
		}
		
	}
	
	private void back()
	{
		if(count-1 >= 0)
		{
			content.remove(tt.get(count));
			count--;
			content.add(tt.get(count));
			frame.getContentPane().add(content);			
			frame.setTitle(access.getTeacher(count+1));
			content.getRootPane().revalidate();
			content.updateUI();
		}	}
}
