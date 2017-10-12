package interaction.types;

import interaction.PairExp;

public class VarInteraction {
	private String varName;
	private PairExp pair;
	private String exp;
	
	public String getVarName() {
		return varName;
	}
	public void setVarName(String varName) {
		this.varName = varName;
	}
	public PairExp getPair() {
		return pair;
	}
	public void setPair(PairExp pair) {
		this.pair = pair;
	}
	public String getExp() {
		return exp;
	}
	public void setExp(String exp) {
		this.exp = exp;
	}
	public VarInteraction(String varName, PairExp pair, String exp) {
		super();
		this.varName = varName;
		this.pair = pair;
		this.exp = exp;
	}
	
	@Override
	public String toString() {
		return varName + " [" +  pair + " ]";		
	}
	
}
