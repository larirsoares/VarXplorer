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
import info.leadinglight.jdot.*;
import info.leadinglight.jdot.enums.*;
import interaction.InteractionFinder;
import interaction.InteractionFinder.PairExp;
import interaction.dataflow.DataInteraction;
import interaction.dataflow.DataFlowControl;
import interaction.dataflow.DataVar;
/**
 * has...
 * 
 * @author Larissa Rocha
 *  
 */


public class InteractGraph {

	public void createGraphInter(Map<PairExp, List<String>> hashMap, Collection<SingleFeatureExpr> features, List<SingleFeatureExpr> noEffectlist, List<FeatureExpr> expressions, File workingDir, List<List> allVars, List<DataInteraction> dataInteracList){
		Graph g = new Graph("FeatureInteractions");		
		String A = "";
		String B = "";
		List<String> drawninteractions = new ArrayList<>();
		
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
						String shownVars = "";
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
					else if(exp.contains("enables")){
						
						String shownVars = "";
						for(List<String> vars: allVars){
							expV = vars.get(0);			
							if(expV.contains(A) && expV.contains(B)){
								
								for(int i=1; i<vars.size();i++){//String v: vars){
									String a = vars.get(i).substring(0,vars.get(i).length()-3);
									a = "var"+i + ": " + a;
									shownVars += a;
									if(i<vars.size()-1)
										shownVars += "\n";
								}
								System.out.println("Overwritten vars from [" + A + "," + B + "] =" + shownVars);
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
								
//								if(allVars.size()>1)
//									allVars.remove(vars);
								
								continue;
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
						
						String shownVars = "";
						for(List<String> vars: allVars){
							expV = vars.get(0);
						
							if(expV.contains(A) && expV.contains(B)){
								for(int i=1; i<vars.size();i++){//String v: vars){
									String a = vars.get(i).substring(0,vars.get(i).length()-3);
									a = "var"+i + ": " + a;
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
		
		//fazer aqui o dataInteraction
		drawDataInteractions(dataInteracList, features, g, drawninteractions);
		
		
		String concat = "";
		for(FeatureExpr featureexpr : expressions){
			concat+= Conditional.getCTXString(featureexpr) + "\n";
		}
		g.setLabel(concat);
		
		new SVGCanvas(g,workingDir);
	}

	private void drawDataInteractions(List<DataInteraction> dataInteracList, Collection<SingleFeatureExpr> features, Graph g, List<String> drawninteractions) {
		
		String A = "";
		String B = "";
		String C = "";
		for(DataInteraction Interaction: dataInteracList){
			//List<FeatureExpr> dFeatures = Interaction.getFeatures();
			List<FeatureExpr> fList = Interaction.getFeatures();
			if(fList.size()==2){
				A = Conditional.getCTXString(fList.get(0));
				B = Conditional.getCTXString(fList.get(1));
				String shownVars = "";
				int i = 1;
				for(DataVar var: Interaction.getDataVars()){
					String a = var.getName().substring(0,var.getName().length()-3);
					a = "var"+i + ": " + a;
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
					String a = var.getName().substring(0,var.getName().length()-3);
					a = "var"+i + ": " + a;
					shownVars += a;
					if(i<Interaction.getDataVars().size())
						shownVars += "\n";
					i++;
				}
				System.out.println("Data Interaction: [" + A + "," + B + "] = " + shownVars);
				
				
				//verify if controlflow exists
				if(!hasInControlflow(A, B,drawninteractions)){
					Edge edge = new Edge(A,B);
					edge.setColor(Color.X11.orange).setStyle(Style.Edge.dashed).setArrowHead(ArrowType.none);
					edge.setFontSize(10).setLabel(shownVars).setFontColor(Color.X11.black);
					g.addEdge(edge);
					
				} else if(!hasInControlflow(A, C,drawninteractions)){
					Edge edge = new Edge(A,C);
					edge.setColor(Color.X11.orange).setStyle(Style.Edge.dashed).setArrowHead(ArrowType.none);
					edge.setFontSize(10).setLabel(shownVars).setFontColor(Color.X11.black);
					g.addEdge(edge);
					
				} else if(!hasInControlflow(B, C, drawninteractions)){
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

	private boolean hasInControlflow(String a, String b, List<String> drawninteractions) {
		
		if(drawninteractions.contains(a+","+b) || drawninteractions.contains(b+","+a)){
			return true;
		}
		
		
		return false;
	}
	
	

	
}
