package interaction.dataflow;

import java.util.List;

import interaction.types.VarInteraction;

public class GeneralDataInteraction {
	
	private List<DataVar> dataVarList;


	public  GeneralDataInteraction(List<DataVar> dataVarList) {
		this.dataVarList = dataVarList;
	}
	
	public List<VarInteraction> getGeneralInte(){
		
		VarInteractionControl varInt = new VarInteractionControl();
		
		List<VarInteraction> list = varInt.findGeneralInteractions(dataVarList);
		
		return list;
	}

}
