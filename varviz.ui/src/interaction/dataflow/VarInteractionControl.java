package interaction.dataflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cmu.conditional.Conditional;
import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;
import de.fosd.typechef.featureexpr.SingleFeatureExpr;
import interaction.PairExp;
import interaction.types.VarInteraction;

public class VarInteractionControl {

	private List<VarInteraction> intList = new ArrayList<>();

	public List<VarInteraction> findInteractionsPerVar(List<DataVar> dataVarList) {
		Collection<SingleFeatureExpr> features = Conditional.features.values();//the whole set of features	
		//ControlflowControl cflow = new ControlflowControl();
		//getting each variable
		//int a = 0;
		for(DataVar var: dataVarList){
			String name = var.getName();
			List<FeatureExpr> expressions = var.getCtxList();
			//List<PairExp> pairs = cflow.getExpressionsPairs(expressions);
			
			getRelations(features, expressions, name);
		}
		return intList;
	}
	
	public List<VarInteraction> findGeneralDataInteractions(List<DataVar> dataVarList) {
		Collection<SingleFeatureExpr> features = Conditional.features.values();//the whole set of features	

		//creating the whole set of exp
		List<FeatureExpr> expressions = new ArrayList<>();
		for(DataVar var: dataVarList){
			expressions.addAll( var.getCtxList());
		}
		getRelations(features, expressions, "");
		return intList;
	}
	
	public List<VarInteraction> findAllInteractions(List<FeatureExpr> expressions) {
		Collection<SingleFeatureExpr> features = Conditional.features.values();//the whole set of features	

		//creating the whole set of exp
		//List<FeatureExpr> expressions = new ArrayList<>();
		

		getRelations(features, expressions, "");
		return intList;
	}

	static FeatureExpr createUnique(SingleFeatureExpr feature, List<FeatureExpr> contexts) {
		FeatureExpr unique = FeatureExprFactory.False();
		for (FeatureExpr ctx : contexts) {
			unique = unique.or(ctx.unique(feature));
		}
		return unique;
	}
	
	private void getRelations(Collection<SingleFeatureExpr> features, List<FeatureExpr> expressions, String name) {
		
		
		for (SingleFeatureExpr feature1 : features) {
				
			if (Conditional.isTautology(feature1)) {
				continue;
			}
			
			final FeatureExpr unique = createUnique(feature1, expressions);
				
			if (Conditional.isContradiction(unique)) {//when a feature doesn't appear in the expressions
				continue;
			}		
				
			for (SingleFeatureExpr feature2 : features) {
				PairExp pairAB = new PairExp(feature1, feature2);
				
				if (feature1 == feature2 || Conditional.isTautology(feature2)) {
					continue;//Conditional.isTautology(feature2) when the feature is the feature model root feature
				}
				
				FeatureExpr first = feature2.implies(unique.not());
				FeatureExpr second = feature2.not().implies(unique.not());
				String phrase = null;
										
				if (first.isTautology()) {
					System.out.println(Conditional.getCTXString(feature2) + " suppresses " + Conditional.getCTXString(feature1));
					phrase = Conditional.getCTXString(feature2) + " suppresses " + Conditional.getCTXString(feature1);
					addphrase(phrase, pairAB, name);
				}
				if (second.isTautology()) {
					System.out.println(Conditional.getCTXString(feature1) + " requires " + Conditional.getCTXString(feature2));
					phrase = Conditional.getCTXString(feature1) + " requires " + Conditional.getCTXString(feature2);
					addphrase(phrase, pairAB, name);
				}
		
			}
		}
	}

	private void addphrase(String phrase, PairExp pairAB, String name) {
		//Map<PairExp, List<String>> hashMap 
//		if(!hashMap.containsKey(pairAB)){
//			hashMap.put(pairAB, new ArrayList<>());
//			hashMap.get(pairAB).add(name);
//			hashMap.get(pairAB).add(phrase);
//		}else{
//			if(!hashMap.get(pairAB).contains(name)){
//				hashMap.get(pairAB).add(name);
//			}
//			hashMap.get(pairAB).add(phrase);
//		}
		VarInteraction inter = new VarInteraction(name, pairAB, phrase);
		intList.add(inter);
		
	}
	
	
	

}
