package interaction.types;

import interaction.PairExp;

public class ControInteraction {
	private PairExp pair;
	private String exp;
	
	public ControInteraction(PairExp pair, String exp) {
		super();
		this.pair = pair;
		this.exp = exp;
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

	@Override
	public String toString() {
		return pair + " [" +  exp + " ]";
		
	}
}
