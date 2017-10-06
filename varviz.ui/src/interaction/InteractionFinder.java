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
import cmu.varviz.trace.view.VarvizView;
import cmu.vatrace.LocalStoreStatement;
import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.SingleFeatureExpr;
import interaction.controlflow.ControlflowControl;
import interaction.dataflow.DataFlowControl;
import interaction.dataflow.DataInteraction;
import interaction.dataflow.DataVar;
import interaction.spec.SpecControl;
import interaction.spec.Specification;
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
	List<DataVar> dataVarList = new ArrayList<>();
	
	private ArrayList<Specification> specList = new ArrayList<>();
	
	public ArrayList<Specification> getSpecList() {
		return specList;
	}

	public void getImplications(List<Edge> edges, File workingDir) {
		
		List<String> featureVars = new ArrayList<>();
		List<DataInteraction> DataInteracList = new ArrayList<>();
		List<List> allVars = new ArrayList<>();
		Statement<?> END = VarvizView.TRACE.getEND();
		
		DataFlowControl dataControl = new DataFlowControl();
		
		List<FeatureExpr> expressions = new ArrayList<>();		
		for (Edge edge : edges) {
			FeatureExpr ctx = edge.getCtx();
			System.out.println("edge: " + edge);
			int a = 1;
			if (!ctx.isTautology()) {
				
				dataControl.getDataInteraction(edge, DataInteracList, END);
				DataInteracList = dataControl.getDataInteracList();
			}
			
			if (!expressions.contains(ctx) && !ctx.isTautology()) {
				expressions.add(ctx);
				System.out.println("");
				System.out.println(ctx);			
				
				getFeatureVars(ctx, edge, featureVars, allVars, END);
			} 
		}
		ControlflowControl finder = new ControlflowControl();
			
		Map<PairExp, List<String>> hashMap = finder.getInteractionsTable(expressions);	
		//setSpecification(finder, "sign", "addressbook");
		//setSpecification(finder, "decrypt", "addressbook");
		//setSpecification(finder, "decrypt", "encrypt");
		
		InteractGraph g = new InteractGraph();		
		g.createGraphInter(hashMap, finder.getFeatures(), finder.getNoEffectlist(), expressions, workingDir, allVars, DataInteracList, specList);	
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

	private void getFeatureVars(FeatureExpr ctx, Edge edge, List<String> featureVars, List<List> allVars, Statement<?> eND) {

		String ctxString = Conditional.getCTXString(ctx);
		featureVars = new ArrayList<>();
		Statement<?> s = edge.getTo();

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

						if(nextVar.getCTX().equivalentTo(s.getCTX())){
							featureVars.add( s.to.toList().get(1).toString());
							System.out.println("Overwritten Var: " + s.to.toList().get(1).toString());
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

	
	public void collectVarExpressions(Method<?> mainMethod) {
		List<MethodElement<?>> children = mainMethod.getChildren();
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
				if(methodElement instanceof LocalStoreStatement){
					varList.add((LocalStoreStatement) methodElement);
					LocalStoreStatement var = (LocalStoreStatement) methodElement;
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


