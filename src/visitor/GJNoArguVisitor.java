//
// Generated by JTB 1.3.2 DIT@UoA patched
//

package visitor;
import syntaxtree.*;
import java.util.*;

/**
 * All GJ visitors with no argument must implement this interface.
 */

public interface GJNoArguVisitor<R> {

   //
   // GJ Auto class visitors with no argument
   //

   public R visit(NodeList n) ;
   public R visit(NodeListOptional n) ;
   public R visit(NodeOptional n) ;
   public R visit(NodeSequence n) ;
   public R visit(NodeToken n) ;

   //
   // User-generated visitor methods below
   //

   /**
    * f0 -> ( Operators() ( Operators() )* )?
    */
   public R visit(Query n);

   /**
    * f0 -> SelectionOp()
    *       | ProjectionOp()
    *       | JoinOp()
    *       | GroupingOp()
    *       | SetOps()
    *       | ParOp()
    */
   public R visit(Operators n);

   /**
    * f0 -> "sel"
    * f1 -> "["
    * f2 -> Condition()
    * f3 -> "]"
    * f4 -> "("
    * f5 -> Relation()
    * f6 -> ")"
    */
   public R visit(SelectionOp n);

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
   public R visit(ProjectionOp n);

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
   public R visit(JoinOp n);

   /**
    * f0 -> "groupby"
    * f1 -> "["
    * f2 -> Attribute()
    * f3 -> ( "," Attribute() )*
    * f4 -> "]"
    * f5 -> UDF()
    * f6 -> ( HavingClause() )?
    */
   public R visit(GroupingOp n);

   /**
    * f0 -> "having"
    * f1 -> "["
    * f2 -> Condition()
    * f3 -> "]"
    * f4 -> "("
    * f5 -> Relation()
    * f6 -> ")"
    */
   public R visit(HavingClause n);

   /**
    * f0 -> Ops()
    * f1 -> "("
    * f2 -> Relation()
    * f3 -> ")"
    * f4 -> "("
    * f5 -> Relation()
    * f6 -> ")"
    */
   public R visit(SetOps n);

   /**
    * f0 -> "("
    * f1 -> Operators()
    * f2 -> ")"
    */
   public R visit(ParOp n);

   /**
    * f0 -> "inter"
    *       | "union"
    *       | "diff"
    */
   public R visit(Ops n);

   /**
    * f0 -> <ALPHA_NUM_IDENT>
    *       | Operators()
    */
   public R visit(Relation n);

   /**
    * f0 -> <ALPHA_NUM_IDENT>
    */
   public R visit(Attribute n);

   /**
    * f0 -> SimpleAggregations()
    */
   public R visit(UDF n);

   /**
    * f0 -> "min"
    *       | "max"
    *       | "sum"
    *       | "avg"
    *       | "count"
    */
   public R visit(SimpleAggregations n);

   /**
    * f0 -> Atom()
    * f1 -> ( ComplexCondition() )?
    */
   public R visit(Condition n);

   /**
    * f0 -> ( "or" | "and" )
    * f1 -> Atom()
    */
   public R visit(ComplexCondition n);

   /**
    * f0 -> AtomPart()
    * f1 -> ( "=" | ">" | "<" | "<=" | ">=" )
    * f2 -> AtomPart()
    */
   public R visit(Atom n);

   /**
    * f0 -> ( AtomAttr() | <INTEGER_LITERAL> | <FLOATING_POINT_LITERAL> | <STRING_LITERAL> )
    * f1 -> ( ComplexAtomPart() )?
    */
   public R visit(AtomPart n);

   /**
    * f0 -> ( "+" | "-" )
    * f1 -> ( <INTEGER_LITERAL> | <FLOATING_POINT_LITERAL> )
    */
   public R visit(ComplexAtomPart n);

   /**
    * f0 -> <ALPHA_NUM_IDENT>
    * f1 -> ( AtRel() )?
    */
   public R visit(AtomAttr n);

   /**
    * f0 -> "."
    * f1 -> <ALPHA_NUM_IDENT>
    */
   public R visit(AtRel n);

}

