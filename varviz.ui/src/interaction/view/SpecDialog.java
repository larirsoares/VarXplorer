package interaction.view;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class SpecDialog extends JFrame{
	


	public Boolean askSpec() {
		JPanel jpanel = new JPanel();
		int option = JOptionPane.showConfirmDialog(null, "Do you want to apply previous specification?");
        jpanel.setVisible(true);
		
		if(option ==0){
			return true;
		}else{
			return false;
		}
		
	}

}
