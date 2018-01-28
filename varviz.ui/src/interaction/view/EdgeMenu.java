package interaction.view;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;


public class EdgeMenu extends JPanel {

	public JPopupMenu popup;
	 
	public EdgeMenu(ArrayList<String> varEdgeinfoList){
		   popup = new JPopupMenu();
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
		    		 popup.addSeparator();
		    		 continue;
		    	}
		    	if(info.equals(first)){
		    		JMenuItem item;				 
				    popup.add(item = new JMenuItem(first));
				    item.setHorizontalTextPosition(JMenuItem.RIGHT);
				    item.addActionListener(menuListener);
		    	}else{
		    			    		
		    		JCheckBoxMenuItem label = new JCheckBoxMenuItem(info);
			    	popup.add(label);
			    	label.setHorizontalTextPosition(JMenuItem.RIGHT);
			    	label.addActionListener(menuListener);
		    	}		    	
		    }

		   // popup.setBorder(new BevelBorder(BevelBorder.RAISED));
		   // popup.addPopupMenuListener(new PopupPrintListener());

		    //addMouseListener(new MousePopupListener());
		    this.setComponentPopupMenu(popup);
		
	}
	
	  // An inner class to check whether mouse events are the popup trigger
	  class MousePopupListener extends MouseAdapter {
	    public void mousePressed(MouseEvent e) {
	      //checkPopup(e);
	    }

	    public void mouseClicked(MouseEvent e) {
	     // checkPopup(e);
	    }

	    public void mouseReleased(MouseEvent e) {
	     // checkPopup(e);
	    }

	   // private void checkPopup(MouseEvent e) {
	     // if (e.isPopupTrigger()) {
	      //  popup.show(EdgeMenu.this, e.getX(), e.getY());
	    //  }
	    //}
	  }

	  // An inner class to show when popup events occur
	  class PopupPrintListener implements PopupMenuListener {
	    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
	      System.out.println("Popup menu will be visible!");
	    }

	    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
	      System.out.println("Popup menu will be invisible!");
	    }

	    public void popupMenuCanceled(PopupMenuEvent e) {
	      System.out.println("Popup menu is hidden!");
	    }
	  }
}
