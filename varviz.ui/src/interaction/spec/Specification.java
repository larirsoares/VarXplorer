package interaction.spec;

import cmu.conditional.Conditional;
import de.fosd.typechef.featureexpr.SingleFeatureExpr;
import interaction.PairExp;

/**
 * has...
 * 
 * @author Larissa Rocha
 *  
 */

public class Specification {
	
	private PairExp pair;
	private Type type;
	private Relationship relation;
	//private Target target;
	private String var;
	
	public Type getType() {	return type;}
	public void setType(Type type) { this.type = type;}
	
	public PairExp getPair() { return pair;}
	public void setPair(SingleFeatureExpr a, SingleFeatureExpr b) {	pair = new PairExp(a, b);}
	
	public Relationship getRelation() {	return relation;}
	public void setRelation(Relationship relation) { this.relation = relation;}
	
	public String getVar() { return this.var;}
	public void setVar(String var) { 
		this.var = var;
	}

	
	public enum Type {
		Allow("Allow"),
		Forbid("Forbid"); 
		
		private String name;
		Type (String name){
			this.name = name;
		}
		public String getName(){ return name;}		
	}
	
	public enum Relationship {
		Require("Require"),	
		Suppress("Suppress");
		
		private String name;
		Relationship (String name){
			this.name = name;
		}
		public String getName(){ return name;}
	}
	
	
	@Override
	public String toString() {
		return Conditional.getCTXString(pair.getA()) + ", " + Conditional.getCTXString(pair.getB()) + " " +  type.getName() + " " +  relation.getName() + " " +  var;	
	}
	
	
//	public enum Target {
//		Var("Var"), 
//		Method("Method"), 
//		Class("Class"); 
//		
//		private String name;
//		Target (String name){
//			this.name = name;
//		}
//		public String getName(){ return name;}
//		
//		private String var = "";
//		public String getVar() { return var; }
//		public void setVar(String var) { this.var = var; }
		
//	}
	

}

// class Target{
//	String var;
//
//	String method;
//	String cllass;
//	
//	public String getVar() {
//		return var;
//	}
//	public void setVar(String var) {
//		this.var = var;
//	}
//	
//	
//}
