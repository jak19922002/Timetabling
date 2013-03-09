package myTimetabling;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
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
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class StudentTimetables extends JFrame implements ActionListener
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

	public StudentTimetables()
	{			
		access.createConnection();

		backNext = new JPanel();
		content = new JPanel();
		frame = new JFrame(access.getStudent(1));
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
	    
		
		for(int student = 1; student <= access.getNumStudents(); student++)
		{		
			tt.add(createTimetable(student));
		}
		content.add(tt.get(count), BorderLayout.CENTER);
		content.add(backNext, BorderLayout.PAGE_START);
		frame.getContentPane().add(content);
		frame.pack();
		frame.setVisible(true);
		

		
	}
	
	private JScrollPane createTimetable(int student)
	{
		StudentModel model = new StudentModel(student);
		JTable table = new JTable(model);
		table.setModel(model);
	    table.setRowHeight(40);
		table.setPreferredScrollableViewportSize(new Dimension(800, 200));
	    table.setDefaultRenderer(StudentModel.class, new MultiLineCellRenderer());
	    table.setFillsViewportHeight(true);
	   
	    
	    TableCellRenderer jTableCellRenderer = new TableCellRenderer() 
	    {
	    	/* These are necessary variables to store the row's height */
	    	private int minHeight = -1;
	    	private int currHeight = -1;
	    	
	    	@SuppressWarnings("serial")
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) 
	    	{
	    	/* If what we're displaying isn't an array of values we
	    	return the normal renderer*/
		    	if(!value.getClass().isArray())
		    	{
		    		return table.getDefaultRenderer(value.getClass()).getTableCellRendererComponent(table, value, isSelected, hasFocus,row, column);
		    	}
		    	else
		    	{
		    		final Object[] passed = (Object[])value;
			    	/* We calculate the row's height to display data
			    	* THis is not complete and has some bugs that
			    	* will be analyzed in further articles */
			    	if(minHeight == -1)
			    	{
			    		minHeight = table.getRowHeight();
			    	}
			    	if(currHeight != passed.length*minHeight)
			    	{
				    	currHeight = passed.length * minHeight;
				    	table.setRowHeight(row,currHeight);
			    	}
	    	
			    	/* We create the table that will hold the multivalue
			    	 *fields and that will be embedded in the main table */
			    	return new JTable(new AbstractTableModel() 
			    	{
				    	public int getColumnCount() 
				    	{
				    		return 1;
				    	}
				    	public int getRowCount() 
				    	{
				    		return passed.length;
				    	}
				    	public Object getValueAt(int row, int col) 
				    	{
				    		return passed[row];
				    	}
				    	public boolean isCellEditable(int row, int col)
				    	{ 
				    		return true; 
				    	}
				    });
		    	}
	    	}
	    };
	    	/* Finally we apply the new cellRenderer to the whole table */
	    	TableColumnModel tcm = table.getColumnModel();
	    	for(int i = 0; i < tcm.getColumnCount(); i++)
	    	{
	    		tcm.getColumn(i).setCellRenderer(jTableCellRenderer);
	    	
	    	}
	    	
	    
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
			frame.setTitle(access.getStudent(count+1));
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
			frame.setTitle(access.getStudent(count+1));
			content.getRootPane().revalidate();
			content.updateUI();
		}	}
}
