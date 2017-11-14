package interaction;

import java.util.ArrayList;

import cmu.conditional.Conditional;
import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.SingleFeatureExpr;

public class Interaction {

	private PairExp pair;
	private ArrayList<Relationship> relations;

	public Interaction() {
		super();
		this.relations = new ArrayList<>();
	}
	public ArrayList<Relationship> getRelations() {
		return relations;
	}
	public void setRelation(Relationship relation) {
		this.relations.add(relation);
	}

	public PairExp getPair() {	return pair;}
	public void setPair(FeatureExpr a, FeatureExpr b) { 
		this.pair = new PairExp((SingleFeatureExpr)a, (SingleFeatureExpr)b);
		}
	
	@Override
	public String toString() {
		return " [" + Conditional.getCTXString(pair.getA()) + " , " + Conditional.getCTXString(pair.getB()) + "] ";
	}
	
//	public enum Relationship {
//		Require("Require"),	
//		Suppress("Suppress");
//		
//		private String name;
//		Relationship (String name){
//			this.name = name;
//		}
//		public String getName(){ return name;}
//
//		private ArrayList<String> vars;
//		public ArrayList<String> getVars() { return vars;}
//		public void setVars(String var) { this.vars.add(var);}
//	}
}


