package interaction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cmu.conditional.Conditional;
import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.SingleFeatureExpr;

public class GraphFile {

	private List<String> FileList = new ArrayList<>();
	private String [] featuresList;
	private List<String> varsToFileList = new ArrayList<>();
	private List<String> numberVarsList = new ArrayList<>();
	
	public void createFile(ArrayList<Interaction> finalList, InteractionCreator resultingGraph, File workingDir, List<FeatureExpr> expressions) {
				

		featuresList = new String[resultingGraph.getFeatures().size()];
		
		FileList.add("noEffectF:");
		int iList = 0;
		for (SingleFeatureExpr feature1 : resultingGraph.getNoEffectlist()) {
			 String f = Conditional.getCTXString(feature1);
			 FileList.add(f);
			 featuresList[iList++] = f;
			// g.addNode(new Node(f).setStyle(Style.Node.dashed).setColor(Color.X11.grey).setFontColor(Color.X11.gray)); 
		}
		
		//creating circles and dashed circles
		FileList.add("EffectF:");
		for (SingleFeatureExpr feature1 : resultingGraph.getDoNotInterctList()) {
			 String f = Conditional.getCTXString(feature1);
					 
			if(!Conditional.isTautology(feature1)){
					//g.addNode(new Node(f));
				 	FileList.add(f);
				 	featuresList[iList++] = f;
			}
		}
		
		//adding the other features
		for (SingleFeatureExpr feature1 : resultingGraph.getFeatures()) {
			String f = Conditional.getCTXString(feature1);
			if(!FileList.contains(f)){
				FileList.add(f);
				featuresList[iList++] = f;
			}
		
		}	
		
		FileList.add("EdgesF:");
		for(Interaction inter: finalList){
			String A = Conditional.getCTXString(inter.getPair().getA());
			String B = Conditional.getCTXString(inter.getPair().getB());
			int idA = 0;
			int idB = 0;
			for(int i=0; i<featuresList.length;i++){
				if(featuresList[i].equals(A)){
					idA = i;
				}
				else if(featuresList[i].equals(B)){
					idB = i;
				}
			}
			
			String relation = inter.getRelations().get(0).getRelation();
			String shownVars = "";
			
			for(String var: inter.getRelations().get(0).getVars()){
				if(shownVars!=""){
					shownVars += "\n";
				}
				
				if(!shownVars.contains(var)){
					shownVars += var;
				}
			}
					
			if(relation.equals("Require")){
				//save the vars to the file
				getVarsToFile(idA, idB, "require", shownVars);
				
				//Edge edge = new Edge(A,B);
				FileList.add(Integer.toString(idA));
				FileList.add(Integer.toString(idB));
				FileList.add("requires");
				
			}else if(relation.equals("Suppress")){
				getVarsToFile(idA, idB, "suppress", shownVars);			
				
				//Edge edge = new Edge(A,B);
				FileList.add(Integer.toString(idA));
				FileList.add(Integer.toString(idB));
				FileList.add("suppresses");

			}else{
				getVarsToFile(idA, idB, "no relationship", shownVars);
					
			}
		}	
		
		//adding vars to file
		FileList.add("VarF:");
		FileList.addAll(varsToFileList);
		
		//adding number of vars to file
		FileList.add("NumberVarPerEdge:");
		FileList.addAll(numberVarsList);

		
		 
	}
	
	//get the vars to save in the file
	private void getVarsToFile(int idA, int idB, String type, String shownVars) {
		String[] eachvar = shownVars.split("\n");
	
		for(int i=0; i<eachvar.length;i++){
			String var = eachvar[i];
			
			String s1 = Integer.toString(idA) + " " + Integer.toString(idB) + " " + type + " " + var;
			String s2 = Integer.toString(idA) + " " + Integer.toString(idB) + " " + eachvar.length + " " + type;
			if(!varsToFileList.contains(s1)){
				varsToFileList.add(s1);
			}
			if(!numberVarsList.contains(s2)){
				numberVarsList.add(s2);
			}		
			
			System.out.println("F " + idA + "and F " + idB + " var:" +var);
			
		}				
	}
	
	public void write(ArrayList<Interaction> finalList, InteractionCreator resultingGraph, File workingDir, List<FeatureExpr> expressionscontent) throws UnsupportedEncodingException, FileNotFoundException, IOException{

		BufferedWriter writer = null;
        try {

            File logFile = new File("/Users/larissasoares/git/fork/varviz/varviz.ui/src/interaction/web/" + "VarXPlorerTest.txt");

            // This will output the full path where the file will be written to...
            System.out.println(logFile.getCanonicalPath());

            writer = new BufferedWriter(new FileWriter(logFile));
            
            for(String var: FileList){
            	 writer.write(var + "\n");
            }
           
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
	}
}
