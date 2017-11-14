package interaction;

import java.util.ArrayList;

public class Relationship {
	 
			private ArrayList<String> vars; 
			private String relation;
			private boolean dataRelation = false;
	
			public Relationship() {
				super();
				this.vars = new ArrayList<>();
			}
			
			
			public String getRelation() {
				return relation;
			}
			public void setRelation(String relation) {
				this.relation = relation;
			}

			
			public ArrayList<String> getVars() { return vars;}
			public void setVars(String var) { this.vars.add(var);}
			
			public boolean isDataRelation() { return dataRelation;}
			public void setDataRelation(boolean dataRelation) {
				this.dataRelation = dataRelation;
			}
			
			@Override
			public String toString() {
				return relation;
			}
		
}
