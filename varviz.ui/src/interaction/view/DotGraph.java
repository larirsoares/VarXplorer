package interaction.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cmu.conditional.Conditional;
import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.SingleFeatureExpr;
import info.leadinglight.jdot.Edge;
import info.leadinglight.jdot.Graph;
import info.leadinglight.jdot.Node;
import info.leadinglight.jdot.enums.ArrowType;
import info.leadinglight.jdot.enums.Color;
import info.leadinglight.jdot.enums.Style;
import interaction.Interaction;
import interaction.InteractionCreator;

public class DotGraph {

	public void createGraph(ArrayList<Interaction> finalList, InteractionCreator resultingGraph, File workingDir, List<FeatureExpr> expressions) {
		Graph g = new Graph("FeatureInteractions");	
		
		//creating circles and dashed circles
		for (SingleFeatureExpr feature1 : resultingGraph.getDoNotInterctList()) {
			 String f = Conditional.getCTXString(feature1);
			 
			if(!Conditional.isTautology(feature1)){
					g.addNode(new Node(f));
			}
		}
		for (SingleFeatureExpr feature1 : resultingGraph.getNoEffectlist()) {
			 String f = Conditional.getCTXString(feature1);
			 
			 g.addNode(new Node(f).setStyle(Style.Node.dashed).setColor(Color.X11.grey).setFontColor(Color.X11.gray)); 
		}
		
		for(Interaction inter: finalList){
			String A = Conditional.getCTXString(inter.getPair().getA());
			String B = Conditional.getCTXString(inter.getPair().getB());
			
			String relation = inter.getRelations().get(0).getRelation();
			String shownVars = "";
			
			for(String var: inter.getRelations().get(0).getVars()){
				if(shownVars!=""){
					shownVars += "\n";
				}
				
				if(!shownVars.contains(var)){
					shownVars += var;
				}
			}
					
			if(relation.equals("Require")){
				if(shownVars!=""){
					shownVars = "requires \n" + shownVars;
				}else{
					shownVars = "requires";
				}
				
				Edge edge = new Edge(A,B);
				if(inter.getRelations().get(0).isDataRelation()){					
					edge.setColor(Color.X11.green).setStyle(Style.Edge.dashed).setArrowHead(ArrowType.empty);					
				}else{
					edge.setColor(Color.X11.green).setArrowHead(ArrowType.empty);
				}
				edge.setLabel(shownVars).setFontSize(14);
				g.addEdge(edge);
				
				
				
			}else if(relation.equals("Suppress")){
				if(shownVars!=""){
					shownVars = "suppresses \n" + shownVars;
				}else{
					shownVars = "suppresses";
				}
				
				Edge edge = new Edge(A,B);
				if(inter.getRelations().get(0).isDataRelation()){					
					edge.setColor(Color.X11.red).setStyle(Style.Edge.dashed).setArrowHead(ArrowType.empty);				
				}else{
					edge.setColor(Color.X11.red).setArrowHead(ArrowType.empty);
				}
				edge.setLabel(shownVars).setFontSize(14);
				g.addEdge(edge);

			}else{
				g.addEdge(new Edge(A,B).setArrowHead(ArrowType.none).setLabel(shownVars).setFontSize(14).setFontColor(Color.X11.black));	
			}
		}
		
		//here print all the expressions in the graph
		String concat = "";
		for(FeatureExpr featureexpr : expressions){
			concat+= Conditional.getCTXString(featureexpr) + "\n";
		}		
		g.setLabel("New \n" + concat);
		new SVGCanvas(g,workingDir);
		 
	}

}
