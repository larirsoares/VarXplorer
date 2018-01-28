package interaction.view;


import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;

public class ClickHandlerNewGraph extends JFrame
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2764911804288120883L;
	private String systemInfo = "";
	private JLabel syslabel = null;
	private List<PopOption> allOptionsSelected = new ArrayList<>();
	private PopMenu pop = new PopMenu();
	final mxGraph graph = new mxGraph();
	final mxGraphComponent graphComponent = new mxGraphComponent(graph);
	Object parent = graph.getDefaultParent();	
	List<PopMenu> popList = new ArrayList<>();
	
	public PopMenu getPop() {	return pop;}
	public List<PopOption> getAllOptionsSelected() {return allOptionsSelected;}

	public ClickHandlerNewGraph(List<List> allListGraph)
	{
		super("Feature-interaction graph with specifications applied");			
		this.setSize(800, 600);
		
		graph.getModel().beginUpdate();
		
		initializeGraph(allListGraph);
		
	
	}


	

	
	private void initializeGraph(List<List> allListGraph){
		List<String> noeffectList = allListGraph.get(0);
		List<String> featuresList = allListGraph.get(1);
		List<String> edgesList = allListGraph.get(2); //F1, F2, relation, variables
		List<String> featuresNameList = new ArrayList<>();
		List<Object> featuresObjectList = new ArrayList<>();
		int totalFeatures = 0;
		
		try
		{			
			ArrayList<Object> nodesvList = new ArrayList<>();
			ArrayList<Object> edgesvList = new ArrayList<>();
			
			//------noEffect features
			int vheigh = 20;
			for(int i=0; i<noeffectList.size();i++){
			Object ver = graph.insertVertex(parent, null, noeffectList.get(i) , this.getWidth()-80, vheigh, 50,
			         50, "shape=ellipse;perimeter=ellipsePerimeter;strokeColor=grey;fillColor=white;dashed=true");		    	
	    	nodesvList.add(ver);
	    	vheigh+=30;
	    	featuresNameList.add(noeffectList.get(i));
	    	featuresObjectList.add(ver);
			}
			
			//------Effect features
			int numberNodes = featuresList.size();
			for(int i=0; i<numberNodes;i++){
		    	
		    	Object v = graph.insertVertex(parent, null, featuresList.get(i), 20, 20, 50,
				         50, "shape=ellipse;perimeter=ellipsePerimeter;fillColor=white");		    	
		    	nodesvList.add(v);
		    	featuresNameList.add(featuresList.get(i));
		    	featuresObjectList.add(v);
		    }
			totalFeatures = noeffectList.size() + featuresList.size();
			
			//------ edges
			int edgeID = totalFeatures+1;
			for(int i=0; i<edgesList.size();i++){
				Object from = null;
				Object to = null;
				for(int j=0; j<featuresNameList.size(); j++){
					if(featuresNameList.get(j).equals(edgesList.get(i))){
						from = nodesvList.get(j);
					}else if(featuresNameList.get(j).equals(edgesList.get(i+1))){
						to  = nodesvList.get(j);
					}
				}
				Object v = null;
				if(edgesList.get(i+2).equals("requires\n")){
					v = graph.insertEdge(parent, null, edgesList.get(i+2) + edgesList.get(i+3), from, to, "strokeColor=green;dashed=true");
					edgesvList.add(v);edgesvList.add(edgeID++);
				}
				else if(edgesList.get(i+2).equals("suppresses\n")){
					v = graph.insertEdge(parent, null, edgesList.get(i+2) + edgesList.get(i+3), from, to, "strokeColor=red;dashed=true");
					edgesvList.add(v);edgesvList.add(edgeID++);
				}
				else{
					v= graph.insertEdge(parent, null, edgesList.get(i+3), from, to);
					edgesvList.add(v);edgesvList.add(edgeID++);
				}			 
				 i = i + 3;
			}

		   
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
				
		getContentPane().add(graphComponent,BorderLayout.CENTER);
		syslabel = new JLabel(systemInfo);
		syslabel.setLocation(20, graphComponent.getHeight()-20);
		syslabel.setVisible(true);
		
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
	            }
	        });
	        morph.startAnimation();
	    }
	}
	
	public static void main(String[] args)
	{

	}

}
