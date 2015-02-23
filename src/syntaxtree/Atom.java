//
// Generated by JTB 1.3.2 DIT@UoA patched
//

package syntaxtree;

/**
 * Grammar production:
 * f0 -> AtomPart()
 * f1 -> ( "=" | ">" | "<" | "<=" | ">=" )
 * f2 -> AtomPart()
 */
public class Atom implements Node {
   public AtomPart f0;
   public NodeChoice f1;
   public AtomPart f2;

   public Atom(AtomPart n0, NodeChoice n1, AtomPart n2) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
   }

   public void accept(visitor.Visitor v) {
      v.visit(this);
   }
   public <R,A> R accept(visitor.GJVisitor<R,A> v, A argu) {
      return v.visit(this,argu);
   }
   public <R> R accept(visitor.GJNoArguVisitor<R> v) {
      return v.visit(this);
   }
   public <A> void accept(visitor.GJVoidVisitor<A> v, A argu) {
      v.visit(this,argu);
   }
}

