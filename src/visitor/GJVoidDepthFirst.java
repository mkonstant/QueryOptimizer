//
// Generated by JTB 1.3.2 DIT@UoA patched
//

package visitor;
import syntaxtree.*;
import java.util.*;

/**
 * Provides default methods which visit each node in the tree in depth-first
 * order.  Your visitors may extend this class.
 */
public class GJVoidDepthFirst<A> implements GJVoidVisitor<A> {
   //
   // Auto class visitors--probably don't need to be overridden.
   //
   public void visit(NodeList n, A argu) {
      int _count=0;
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         e.nextElement().accept(this,argu);
         _count++;
      }
   }

   public void visit(NodeListOptional n, A argu) {
      if ( n.present() ) {
         int _count=0;
         for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            e.nextElement().accept(this,argu);
            _count++;
         }
      }
   }

   public void visit(NodeOptional n, A argu) {
      if ( n.present() )
         n.node.accept(this,argu);
   }

   public void visit(NodeSequence n, A argu) {
      int _count=0;
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         e.nextElement().accept(this,argu);
         _count++;
      }
   }

   public void visit(NodeToken n, A argu) {}

   //
   // User-generated visitor methods below
   //

   /**
    * f0 -> ( Operators() ( Operators() )* )?
    */
   public void visit(Query n, A argu) {
      n.f0.accept(this, argu);
   }

   /**
    * f0 -> SelectionOp()
    *       | ProjectionOp()
    *       | JoinOp()
    *       | GroupingOp()
    *       | SetOps()
    *       | ParOp()
    */
   public void visit(Operators n, A argu) {
      n.f0.accept(this, argu);
   }

   /**
    * f0 -> "sel"
    * f1 -> "["
    * f2 -> Condition()
    * f3 -> "]"
    * f4 -> "("
    * f5 -> Relation()
    * f6 -> ")"
    */
   public void visit(SelectionOp n, A argu) {
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      n.f5.accept(this, argu);
      n.f6.accept(this, argu);
   }

   /**
    * f0 -> "proj"
    * f1 -> "["
    * f2 -> Attribute()
    * f3 -> ( "," Attribute() )*
    * f4 -> "]"
    * f5 -> "("
    * f6 -> Relation()
    * f7 -> ")"
    */
   public void visit(ProjectionOp n, A argu) {
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      n.f5.accept(this, argu);
      n.f6.accept(this, argu);
      n.f7.accept(this, argu);
   }

   /**
    * f0 -> "join"
    * f1 -> "["
    * f2 -> Condition()
    * f3 -> "]"
    * f4 -> "("
    * f5 -> Relation()
    * f6 -> ")"
    * f7 -> "("
    * f8 -> Relation()
    * f9 -> ")"
    */
   public void visit(JoinOp n, A argu) {
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      n.f5.accept(this, argu);
      n.f6.accept(this, argu);
      n.f7.accept(this, argu);
      n.f8.accept(this, argu);
      n.f9.accept(this, argu);
   }

   /**
    * f0 -> "groupby"
    * f1 -> "["
    * f2 -> Attribute()
    * f3 -> ( "," Attribute() )*
    * f4 -> "]"
    * f5 -> UDF()
    * f6 -> ( HavingClause() )?
    * f7 -> "("
    * f8 -> Relation()
    * f9 -> ")"
    */
   public void visit(GroupingOp n, A argu) {
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      n.f5.accept(this, argu);
      n.f6.accept(this, argu);
      n.f7.accept(this, argu);
      n.f8.accept(this, argu);
      n.f9.accept(this, argu);
   }

   /**
    * f0 -> "having"
    * f1 -> "["
    * f2 -> Condition()
    * f3 -> "]"
    */
   public void visit(HavingClause n, A argu) {
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
   }

   /**
    * f0 -> Ops()
    * f1 -> "("
    * f2 -> Relation()
    * f3 -> ")"
    * f4 -> "("
    * f5 -> Relation()
    * f6 -> ")"
    */
   public void visit(SetOps n, A argu) {
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      n.f5.accept(this, argu);
      n.f6.accept(this, argu);
   }

   /**
    * f0 -> "("
    * f1 -> Operators()
    * f2 -> ")"
    */
   public void visit(ParOp n, A argu) {
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
   }

   /**
    * f0 -> "inter"
    *       | "union"
    *       | "diff"
    */
   public void visit(Ops n, A argu) {
      n.f0.accept(this, argu);
   }

   /**
    * f0 -> AlphaNumIdent()
    *       | Operators()
    */
   public void visit(Relation n, A argu) {
      n.f0.accept(this, argu);
   }

   /**
    * f0 -> AlphaNumIdent()
    */
   public void visit(Attribute n, A argu) {
      n.f0.accept(this, argu);
   }

   /**
    * f0 -> SimpleAggregations()
    */
   public void visit(UDF n, A argu) {
      n.f0.accept(this, argu);
   }

   /**
    * f0 -> "min"
    *       | "max"
    *       | "sum"
    *       | "avg"
    *       | "count"
    */
   public void visit(SimpleAggregations n, A argu) {
      n.f0.accept(this, argu);
   }

   /**
    * f0 -> Atom()
    * f1 -> ( ComplexCondition() )*
    */
   public void visit(Condition n, A argu) {
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
   }

   /**
    * f0 -> ( "or" | "and" )
    * f1 -> Atom()
    */
   public void visit(ComplexCondition n, A argu) {
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
   }

   /**
    * f0 -> AtomPart()
    * f1 -> ( "=" | ">" | "<" | "<=" | ">=" )
    * f2 -> AtomPart()
    */
   public void visit(Atom n, A argu) {
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
   }

   /**
    * f0 -> ( AtomAttr() | IntegerLiteral() | FloatLiteral() | StringLiteral() )
    * f1 -> ( ComplexAtomPart() )?
    */
   public void visit(AtomPart n, A argu) {
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
   }

   /**
    * f0 -> ( "+" | "-" )
    * f1 -> ( IntegerLiteral() | FloatLiteral() )
    */
   public void visit(ComplexAtomPart n, A argu) {
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
   }

   /**
    * f0 -> AlphaNumIdent()
    * f1 -> ( AtRel() )?
    */
   public void visit(AtomAttr n, A argu) {
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
   }

   /**
    * f0 -> "."
    * f1 -> AlphaNumIdent()
    */
   public void visit(AtRel n, A argu) {
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
   }

   /**
    * f0 -> <ALPHA_NUM_IDENT>
    */
   public void visit(AlphaNumIdent n, A argu) {
      n.f0.accept(this, argu);
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public void visit(IntegerLiteral n, A argu) {
      n.f0.accept(this, argu);
   }

   /**
    * f0 -> <FLOATING_POINT_LITERAL>
    */
   public void visit(FloatLiteral n, A argu) {
      n.f0.accept(this, argu);
   }

   /**
    * f0 -> <STRING_LITERAL>
    */
   public void visit(StringLiteral n, A argu) {
      n.f0.accept(this, argu);
   }

}
