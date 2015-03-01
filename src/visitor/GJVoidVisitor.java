//
// Generated by JTB 1.3.2 DIT@UoA patched
//

package visitor;
import syntaxtree.*;
import java.util.*;

/**
 * All GJ void visitors must implement this interface.
 */

public interface GJVoidVisitor<A> {

   //
   // GJ void Auto class visitors
   //

   public void visit(NodeList n, A argu);
   public void visit(NodeListOptional n, A argu);
   public void visit(NodeOptional n, A argu);
   public void visit(NodeSequence n, A argu);
   public void visit(NodeToken n, A argu);

   //
   // User-generated visitor methods below
   //

   /**
    * f0 -> ( Operators() ( Operators() )* )?
    */
   public void visit(Query n, A argu);

   /**
    * f0 -> SelectionOp()
    *       | ProjectionOp()
    *       | JoinOp()
    *       | GroupingOp()
    *       | SetOps()
    *       | ParOp()
    */
   public void visit(Operators n, A argu);

   /**
    * f0 -> "sel"
    * f1 -> "["
    * f2 -> Condition()
    * f3 -> "]"
    * f4 -> "("
    * f5 -> Relation()
    * f6 -> ")"
    */
   public void visit(SelectionOp n, A argu);

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
   public void visit(ProjectionOp n, A argu);

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
   public void visit(JoinOp n, A argu);

   /**
    * f0 -> "groupby"
    * f1 -> "["
    * f2 -> Attribute()
    * f3 -> ( "," Attribute() )*
    * f4 -> "]"
    * f5 -> UDF()
    * f6 -> ( HavingClause() )?
    */
   public void visit(GroupingOp n, A argu);

   /**
    * f0 -> "having"
    * f1 -> "["
    * f2 -> Condition()
    * f3 -> "]"
    * f4 -> "("
    * f5 -> Relation()
    * f6 -> ")"
    */
   public void visit(HavingClause n, A argu);

   /**
    * f0 -> Ops()
    * f1 -> "("
    * f2 -> Relation()
    * f3 -> ")"
    * f4 -> "("
    * f5 -> Relation()
    * f6 -> ")"
    */
   public void visit(SetOps n, A argu);

   /**
    * f0 -> "("
    * f1 -> Operators()
    * f2 -> ")"
    */
   public void visit(ParOp n, A argu);

   /**
    * f0 -> "inter"
    *       | "union"
    *       | "diff"
    */
   public void visit(Ops n, A argu);

   /**
    * f0 -> <ALPHA_NUM_IDENT>
    *       | Operators()
    */
   public void visit(Relation n, A argu);

   /**
    * f0 -> <ALPHA_NUM_IDENT>
    */
   public void visit(Attribute n, A argu);

   /**
    * f0 -> SimpleAggregations()
    */
   public void visit(UDF n, A argu);

   /**
    * f0 -> "min"
    *       | "max"
    *       | "sum"
    *       | "avg"
    *       | "count"
    */
   public void visit(SimpleAggregations n, A argu);

   /**
    * f0 -> Atom()
    * f1 -> ( ComplexCondition() )?
    */
   public void visit(Condition n, A argu);

   /**
    * f0 -> ( "or" | "and" )
    * f1 -> Atom()
    */
   public void visit(ComplexCondition n, A argu);

   /**
    * f0 -> AtomPart()
    * f1 -> ( "=" | ">" | "<" | "<=" | ">=" )
    * f2 -> AtomPart()
    */
   public void visit(Atom n, A argu);

   /**
    * f0 -> ( AtomAttr() | <INTEGER_LITERAL> | <FLOATING_POINT_LITERAL> | <STRING_LITERAL> )
    * f1 -> ( ComplexAtomPart() )?
    */
   public void visit(AtomPart n, A argu);

   /**
    * f0 -> ( "+" | "-" )
    * f1 -> ( <INTEGER_LITERAL> | <FLOATING_POINT_LITERAL> )
    */
   public void visit(ComplexAtomPart n, A argu);

   /**
    * f0 -> <ALPHA_NUM_IDENT>
    * f1 -> ( AtRel() )?
    */
   public void visit(AtomAttr n, A argu);

   /**
    * f0 -> "."
    * f1 -> <ALPHA_NUM_IDENT>
    */
   public void visit(AtRel n, A argu);

}

