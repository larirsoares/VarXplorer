package interaction.types;

import java.util.ArrayList;
import java.util.List;

import cmu.conditional.Conditional;
import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.SingleFeatureExpr;
import interaction.dataflow.DataVar;

/**
 * has...
 * 
 * @author Larissa Rocha
 *  
 */
public class DataInteraction {	
	FeatureExpr A, B, C;
	List<DataVar> dataVarList = new ArrayList<>();
//	List<FeatureExpr> dataExpressions = new ArrayList<>();

	public FeatureExpr getA() {
		return A;
	}
	public FeatureExpr getB() {
		return B;
	}
	public FeatureExpr getC() {
		return C;
	}
	public void setA(FeatureExpr a) {
		A = a;
	}
	public void setB(FeatureExpr b) {
		B = b;
	}
	public void setC(FeatureExpr c) {
		C = c;
	}
	public List<DataVar> getDataVars() {
		return dataVarList;
	}
//	public List<FeatureExpr> getDataExpressions() {
//		return dataExpressions;
//	}
//	public void setDataExpressions(FeatureExpr exp) {
//		this.dataExpressions.add(exp);
//	}
//	public Boolean hasDataExpressions(FeatureExpr exp) {
//		this.dataExpressions.add(exp);
//		for(FeatureExpr e: this.dataExpressions){
//			if(e.equivalentTo(exp)){
//				return true;
//			}
//		}
//		return false;
//	}
	public void setVar(String var, FeatureExpr context) {
		DataVar dVar = new DataVar(var, context);
		//this.dataVars.add(var);
		this.dataVarList.add(dVar);
	}
	public void setVar(String var, List<FeatureExpr> context) {
		DataVar dVar = new DataVar(var, context);
		//this.dataVars.add(var);
		this.dataVarList.add(dVar);
	}
	public void updateVar(DataVar var) {
		for(int i=0; i<this.dataVarList.size(); i++){
			if(this.dataVarList.get(i).getName().equals(var.getName())){
				this.dataVarList.set(i, var);
			}
		}
	}
	public DataInteraction(SingleFeatureExpr a, SingleFeatureExpr b) {
		A = a;
		B = b;
	}
	public DataInteraction(SingleFeatureExpr a, SingleFeatureExpr b, SingleFeatureExpr c) {
		A = a;
		B = b;
		C = c;
	}
	public boolean containsVar(DataVar var){
		if(this.dataVarList.size()<1){
			return false;
		}
		for(int i=0; i<this.dataVarList.size(); i++){
			if(this.dataVarList.get(i).getName().equals(var.getName())){
				return true;
			}
		}
		return false;
	}
	public boolean containsVar(String varname){
		if(this.dataVarList.size()<1){
			return false;
		}
		for(int i=0; i<this.dataVarList.size(); i++){
			if(this.dataVarList.get(i).getName().equals(varname)){
				return true;
			}
		}
		return false;
	}
	public DataVar getDataVar(DataVar var){
		if(this.dataVarList.size()<1){
			return null;
		}
		for(int i=0; i<this.dataVarList.size(); i++){
			if(this.dataVarList.get(i).getName().equals(var.getName())){
				return this.dataVarList.get(i);
			}
		}
		return null;
	}
	public DataVar getDataVar(String varname){
		if(this.dataVarList.size()<1){
			return null;
		}
		for(int i=0; i<this.dataVarList.size(); i++){
			if(this.dataVarList.get(i).getName().equals(varname)){
				return this.dataVarList.get(i);
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		if(!(C==null)){
			return Conditional.getCTXString(A) + ", " + Conditional.getCTXString(B) + Conditional.getCTXString(C);
		}
		else
			return Conditional.getCTXString(A) + ", " + Conditional.getCTXString(B);
	}
	
	public List<FeatureExpr> getFeatures() {
		
		List<FeatureExpr> fList = new ArrayList<>();
		if(!(C==null)){
			fList.add(this.A);
			fList.add(this.B);
			fList.add(this.C);
			return fList;
		}
		else{
			fList.add(this.A);
			fList.add(this.B);
			return fList;
		}
	}
}

