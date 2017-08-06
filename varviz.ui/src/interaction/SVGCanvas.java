package interaction;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import info.leadinglight.jdot.Graph;
import info.leadinglight.jdot.impl.Util;

public class SVGCanvas {
	
	
	public SVGCanvas (Graph g) {
		//gera a imagem svg a partir da stream de dados
		Util.toFile(Graph.dot2out(Graph.DEFAULT_CMD,"svg",g.toDot()), new File("graph.svg"));
	    BufferedImage bufferedImage = null;
		try {
			bufferedImage = GraphPanel.rasterize(new File("graph.svg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    //instancia um jframe (janela)
		JFrame svgCanvas = new JFrame();
		
		//ajusta dimensoes e o layout
		svgCanvas.setVisible(true);
		svgCanvas.setLayout(new BorderLayout());
        svgCanvas.setSize(bufferedImage.getWidth()+10,bufferedImage.getHeight()+20);
        
        //adiciona o componente ao jframe
        svgCanvas.add(new GraphPanel(bufferedImage), BorderLayout.CENTER);
        
		//isso daqui é só pra fechar o frame sem fechar a aplicação
		svgCanvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//forço o método de repintar o frame
		svgCanvas.repaint();
	}
	

}
