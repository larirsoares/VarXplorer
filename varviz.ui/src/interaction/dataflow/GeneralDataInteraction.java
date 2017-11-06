package interaction.dataflow;

import java.util.ArrayList;
import java.util.List;

import de.fosd.typechef.featureexpr.FeatureExpr;
import interaction.types.VarInteraction;

public class GeneralDataInteraction {
	
	//private List<DataVar> dataVarList;


//	public  GeneralDataInteraction(List<DataVar> dataVarList) {
//		this.dataVarList = dataVarList;
//	}
	
	public List<VarInteraction> getDATAGeneralInte(List<DataVar> dataVarList){
		
		VarInteractionControl varInt = new VarInteractionControl();
		
		List<VarInteraction> list = varInt.findGeneralDataInteractions(dataVarList);
		
		return list;
	}
	
	public List<VarInteraction> getALLInte(List<FeatureExpr> expression, List<DataVar> dataVarList){
		
		VarInteractionControl varInt = new VarInteractionControl();
		
		List<FeatureExpr> expressionALL = new ArrayList<>();
		expressionALL.addAll(expression);
		
		for(DataVar var: dataVarList){
			expressionALL.addAll( var.getCtxList());
		}
		
		//to remove repeated elements
//		HashSet hs = new HashSet();
//		hs.addAll(expressionALL);
//		expressionALL.clear();
//		expressionALL.addAll(hs);
		
		List<VarInteraction> list = varInt.findAllInteractions(expressionALL);
		
		return list;
	}

}
