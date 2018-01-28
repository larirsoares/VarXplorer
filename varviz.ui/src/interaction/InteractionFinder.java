package interaction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;

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
import interaction.types.VarInteraction;
import interaction.view.ClickHandler;
import interaction.view.DotGraph;
import interaction.view.InteractGraph;
import interaction.view.SpecDialog;

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
	


	public void getImplications(File workingDir, List<VarInteraction> interactionsPerVarList, List<VarInteraction> generalInteractionDataList) {
		
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
		
		Map<PairExp, List<String>> hashMap = finder.getInteractionsTable(allAxpressions);//has everybody that interacts
		
		//relationships from presence conditions
		List<ControInteraction> controlFlowInteracList = finder.getInteractionList();
				
		if(isSpecificationOn()){
			treatSpecification();
		}
	
		
		InteractGraph g = new InteractGraph(interactionsPerVarList, specList, workingDir);		
		//g.createGraphInter(hashMap, finder.getFeatures(), finder.getNoEffectlist(), expressions);	
		
		System.out.println(workingDir.getAbsolutePath());
		
		InteractionCreator resultingGraph = new InteractionCreator(hashMap, finder.getFeatures(), finder.getNoEffectlist(), expressions,
				interactionsPerVarList, specList);
		ArrayList<Interaction> finalList = resultingGraph.getInteractionsToShow();
		printFinalList(finalList, resultingGraph);
		
		//creating dot graph
		DotGraph dot = new DotGraph();
		dot.createGraph(finalList, resultingGraph, workingDir, expressions);
		GraphFile file = new GraphFile();
		file.createFile(finalList, resultingGraph, workingDir, expressions);
		try {
			file.write(finalList, resultingGraph, workingDir, expressions);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//creating JgraphX
		List<String> finalString = file.getFileList();
		List<String> edgestoGraphx = file.getEdgestoGraphx();
		createJGraphX(finalString, resultingGraph,edgestoGraphx);
	}
	


	private void treatSpecification() {
		//Examples of specifications	
				//setAllow(finder, "F", "W");
				//setReqAllow(finder, "F", "W", "String c");
				//setReqAllow(finder, "F", "W", "String weather");
				//setSupAllow(finder, "S", "F", "String c");
				//setSpecification(finder, "decrypt", "addressbook");
				//setSpecification(finder, "decrypt", "encrypt");
		
		//set allow require with var example:
		/*setReqAllow(ControlflowControl finder, String s1, String s2, String var) {
		SpecControl specControl = new SpecControl();
		
		SingleFeatureExpr[] a = getFeaturesSpec(finder, s1, s2);
		Specification spec = specControl.createAllowReq(a[0], a[1], var);
		specList.add(spec);*/
		
	}

	private Boolean isSpecificationOn() {
		
		SpecDialog dialog = new SpecDialog();
		return dialog.askSpec();
	
	}

	private void createJGraphX(List<String> finalString, InteractionCreator resultingGraph, List<String> edgestoGraphx) {
		
		List<String> noeffectGraph =  new ArrayList<>();
		for (SingleFeatureExpr feature1 : resultingGraph.getNoEffectlist()) {
			 String f = Conditional.getCTXString(feature1);
			noeffectGraph.add(f);		
		}
		
		
		//adding the other features
		List<String> featuresGraph =  new ArrayList<>();
		for (SingleFeatureExpr feature1 : resultingGraph.getFeatures()) {
			String f = Conditional.getCTXString(feature1);
			if(!noeffectGraph.contains(f)){
				featuresGraph.add(f);
				
			}
				
		}					
		
		List<List> allListGraph = new ArrayList<>();
		allListGraph.add(noeffectGraph);
		allListGraph.add(featuresGraph);
		allListGraph.add(edgestoGraphx);
		
		ClickHandler frame = new ClickHandler(allListGraph);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setVisible(true);	
		
	}

	private void printFinalList(ArrayList<Interaction> finalList, InteractionCreator resultingGraph) {
		System.out.println("");
		System.out.println("---------------All Interactions---------------");
		for(Interaction inte: finalList){
			if(inte.getRelations()!=null){
				for(Relationship r: inte.getRelations()){
					System.out.print(Conditional.getCTXString(inte.getPair().getA()) + " " + r.getRelation() + " " + Conditional.getCTXString(inte.getPair().getB()));
					if(r.getVars()!=null){
						System.out.print(": vars ");
						for(String var: r.getVars()){
							System.out.print(var + ", ");
						}
					}
				}
				System.out.println("");
			}
			else{
				System.out.println(Conditional.getCTXString(inte.getPair().getA()) + " , " + Conditional.getCTXString(inte.getPair().getB()));
			}
			
		}
		
		System.out.println("-> No interacting: " );
		for(SingleFeatureExpr feature: resultingGraph.getDoNotInterctList()){
			System.out.print(Conditional.getCTXString(feature)+ ", ");
		}
		System.out.println("\n-> No effect: ");
		for(SingleFeatureExpr feature: resultingGraph.getNoEffectlist()){
			System.out.print(Conditional.getCTXString(feature)+ ", ");
		}
		
		//printing interactions removed from spec
		System.out.println("\n------> Removed from spec:");
		if(resultingGraph.getRemovedInteractions() !=null){
			for(Interaction inte: resultingGraph.getRemovedInteractions()){
				if(inte.getRelations()!=null){
					for(Relationship r: inte.getRelations()){
						System.out.print(Conditional.getCTXString(inte.getPair().getA()) + " " + r.getRelation() + " " + Conditional.getCTXString(inte.getPair().getB()));
						if(r.getVars()!=null){
							System.out.print(": vars ");
							for(String var: r.getVars()){
								System.out.print(var + ", ");
							}
						}
					}
					System.out.println("");
				}else{
					System.out.println(Conditional.getCTXString(inte.getPair().getA()) + " , " + Conditional.getCTXString(inte.getPair().getB()));
				}
			}
		}
	
	}

	private void setAllow(ControlflowControl finder, String s1, String s2) {
		SpecControl specControl = new SpecControl();
			
		SingleFeatureExpr[] a = getFeaturesSpec(finder, s1, s2);
		specList.add(specControl.createAllow(a[0], a[1]));
		
	}
	
	private void setReqAllow(ControlflowControl finder, String s1, String s2, String var) {
		SpecControl specControl = new SpecControl();
		
		SingleFeatureExpr[] a = getFeaturesSpec(finder, s1, s2);
		Specification spec = specControl.createAllowReq(a[0], a[1], var);
		specList.add(spec);
	
	}
	
	private void setSupAllow(ControlflowControl finder, String s1, String s2, String var) {
		SpecControl specControl = new SpecControl();
		
		SingleFeatureExpr[] a = getFeaturesSpec(finder, s1, s2);
		Specification spec = specControl.createAllowSup(a[0], a[1], var);
		specList.add(spec);
	
	}

	private SingleFeatureExpr[] getFeaturesSpec(ControlflowControl finder, String s1, String s2) {
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
		return a;
		
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


