package interaction.view;


import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;

public class ClickHandler extends JFrame
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2764911804288120883L;
	private String systemInfo = "";
	private JLabel syslabel = null;
	
	

	public ClickHandler(ArrayList<String> info)
	{
		super("VarXplorer: dynamic analysis of feature interactions");
		
		final mxGraph graph = new mxGraph();
		Object parent = graph.getDefaultParent();	
		
		systemInfo = "Elevator System \n Number of nodes: 5 \n Number of suppresions: 3";
		
		
		graph.getModel().beginUpdate();
		try
		{
			
			ArrayList<Object> nodesvList = new ArrayList<>();
			int numberNodes = 5;
			for(int i=0; i<numberNodes;i++){
		    	
		    	Object v = graph.insertVertex(parent, null, "v" + i, 20, 20, 50,
				         50, "shape=ellipse;perimeter=ellipsePerimeter;fillColor=white");		    	
		    	nodesvList.add(v);
		    }
			this.setSize(800, 600);
			int a = this.getWidth();
			//noEffect features
			Object v = graph.insertVertex(parent, null, "v5" , this.getWidth(), 20, 50,
			         50, "shape=ellipse;perimeter=ellipsePerimeter;strokeColor=grey;fillColor=white;dashed=true");		    	
	    	nodesvList.add(v);

//		   Object v1 = graph.insertVertex(parent, null, "v1", 20, 20, 80,
//		         30, "shape=ellipse;perimeter=ellipsePerimeter");

//		   graph.insertEdge(parent, null, "Edge", v1, v2);
		   graph.insertEdge(parent, null, "E1", nodesvList.get(0), nodesvList.get(1), "strokeColor=red;dashed=true");
		   graph.insertEdge(parent, null, "E2", nodesvList.get(0), nodesvList.get(2));
		   graph.insertEdge(parent, null, "E3", nodesvList.get(1), nodesvList.get(3), "strokeColor=green;dashed=true");
		   graph.insertEdge(parent, null, "E4", nodesvList.get(2), nodesvList.get(0));
		   graph.insertEdge(parent, null, "E5", nodesvList.get(2), nodesvList.get(1));
		   graph.insertEdge(parent, null, "E6", nodesvList.get(3), nodesvList.get(0));
		   
		   
		   graph.setCellsEditable(false);
		   //edge cannot be moved without connecting to a node
		   graph.setAllowDanglingEdges(false);
		   graph.setAllowLoops(false);
		   graph.setAutoSizeCells(true);
		  // graph.setConnectableEdges(false);
		   graph.setPortsEnabled(false);
		   //nao mudou nada =/
		   graph.setEnabled(false);
		   	   
		}
		finally
		{
		   graph.getModel().endUpdate();
		}
		
		
		//mxConstants.STYLE_EDGE = mxEdgeStyle.SegmentConnector 		
		
		final mxGraphComponent graphComponent = new mxGraphComponent(graph);
		getContentPane().add(graphComponent,BorderLayout.CENTER);
		syslabel = new JLabel(systemInfo);
		syslabel.setLocation(20, graphComponent.getHeight()-20);
		syslabel.setVisible(true);
		//graphComponent.add(syslabel);
		
		//to not create new edges
		graphComponent.setConnectable(false);
		
		new mxParallelEdgeLayout(graph).execute(graph.getDefaultParent());

		mxIGraphLayout layout = new mxFastOrganicLayout(graph);
		// layout graph
	    layout.execute(graph.getDefaultParent());
	    // set some properties
	    ((mxFastOrganicLayout) layout).setForceConstant(300); // the higher, the more separated
	    ((mxFastOrganicLayout) layout).setDisableEdgeStyle( true); // true transforms the edges and makes them direct lines
	    ((mxFastOrganicLayout) layout).setMinDistanceLimit(40);
	
	   
	    
	 // layout using morphing
	    graph.getModel().beginUpdate();
	    try {
	        layout.execute(graph.getDefaultParent());
	    } finally {
	        mxMorphing morph = new mxMorphing(graphComponent, 20, 1.2, 20);

	        morph.addListener(mxEvent.DONE, new mxIEventListener() {

	            @Override
	            public void invoke(Object arg0, mxEventObject arg1) {
	                graph.getModel().endUpdate();
	                // fitViewport();
	            }

	        });

	        morph.startAnimation();
	    }
		
		
		graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
		{
			
			PopMenu pop = new PopMenu();
			public void mousePressed(MouseEvent e){
				Object cell = graphComponent.getCellAt(e.getX(), e.getY());
				   
				
				if (cell != null)
				{
					
					pop.setEnabled(true);
					pop.setVisible(true);
					pop.removeAll();
					
					System.out.println("cell pressed="+graph.getLabel(cell));
					
					if(SwingUtilities.isRightMouseButton(e)){
						System.out.println("direito");
						callwindow(e,pop);
						
					}
				}else{
					pop.removeAll();
					pop.setEnabled(false);
					pop.setVisible(false);
				}
			}

            private void callwindow(MouseEvent e, PopMenu pop) {
            	// TODO Auto-generated method stub
            	ArrayList<String> featureVarsList = new ArrayList<>();
            	featureVarsList.add("Feature Interaction: F-W");
            	featureVarsList.add("Allow require on String c");
            	featureVarsList.add("Forbid require on String c");
            	featureVarsList.add("separator");
            	featureVarsList.add("Allow require on String weather");
            	featureVarsList.add("Forbid require on String weather");
            	           	
//            	JFrame frame = new JFrame("Specification");
//                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//                frame.setContentPane(new EdgeMenu(featureVarsList));
//                frame.setSize(new Dimension(200, 200));
//                frame.setLocation(e.getX(), e.getY());
//                frame.setVisible(true);
               
            	pop.setinfoList(featureVarsList);
            	//PopMenu frame = new PopMenu(featureVarsList);                           
                pop.setLocation(e.getX(), e.getY());
                pop.setVisible(true);
            }
			
		});
		
	}

	public static void main(String[] args)
	{
		ArrayList<String> info = new ArrayList<>();
		ClickHandler frame = new ClickHandler(info);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setVisible(true);
	}

}
