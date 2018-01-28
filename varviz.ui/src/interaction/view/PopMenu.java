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
	private String from = null;
	private String to = null;
	private ArrayList<PopOption> optionsPOP = new ArrayList<>();
	//lembrar de na hora de passar pro resto do programa pegar aqui esse arraylist com os ultimos pop

	public ArrayList<PopOption> getOptionsPOP() {
		return optionsPOP;
	}
	public String getFrom() {
		return from;
	}
	public String getTo() {
		return to;
	}

	public void setinfoList(ArrayList<String> varEdgeinfoList, String from, String to, ArrayList<String> optionTrue) {
		//this.varEdgeinfoList = varEdgeinfoList;
		this.from = from;
		this.to = to;
		optionsPOP = new ArrayList<>();
		
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
		    		
		    		optionsPOP.add(new PopOption(info, from, to));
		    		JCheckBoxMenuItem label = new JCheckBoxMenuItem(info);
			    	this.add(label);
			    	label.setHorizontalTextPosition(JMenuItem.RIGHT);
			    	label.addActionListener(menuListener);
			    	
			    	System.out.println("entered");
			    	
			    	for(String in: optionTrue){
			    		if(in.equals(info)){
					    	label.setSelected(true);
					    	for(int i=0; i<optionsPOP.size(); i++){
					    		PopOption option =  optionsPOP.get(i);
					    		if(option.getInfo().equals(info)){
					    			optionsPOP.get(i).setState(true);
					    			break;
					    		}
					    	}
					    	updateUI(); //just trying
					    	revalidate(); //get desparate
					    	repaint(); //last attempt		    		
			    		}
			    		
			    	}

		    	}		    	
		    }

	}

	public PopMenu(ArrayList<String> varEdgeinfoList) {
		ActionListener menuListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("Popup menu item ["
			            + e.getActionCommand() + "] was pressed1.");
				
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
				
				for(PopOption p: optionsPOP){
					if(p.getInfo().equals(e.getActionCommand())){
						p.markState();
					}
				}									
				
			}

		    };
		    
		    
	}
	
	
}
