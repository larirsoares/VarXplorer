package interaction.spec;

import de.fosd.typechef.featureexpr.SingleFeatureExpr;
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
}
