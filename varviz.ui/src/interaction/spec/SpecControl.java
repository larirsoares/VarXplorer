package interaction.spec;

import de.fosd.typechef.featureexpr.SingleFeatureExpr;
import interaction.spec.Specification.Relationship;
import interaction.spec.Specification.Type;

public class SpecControl {
	
	public Specification createAllow(SingleFeatureExpr a, SingleFeatureExpr b){
		
		Specification spec =  new Specification();
		spec.setType(Type.Allow);
		spec.setPair(a, b);
		printSpec(spec);
		return spec;
	}
	public String printSpec(Specification spec){
		String print = spec.getType().toString()+" "+ spec.getPair().toString();
		System.out.println("Specificação: " + print);
		return print;
	}
	
	public Specification createAllowReq(SingleFeatureExpr a, SingleFeatureExpr b){
		
		Specification spec =  new Specification();
		spec.setType(Type.Allow);
		spec.setPair(a, b);
		spec.setRelation(Relationship.Require);
		printSpec(spec);
		return spec;
	}
	public Specification createAllowSup(SingleFeatureExpr a, SingleFeatureExpr b){
		
		Specification spec =  new Specification();
		spec.setType(Type.Allow);
		spec.setPair(a, b);
		spec.setRelation(Relationship.Suppress);
		printSpec(spec);
		return spec;
	}
	public Specification createAllowReq(SingleFeatureExpr a, SingleFeatureExpr b, String var) {
		Specification spec =  new Specification();
		spec.setType(Type.Allow);
		spec.setPair(a, b);
		spec.setRelation(Relationship.Require);
		spec.setVar(var);
		printSpec(spec);
		return spec;
	}
	
	public Specification createAllowSup(SingleFeatureExpr a, SingleFeatureExpr b, String var) {
		Specification spec =  new Specification();
		spec.setType(Type.Allow);
		spec.setPair(a, b);
		spec.setRelation(Relationship.Suppress);
		spec.setVar(var);
		printSpec(spec);
		return spec;
	}
}
