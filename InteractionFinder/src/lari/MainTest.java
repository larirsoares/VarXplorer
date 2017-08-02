package lari;

import java.util.ArrayList;
import java.util.List;

import cmu.conditional.Conditional;
import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;
import de.fosd.typechef.featureexpr.SingleFeatureExpr;

public class MainTest {

	
	static {
		FeatureExprFactory.setDefault(FeatureExprFactory.bdd());
	}
	
	final static SingleFeatureExpr blocking = Conditional.createFeature("blocking");
	final static SingleFeatureExpr voiceMail = Conditional.createFeature("VoiceMail");
	final static SingleFeatureExpr parallel = Conditional.createFeature("Parallel");
	final static SingleFeatureExpr weight = Conditional.createFeature("weight");
	final static SingleFeatureExpr overloaded = Conditional.createFeature("overloaded");
	final static SingleFeatureExpr forward = Conditional.createFeature("Forward");
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		testInteraction();

	}
	
	private static void testInteraction() {
		List<FeatureExpr> expressions = new ArrayList<>();
		expressions.add(blocking);
		expressions.add(blocking.not());		
		expressions.add(blocking.not().and(voiceMail));
		expressions.add(blocking.not().and(voiceMail.not()));	
		expressions.add(blocking.not().and(parallel).and(overloaded));
		expressions.add(weight.and(parallel).and(overloaded));
		
		InteractionFinder finder = new InteractionFinder();
		finder.getSuppressionsForm(expressions);
		
	}

}
