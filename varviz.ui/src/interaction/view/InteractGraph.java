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
import interaction.dataflow.DataInteraction;
import interaction.dataflow.DataVar;
import interaction.spec.Specification;
/**
 * has...
 * 
 * @author Larissa Rocha
 *  
 */


public class InteractGraph {
	ArrayList<Specification> SpecList;
	

	public void createGraphInter(Map<PairExp, List<String>> hashMap, Collection<SingleFeatureExpr> features, List<SingleFeatureExpr> noEffectlist, List<FeatureExpr> expressions, File workingDir, List<List> allVars, List<DataInteraction> dataInteracList, ArrayList<Specification> specList){
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
						
						String shownVars = "";
						for(List<String> vars: allVars){
							expV = vars.get(0);						
							if((expV.contains(A) && expV.contains(B)) && shownVars==""){
								
								for(int i=1; i<vars.size();i++){//String v: vars){
									String a = vars.get(i).substring(0,vars.get(i).length()-3);
									//a = "var"+i + ": " + a;
									shownVars += a;
									if(i<vars.size()-1)
										shownVars += "\n";
								}
								System.out.println("Overwritten vars from [" + A + "," + B + "] =" + shownVars);
								g.addEdge(new Edge(A,B).setArrowHead(ArrowType.none).setLabel(shownVars).setFontSize(10).setFontColor(Color.X11.black));							
								drawninteractions.add(A+","+B);
								continue;
							}
						}
						if(shownVars==""){
							g.addEdge(
									new Edge(A,B).setArrowHead(ArrowType.none));
							drawninteractions.add(A+","+B);
						}
					}
					else if(exp.contains("enables")){
						if(checkSpec(specList, A, B)){
							continue;
						}
						
						String shownVars = "";
						for(List<String> vars: allVars){
							expV = vars.get(0);
							if(shownVars!=""){
								shownVars += "\n";
							}
							if(expV.contains(A) && expV.contains(B)){
								
								for(int i=1; i<vars.size();i++){//String v: vars){
									String a = vars.get(i).substring(0,vars.get(i).length()-3);
									//a = "var"+i + ": " + a;
									shownVars += a;
									if(i<vars.size()-1)
										shownVars += "\n";
								}
							}
						}
						System.out.println("Overwritten vars from [" + A + "," + B + "] =" + shownVars);
							
						if(shownVars!=""){
							if(exp.startsWith(concat)){
								Edge edge = new Edge(B,A);
								edge.setColor(Color.X11.green).setArrowHead(ArrowType.empty);
								edge.setLabel("enables \n" + shownVars).setFontSize(10);
								edge.setToolTip("A");
								g.addEdge(edge);
								drawninteractions.add(A+","+B);
								//g.addEdge(new Edge(B,A).setColor(Color.X11.green).setArrowHead(ArrowType.empty).setLabel("enables").setFontSize(10));
							}else{
								Edge edge = new Edge(A,B);
								edge.setColor(Color.X11.green).setArrowHead(ArrowType.empty);
								edge.setLabel("enables \n" + shownVars).setFontSize(10);
								edge.setToolTip(shownVars);
								g.addEdge(edge);
								drawninteractions.add(A+","+B);
								//g.addEdge(new Edge(A,B).setColor(Color.X11.green).setArrowHead(ArrowType.empty).setLabel("enables").setFontSize(10));
							}
						}
						
						if(shownVars==""){
							if(exp.startsWith(concat)){
								Edge edge = new Edge(B,A);
								edge.setColor(Color.X11.green).setArrowHead(ArrowType.empty);
								edge.setLabel("enables \n" + shownVars).setFontSize(10);
								edge.setToolTip("A");
								g.addEdge(edge);
								drawninteractions.add(A+","+B);
								//g.addEdge(new Edge(B,A).setColor(Color.X11.green).setArrowHead(ArrowType.empty).setLabel("enables").setFontSize(10));
							}else{
								Edge edge = new Edge(A,B);
								edge.setColor(Color.X11.green).setArrowHead(ArrowType.empty);
								edge.setLabel("enables \n" + shownVars).setFontSize(10);
								edge.setToolTip(shownVars);
								g.addEdge(edge);
								drawninteractions.add(A+","+B);
								//g.addEdge(new Edge(A,B).setColor(Color.X11.green).setArrowHead(ArrowType.empty).setLabel("enables").setFontSize(10));
							}
								
						}
					}
					else if(exp.contains("suppresses")){
						if(checkSpec(specList, A, B)){
							continue;
						}
						
						
						String shownVars = "";
						for(List<String> vars: allVars){
							expV = vars.get(0);
						
							if(expV.contains(A) && expV.contains(B)){
								for(int i=1; i<vars.size();i++){//String v: vars){
									String a = vars.get(i).substring(0,vars.get(i).length()-3);
									//a = "var"+i + ": " + a;
									shownVars += a;
									if(i<vars.size()-1)
										shownVars += "\n";
								}
								System.out.println("Overwritten vars from [" + A + "," + B + "] =" + shownVars);
								if(exp.startsWith(concat)){
									Edge edge = new Edge(B,A);
									edge.setColor(Color.X11.red).setArrowHead(ArrowType.empty);
									edge.setFontSize(10).setLabel("suppresses \n" + shownVars);
									edge.setLabelTooltip(shownVars);
									g.addEdge(edge);
									drawninteractions.add(A+","+B);
									//g.addEdge(new Edge(B,A).setColor(Color.X11.red).setArrowHead(ArrowType.empty).setFontSize(10).setLabel("suppresses"));
								}else{
									Edge edge = new Edge(A,B);
									edge.setColor(Color.X11.red).setArrowHead(ArrowType.empty);
									edge.setFontSize(10).setLabel("suppresses \n" + shownVars);
									edge.setLabelTooltip(shownVars);
									g.addEdge(edge);
									drawninteractions.add(A+","+B);
									//g.addEdge(new Edge(A,B).setColor(Color.X11.red).setArrowHead(ArrowType.empty).setFontSize(10).setLabel("suppresses"));
								}
								
//								if(allVars.size()>1)
//									allVars.remove(vars);
								continue;
							}
						}
						String showVars = "";
						for(List<String> vars: allVars){
							expV = vars.get(0);						
							if((expV.contains(A) && expV.contains(B)) && shownVars==""){
								
								for(int i=1; i<vars.size();i++){//String v: vars){
									String a = vars.get(i).substring(0,vars.get(i).length()-3);
									a = "var"+i + ": " + a;
									shownVars += a;
									if(i<vars.size()-1)
										shownVars += "\n";
								}
								System.out.println("Overwritten vars from [" + A + "," + B + "] =" + shownVars);
								g.addEdge(new Edge(A,B).setArrowHead(ArrowType.none).setLabel(shownVars).setFontSize(10).setFontColor(Color.X11.black));							
								drawninteractions.add(A+","+B);
								continue;
							}
						}
						if(shownVars==""){
							g.addEdge(
									new Edge(A,B).setArrowHead(ArrowType.none));
							drawninteractions.add(A+","+B);
						}
					}
				}
			
			
			
			//}
		}
		
		List<FeatureExpr> dataExpressions = new ArrayList<>();
		//data interaction
		drawDataInteractions(dataInteracList, features, g, drawninteractions, dataExpressions);
		
		
		//here print all the expressions in the graph
		String concat = printExpressions(dataExpressions, expressions);
		g.setLabel(concat);
		new SVGCanvas(g,workingDir);
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

	private void drawDataInteractions(List<DataInteraction> dataInteracList, Collection<SingleFeatureExpr> features, Graph g, List<String> drawninteractions, List<FeatureExpr> dataExpressions) {
		
		String A = "";
		String B = "";
		String C = "";
		
		for(DataInteraction Interaction: dataInteracList){
					
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
				int i = 1;
				for(DataVar var: Interaction.getDataVars()){
					updateDataExp(dataExpressions, var.getCtxList());
					
					//get the var to show in the graph
					String a = var.getName().substring(0,var.getName().length()-3);
					//a = "var"+i + ": " + a;
					shownVars += a;
					if(i<Interaction.getDataVars().size())
						shownVars += "\n";
					i++;
				}
				System.out.println("Data Interaction: [" + A + "," + B + "] = " + shownVars);
					
				Edge edge = new Edge(A,B);
				edge.setColor(Color.X11.orange).setStyle(Style.Edge.dashed).setArrowHead(ArrowType.none);
				edge.setFontSize(10).setLabel(shownVars).setFontColor(Color.X11.black);
				g.addEdge(edge);
			}
			else if(fList.size()==3){
				System.out.println("WARNING: interaction order higher than 2");
				A = Conditional.getCTXString(fList.get(0));
				B = Conditional.getCTXString(fList.get(1));
				C = Conditional.getCTXString(fList.get(2));
				
				String shownVars = "";
				int i = 1;
				for(DataVar var: Interaction.getDataVars()){
					updateDataExp(dataExpressions, var.getCtxList());
					String a = var.getName().substring(0,var.getName().length()-3);
					//a = "var"+i + ": " + a;
					shownVars += a;
					if(i<Interaction.getDataVars().size())
						shownVars += "\n";
					i++;
				}
				System.out.println("Data Interaction: [" + A + "," + B + "] = " + shownVars);
				
				
				//verify if controlflow exists
				if(!hasInControlflow(A, B,drawninteractions)){
					if(checkSpec(SpecList, A, B)){
						continue;
					}
					
					Edge edge = new Edge(A,B);
					edge.setColor(Color.X11.orange).setStyle(Style.Edge.dashed).setArrowHead(ArrowType.none);
					edge.setFontSize(10).setLabel(shownVars).setFontColor(Color.X11.black);
					g.addEdge(edge);
					
				} else if(!hasInControlflow(A, C,drawninteractions)){
					if(checkSpec(SpecList, A, C)){
						continue;
					}
					
					Edge edge = new Edge(A,C);
					edge.setColor(Color.X11.orange).setStyle(Style.Edge.dashed).setArrowHead(ArrowType.none);
					edge.setFontSize(10).setLabel(shownVars).setFontColor(Color.X11.black);
					g.addEdge(edge);
					
				} else if(!hasInControlflow(B, C, drawninteractions)){
					if(checkSpec(SpecList, B, C)){
						continue;
					}
					
					Edge edge = new Edge(B, C);
					edge.setColor(Color.X11.orange).setStyle(Style.Edge.dashed).setArrowHead(ArrowType.none);
					edge.setFontSize(10).setLabel(shownVars).setFontColor(Color.X11.black);
					g.addEdge(edge);
					
				}
					
				
			}
			else if(fList.size()>=3){
				System.out.println("WARNING: interaction order higher than 3");
			}
				
		}
		
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
