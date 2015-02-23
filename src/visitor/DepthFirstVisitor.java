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
public class DepthFirstVisitor implements Visitor {
   //
   // Auto class visitors--probably don't need to be overridden.
   //
   public void visit(NodeList n) {
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
         e.nextElement().accept(this);
   }

   public void visit(NodeListOptional n) {
      if ( n.present() )
         for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
            e.nextElement().accept(this);
   }

   public void visit(NodeOptional n) {
      if ( n.present() )
         n.node.accept(this);
   }

   public void visit(NodeSequence n) {
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
         e.nextElement().accept(this);
   }

   public void visit(NodeToken n) {}

   //
   // User-generated visitor methods below
   //

   /**
    * f0 -> ( Operators() ( Operators() )* )?
    */
   public void visit(Query n) {
      n.f0.accept(this);
   }

   /**
    * f0 -> SelectionOp()
    *       | ProjectionOp()
    *       | JoinOp()
    *       | GroupingOp()
    *       | SetOps()
    *       | ParOp()
    */
   public void visit(Operators n) {
      n.f0.accept(this);
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
   public void visit(SelectionOp n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      n.f5.accept(this);
      n.f6.accept(this);
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
   public void visit(ProjectionOp n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      n.f5.accept(this);
      n.f6.accept(this);
      n.f7.accept(this);
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
   public void visit(JoinOp n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      n.f5.accept(this);
      n.f6.accept(this);
      n.f7.accept(this);
      n.f8.accept(this);
      n.f9.accept(this);
   }

   /**
    * f0 -> "groupby"
    * f1 -> "["
    * f2 -> Attribute()
    * f3 -> ( "," Attribute() )*
    * f4 -> "]"
    * f5 -> UDF()
    * f6 -> ( HavingClause() )?
    */
   public void visit(GroupingOp n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      n.f5.accept(this);
      n.f6.accept(this);
   }

   /**
    * f0 -> "having"
    * f1 -> "["
    * f2 -> Condition()
    * f3 -> "]"
    * f4 -> "("
    * f5 -> Relation()
    * f6 -> ")"
    */
   public void visit(HavingClause n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      n.f5.accept(this);
      n.f6.accept(this);
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
   public void visit(SetOps n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      n.f5.accept(this);
      n.f6.accept(this);
   }

   /**
    * f0 -> "("
    * f1 -> Operators()
    * f2 -> ")"
    */
   public void visit(ParOp n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
   }

   /**
    * f0 -> "inter"
    *       | "union"
    *       | "diff"
    */
   public void visit(Ops n) {
      n.f0.accept(this);
   }

   /**
    * f0 -> <ALPHA_NUM_IDENT>
    *       | Operators()
    */
   public void visit(Relation n) {
      n.f0.accept(this);
   }

   /**
    * f0 -> <ALPHA_NUM_IDENT>
    */
   public void visit(Attribute n) {
      n.f0.accept(this);
   }

   /**
    * f0 -> SimpleAggregations()
    */
   public void visit(UDF n) {
      n.f0.accept(this);
   }

   /**
    * f0 -> "min"
    *       | "max"
    *       | "sum"
    *       | "avg"
    *       | "count"
    */
   public void visit(SimpleAggregations n) {
      n.f0.accept(this);
   }

   /**
    * f0 -> Atom()
    * f1 -> ( ComplexCondition() )?
    */
   public void visit(Condition n) {
      n.f0.accept(this);
      n.f1.accept(this);
   }

   /**
    * f0 -> ( "or" | "and" )
    * f1 -> Atom()
    */
   public void visit(ComplexCondition n) {
      n.f0.accept(this);
      n.f1.accept(this);
   }

   /**
    * f0 -> AtomPart()
    * f1 -> ( "=" | ">" | "<" | "<=" | ">=" )
    * f2 -> AtomPart()
    */
   public void visit(Atom n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
   }

   /**
    * f0 -> <ALPHA_NUM_IDENT>
    * f1 -> ( AtRel() )?
    */
   public void visit(AtomPart n) {
      n.f0.accept(this);
      n.f1.accept(this);
   }

   /**
    * f0 -> "."
    * f1 -> <ALPHA_NUM_IDENT>
    */
   public void visit(AtRel n) {
      n.f0.accept(this);
      n.f1.accept(this);
   }

}
