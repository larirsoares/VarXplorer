package cmu.varviz.trace.filters;

import cmu.varviz.trace.Statement;

public class Or implements StatementFilter {

	private final StatementFilter[] filter;
	public Or(StatementFilter... filter) {
		this.filter = filter;
	}
	
	@Override
	public boolean filter(Statement s) {
		for (StatementFilter f : filter) {
			if (f.filter(s)) {
				return true;
			}
		}
		return false;
	}
}
