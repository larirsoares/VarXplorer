package interaction.view;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import interaction.spec.Specification;
import interaction.spec.SpecificationXML;

public class ClickHandler extends JFrame
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
	private List<List> graphDataList = new ArrayList<>();
	List<String> noeffectList = new ArrayList<>();
	List<String> featuresList = new ArrayList<>();
	List<String> edgesList = new ArrayList<>(); //F1, F2, relation, variables
	List<String> forbidEdgesList = new ArrayList<>(); //F1, F2, relation, variables
	List<String> featuresNameList = new ArrayList<>();
	List<Object> featuresObjectList = new ArrayList<>();
	private ArrayList<Specification> specList;
	List<String> notRepeatingEdgeList = new ArrayList<>();
	
	public PopMenu getPop() {	return pop;}
	public List<PopOption> getAllOptionsSelected() {return allOptionsSelected;}

	public ClickHandler(List<List> allListGraph, ArrayList<Specification> specList)
	{
		super("VarXplorer: dynamic analysis of feature interactions");			
		//this.setSize(1200, 800);
		//this.preferredSize();
		this.graphDataList = allListGraph;
		this.specList = specList;
		graph.getModel().beginUpdate();
		
		
		
		applySpec(specList);
		initializeGraph(allListGraph);
		graphListener();
		createButton();
	
	}

	private void applySpec(ArrayList<Specification> specList) {//here just treats forbid, how about allow??
		if(specList != null){
			for(Specification spec: specList){
				if(spec.getType().getName().equals("Forbid")){
					forbidEdgesList.add(spec.getPair().toStringA());//F1, F2, relation, variables
					forbidEdgesList.add(spec.getPair().toStringB());
					forbidEdgesList.add(spec.getRelation().getName());
					forbidEdgesList.add(spec.getVar());
				}
					
			}
		}
		
	}
	private void createButton() {
		JButton button = new JButton("Apply Spec & See New Graph");
		JButton buttonForb = new JButton("See Forbidden interactions");
		JPanel jpanel = new JPanel();
		jpanel.add(button);jpanel.add(buttonForb);
		add(jpanel,  BorderLayout.SOUTH);
		
		button.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) {
				
				System.out.println("Finish butter" + e.getActionCommand() + " was pressed.");
				
				//getting the last pop
				List<PopOption> popsToRemoveList = new ArrayList<>();
				for(int i=0; i<allOptionsSelected.size();i++){
					PopOption oldpop = allOptionsSelected.get(i);
					if(oldpop.getFrom().equals(pop.getOptionsPOP().get(0).getFrom()) && oldpop.getTo().equals(pop.getOptionsPOP().get(0).getTo())){									
						//allOptionsSelected.remove(i);
						popsToRemoveList.add(allOptionsSelected.get(i));
					}
				}
				allOptionsSelected.removeAll(popsToRemoveList);
				allOptionsSelected.addAll(pop.getOptionsPOP());
				
				//creating spec XML file
				SpecificationXML xpec = new SpecificationXML();
				
				//test to know if the XML is empty or not. If not, we need to add the new info in the old one;
				if(!specList.isEmpty()){
					xpec.update(allOptionsSelected);
				}else{
					xpec.create(allOptionsSelected);
				}
				
				//generating new graph
				newGraphGeneration();
				
				//JOptionPane.showMessageDialog(jpanel, "Specification XML file and Graph created successfully", "Specification", JOptionPane.INFORMATION_MESSAGE);
                //dispose();
			}

			private void newGraphGeneration() {
				
				List<String> edgesListnew = treatNewGraphInfo();//removing from the new graph, the interactions marked as allowed
				graphDataList.add(2, edgesListnew);
				ClickHandlerNewGraph framenew = new ClickHandlerNewGraph(graphDataList);
				framenew.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				framenew.setSize(800, 600);
				framenew.setVisible(true);				
			}
			
			//removing from the new graph, the interactions marked as allowed
			private List<String> treatNewGraphInfo() {
				
				List<String> edgesListToRemove = new ArrayList<>(); //F1, F2, relation, variables
				List<String> edgesListNew = new ArrayList<>(); //F1, F2, relation, variables
				List<String> edgesListCopy = new ArrayList<>(); //F1, F2, relation, variables
				edgesListNew.addAll(edgesList);
				edgesListCopy.addAll(edgesList);
				for(PopOption p: allOptionsSelected){
					if(p.getState()){
						String[] info = p.getInfo().split(" ");
						if(info[1].contains("Allow")){//allow
							
							for(int i=0; i<edgesList.size();i++){
								String from = edgesList.get(i);
								String to = edgesList.get(i+1);
								
								if(from.equals(p.getFrom()) && to.equals(p.getTo())){
									String[] vars = edgesListNew.get(i+3).split(" |\n");
									if(vars.length == 2 || edgesListNew.get(i+3).equals("")){//when there is 1 var (length=2) or there's no var
										edgesListNew.set(i, null);									
										edgesListNew.set(i+1, null);	
										edgesListNew.set(i+2, null);	
										edgesListNew.set(i+3, null);		
									}else{
										String newVars = "";	
										int countpar = 0;
										for(int j=0; j<vars.length; j++){

											if(vars[j]!= null){
												if(vars[j].equals(info[3]) && vars[j+1].equals(info[4])){
													vars[j] = null;
													vars[j+1] = null;
												}
											}
											if(vars[j]!= null && newVars!=""){
												 if((countpar  % 2) != 0) {//se é impar é msm var
													 newVars = newVars + " " + vars[j];
												 }else{//se é par, outra var
													 newVars = newVars + "\n" + vars[j];
												 }	
												 countpar++;
											}
											else if(vars[j]!= null && newVars==""){
												newVars = vars[j];
												countpar++;
											}
										}
										edgesListNew.set(i+3, newVars);					
									}
								}
								i = i+3;
							}
						}
					}
				}
				for(int k=0; k<edgesListNew.size(); k++){
					if(edgesListNew.get(k) == null){
						edgesListNew.remove(k);
						k = k-1;
					}
				}
				return edgesListNew;
			}
		});
		
		buttonForb.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) {
				applySpec(specList);
				
				String text = "";
				for(int j=0; j<forbidEdgesList.size();j++){//F1, F2, relation, variables
					text = text + "Forbidded " + forbidEdgesList.get(j+2) + " from " + forbidEdgesList.get(j) + " to " + forbidEdgesList.get(j+1) + " on " + forbidEdgesList.get(j+3) + "\n";
					j = j +3;
				}
				if(text ==""){
					text = "None.";
					JOptionPane.showMessageDialog(jpanel, text, "Information", JOptionPane.ERROR_MESSAGE);
				}else{
					JOptionPane.showMessageDialog(jpanel, text);
				}
			}
		});
	}
	
	private void graphListener() {
		graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
		{
			
			//pop = new PopMenu();
			public void mousePressed(MouseEvent e){
				Object cell = graphComponent.getCellAt(e.getX(), e.getY());
				System.out.println("cell id" + graph.getSelectionCell());   
//				String cellSource = ((mxCell)graph.getSelectionCell()).getSource().getId();				
				
				if (cell != null)
				{
					
					pop.setEnabled(true);
					pop.setVisible(true);					
					
					System.out.println("cell pressed="+graph.getLabel(cell));
					
					if(SwingUtilities.isRightMouseButton(e)){
						System.out.println("direito "  +graph.getLabel(cell));
						
						ArrayList<String> popStrings = new ArrayList<>();
						if(((mxCell) graph.getSelectionCell()).isEdge()){
							Object cellSname = ((mxCell)graph.getSelectionCell()).getSource().getValue();
							Object cellTname = ((mxCell)graph.getSelectionCell()).getTarget().getValue();
							String popTitle = "Feature Interaction: " + cellSname + " - " + cellTname;
							popStrings.add(popTitle);
							createPopText((mxCell)graph.getSelectionCell(),popStrings, cellSname, cellTname);
									
							//replacing info from the same 'from' to 'to
							List<PopOption> popsToRemoveList = new ArrayList<>();
							for(int i=0; i<allOptionsSelected.size();i++){
								PopOption oldpop = allOptionsSelected.get(i);
								if(oldpop.getFrom().equals(pop.getOptionsPOP().get(0).getFrom()) && oldpop.getTo().equals(pop.getOptionsPOP().get(0).getTo())){									
									//allOptionsSelected.remove(i);
									popsToRemoveList.add(allOptionsSelected.get(i));
								}
							}
							allOptionsSelected.removeAll(popsToRemoveList);
							allOptionsSelected.addAll(pop.getOptionsPOP());
							
							pop.removeAll();
							callwindow(e,pop,popStrings, (String)cellSname, (String)cellTname);						
						}
						else{
							System.out.println("You clicked on a node");
						}						
					}
				}else{
					pop.removeAll();
					pop.setEnabled(false);
					pop.setVisible(false);
				}
			}

			private void createPopText(mxCell selectionCell, ArrayList<String> popStrings, Object cellSname, Object cellTname) {
				String vars = (String) selectionCell.getValue();
				String source = (String) cellSname;
				String target = (String) cellTname;
				
				//notRepeatingEdgeList.add(edgesList.get(i+2)+fromString+toString);
				//notRepeatingEdgeList.add(edgesList.get(i+3));
				String[] t = vars.split ("\n");
				String varsNotShowed = "";
				String relation = t[0];
				for(int i=0; i<notRepeatingEdgeList.size(); i++){
					String compare = relation+"\n"+source+target;
					if(notRepeatingEdgeList.get(i).equals(compare)){
						varsNotShowed= notRepeatingEdgeList.get(i+1);
					}
					i = i+1;
				}
				
				t = varsNotShowed.split ("\n");
				if(t.length>0){
					for(int i=0; i<t.length;i++){
						popStrings.add((i+1) +". "+"Allow '" + relation + "' on " +  t[i]);
						popStrings.add("Forbid '" + relation + "' on " +  t[i]);
					}				
				}
//				if(t.length>1){
//					for(int i=1; i<t.length;i++){
//						popStrings.add("Allow '" + t[0] + "' on " +  t[i]);
//						popStrings.add("Forbid '" + t[0] + "' on " +  t[i]);
//					}				
//				}			
			}

			private void callwindow(MouseEvent e, PopMenu pop, ArrayList<String> popStrings, String from, String to) {
            	
            	ArrayList<String> featureVarsList = new ArrayList<>();    
            	featureVarsList.add("-->" + popStrings.get(0));
            	featureVarsList.add("separator");
            	if(popStrings.size()>1){
	            	for(int i=1; i<popStrings.size();i++){
	            		featureVarsList.add(popStrings.get(i));
	            		featureVarsList.add(popStrings.get(i+1));
	            		featureVarsList.add("separator");
	            		i = i+1;
	            	}
				}
               
            	//checking past options selected to paint as previously selected
            	String option = "";
            	ArrayList<String> optionsTrue = new ArrayList<>();
            	for(int i=0; i<allOptionsSelected.size(); i++){
            		PopOption op = allOptionsSelected.get(i);
            		if(op.getFrom().equals(from) && op.getTo().equals(to)){
            			if(op.getState()){
            				optionsTrue.add(op.getInfo());
            			}
            		}
            	}
            	
            	pop.setinfoList(featureVarsList, from, to, optionsTrue);
            	//PopMenu frame = new PopMenu(featureVarsList);                           
              
            	int y = e.getY();
            	System.out.println("----> y : " + y);
            	if(e.getY()<400) {
            		y = 0;
            	}else if(e.getY()>1000){
            		y = 600;
            	}else {
            	
            		y = y-400;
            	}
            	pop.setLocation(e.getX(), y);
//            	pop.setLocation(e.getX(), e.getY());
            	//pop.setLocation(100, 100);
                pop.setVisible(true);
                popList.add(pop);
                
            }			
		});		
	}
	
	private void initializeGraph(List<List> allListGraph){
		
		mxStylesheet stylesheet = graph.getStylesheet();
//	    Hashtable<String, Object> style = new Hashtable<>();
//	    stylesheet.putCellStyle("ROUNDED", style);

	    Map<String, Object> vertexStyle = stylesheet.getDefaultVertexStyle();
	    vertexStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
	    vertexStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
	    vertexStyle.put(mxConstants.STYLE_AUTOSIZE, 1);
	    vertexStyle.put(mxConstants.STYLE_SPACING, "30");
	    vertexStyle.put(mxConstants.STYLE_ORTHOGONAL, "true");
	    vertexStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);

	    Map<String, Object> edgeStyle = stylesheet.getDefaultEdgeStyle();
	    edgeStyle.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ORTHOGONAL);
	    edgeStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CURVE);
	    edgeStyle.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
		
//	    mxStylesheet stylesheet = new mxStylesheet();
	    stylesheet.setDefaultVertexStyle(vertexStyle);
	    stylesheet.setDefaultEdgeStyle(edgeStyle);
	    graph.setStylesheet(stylesheet);
		
		noeffectList = allListGraph.get(0);
		featuresList = allListGraph.get(1);
		edgesList = allListGraph.get(2); //F1, F2, relation, variables
		featuresNameList = new ArrayList<>();
		featuresObjectList = new ArrayList<>();
		notRepeatingEdgeList = new ArrayList<>();
		
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
				String fromString = "";
				String toString = "";
				for(int j=0; j<featuresNameList.size(); j++){
					if(featuresNameList.get(j).equals(edgesList.get(i))){
						from = nodesvList.get(j);
						fromString = edgesList.get(i);
					}else if(featuresNameList.get(j).equals(edgesList.get(i+1))){
						to  = nodesvList.get(j);
						toString = edgesList.get(i+1);
					}
				}
				Object v = null;
				
				
				//number of vars:
				String[] t = edgesList.get(i+3).split ("\n");
				int numberVars = t.length;
				String numberOfVarsToShow = "";//it's empty when the relation is total instead of partial
				String arrowDesignRequire = "strokeColor=green;verticalLabelPosition=middle";//to create dashed and full lines
				String arrowDesignSuppress = "strokeColor=red;verticalAlign=top;verticalLabelPosition=bottom;labelPosition=left";//to create dashed and full lines
				if(!edgesList.get(i+3).equals("")) {
					numberOfVarsToShow = " ("+numberVars+")";
					arrowDesignRequire = "strokeColor=green;dashed=true;verticalLabelPosition=middle";
					arrowDesignSuppress = "strokeColor=red;dashed=true;verticalAlign=top;verticalLabelPosition=bottom;labelPosition=left";
				}
				
				
				// checking if there are 2 or more edges  that are equal
				String doubleEdge = edgesList.get(i+2)+fromString+toString;//ex.: "requires\n45"
				if(checkDoubleEdge(doubleEdge, notRepeatingEdgeList)){
					//updateLabel(edgesvList, edgesList.get(i+2), edgesList.get(i+3), fromString, toString);
				
				}else{
					if(edgesList.get(i+2).equals("requires\n")){
						if(isforbidden(fromString, toString, "Require")){						
							v = graph.insertEdge(parent, null, edgesList.get(i+2) + numberOfVarsToShow, from, to, "strokeColor=purple;strokeWidth=10");													
						}else{
							v = graph.insertEdge(parent, null, edgesList.get(i+2)+ numberOfVarsToShow, from, to, arrowDesignRequire);				
						}
						notRepeatingEdgeList.add(edgesList.get(i+2)+fromString+toString);
						notRepeatingEdgeList.add(edgesList.get(i+3));
						edgesvList.add(v);//edgesvList.add(edgeID++);
					}
					else if(edgesList.get(i+2).equals("suppresses\n")){
						if(isforbidden(fromString, toString, "Suppress")){
							v = graph.insertEdge(parent, null, edgesList.get(i+2)+ numberOfVarsToShow, from, to, "strokeColor=purple;strokeWidth=10;verticalAlign=top;verticalLabelPosition=bottom");
						}else{
							v = graph.insertEdge(parent, null, edgesList.get(i+2)+ numberOfVarsToShow, from, to, arrowDesignSuppress);//"strokeColor=red;dashed=true;ROUNDED"
						}
						notRepeatingEdgeList.add(edgesList.get(i+2)+fromString+toString);
						notRepeatingEdgeList.add(edgesList.get(i+3));
						edgesvList.add(v);//edgesvList.add(edgeID++);
					}
					else{
						v= graph.insertEdge(parent, null, edgesList.get(i+3), from, to, "strokeColor=black");
						notRepeatingEdgeList.add(fromString+toString);
						notRepeatingEdgeList.add(edgesList.get(i+3));
						edgesvList.add(v);//edgesvList.add(edgeID++);
					}
					
				}	
					
					
				 i = i + 3;
			}

		   
		   graph.setCellsEditable(true);//aqui
		   //edge cannot be moved without connecting to a node
		   graph.setAllowDanglingEdges(false);//aqui
		   graph.setAllowLoops(true);
		   graph.setAutoSizeCells(true);
		   graph.setPortsEnabled(true);
		   //nao mudou nada =/
		   graph.setEnabled(true);//aqui
		   	   
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
		
	
		new mxCircleLayout(graph).execute(graph.getDefaultParent());				
		
		new mxParallelEdgeLayout(graph).execute(graph.getDefaultParent());
		graph.getModel().endUpdate();

		mxIGraphLayout layout = new mxFastOrganicLayout(graph);
		//mxHierarchicalLayout layout2 = new mxHierarchicalLayout(graph);
		
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
	
	private void updateLabel(ArrayList<Object> edgesvList, String relation, String var, String fromString, String toString) {
		
		//v = graph.insertEdge(parent, null, edgesList.get(i+2) + edgesList.get(i+3), from, to, "strokeColor=red;dashed=true;ROUNDED");
		for(int i=0; i<edgesvList.size(); i++){
			Object cell = edgesvList.get(i);
			System.out.println(cell);
			String value = (String) ((mxCell) cell).getValue();					
			
			if(((mxCell) cell).getSource().getValue().equals(fromString) && 
					((mxCell) cell).getTarget().getValue().equals(toString) &&
					((mxCell) cell).getValue().toString().contains(relation)){
				
				((mxCell) cell).setValue(value + ", " +var);
				edgesvList.set(i, cell);	
			}
		
		}
		
	}
	private boolean checkDoubleEdge(String NewEdge, List<String> notRepeatingEdgeList) {
		
		for(String oldEdge: notRepeatingEdgeList){
			if(NewEdge.equals(oldEdge)){
				return true;
			}
		}
		return false;
	}
	private boolean isforbidden(String from, String to, String relation) {
		
		for(int j=0; j<forbidEdgesList.size();j++){//F1, F2, relation, variables
			if(forbidEdgesList.get(j).equals(from) && forbidEdgesList.get(j+1).equals(to)
					&& forbidEdgesList.get(j+2).equals(relation)){
				
				return true;
			}
			j = j +3;
		}
		
		return false;
	}
	
	public static void main(String[] args)
	{
	}

}
