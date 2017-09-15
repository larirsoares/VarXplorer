package interaction.spec;

import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.SingleFeatureExpr;
import interaction.InteractionFinder;
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
	private Flow flow;
	private Target target;
	
	public Type getType() {	return type;}
	public void setType(Type type) { this.type = type;}
	
	public PairExp getPair() {
		return pair;
	}
	public void setPair(SingleFeatureExpr a, SingleFeatureExpr b) {
		pair = new PairExp(a, b);
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
	
	public enum Flow {
		DataF, ControlF; 
	}
	public enum Target {
		All, Var, Method, Class; 
	}
}
