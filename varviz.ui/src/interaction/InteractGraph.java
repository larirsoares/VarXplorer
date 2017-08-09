package interaction;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cmu.conditional.Conditional;
import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.SingleFeatureExpr;
import info.leadinglight.jdot.*;
import info.leadinglight.jdot.enums.*;
import interaction.InteractionFinder.PairExp;

public class InteractGraph {

	public void createGraphInter(Map<PairExp, List<String>> hashMap, Collection<SingleFeatureExpr> features, List<SingleFeatureExpr> noEffectlist, List<FeatureExpr> expressions){
		Graph g = new Graph("FeatureInteractions");//.setType(GraphType.graph);
		
		String A = "";
		String B = "";
		
		for (SingleFeatureExpr feature1 : features) {
			 String f = Conditional.getCTXString(feature1);
			 if(noEffectlist.contains(feature1)){
			 	g.addNode(new Node(f).setStyle(Style.Node.dashed).setColor(Color.X11.grey).setFontColor(Color.X11.gray)); 
			 }else if(!Conditional.isTautology(feature1)){
				g.addNode(new Node(f));
			 }
		 }
		
		
		for (Entry<PairExp, List<String>> pair : hashMap.entrySet()) {
			
			A = Conditional.getCTXString(pair.getKey().A);
			B = Conditional.getCTXString(pair.getKey().B);
		//	System.out.println("Pair = [" + A + "," + B + "= " + pair.getValue() + "]");
			String concat = B;
			
			List<String> l = pair.getValue();
			for(String exp: l){
				System.out.println("expr: " + exp);
			
				if(exp.contains("not interact")){
					continue;
				}
				else if(exp.contains("interact")){
					g.addEdge(
							new Edge(A,B).setArrowHead(ArrowType.none));
				}
				else if(exp.contains("enables")){
					
					if(exp.startsWith(concat)){
						g.addEdge(new Edge(B,A).setColor(Color.X11.green).setArrowHead(ArrowType.empty).setLabel("enables").setFontSize(10));
					}else{
						g.addEdge(new Edge(A,B).setColor(Color.X11.green).setArrowHead(ArrowType.empty).setLabel("enables").setFontSize(10));
					}
				}
				else if(exp.contains("suppresses")){
					
					if(exp.startsWith(concat)){
						g.addEdge(new Edge(B,A).setColor(Color.X11.red).setArrowHead(ArrowType.empty).setFontSize(10).setLabel("suppresses"));
					}else{				
						g.addEdge(new Edge(A,B).setColor(Color.X11.red).setArrowHead(ArrowType.empty).setFontSize(10).setLabel("suppresses"));
					}
				}
			}
		}
		
		String concat = "";
		for(FeatureExpr featureexpr : expressions){
			concat+= Conditional.getCTXString(featureexpr) + "\n";
		}
		g.setLabel(concat);
		
		new SVGCanvas(g);
	}
	
	

	
}
