package interaction.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import info.leadinglight.jdot.Graph;
import info.leadinglight.jdot.impl.Util;


public class SVGCanvas {
	
	/**
	 * has...
	 * 
	 * @author Larissa Rocha
	 *  
	 */
	
	public SVGCanvas (Graph g, File workingDir) {
		
		//get dimension of the screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = screenSize.width;
		int height = screenSize.height;
		int verticalOffset = 40;
		int horizontalOffset = 10;
		
		//gera a imagem svg a partir da stream de dados
		Util.toFile(Graph.dot2out(Graph.DEFAULT_CMD,"svg",g.toDot()), new File( workingDir.getAbsolutePath() + "/graph.svg"));
	    BufferedImage bufferedImage = null;
		try {
			bufferedImage = GraphPanel.rasterize(new File(workingDir.getAbsolutePath() + "/graph.svg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    //instancia um jframe (janela)
		JFrame svgCanvas = new JFrame();
		JScrollPane jpane = new JScrollPane();
		
		
		//ajusta dimensoes e o layout
		svgCanvas.setVisible(true);
		svgCanvas.setLayout(new BorderLayout());
        
		if (width >= bufferedImage.getWidth()) {
			svgCanvas.setSize(bufferedImage.getWidth()+horizontalOffset,bufferedImage.getHeight()+verticalOffset);
        } else {
        	svgCanvas.setSize(width,bufferedImage.getHeight()+verticalOffset);
        }
		
        //adiciona o componente ao jframe
        GraphPanel graphPanel = new GraphPanel(bufferedImage);
        graphPanel.setPreferredSize(new Dimension(bufferedImage.getWidth(), bufferedImage.getHeight()));
        
        
        jpane.setViewportView(graphPanel);
        svgCanvas.add(jpane,BorderLayout.CENTER);      
        
		//isso daqui é só pra fechar o frame sem fechar a aplicação
		svgCanvas.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		//forço o método de repintar o frame
		svgCanvas.repaint();
	}
	

}
