package interaction.dataflow;

import java.util.ArrayList;
import java.util.List;

import de.fosd.typechef.featureexpr.FeatureExpr;
import interaction.types.VarInteraction;

public class GeneralDataInteraction {
	
	private List<FeatureExpr> expressionALL = new ArrayList<>();
	//private List<DataVar> dataVarList;


//	public  GeneralDataInteraction(List<DataVar> dataVarList) {
//		this.dataVarList = dataVarList;
//	}
	
	public List<FeatureExpr> getExpressionALL() {
		return expressionALL;
	}

	public List<VarInteraction> getDATAGeneralInte(List<DataVar> dataVarList){
		
		VarInteractionControl varInt = new VarInteractionControl();
		
		List<VarInteraction> list = varInt.findGeneralDataInteractions(dataVarList);
		
		return list;
	}
	
	public List<VarInteraction> getALLInte(List<FeatureExpr> expression, List<DataVar> dataVarList){
		
		VarInteractionControl varInt = new VarInteractionControl();
		
		expressionALL =  new ArrayList<>();
		expressionALL.addAll(expression);
		
		List<FeatureExpr> listofAVAR;
		for(DataVar var: dataVarList){
			
			listofAVAR = var.getCtxList();
			for(FeatureExpr expVar: listofAVAR) {
				if(!expressionALL.contains(expVar)) {
					expressionALL.add(expVar);
				}
			}			
		}
		
		//to remove repeated elements PRECISO REMOVER OS REPETIDOS DE expressionAll
//		HashSet hs = new HashSet();
//		hs.addAll(expressionALL);
//		expressionALL.clear();
//		expressionALL.addAll(hs);
		System.out.println("--> Calculates all interactions (contral + data flow)" );
		List<VarInteraction> list = varInt.findAllInteractions(expressionALL);
		
		return list;
	}

}
