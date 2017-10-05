package interaction.controlflow;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import cmu.conditional.Conditional;
import cmu.varviz.trace.Edge;
import cmu.varviz.trace.Statement;
import cmu.varviz.trace.view.VarvizView;
import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;
import de.fosd.typechef.featureexpr.SingleFeatureExpr;
import interaction.dataflow.DataFlowControl;
import interaction.dataflow.DataInteraction;
import interaction.view.InteractGraph;
import interaction.PairExp;
import scala.Option;
import scala.Tuple2;
import scala.collection.immutable.Set;

/**
 * has...
 * 
 * @author Larissa Rocha
 *  
 */

public class ControlflowControl {	

	Collection<SingleFeatureExpr> features;
	List<SingleFeatureExpr> noEffectlist;
	Map<PairExp, List<String>> hashMap;
	List<FeatureExpr> expressions;
	
	public Collection<SingleFeatureExpr> getFeatures() {return features;}
	public List<SingleFeatureExpr> getNoEffectlist() {return noEffectlist;}
	public Map<PairExp, List<String>> getHashMap() {return hashMap;}
	public List<FeatureExpr> getExpressions() {return expressions;}
	
	static {
		FeatureExprFactory.setDefault(FeatureExprFactory.bdd());
	}
	
	static FeatureExpr createUnique(SingleFeatureExpr feature, List<FeatureExpr> contexts) {
		FeatureExpr unique = FeatureExprFactory.False();
		for (FeatureExpr ctx : contexts) {
			unique = unique.or(ctx.unique(feature));
		}
		return unique;
	}
		
	public Map<PairExp, List<String>> getInteractionsTable(List<FeatureExpr> expressions){//, File workingDir, List<List> allVars, List<DataInteraction> dataInteracList) {
		
		Collection<SingleFeatureExpr> features = Conditional.features.values();//the whole set of features	
		List<PairExp> exprPairs = new ArrayList<>();//the pairs present in the expressions
		List<PairExp> contain = new ArrayList<>();//only to not repeat the same pair "do not interact"
		
		List<SingleFeatureExpr> noEffectlist = new ArrayList<>();
		Map<PairExp, List<String>> hashMap = new HashMap<>();
		
		exprPairs = getExpressionsPairs(expressions);//get all the pairs in the expressions
		noEffectlist = getNoEffectlist(features, expressions);//list of features that do not appear in the expressions
		
		for (SingleFeatureExpr feature1 : features) {
			
			if (Conditional.isTautology(feature1)) {
				continue;
			}
			final FeatureExpr unique = createUnique(feature1, expressions);
			
			if (Conditional.isContradiction(unique)) {//when a feature doesn't appear in the expressions
				continue;
			}		
			
			for (SingleFeatureExpr feature2 : features) {
				if (feature1 == feature2 || Conditional.isTautology(feature2)) {
					continue;//Conditional.isTautology(feature2) when the feature is the feature model root feature
				}			
				FeatureExpr first = feature2.implies(unique.not());
				FeatureExpr second = feature2.not().implies(unique.not());
				String phrase = new String("a");
									
				if (first.isTautology()) {
					System.out.println(Conditional.getCTXString(feature1) + " suppresses " + Conditional.getCTXString(feature2));
					phrase = Conditional.getCTXString(feature1) + " suppresses " + Conditional.getCTXString(feature2);
				}
				if (second.isTautology()) {
					System.out.println(Conditional.getCTXString(feature2) + " enables " + Conditional.getCTXString(feature1));
					phrase = Conditional.getCTXString(feature2) + " enables " + Conditional.getCTXString(feature1);
					//phrase = Conditional.getCTXString(feature1) + " enables " + Conditional.getCTXString(feature2);
				}			
					
				PairExp pairAB = new PairExp(feature1, feature2);
				PairExp pairBA = new PairExp(feature2, feature1);
				
				if(phrase.equals("a")){
					//if the pair is no present in the expressions
					if (!exprPairs.contains(pairAB) && !exprPairs.contains(pairBA) && !contain.contains(pairAB)){
						
						if (!noEffectlist.contains(feature1) && !noEffectlist.contains(feature2)) {				
							phrase = "do not interact";
						}
						else if(noEffectlist.contains(feature1)){
							phrase = Conditional.getCTXString(feature1) + " has no effect";
						}
						
						else if(noEffectlist.contains(feature2)){
							phrase = Conditional.getCTXString(feature2) + " has no effect";
						}
						contain.add(pairAB);//to avoid repeat the same pair in a different order
						contain.add(pairBA);
					}
				}
				
				if((!hashMap.containsKey(pairAB)) && (!hashMap.containsKey(pairBA)) && (!phrase.equals("a"))){
					hashMap.put(pairAB, new ArrayList<>());
					hashMap.get(pairAB).add(phrase);
				}
				else{
					if(!phrase.equals("a")){
					hashMap.get(pairAB).add(phrase);
					}
				}
			}	
		}
		
		//when both features of a pair have no effect
		addDoubleNoEffect(noEffectlist, hashMap);
		
		//when both features of a pair interact but they are not suppressing or enabling each other
		String phrase = "a";
		for(PairExp pair: exprPairs){
			if(!hashMap.containsKey(pair)){
				phrase = "do interact";
				hashMap.put(pair, new ArrayList<>());
				hashMap.get(pair).add(phrase);			
			}
		}
				
		//creates excel table
		//createExcelTable(hashMap, features);
		
		this.features = features;
		this.hashMap = hashMap;
		this.noEffectlist = noEffectlist;
		this.expressions = expressions;
		
		//creates graph
//		InteractGraph g = new InteractGraph();		
//		g.createGraphInter(hashMap, features, noEffectlist, expressions, workingDir, allVars, dataInteracList);	
		return hashMap;
		
	
	}

	//add in the table when both features of a pair have no effect
	private void addDoubleNoEffect(List<SingleFeatureExpr> noEffectlist, Map<PairExp, List<String>> hashMap) {
		
		if(noEffectlist.size()>1){
			String phrase = "a";
			for(int i = 0; i<noEffectlist.size(); i++){
				for(int j = i+1; j<noEffectlist.size(); j++){	
					//System.out.println("no effect list: " + "i " + i + " " + noEffectlist.get(i) + " j " + j + " " + noEffectlist.get(j));
					SingleFeatureExpr noA = noEffectlist.get(i);
					SingleFeatureExpr noB = noEffectlist.get(j);
					PairExp pairNoEffect = new PairExp(noA, noB);
					PairExp pairNoEffect2 = new PairExp(noB, noA);
					if(!hashMap.containsKey(pairNoEffect) && !hashMap.containsKey(pairNoEffect2)){
						hashMap.put(pairNoEffect, new ArrayList<>());
						phrase = Conditional.getCTXString(noEffectlist.get(i)) + " has no effect";
						hashMap.get(pairNoEffect).add(phrase);
						phrase = Conditional.getCTXString(noEffectlist.get(j)) + " has no effect";
						hashMap.get(pairNoEffect).add(phrase);
					}
					else{
						hashMap.get(pairNoEffect).add(phrase);
					}
				}
			}
		}		
	}

	//list of features that do not appear in the expressions
	private List<SingleFeatureExpr> getNoEffectlist(Collection<SingleFeatureExpr> features,
			List<FeatureExpr> expressions) {
		
		List<SingleFeatureExpr> noEffectlist = new ArrayList<>();
		for (SingleFeatureExpr feature : features) {
			final FeatureExpr unique = createUnique(feature, expressions);
			
			if (Conditional.isTautology(feature)) {
				continue;
			}
			
			if (Conditional.isContradiction(unique)) {//when a feature doesn't appear in the expressions
				noEffectlist.add(feature);
			}
		}
		return noEffectlist;
	}

	//get all the pairs in the expressions
	private List<PairExp> getExpressionsPairs(List<FeatureExpr> expressions) {
		List<PairExp> exprPairs = new ArrayList<>();
		List<SingleFeatureExpr> flist = new ArrayList<>();
		List<List> featuresinExpres = new ArrayList<>();
		
		for(FeatureExpr featureexpr : expressions){
			
			Set<String> dist = featureexpr.collectDistinctFeatures();
			
			if(dist.size() < 2){
				continue;
			}
			
			scala.collection.Iterator<String> fs = dist.iterator();
			flist = new ArrayList<>();
			for(int i=0; i<dist.size(); i++){
				String s = fs.next().substring(7);
				SingleFeatureExpr f  = Conditional.createFeature(s);
				flist.add(f);
				if((i == dist.size()-1) && !featuresinExpres.contains(flist)){
					//if(flist.size()>2){
						for(int j=0; j<flist.size(); j++){
							for(int k=j+1; k<flist.size(); k++){
								if(flist.get(j) == flist.get(k)){
									continue;
								}
								PairExp pairAB = new PairExp(flist.get(j), flist.get(k));			
								if (!exprPairs.contains(pairAB)){
									exprPairs.add(pairAB);
								}
							}
						}
						featuresinExpres.add(flist);
				}
			}
		}
		
		return exprPairs;
	}	

	
//	private void createExcelTable(Map<PairExp, List<String>> hashMap, Collection<SingleFeatureExpr> features) {
//	//print hash
//	for (Entry<PairExp, List<String>> pair : hashMap.entrySet()) {
//		System.out.println("Pair = [" + pair.getKey() + " , " + pair.getValue() + "]");
//	}
//	
//	 Map < String, Object[] > excelTable = new TreeMap < String, Object[] >();
//	 int count = 0;
//	 Object[] line1 = new Object[features.size()+1];
//	 line1[count++] = "Features";//array of 1 line
//	 for (SingleFeatureExpr feature1 : features) {
//		 line1[count++] = Conditional.getCTXString(feature1);
//	 }
//	 excelTable.put( Integer.toString(1), line1);//first line with the name of all features
//	 
//	 
//	 int excelline = 2;
//	 for (int i = 1; i< line1.length; i++) {
//		 count = 1;
//		 Object[] line = new Object[features.size()+1];
//		 line[0] = line1[i];
//		 for (int j = 1; j< line1.length; j++) {
//			 
//			 if(line1[j].equals(line[0])){
//				 line[count] = " X ";
//				 System.out.println("line[" + count + "] = " + line[count]);
//				 count++;
//				 continue;
//			 }
//			 
//			 for (Entry<PairExp, List<String>> pair : hashMap.entrySet()) {
//				 String A = Conditional.getCTXString(pair.getKey().A);
//				 String B = Conditional.getCTXString(pair.getKey().B);
//				 //System.out.println("A: " + A + " = " + line[0] + " && " + "B: " + B + " = " + line1[j]);
//				// System.out.println("A: " + A + " = " + line1[j] + " && " + "B: " + B + " = " + line[0]);
//				 
//				 
//				 if(A.equals(line[0]) && B.equals(line1[j])
//					|| A.equals(line1[j]) && B.equals(line[0])) {
//					 
//					 line[count] = pair.getValue().get(0);
//					 if(pair.getValue().size() >1){
//						 line[count] = "both have no effect";
//					 }
//					 
//					 System.out.println("line[" + count + "] = " + line[count]);
//					 count++;
//				 }
//			 }
//		 }
//		 
//		 excelTable.put( Integer.toString(excelline++), line);
//	 }
//	
//	Excel ex = new Excel();
//	try {
//		ex.writesheet(excelTable, new File(""));
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	
//}
}


