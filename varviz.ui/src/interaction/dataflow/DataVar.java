package interaction.dataflow;

import java.util.ArrayList;
import java.util.List;
import cmu.conditional.Conditional;
import cmu.varviz.trace.Trace;
import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.SingleFeatureExpr;

import de.fosd.typechef.featureexpr.FeatureExpr;

/**
 * has...
 * 
 * @author Larissa Rocha
 *  
 */

public class DataVar {
	
	String name;
	List<FeatureExpr> ctxList = new ArrayList<>();
	public DataVar(String name, FeatureExpr context) {
		super();
		this.name = name;
		this.ctxList.add(context);
	}
	public DataVar(String name, List<FeatureExpr> context) {
		super();
		this.name = name;
		this.ctxList = context;
	}
	
	public String getName() {
		return name;
	}
	public List<FeatureExpr> getCtxList() {
		return ctxList;
	}	
	public void setContext(FeatureExpr context){
		this.ctxList.add(context);
	}
	public boolean hascontext(FeatureExpr context){
		if(this.ctxList.contains(context)){
			return true;
		}
		return false;
	}
	public void updateCtx(List<FeatureExpr> newList){
		for(FeatureExpr context: newList){
			if(!this.ctxList.contains(context)){
				this.ctxList.add(context);
			}
		}
	}
	@Override
	public String toString() {
		return this.name;
	}
	
}