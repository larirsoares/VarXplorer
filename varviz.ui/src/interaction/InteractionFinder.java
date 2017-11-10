package interaction;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cmu.conditional.Conditional;
import cmu.varviz.trace.Edge;
import cmu.varviz.trace.Method;
import cmu.varviz.trace.MethodElement;
import cmu.varviz.trace.Statement;
import cmu.vatrace.FieldPutStatement;
import cmu.vatrace.LocalStoreStatement;
import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.SingleFeatureExpr;
import interaction.controlflow.ControlflowControl;
import interaction.dataflow.DataVar;
import interaction.dataflow.GeneralDataInteraction;
import interaction.dataflow.VarInteractionControl;
import interaction.spec.SpecControl;
import interaction.spec.Specification;
import interaction.types.ControInteraction;
import interaction.types.DataInteraction;
import interaction.types.VarInteraction;
import interaction.view.InteractGraph;
import scala.collection.immutable.Set;

/**
 * has...
 * 
 * @author Larissa Rocha
 *  
 */

public class InteractionFinder {
	
	List<LocalStoreStatement> varList = new ArrayList<>();
	List<DataVar> dataVarList = new ArrayList<>();	//list with all the vars and their expressions
	List<FeatureExpr> dataExpressions = new ArrayList<>();//list with all data expressions
	private ArrayList<Specification> specList = new ArrayList<>();
	
	private Method<?> mainMethod;
	private List<Edge> edges;
	
	public InteractionFinder(Method<?> mainMethod, List<Edge> ed) {
		super();
		this.mainMethod = mainMethod;
		this.edges = ed;
	}

	public ArrayList<Specification> getSpecList() {
		return specList;
	}
	
	public void findInteractions(File workingDir){
		//gets all the variables and its context (presence conditions)
		collectVarExpressions();
		
		//getting all expressions from data
		for(DataVar var: dataVarList){
			dataExpressions.addAll( var.getCtxList());
		}
		
		//get data flow interactions in general
		GeneralDataInteraction generalDataI = new GeneralDataInteraction();
		List<VarInteraction> generalInteractionDataList = generalDataI.getDATAGeneralInte(dataVarList);
		
		//to analyze suppress and enable only in vars
		VarInteractionControl varInt = new VarInteractionControl();
		List<VarInteraction> interactionsPerVarList = varInt.findInteractionsPerVar(dataVarList);
		
		//control and data flow analysis
		getImplications(workingDir,interactionsPerVarList,generalInteractionDataList);
	}
	
//	public void getImplications(File workingDir, List<VarInteraction> interactionsPerVarList, List<VarInteraction> generalInteractionDataList) {
//		
//		List<String> featureVars = new ArrayList<>();
//		List<DataInteraction> DataInteracList = new ArrayList<>();
//		List<List> allVars = new ArrayList<>();
//		Statement<?> END = VarvizView.TRACE.getEND();
//		
//		DataFlowControl dataControl = new DataFlowControl();
//		
//		List<FeatureExpr> expressions = new ArrayList<>();		
//		for (Edge edge : this.edges) {
//			FeatureExpr ctx = edge.getCtx();
//			//System.out.println("edge: " + edge);
//			
//			if (!ctx.isTautology()) {				
//					dataControl.getDataInteraction(edge, DataInteracList, END);
//					DataInteracList = dataControl.getDataInteracList();
//				
//				//acho que talvez msm se já conter a expressao tem tb q testar se a variavel já existe
//				//tenho que rever isso aqui
//				if (!expressions.contains(ctx)) {
//					expressions.add(ctx);
//					System.out.println(ctx);			
//					
//					getFeatureVars(ctx, edge, featureVars, allVars, END);
//				}else{
//					System.out.println(ctx);			
//					
//					getFeatureVars(ctx, edge, featureVars, allVars, END);
//				}
//			}
//		}
//			
//		//analyzing all interactions together: control + data exp
//		GeneralDataInteraction generalDataI = new GeneralDataInteraction();
//		List<VarInteraction> generalInteractionALLList = generalDataI.getALLInte(expressions, dataVarList);
//		List<FeatureExpr> allAxpressions = new ArrayList<>();	
//		allAxpressions = generalDataI.getExpressionALL();
//		
//		ControlflowControl finder = new ControlflowControl();
//		//Map<PairExp, List<String>> hashMap = finder.getInteractionsTable(expressions);
//		Map<PairExp, List<String>> hashMap = finder.getInteractionsTable(allAxpressions);//has everybody that interacts
//		
//		//relationships from presence conditions
//		List<ControInteraction> controlFlowInteracList = finder.getInteractionList();
//		
//		//Examples of specifications	
//		//setSpecification(finder, "sign", "addressbook");
//		//setSpecification(finder, "decrypt", "addressbook");
//		//setSpecification(finder, "decrypt", "encrypt");
//	
//		
//		InteractGraph g = new InteractGraph(DataInteracList,interactionsPerVarList,controlFlowInteracList);		
//		g.createGraphInter(hashMap, finder.getFeatures(), finder.getNoEffectlist(), expressions, workingDir, allVars, specList);	
//	}

	public void getImplications(File workingDir, List<VarInteraction> interactionsPerVarList, List<VarInteraction> generalInteractionDataList) {
		//apagar depois
		List<DataInteraction> DataInteracList = new ArrayList<>();
		List<List> allVars = new ArrayList<>();
		
		List<FeatureExpr> expressions = new ArrayList<>();		
		for (Edge edge : this.edges) {
			FeatureExpr ctx = edge.getCtx();
			
			if (!ctx.isTautology()) {				
				if (!expressions.contains(ctx)) {
					expressions.add(ctx);
					System.out.println(ctx);								
				}
			}
		}
			
		//analyzing all interactions together: control + data exp
		GeneralDataInteraction generalDataI = new GeneralDataInteraction();
		List<VarInteraction> generalInteractionALLList = generalDataI.getALLInte(expressions, dataVarList);
		List<FeatureExpr> allAxpressions = new ArrayList<>();	
		allAxpressions = generalDataI.getExpressionALL();
		
		ControlflowControl finder = new ControlflowControl();
		//Map<PairExp, List<String>> hashMap = finder.getInteractionsTable(expressions);
		Map<PairExp, List<String>> hashMap = finder.getInteractionsTable(allAxpressions);//has everybody that interacts
		
		//relationships from presence conditions
		List<ControInteraction> controlFlowInteracList = finder.getInteractionList();
		
		//Examples of specifications	
		setSpecification(finder, "S", "F");
		//setSpecification(finder, "decrypt", "addressbook");
		//setSpecification(finder, "decrypt", "encrypt");
	
		
		InteractGraph g = new InteractGraph(DataInteracList,interactionsPerVarList,controlFlowInteracList);		
		g.createGraphInter(hashMap, finder.getFeatures(), finder.getNoEffectlist(), expressions, workingDir, allVars, specList);	
	}
	
	private void setSpecification(ControlflowControl finder, String s1, String s2) {
		SpecControl specControl = new SpecControl();
		Collection<SingleFeatureExpr> feat = finder.getFeatures();
		
		SingleFeatureExpr[] a = new SingleFeatureExpr[2];
		int i = 0;
		for (SingleFeatureExpr feature1 : feat) {
			
			if(Conditional.getCTXString(feature1).equals(s1)){
				a[i] = feature1;
				i++;
			}
			else if(Conditional.getCTXString(feature1).equals(s2)){
				a[i] = feature1;
				i++;
			}
		}
		specList.add(specControl.createAllow(a[0], a[1]));
		
	}

	//method to get the var of the control flow
	private void getFeatureVars(FeatureExpr ctx, Edge edge, List<String> featureVars, List<List> allVars, Statement<?> eND) {

		String ctxString = Conditional.getCTXString(ctx);
		featureVars = new ArrayList<>();
		Statement<?> s = edge.getTo();
		
		if(s.equals(eND)){
			return;
		}

		if(ctx.size()>1 && checkExpression(edge)) {

			System.out.println(s.toString());

			if(!(s.toString().contains("if (")) && !(s.toString().contains("return ")) && (s.to.toList().get(1)!=null)){

				if(s.to.size()>1 && !(s.to.toList().get(1).toString().contains("if ("))){
					featureVars.add(ctxString);
					System.out.println("Expr: " + ctxString);
					featureVars.add(s.toString());
					System.out.println("Overwritten Var: " + s.toString());

					if (!s.to.toList().get(1).equals(eND)){
						System.out.println(s.getTo() );
						Statement<?> nextVar = s.getTo().toList().get(1);

						if(nextVar.getCTX().equivalentTo(ctx)){//.equivalentTo(s.getCTX())){
							if(!s.to.toList().get(1).toString().contains("return ")){													
								featureVars.add( s.to.toList().get(1).toString());
								System.out.println("Overwritten Var: " + s.to.toList().get(1).toString());
							}
						}
					}
					allVars.add(featureVars);
				}
				else if (s.to.size() == 1 && !s.equals(eND)){
					featureVars.add(ctxString);
					featureVars.add(s.toString());
					System.out.println("Overwritten Var: " + s.toString());

					if (!s.to.toList().get(0).equals(eND)){
						System.out.println(s.getTo() );
						Statement<?> nextVar = s.getTo().toList().get(0);

						if(nextVar.getCTX().equivalentTo(s.getCTX())){
							featureVars.add( s.to.toList().get(0).toString());
							System.out.println("Overwritten Var: " + s.to.toList().get(0).toString());
						}
					}

					allVars.add(featureVars);
				}

			}
		}

	}

	//check if the expression is an AND between features to get the variables of control-flow
	private Boolean checkExpression(Edge edge) {
		FeatureExpr ctx = edge.getCtx();
		Statement<?> s = edge.getTo();

		Set<String> edgef = ctx.collectDistinctFeatures();
		scala.collection.Iterator<String> fs = edgef.iterator();

		List<FeatureExpr> listUniqueExp =  new ArrayList<>();

		String uniqueExp = fs.next().substring(7);
		FeatureExpr f  = Conditional.createFeature(uniqueExp);
		System.out.println(edgef.size());
		for(int i=1; i<edgef.size();i++){
			f =  f.and( Conditional.createFeature(fs.next().substring(7)) ); 
		}
		listUniqueExp.add(f);//has the "and expressions"
		return ctx.equivalentTo(f);

	}

	//getting all the variables of the trace
	public void collectVarExpressions() {
		List<MethodElement<?>> children = this.mainMethod.getChildren();
		recursiveMethod(children.get(0));	
		
	}
	private void recursiveMethod(MethodElement<?> methodElement){
		
			if (methodElement instanceof Method) {
				System.out.println(methodElement);
				List a = ((Method) methodElement).getChildren();
				
				for(int i=0; i<a.size(); i++){
					recursiveMethod((MethodElement<?>) a.get(i));
				}
			} else {
				if(methodElement instanceof LocalStoreStatement || methodElement instanceof FieldPutStatement){
					//varList.add((LocalStoreStatement) methodElement);
					Statement var = (Statement) methodElement;
					String name = var.toString();
					name = name.substring(0,name.length()-3);
					Conditional<String> value = var.getValue();
					for ( Entry<String, FeatureExpr> v: value.toMap().entrySet()){
						DataVar data = new DataVar(name, v.getValue());
						if(!containsData(data,v.getValue())){
							dataVarList.add(data);
							System.out.println("Var: " + data.getName() + " value: " + v.getValue());
						}
					}
			
				}
			}
	}

	//used by the recursive method to see if dataVarList has the variable and its context
	private boolean containsData(DataVar data, FeatureExpr featureExpr) {
		
		for(int i=0; i<dataVarList.size(); i++){
			if(dataVarList.get(i).getName().equals(data.getName())){
				if(!dataVarList.get(i).hascontext(featureExpr)){
					dataVarList.get(i).setContext(featureExpr);
					System.out.println("Var: " + dataVarList.get(i).getName() + " value: " + featureExpr);
				}
				return true;
			}
		}
		return false;
	}
	




}


