package interaction.dataflow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import cmu.conditional.Conditional;
import cmu.varviz.trace.Edge;
import cmu.varviz.trace.Statement;
import de.fosd.typechef.featureexpr.FeatureExpr;
import interaction.types.DataInteraction;
import scala.collection.immutable.Set;

/**
 * has...
 * 
 * @author Larissa Rocha
 *  
 */

public class DataFlowControl {
	
	List<DataInteraction> dataInteracList;

	public List<DataInteraction> getDataInteracList() {
		return dataInteracList;
	}

	public void getDataInteraction(Edge edge, List<DataInteraction> dataInteracList, Statement eND) {
		
		this.dataInteracList = dataInteracList;
		
		FeatureExpr ctx = edge.getCtx();
		Statement s = (Statement) edge.getTo();// TODO returns also methods
		FeatureExpr sCtx = s.getCTX();	
		Conditional<?> value = s.getValue();
		
//		System.out.println("pq dá pau aqui s: " + s);
//		System.out.println("pq dá pau aqui s.to: " + s.to);

//		SingleFeatureExpr a = Conditional.createFeature("decrypt");
//		SingleFeatureExpr b = Conditional.createFeature("encrypt");
//		SingleFeatureExpr c = Conditional.createFeature("f1");
//	    List<FeatureExpr> sourceList = new ArrayList<>();
//	    List<FeatureExpr> destinationList = new ArrayList<>();
//	    sourceList.add(a); sourceList.add(b);
//	    destinationList.add(b); destinationList.add(a);
//	    equalsList(sourceList, destinationList);
//	    destinationList.remove(b); destinationList.add(c);
//	    equalsList(sourceList, destinationList);
	    //--------------

		if(s==null || s.equals(eND)){
			return;
		}
		else if(s.to==null){
			return;
		}
		else if(s.to.toList().get(1)==null){
			return;
		}
		else if(s.toString().contains("if (") || s.toString().contains("return ")){
			return;
		}
		else if(s.to.toList().get(1).equals(eND)){
			System.out.println("It's the END, but...");
		}
		boolean flag = false;
		
		DataInteraction interaction = null;
		String var = s.toString();
		
		for (Entry<?, FeatureExpr> e : value.toMap().entrySet()) {
			FeatureExpr context = e.getValue();
			//System.out.println("context from value: " + context + " and ctx: " + ctx );
			//se entrar aqui é porque a var depende de mais F do que a edge. 
			if(context.collectDistinctFeatures().size()>ctx.size()){

				if(interaction!=null && interaction.getFeatures().equals(getFeaturesFromContext(context))){					
					//return;
					//aqui é a msm var com o msm contexto, olhar se a expressao é diferente
					
					//primeiro saber se a interação atual já tem essa expressão;
					if(!interaction.getDataVar(var.toString()).hascontext(context)){
						interaction.getDataVar(var.toString()).setContext(context);
						updateDataInteractionList(interaction);
						continue;
					}
					continue;
				}
				
				
				DataInteraction list = isInteractionNew(dataInteracList, context);
				flag = true;
				
				interaction = createInteraction(context);
				if(interaction==null){
					return;
				}
				
				
				interaction.setVar(var, context);
				System.out.println("data interaction: " + s.toString());
				
				if(list!=null){//se a interação existir
					updateDataInteractionList(interaction);
				}
				else{	
					DataInteraction newInteraction = createInteraction(context);
					newInteraction.setVar(var, context);
					dataInteracList.add(newInteraction);	
				}
				getNextVars(interaction, s, ctx, sCtx, eND);				
				
			}				
		}
		if(flag){
			if(s.getTo().toList().get(1).toString().contains("if (") || s.to.toList().get(1).equals(eND)  ){
				return;
			}
			value = ((Statement) s.getTo().toList().get(1)).getValue();
			for (Entry<?, FeatureExpr> e : value.toMap().entrySet()) {
				FeatureExpr context = e.getValue();
				System.out.println(context);
				//se entrar aqui é porque a var depende de mais F do que a edge. 
				if(context.collectDistinctFeatures().size()>ctx.size()){
					System.out.println("falta fazer aqui");
				}
			}
		}
		
	}
	
	private DataInteraction createInteraction(FeatureExpr context) {
		
		DataInteraction interaction = null;
		Set<String> dataF = context.collectDistinctFeatures();
		scala.collection.Iterator<String> dataI = dataF.iterator();
		
		if(dataF.size()==2){
			return interaction = new DataInteraction(Conditional.createFeature(dataI.next().substring(7)), 
											  Conditional.createFeature(dataI.next().substring(7)));
		}
		else if(dataF.size()==3){
			return interaction = new DataInteraction(Conditional.createFeature(dataI.next().substring(7)), 
											  Conditional.createFeature(dataI.next().substring(7)),
											  Conditional.createFeature(dataI.next().substring(7)));
		}
		else if(dataF.size()>=3){
			System.out.println("WARNING: data interaction higher than 3: " + context);
			return null;
		}
		return null;
		
	}

	private void getNextVars(DataInteraction interaction, Statement s, FeatureExpr ctx, FeatureExpr sCtx, Statement eND) {

			//s.to = {¬decrypt:null ; decrypt:int Email.encryptionKey =  }
			if(s.to.size()>1 &&
				!(s.to.toList().get(1).toString().contains("if (")) && !s.to.toList().get(1).equals(eND) && !(s.to.toList().get(1).toString().contains("return "))){
				
				Conditional<?> varValue = ((Statement) s.getTo().toList().get(1)).getValue();
				//Statement<?> nextVar2 = s.getTo().toList().get(0);
				
				for (Entry<?, FeatureExpr> e : varValue.toMap().entrySet()) {
					FeatureExpr context = e.getValue();
					if(context.collectDistinctFeatures().size()>ctx.size()){
						//verify if interaction already exists
						DataInteraction list = isInteractionNew(dataInteracList, context);						
						Statement var = (Statement) s.to.toList().get(1);
						boolean sameContext = interaction.getFeatures().equals(getFeaturesFromContext(context));
						int text = 0;
						//no caso de a variavel nova ter o mesmo contexto da primeira
						if(sameContext && isVarDifferent(interaction, var.toString())){			
							interaction.setVar(var.toString(), context);
							System.out.println("data interaction: " + var.toString());
						
							updateDataInteractionList(interaction);
							continue;
							
						}
						//se for a msm var com o msm contexto, olhar se a expressao é diferente
						if(sameContext && !isVarDifferent(interaction, var.toString())){
							//primeiro saber se a interação atual já tem essa expressão;
							if(!interaction.getDataVar(var.toString()).hascontext(context)){
								interaction.getDataVar(var.toString()).setContext(context);
								updateDataInteractionList(interaction);
								continue;
							}
							continue;
						}
						
						//se o contexto for diferente e a interação já existir
						else if(!sameContext && list!=null){
							//aqui eu tenho que checar se a var já existe tb
							if(!list.containsVar(var.toString())){
								list.setVar(var.toString(), context);
								System.out.println("data interaction: " + var.toString());						
								updateDataInteractionList(list);
								continue;
							}else{
								//se conter a var, então saber se contém a exp
								if(!list.getDataVar(var.toString()).hascontext(context)){
									interaction.getDataVar(var.toString()).setContext(context);
									updateDataInteractionList(interaction);
									continue;
								}
								continue;//ja tem a expressão e já tem a var
								
							}
							
							
						}
						
						//se o contexto for diferente e a interação não exisitr
						else if(list!=null && !list.getFeatures().equals(interaction.getFeatures())){
							
							DataInteraction newinteraction = createInteraction(context);
							if(newinteraction==null){
								continue;
							}							
							newinteraction.setVar(var.toString(), context);
							System.out.println("data interaction: " + var.toString());
							
							dataInteracList.add(newinteraction);
							continue;			
						}
					}
				
				}
				
				
			}else if (s.to.size() == 1 && !s.equals(eND)){	
				System.out.println("entender pq entrou aqui");
				
				if (!s.to.toList().get(0).equals(eND)){
					System.out.println(s.getTo() );
					Statement nextVar = (Statement) s.getTo().toList().get(0);
					
					if(nextVar.getCTX().equivalentTo(s.getCTX())){
						//interaction.setVar( s.to.toList().get(0).toString(), context);
						//System.out.println("data interaction: " + s.to.toList().get(0).toString());
						
					}
				}
//				Statement<?> nextVar = s.getTo().toList().get(1);
//				//Statement<?> nextVar2 = s.getTo().toList().get(0);
//				if(!nextVar.getCTX().equivalentTo(sCtx)){
//					Statement<?> a = s.to.toList().get(1);
//					dataVars.add(s.to.toList().get(1).toString());
//					System.out.println("data interaction: " + s.to.toList().get(1).toString());
//					
//					Set<String> dataF = nextVar.getCTX().collectDistinctFeatures();
//					scala.collection.Iterator<String> dataI = dataF.iterator();
//					
//					String f1 = dataI.next().substring(7);
//					FeatureExpr f  = Conditional.createFeature(f1);
//					dataFeatures.add(f);
//					System.out.println(dataF.size());
//					for(int i=1; i<dataF.size();i++){
//						dataFeatures.add(Conditional.createFeature(dataI.next().substring(7))); 
//					}
//					if(dataF.size()==2){
//						SingleFeatureExpr ff  = Conditional.createFeature(dataI.next().substring(7));
//						SingleFeatureExpr fff  = Conditional.createFeature(dataI.next().substring(7));
//						DataInteraction interaction = new DataInteraction(ff, fff);
//						interaction.setDataVars(dataVars);
//						dataInteracList.add(interaction);
//					}
//					
//				}
			}
		
		
	}

	//updating vars of an interaction in the dataInteractionList
	private void updateDataInteractionList(DataInteraction interaction) {		
		
		if(!dataInteracList.isEmpty()){
			for(int i=0; i<dataInteracList.size(); i++){
				DataInteraction interFromList = dataInteracList.get(i);
				//if(interFromList.getFeatures().equals(interaction.getFeatures())){
				if(equalsList(interFromList.getFeatures(), interaction.getFeatures())){
					//entao tenho que atualizar as variaveis de dataInteracList.get(i) com a list de "interaction"
					for(DataVar var: interaction.getDataVars()){
						if(interFromList.containsVar(var)){
							//eu tenho que saber agora se a var tem o context
							DataVar oldVar = interFromList.getDataVar(var);
							oldVar.updateCtx(var.getCtxList());
							interFromList.updateVar(oldVar);
							dataInteracList.set(i, interFromList);
						}else{
							interFromList.setVar(var.getName(), var.getCtxList());
							dataInteracList.set(i, interFromList);
						}
						
					}
				}
			}
		}
		
	}

	private boolean isVarDifferent(DataInteraction interaction, String var) {
		interaction.getDataVars();
		int a = 0;
		if(interaction==null)
			return true;
		
		for(int i = 0; i<interaction.getDataVars().size(); i++){
			if(interaction.getDataVars().get(i).getName().equals(var)){
				return false;
			}
		}
		return true;
	}


	private DataInteraction isInteractionNew(List<DataInteraction> dataInteracList, FeatureExpr context) {
		
		List<FeatureExpr> dataFeatures = new ArrayList<>();
		
		dataFeatures = getFeaturesFromContext(context);
		
		if(dataInteracList.isEmpty()){
			return null;
		}
		
		else if(!dataInteracList.isEmpty()){
			for(int i=0; i<dataInteracList.size(); i++){
				List<FeatureExpr> list = dataInteracList.get(i).getFeatures();
				//já vi que o problema tá aqui, esse equals não dá certo se a ordem estiver diferente.
				//List<FeatureExpr> listtmp = new ArrayList();			
				//listtmp.addAll(dataFeatures.);
				//listaTmp.addAll(nota1.getItens());
				//listaTmp.removeAll(nota2.getItens());
				//se eu usar o removreall nao precisaria disso
				
				if(equalsList(list, dataFeatures)){
					return dataInteracList.get(i);
				}
				
//				if(list.equals(dataFeatures)){
//					return dataInteracList.get(i);
//				}
			}
		}
		return null;
		
	}
	
	private boolean equalsList(List<FeatureExpr> list, List<FeatureExpr> dataFeatures){
		
		List<FeatureExpr> sourceList = new ArrayList<FeatureExpr>(list);
		 
		if(list.size()!=dataFeatures.size()){
			return false;
		}
		for(FeatureExpr f1: list){
			for(FeatureExpr f2: dataFeatures){
				if(f1.equivalentTo(f2)){
					sourceList.remove(f1);
				}
			}
		}
		if(sourceList.size()==0)
			return true;
		else
			return false;
	}

	private List<FeatureExpr> getFeaturesFromContext(FeatureExpr context) {
		List<FeatureExpr> dataFeatures = new ArrayList<>();
		
		Set<String> dist = context.collectDistinctFeatures();
		
		
		scala.collection.Iterator<String> dataI = dist.iterator();
		
		String f1 = dataI.next().substring(7);
		FeatureExpr f  = Conditional.createFeature(f1);
		dataFeatures.add(f);
		//System.out.println(dist.size());
		for(int i=1; i<dist.size();i++){
			dataFeatures.add(Conditional.createFeature(dataI.next().substring(7))); 
		}
		return dataFeatures;
	}
}
