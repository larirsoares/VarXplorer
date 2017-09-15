package interaction.spec;

import static org.junit.Assert.*;

import org.junit.Test;

import cmu.conditional.Conditional;
import de.fosd.typechef.featureexpr.SingleFeatureExpr;
import interaction.spec.Specification.Type;

public class TestSpec {

	@Test
	public void test1(){
		
		SpecControl specTry = new SpecControl();
		SingleFeatureExpr a = Conditional.createFeature("decrypt");
		SingleFeatureExpr b = Conditional.createFeature("encrypt");

		Specification s = specTry.createAllow(a, b);

		
		Specification spec = new Specification();
		spec.setType(Type.Allow);
		spec.setPair(a, b);
		String print = spec.getType().toString()+" "+ spec.getPair().toString();
		//System.out.println("Specificação: " + spec.getType().toString() + " " + spec.getPair().toString());
		
		assertEquals(specTry.printSpec(s),print);
	
		
	}
}
