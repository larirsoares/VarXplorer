package cmu.varviz.trace;

import java.util.Collection;

import cmu.conditional.Conditional;
import de.fosd.typechef.featureexpr.FeatureExpr;

public class IFStatement<T> extends Statement<T> {

	private FeatureExpr targetContext;
	
	public IFStatement(T op, Method<?> m, int line, FeatureExpr targetContext, FeatureExpr ctx) {
		super(op, m, line, ctx);
		this.targetContext = targetContext;
		this.lineNumber = line;
		setShape(Shape.Mdiamond);
		setColor(NodeColor.white);
	}
		
	@Override
	public String toString() {
		return "if (" + Conditional.getCTXString(targetContext) + ')';
	}

	public FeatureExpr getTargetContext() {
		return targetContext;
	}

	@Override
	public boolean affectsIdentifier(String identifier) {
		return true;
	}
	
	@Override
	public boolean affectsref(int ref) {
		return true;
	}
	
	@Override
	public boolean isInteraction(int degree) {
		return true;
	}
	
	@Override
	protected void collectIFStatements(Collection<IFStatement<?>> ifStatements) {
		ifStatements.add(this);
	}
}
