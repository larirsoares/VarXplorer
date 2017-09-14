package interaction.spec;

/**
 * has...
 * 
 * @author Larissa Rocha
 *  
 */

public class Specification {
	
	public enum ItType {
		Allow, Forbid; 
	}
	public enum ItFlow {
		DataF, ControlF; 
	}
	public enum ItTarget {
		All, Var, Method, Class; 
	}
}
