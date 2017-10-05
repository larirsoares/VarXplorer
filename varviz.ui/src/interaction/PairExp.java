package interaction;

import cmu.conditional.Conditional;
import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.SingleFeatureExpr;

public class PairExp {

		FeatureExpr A, B;

		public PairExp(SingleFeatureExpr a, SingleFeatureExpr b) {
			A = a;
			B = b;
		}
		public FeatureExpr getA() {
			return A;
		}
		public FeatureExpr getB() {
			return B;
		}
		
		@Override
		public boolean equals(Object obj) {
			PairExp other =  (PairExp) obj;
			return (A.equals(other.A) && B.equals(other.B)) ||
					(A.equals(other.B) && B.equals(other.A));
		}
		
		@Override
		public int hashCode() {
			return A.hashCode() * B.hashCode() * 31;
		}
		
		@Override
		public String toString() {
			return Conditional.getCTXString(A) + " , " + Conditional.getCTXString(B);
		}
		
		public String toStringA(){
			return Conditional.getCTXString(A);
		}
		public String toStringB(){
			return Conditional.getCTXString(B);
		}
	
}
