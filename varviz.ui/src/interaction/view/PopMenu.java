package interaction.view;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class PopMenu extends JPopupMenu{
	
	//private ArrayList<String> varEdgeinfoList;
	private ActionListener menuListener;

	public void setinfoList(ArrayList<String> varEdgeinfoList) {
		//this.varEdgeinfoList = varEdgeinfoList;
		
		 String first = varEdgeinfoList.get(0);
		    for(String info: varEdgeinfoList){
		    	if(info.equals("separator")){
		    		 this.addSeparator();
		    		 continue;
		    	}
		    	if(info.equals(first)){
		    		JMenuItem item;				 
				    this.add(item = new JMenuItem(first));
				    item.setHorizontalTextPosition(JMenuItem.RIGHT);
				    item.addActionListener(menuListener);
		    	}else{
		    			    		
		    		JCheckBoxMenuItem label = new JCheckBoxMenuItem(info);
			    	this.add(label);
			    	label.setHorizontalTextPosition(JMenuItem.RIGHT);
			    	label.addActionListener(menuListener);
		    	}		    	
		    }

	}

	public PopMenu(ArrayList<String> varEdgeinfoList) {
		ActionListener menuListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("Popup menu item ["
			            + e.getActionCommand() + "] was pressed.");
				
			}
		    };
		    		   
		    String first = varEdgeinfoList.get(0);
		    for(String info: varEdgeinfoList){
		    	if(info.equals("separator")){
		    		 this.addSeparator();
		    		 continue;
		    	}
		    	if(info.equals(first)){
		    		JMenuItem item;				 
				    this.add(item = new JMenuItem(first));
				    item.setHorizontalTextPosition(JMenuItem.RIGHT);
				    item.addActionListener(menuListener);
		    	}else{
		    			    		
		    		JCheckBoxMenuItem label = new JCheckBoxMenuItem(info);
			    	this.add(label);
			    	label.setHorizontalTextPosition(JMenuItem.RIGHT);
			    	label.addActionListener(menuListener);
		    	}		    	
		    }

		   // popup.setBorder(new BevelBorder(BevelBorder.RAISED));
		   // popup.addPopupMenuListener(new PopupPrintListener());

		    //addMouseListener(new MousePopupListener());
		   
		
	}

	public PopMenu() {
			menuListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("Popup menu item ["
			            + e.getActionCommand() + "] was pressed.");
				
			}
		    };
		    
		    
	}
	
	
}
