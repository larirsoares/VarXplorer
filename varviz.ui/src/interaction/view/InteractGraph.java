package interaction.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cmu.conditional.Conditional;
import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.SingleFeatureExpr;
import info.leadinglight.jdot.Edge;
import info.leadinglight.jdot.Graph;
import info.leadinglight.jdot.Node;
import info.leadinglight.jdot.enums.ArrowType;
import info.leadinglight.jdot.enums.Color;
import info.leadinglight.jdot.enums.Style;
import interaction.PairExp;
import interaction.dataflow.DataVar;
import interaction.spec.Specification;
import interaction.types.ControInteraction;
import interaction.types.DataInteraction;
import interaction.types.VarInteraction;
/**
 * has...
 * 
 * @author Larissa Rocha
 *  
 */


public class InteractGraph {
	ArrayList<Specification> SpecList;
	private List<DataInteraction> dataInteracList;
	private List<VarInteraction> interactionsPerVarList;
	private List<ControInteraction> controlFlowInteracList;
	private boolean blackedges;
	private boolean justControlFlow;
	

	public InteractGraph(List<DataInteraction> dataInteracList, List<VarInteraction> interactionsPerVarList,
			List<ControInteraction> controlFlowInteracList) {
		this.controlFlowInteracList = controlFlowInteracList;
		this.interactionsPerVarList = interactionsPerVarList;
		//this.interactionsPerVarList = null;
		this.dataInteracList = dataInteracList;
		//this.dataInteracList = null;
		
		//to generate the basic interaction graph only with black edge (control and data edges)
		this.blackedges = false;
		//data in black but control flow activated
		this.justControlFlow = false;
	}

	public void createGraphInter(Map<PairExp, List<String>> hashMap, Collection<SingleFeatureExpr> features, List<SingleFeatureExpr> noEffectlist, List<FeatureExpr> expressions, File workingDir, List<List> allVars, ArrayList<Specification> specList){
		
		//allVars = null;
		
		Graph g = new Graph("FeatureInteractions");		
		String A = "";
		String B = "";
		List<String> drawninteractions = new ArrayList<>();
		SpecList = new ArrayList<Specification>(specList);
		
		for (SingleFeatureExpr feature1 : features) {
			 String f = Conditional.getCTXString(feature1);
			 if(noEffectlist.contains(feature1)){
			 	g.addNode(new Node(f).setStyle(Style.Node.dashed).setColor(Color.X11.grey).setFontColor(Color.X11.gray)); 
			 }else if(!Conditional.isTautology(feature1)){
				g.addNode(new Node(f));
			 }
		 }
		
		String expV = "";
		for (Entry<PairExp, List<String>> pair : hashMap.entrySet()) {					
			
				A = Conditional.getCTXString(pair.getKey().getA());
				B = Conditional.getCTXString(pair.getKey().getB());
			//	System.out.println("Pair = [" + A + "," + B + "= " + pair.getValue() + "]");
				String concat = B;
				
				List<String> l = pair.getValue();
				for(String exp: l){
					//System.out.println("expr: " + exp);
				
					if(exp.contains("not interact")){
						continue;
					}
					else if(exp.contains("interact")){
						if(checkSpec(specList, A, B)){
							continue;
						}					
						drawNormalInteraction(allVars, expV, A, B, exp, concat, g, drawninteractions);
					}
					else if(exp.contains("enables")){
						if(checkSpec(specList, A, B)){
							continue;
						}	
						
						if(this.blackedges && this.justControlFlow){
							drawEnable(allVars, expV, A, B, exp, concat, g, drawninteractions);
							continue;
						}else if (this.blackedges){
							drawNormalInteraction(allVars, expV, A, B, exp, concat, g, drawninteractions);
						}else{
							drawEnable(allVars, expV, A, B, exp, concat, g, drawninteractions);
						}
					
					}
					else if(exp.contains("suppresses")){
						if(checkSpec(specList, A, B)){
							continue;
						}	
						
						if(this.blackedges){
							drawNormalInteraction(allVars, expV, A, B, exp, concat, g, drawninteractions);
						}else{
							drawSuppress(allVars, expV, A, B, exp, concat, g, drawninteractions);
						}
					}
				}
			
			
			
			//}
		}
		
		List<FeatureExpr> dataExpressions = new ArrayList<>();
		//data interaction
		drawDataInteractions(features, g, drawninteractions, dataExpressions);
		
		
		//here print all the expressions in the graph
		String concat = printExpressions(dataExpressions, expressions);
		g.setLabel(concat);
		new SVGCanvas(g,workingDir);
	}

	private void drawNormalInteraction(List<List> allVars, String expV, String A, String B, String exp, String concat, Graph g, List<String> drawninteractions) {
		String shownVars = "";
		if(!this.blackedges){
			shownVars = getVarstoShow(allVars, expV, A, B);
		}
			
		System.out.println("Overwritten vars from [" + A + "," + B + "] =" + shownVars);
		
		g.addEdge(new Edge(A,B).setArrowHead(ArrowType.none).setLabel(shownVars).setFontSize(10).setFontColor(Color.X11.black));							
		drawninteractions.add(A+","+B);
		
	}

	private void drawSuppress(List<List> allVars, String expV, String A, String B, String exp, String concat, Graph g,
			List<String> drawninteractions) {
		
		String shownVars = getVarstoShow(allVars, expV, A, B);
		if(shownVars!=""){
			shownVars = "suppresses \n" + shownVars;
		}else{
			shownVars = "suppresses";
		}
		
		System.out.println("Overwritten vars from [" + A + "," + B + "] =" + shownVars);
		if(exp.startsWith(concat)){
			Edge edge = new Edge(B,A);
			edge.setColor(Color.X11.red).setArrowHead(ArrowType.empty);
			edge.setFontSize(10).setLabel(shownVars);
			edge.setLabelTooltip(shownVars);
			g.addEdge(edge);
			drawninteractions.add(A+","+B);
			//g.addEdge(new Edge(B,A).setColor(Color.X11.red).setArrowHead(ArrowType.empty).setFontSize(10).setLabel("suppresses"));
		}else{
			Edge edge = new Edge(A,B);
			edge.setColor(Color.X11.red).setArrowHead(ArrowType.empty);
			edge.setFontSize(10).setLabel(shownVars);
			edge.setLabelTooltip(shownVars);
			g.addEdge(edge);
			drawninteractions.add(A+","+B);
			//g.addEdge(new Edge(A,B).setColor(Color.X11.red).setArrowHead(ArrowType.empty).setFontSize(10).setLabel("suppresses"));
		}
				
//		String showVars = "";
//		for(List<String> vars: allVars){
//			expV = vars.get(0);						
//			if((expV.contains(A) && expV.contains(B)) && shownVars==""){
//				
//				for(int i=1; i<vars.size();i++){//String v: vars){
//					String a = vars.get(i).substring(0,vars.get(i).length()-3);
//					a = "var"+i + ": " + a;
//					shownVars += a;
//					if(i<vars.size()-1)
//						shownVars += "\n";
//				}
//				System.out.println("Overwritten vars from [" + A + "," + B + "] =" + shownVars);
//				g.addEdge(new Edge(A,B).setArrowHead(ArrowType.none).setLabel(shownVars).setFontSize(10).setFontColor(Color.X11.black));							
//				drawninteractions.add(A+","+B);
//				continue;
//			}
//		}
//		if(shownVars==""){
//			g.addEdge(
//					new Edge(A,B).setArrowHead(ArrowType.none));
//			drawninteractions.add(A+","+B);
//		}
		
	}

	private void drawEnable(List<List> allVars, String expV, String A, String B, String exp, String concat, Graph g, List<String> drawninteractions) {
		
		String shownVars = "";
		if(allVars!=null && !this.justControlFlow){
			shownVars = getVarstoShow(allVars, expV, A, B);
		}

		
		System.out.println("Overwritten vars from [" + A + "," + B + "] =" + shownVars);
		
		if(shownVars!=""){
			shownVars = "enables \n" + shownVars;
		}else{
			shownVars = "enables";
		}
			
			if(exp.startsWith(concat)){
				Edge edge = new Edge(B,A);
				edge.setColor(Color.X11.green).setArrowHead(ArrowType.empty);
				//edge.setLabel("enables \n" + shownVars).setFontSize(10);
				edge.setLabel(shownVars).setFontSize(10);
				g.addEdge(edge);
				drawninteractions.add(A+","+B);
				//g.addEdge(new Edge(B,A).setColor(Color.X11.green).setArrowHead(ArrowType.empty).setLabel("enables").setFontSize(10));
			}else{
				Edge edge = new Edge(A,B);
				edge.setColor(Color.X11.green).setArrowHead(ArrowType.empty);
				edge.setLabel("enables \n" + shownVars).setFontSize(10);
				g.addEdge(edge);
				drawninteractions.add(A+","+B);
				//g.addEdge(new Edge(A,B).setColor(Color.X11.green).setArrowHead(ArrowType.empty).setLabel("enables").setFontSize(10));
			}
		
	}

	//getting vars to show from data and control flows and putting them together in var shownVars
	private String getVarstoShow(List<List> allVars, String expV, String A, String B) {
		String shownVars = "";
		if(allVars.isEmpty()){
			return shownVars;
		}
		
		//getting var from control flow
		for(List<String> vars: allVars){
			expV = vars.get(0);
			if(shownVars!=""){
				shownVars += "\n";
			}
			if(expV.contains(A) && expV.contains(B)){
				
				for(int i=1; i<vars.size();i++){//String v: vars){
					String a = vars.get(i).substring(0,vars.get(i).length()-3);
					//a = "var"+i + ": " + a;
					if(!shownVars.contains(a)){
						shownVars += a;
					}
					if(i<vars.size()-1)
						shownVars += "\n";
				}
			}
		}
			
		//getting var from Data interactions
		for(DataInteraction dataI: this.dataInteracList){
			List<FeatureExpr> fList = dataI.getFeatures();
			String aux = "";
			for(FeatureExpr feat: fList){
				aux= aux + " " + Conditional.getCTXString(feat);
			}
			if(aux.contains(A) && aux.contains(B)){
				for(DataVar eachvar: dataI.getDataVars()){
					String a = eachvar.getName().substring(0,eachvar.getName().length()-3);
					if(!shownVars.contains(a)){					
						if(shownVars!=""){
							shownVars += "\n";
						}
						shownVars+= a;
					}
				}
			}
		}
		//comment here to show the vars
		//shownVars = "";
		return shownVars;
	}

	private String printExpressions(List<FeatureExpr> dexpressions, List<FeatureExpr> expressions) {
		List<FeatureExpr> list = new ArrayList<>();
		list.addAll(updateDataExp(dexpressions, expressions));
		
//		String concat = "";
//		for(FeatureExpr featureexpr : expressions){
//			concat+= Conditional.getCTXString(featureexpr) + "\n";
//		}
//		g.setLabel(concat);
		String concat = "";
		for(FeatureExpr featureexpr : list){
			concat+= Conditional.getCTXString(featureexpr) + "\n";
		}
		return concat;
		
	}

	private void drawDataInteractions(Collection<SingleFeatureExpr> features, Graph g, List<String> drawninteractions, List<FeatureExpr> dataExpressions) {
		
		String A = "";
		String B = "";
		String C = "";
		
		if(this.dataInteracList==null){
			return;
		}
		
		for(DataInteraction Interaction: this.dataInteracList){
					
			//List<FeatureExpr> dFeatures = Interaction.getFeatures();
			List<FeatureExpr> fList = Interaction.getFeatures();
			if(fList.size()==2){
				A = Conditional.getCTXString(fList.get(0));
				B = Conditional.getCTXString(fList.get(1));
				
				//checking specification
				if(checkSpec(SpecList, A, B)){
					continue;
				}
				
				String shownVars = "";
				if(!this.blackedges){
					shownVars = dataGetVarstoShow(Interaction, dataExpressions);
				}
				System.out.println("Data Interaction: [" + A + "," + B + "] = " + shownVars);
				
				
				if(blackedges){
					g.addEdge(new Edge(A,B).setArrowHead(ArrowType.none).setLabel(shownVars).setFontSize(10).setFontColor(Color.X11.black));
				}else{
					dataDrawinpair(A, B, shownVars, g);
				}
				
			}
			else if(fList.size()==3){
				System.out.println("WARNING: interaction order higher than 2");
				A = Conditional.getCTXString(fList.get(0));
				B = Conditional.getCTXString(fList.get(1));
				C = Conditional.getCTXString(fList.get(2));
				
				String shownVars = "";
				if(!this.blackedges){
					shownVars = dataGetVarstoShow(Interaction, dataExpressions);
				}
				System.out.println("Data Interaction: [" + A + "," + B + "," + C + "] = " + shownVars);
				
				
				//verify if controlflow exists
				if(!hasInControlflow(A, B,drawninteractions)){
					if(checkSpec(SpecList, A, B)){
						continue;
					}
					if(blackedges){
						g.addEdge(new Edge(A,B).setArrowHead(ArrowType.none).setLabel(shownVars).setFontSize(10).setFontColor(Color.X11.black));
					}else{
						dataDrawinpair(A, B, shownVars, g);
					}
					
					
				} else if(!hasInControlflow(A, C,drawninteractions)){
					if(checkSpec(SpecList, A, C)){
						continue;
					}
					
					if(blackedges){
						g.addEdge(new Edge(A,C).setArrowHead(ArrowType.none).setLabel(shownVars).setFontSize(10).setFontColor(Color.X11.black));
					}else{
						dataDrawinpair(A, C, shownVars, g);
					}
						
				} else if(!hasInControlflow(B, C, drawninteractions)){
					if(checkSpec(SpecList, B, C)){
						continue;
					}
					
					if(blackedges){
						g.addEdge(new Edge(B,C).setArrowHead(ArrowType.none).setLabel(shownVars).setFontSize(10).setFontColor(Color.X11.black));
					}else{
						dataDrawinpair(B, C, shownVars, g);
					}					
					
				}
					
				
			}
			else if(fList.size()>=3){
				System.out.println("WARNING: interaction order higher than 3");
			}
				
		}
		
	}


	private String dataGetVarstoShow(DataInteraction Interaction, List<FeatureExpr> dataExpressions) {
		int i = 1;
		String shownVars = "";
		for(DataVar var: Interaction.getDataVars()){
			updateDataExp(dataExpressions, var.getCtxList());
			
			//get the var to show in the graph
			String a = var.getName().substring(0,var.getName().length()-3);
			shownVars += a;
			if(i<Interaction.getDataVars().size())
				shownVars += "\n";
			i++;
		}
		//comment here to show the vars
		//shownVars = "";
		return shownVars;	
		
	}

	private void dataDrawinpair(String A, String B, String shownVars, Graph g) {
		String type = hasVarInteraction(A, B);
		if(type!=""){
			Edge edge;
			if(type.startsWith(A)){
				edge = new Edge(A,B);
			}else{
				edge = new Edge(B, A);
			}
			if(type.contains("suppress")){
				shownVars = "suppresses \n" + shownVars;									
				edge.setColor(Color.X11.red).setStyle(Style.Edge.dashed).setArrowHead(ArrowType.empty);
			}else if (type.contains("enable")){
				shownVars = "enables \n" + shownVars;									
				edge.setColor(Color.X11.green).setStyle(Style.Edge.dashed).setArrowHead(ArrowType.empty);
			}
			edge.setFontSize(10).setLabel(shownVars).setFontColor(Color.X11.black);
			g.addEdge(edge);
					
		}else{	
			Edge edge = new Edge(A,B);
			edge.setColor(Color.X11.orange).setStyle(Style.Edge.dashed).setArrowHead(ArrowType.none);
			edge.setFontSize(10).setLabel(shownVars).setFontColor(Color.X11.black);
			g.addEdge(edge);
		}
		
	}

	private String hasVarInteraction(String a, String b) {
		if(this.interactionsPerVarList==null){
			return "";
		}
		
		for(VarInteraction varList: this.interactionsPerVarList){
			if(varList.getExp().contains(a) && varList.getExp().contains(b)){
				
				if(varList.getExp().contains("suppress")){
					if(varList.getExp().startsWith(a)){
						return a+" "+"suppress";
					}
					else{
						return b+" "+"suppress";
					}
				}
				else if(varList.getExp().contains("enable")){
					if(varList.getExp().startsWith(a)){
						return a+" "+"enable";
					}
					else{
						return b+" "+"enable";
					}
				}
			}
		}
		return "";
	}
	

	private List<FeatureExpr>  updateDataExp(List<FeatureExpr> dataExpressions, List<FeatureExpr> list) {
		// TODO Auto-generated method stub
		if(dataExpressions.isEmpty())
			dataExpressions.addAll(list);
		else{
			
			List<FeatureExpr> sourceList = new ArrayList<FeatureExpr>(list);
			 
			for(FeatureExpr f1: list){
				for(FeatureExpr f2: dataExpressions){
					if(f1.equivalentTo(f2)){
						sourceList.remove(f1);
						continue;
					}
				}
			}
			if(!sourceList.isEmpty())
				dataExpressions.addAll(sourceList);
		}
		return dataExpressions;
		
	}

	private boolean hasInControlflow(String a, String b, List<String> drawninteractions) {
		
		if(drawninteractions.contains(a+","+b) || drawninteractions.contains(b+","+a)){
			return true;
		}
		
		
		return false;
	}
	
	private boolean checkSpec(ArrayList<Specification> specList, String a, String b){
		for(Specification s: specList){
			if(s.getPair().toStringA().equals(a) && s.getPair().toStringB().equals(b)){
				return true;
			}
			else if(s.getPair().toStringA().equals(b) && s.getPair().toStringB().equals(a)){
				return true;
			}				
		}				
		return false;
	}

	
}
