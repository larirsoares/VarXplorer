package interaction.view;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class SpecDialog extends JFrame{
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public Boolean askSpec() {
		JPanel jpanel = new JPanel();
		int option = JOptionPane.showConfirmDialog(jpanel, "Do you want to apply previous specification?");
        
		if(option ==0){
			return true;
		}else{
			return false;
		}
		
	}

}
