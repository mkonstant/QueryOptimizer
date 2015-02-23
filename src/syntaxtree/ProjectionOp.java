//
// Generated by JTB 1.3.2 DIT@UoA patched
//

package syntaxtree;

/**
 * Grammar production:
 * f0 -> "proj"
 * f1 -> "["
 * f2 -> Attribute()
 * f3 -> ( "," Attribute() )*
 * f4 -> "]"
 * f5 -> "("
 * f6 -> Relation()
 * f7 -> ")"
 */
public class ProjectionOp implements Node {
   public NodeToken f0;
   public NodeToken f1;
   public Attribute f2;
   public NodeListOptional f3;
   public NodeToken f4;
   public NodeToken f5;
   public Relation f6;
   public NodeToken f7;

   public ProjectionOp(NodeToken n0, NodeToken n1, Attribute n2, NodeListOptional n3, NodeToken n4, NodeToken n5, Relation n6, NodeToken n7) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
      f3 = n3;
      f4 = n4;
      f5 = n5;
      f6 = n6;
      f7 = n7;
   }

   public ProjectionOp(Attribute n0, NodeListOptional n1, Relation n2) {
      f0 = new NodeToken("proj");
      f1 = new NodeToken("[");
      f2 = n0;
      f3 = n1;
      f4 = new NodeToken("]");
      f5 = new NodeToken("(");
      f6 = n2;
      f7 = new NodeToken(")");
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

