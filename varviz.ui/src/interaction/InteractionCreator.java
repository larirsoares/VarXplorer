package interaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cmu.conditional.Conditional;
import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.SingleFeatureExpr;
import interaction.spec.Specification;
import interaction.spec.Specification.Relationship;
import interaction.spec.Specification.Type;
import interaction.types.VarInteraction;

public class InteractionCreator {
	
	private ArrayList<Interaction> interactionsList;
	
	private List<SingleFeatureExpr> noEffectlist;
	private List<SingleFeatureExpr> doNotInterctList;
	private Map<PairExp, List<String>> hashMap;
	private Collection<SingleFeatureExpr> features;
	private List<FeatureExpr> expressions;
	private List<VarInteraction> interactionsPerVarList;
	private ArrayList<Specification> specList;
	private ArrayList<Interaction> removedInteractions;


	public Collection<SingleFeatureExpr> getFeatures() { return features;}
	public List<SingleFeatureExpr> getNoEffectlist() {	return this.noEffectlist;}
	public void setNoEffectlist(List<SingleFeatureExpr> noEffectlist) { this.noEffectlist = noEffectlist;}

	public List<SingleFeatureExpr> getDoNotInterctList() {return this.doNotInterctList;}
	public void setDoNotInterctList(List<SingleFeatureExpr> doNotInterctList) {	this.doNotInterctList = doNotInterctList;}
	
	public ArrayList<Interaction> getRemovedInteractions() {	return this.removedInteractions;}

	//constructor
	public InteractionCreator(Map<PairExp, List<String>> hashMap, Collection<SingleFeatureExpr> features,
			List<SingleFeatureExpr> noEffectlist, List<FeatureExpr> expressions,
			List<VarInteraction> interactionsPerVarList, ArrayList<Specification> specList) {
		
		this.features = features;
		this.noEffectlist = noEffectlist;
		this.hashMap = hashMap;
		this.expressions = expressions;
		this.interactionsPerVarList = interactionsPerVarList;
		this.specList = specList;
		
		this.interactionsList = new ArrayList<>();
		this.removedInteractions = new ArrayList<>();
	}

	public ArrayList<Interaction> getInteractionsToShow(){
		
		String A = "";
		String B = "";
		
		for (Entry<PairExp, List<String>> pair : hashMap.entrySet()) {					
			
				A = Conditional.getCTXString(pair.getKey().getA());
				B = Conditional.getCTXString(pair.getKey().getB());
				
				List<String> l = pair.getValue();
				for(String exp: l){
					if(exp.contains("not interact")){
						continue;
					}
					
					if(exp.contains("interact")){

						getPerVar(A, B, pair);
					}
					if(exp.contains("requires")){
						if(treatSpec(pair, hashMap)){
							continue;
						}	
						
						getPerRelation(A, B, pair, exp, "Require");
					
					}
					else if(exp.contains("suppresses")){
						
						getPerRelation(A, B, pair, exp, "Suppress");
					}
				}		
		}
		//getting features that have effect but do not interact
		getNoInteractFeatures();
		return interactionsList;
	}
	
	private void getPerRelation(String A, String B, Entry<PairExp, List<String>> pair, String exp, String relationType) {
		Interaction iinteraction = new Interaction();	
		interaction.Relationship relation = new interaction.Relationship();
		
		for(VarInteraction vars: this.interactionsPerVarList){				
			if(vars.getExp().contains(A) && vars.getExp().contains(B)){
				if(!checkSpecVar(pair, vars.getVarName())){
					relation.setVars(vars.getVarName());
				}
			}
		}
		//create A to B
		if(exp.startsWith(A)){									
			iinteraction.setPair(pair.getKey().getA(), pair.getKey().getB());						
		}
		//create B to A
		else{						
			iinteraction.setPair(pair.getKey().getB(), pair.getKey().getA());
		}
		relation.setRelation(relationType);
		iinteraction.setRelation(relation);
		interactionsList.add(iinteraction);
		
	}
	private void getPerVar(String A, String B, Entry<PairExp, List<String>> pair) {

		int flag = 0;
		for(VarInteraction vars: this.interactionsPerVarList){
				
			if(vars.getExp().contains(A) && vars.getExp().contains(B)){
				flag = 1;
				//check spec per var
				if(checkSpecVar(pair, vars.getVarName())){
					continue;
				}
				
				Interaction iinteraction = new Interaction();
				interaction.Relationship relation = new interaction.Relationship();
				relation.setVars(vars.getVarName());
				
				//create A to B
				if(vars.getExp().startsWith(A)){									
					iinteraction.setPair(pair.getKey().getA(), pair.getKey().getB());						
				}
				//create B to A
				else{						
					iinteraction.setPair(pair.getKey().getB(), pair.getKey().getA());
				}			
				
				if(vars.getExp().contains("suppress")){									
					relation.setRelation("Suppress");	
					relation.setDataRelation(true);
				}
				else if(vars.getExp().contains("require")){
					relation.setRelation("Require");
					relation.setDataRelation(true);
				}else{
					System.out.println("WARNING: it has variable but none relationship");
				}
				iinteraction.setRelation(relation);
				interactionsList.add(iinteraction);
			}
		}
		
		//then, they interact but do not have any variable involved 
		if(flag==0){
			Interaction iinteraction = new Interaction();
			iinteraction.setPair(pair.getKey().getA(), pair.getKey().getB());
			interactionsList.add(iinteraction);
		}
		
	}
	//getting all features that have effect but do not interact
	private void getNoInteractFeatures(){
	
		doNotInterctList = new ArrayList<SingleFeatureExpr>(features);
		ArrayList<SingleFeatureExpr> featuresInteract = new ArrayList<>();
		
		for (SingleFeatureExpr feature1 : features) {
			if(noEffectlist.contains(feature1)){
				doNotInterctList.remove(feature1);
				continue;
			}
		
			for(Interaction inter: interactionsList){
				if(inter.getPair().getA().equivalentTo(feature1) || inter.getPair().getB().equivalentTo(feature1)){
					if(!featuresInteract.contains(feature1)){
						featuresInteract.add(feature1);
					}
				}
			}
		}
				
		doNotInterctList.removeAll(featuresInteract);		
	
	}
	
	private boolean checkSpecVar(Entry<PairExp, List<String>> pair, String varName) {
		
		for (Specification spec: specList){
			if(spec.getPair().equals(pair.getKey())){
				
				if(spec.getType().equals(Type.Allow)){
					if(spec.getVar() != null && spec.getVar().equals(varName)){
						Interaction iinteraction = new Interaction();
						iinteraction.setPair(pair.getKey().getA(), pair.getKey().getB());
						interaction.Relationship relation = new interaction.Relationship();
						
						relation.setRelation(spec.getRelation().getName());
						relation.setVars(varName);
						iinteraction.setRelation(relation);
						removedInteractions.add(iinteraction);
						return true;
					}
				}else{
					//forbided interaction
				}
			}
		}
		return false;
	}
	
	//remove interactions when specifications
	private boolean treatSpec(Entry<PairExp, List<String>> pair, Map<PairExp, List<String>> hashMap) {
		for (Specification spec: specList){
			if(spec.getPair().equals(pair.getKey())){
				//allow everything
				if(spec.getType().equals(Type.Allow)){
					if(spec.getRelation() == null){
						hashMap.remove(pair);
						
						Interaction iinteraction = new Interaction();
						iinteraction.setPair(pair.getKey().getA(), pair.getKey().getB());
						removedInteractions.add(iinteraction);
						return true;
					}else{
						//requires everything
						if(spec.getRelation().equals(Relationship.Require)){
							if(spec.getVar() == null){
								hashMap.remove(pair);
								
								Interaction iinteraction = new Interaction();
								iinteraction.setPair(pair.getKey().getA(), pair.getKey().getB());
								interaction.Relationship relation = new interaction.Relationship();
								relation.setRelation("Require");
								iinteraction.setRelation(relation);
								removedInteractions.add(iinteraction);
								return true;
							}else{

							}
						//suppress everything
						}else if(spec.getRelation().equals(Relationship.Suppress)) {
							if(spec.getVar() == null){
								hashMap.remove(pair);
								
								Interaction iinteraction = new Interaction();
								iinteraction.setPair(pair.getKey().getA(), pair.getKey().getB());
								interaction.Relationship relation = new interaction.Relationship();
								relation.setRelation("Suppress");
								iinteraction.setRelation(relation);
								removedInteractions.add(iinteraction);
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
}

