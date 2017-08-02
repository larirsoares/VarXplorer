package lari;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cmu.conditional.Conditional;
import de.fosd.typechef.featureexpr.SingleFeatureExpr;
import info.leadinglight.jdot.*;
import info.leadinglight.jdot.enums.*;
import lari.InteractionFinder.PairExp;

public class InteractGraph {

	public void createGraphInter(Map<PairExp, List<String>> hashMap, Collection<SingleFeatureExpr> features){
		Graph g = new Graph("FeatureInteractions");//.setType(GraphType.graph);
		
		String A = "";
		String B = "";
		
		for (SingleFeatureExpr feature1 : features) {
			 String f = Conditional.getCTXString(feature1);
			 System.out.println("node:" + f + "|");
			 g.addNode(new Node(f)); 
		 }
		
		
		for (Entry<PairExp, List<String>> pair : hashMap.entrySet()) {
			
			A = Conditional.getCTXString(pair.getKey().A);
			B = Conditional.getCTXString(pair.getKey().B);
			System.out.println("Pair = [" + A + "," + B + "= " + pair.getValue() + "]");
			String concat = "["+B;
			
			if(pair.getValue().toString().contains("not interact")){
				continue;
			}
			else if(pair.getValue().toString().contains("interact")){
				g.addEdge(
						new Edge(A,B).setArrowHead(ArrowType.none));
			}
			else if(pair.getValue().toString().contains("enables")){
				
				if(pair.getValue().toString().startsWith(concat)){
					g.addEdge(new Edge(B,A).setColor(Color.X11.green).setArrowHead(ArrowType.empty).setLabel("enables").setFontSize(10));
				}else{
					g.addEdge(new Edge(A,B).setColor(Color.X11.green).setArrowHead(ArrowType.empty).setFontSize(10));
				}
			}
			else if(pair.getValue().toString().contains("suppresses")){
				
				if(pair.getValue().toString().startsWith(concat)){
					g.addEdge(new Edge(B,A).setColor(Color.X11.red).setArrowHead(ArrowType.empty).setFontSize(10).setLabel("suppresses"));
				}else{				
					g.addEdge(new Edge(A,B).setColor(Color.X11.red).setArrowHead(ArrowType.empty).setFontSize(10).setLabel("suppresses"));
				}
			}
		}
		
		
		
		new SVGCanvas(g);
	}
	
	

	
}
