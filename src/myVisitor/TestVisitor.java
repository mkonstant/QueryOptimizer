/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myVisitor;

import syntaxtree.Atom;
import syntaxtree.Attribute;
import syntaxtree.ComplexCondition;
import syntaxtree.Condition;
import syntaxtree.GroupingOp;
import syntaxtree.HavingClause;
import syntaxtree.JoinOp;
import syntaxtree.Operators;
import syntaxtree.Ops;
import syntaxtree.ParOp;
import syntaxtree.ProjectionOp;
import syntaxtree.Query;
import syntaxtree.Relation;
import syntaxtree.SelectionOp;
import syntaxtree.SetOps;
import syntaxtree.SimpleAggregations;
import syntaxtree.UDF;

/**
 *
 * @author michalis
 */
public class TestVisitor extends visitor.GJDepthFirst<String, String> {
      //
   // User-generated visitor methods below
   //

    
   /**
    * f0 -> ( Operators() ( Operators() )* )?
    */
   public String visit(Query n, String argu) {
      return n.f0.accept(this, argu);
   }

   /**
    * f0 -> SelectionOp()
    *       | ProjectionOp()
    *       | JoinOp()
    *       | GroupingOp()
    *       | SetOps()
    *       | ParOp()
    */
   public String visit(Operators n, String argu) {
      return n.f0.accept(this, argu);
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
   public String visit(SelectionOp n, String argu) {
      String _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      n.f5.accept(this, argu);
      n.f6.accept(this, argu);
      return _ret;
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
   public String visit(ProjectionOp n, String argu) {
      String _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      n.f5.accept(this, argu);
      n.f6.accept(this, argu);
      n.f7.accept(this, argu);
      return _ret;
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
   public String visit(JoinOp n, String argu) {
      String _ret=null;
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
      return _ret;
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
   public String visit(GroupingOp n, String argu) {
      String _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      n.f5.accept(this, argu);
      n.f6.accept(this, argu);
      return _ret;
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
   public String visit(HavingClause n, String argu) {
      String _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      n.f5.accept(this, argu);
      n.f6.accept(this, argu);
      return _ret;
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
   public String visit(SetOps n, String argu) {
      String _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      n.f5.accept(this, argu);
      n.f6.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "("
    * f1 -> Operators()
    * f2 -> ")"
    */
   public String visit(ParOp n, String argu) {
      String _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "inter"
    *       | "union"
    *       | "diff"
    */
   public String visit(Ops n, String argu) {
      return n.f0.accept(this, argu);
   }

   /**
    * f0 -> <STRING_LITERAL>
    */
   public String visit(Relation n, String argu) {
      return n.f0.accept(this, argu);
   }

   /**
    * f0 -> <STRING_LITERAL>
    */
   public String visit(Attribute n, String argu) {
      return n.f0.accept(this, argu);
   }

   /**
    * f0 -> SimpleAggregations()
    */
   public String visit(UDF n, String argu) {
      return n.f0.accept(this, argu);
   }

   /**
    * f0 -> "min"
    *       | "max"
    *       | "sum"
    *       | "avg"
    *       | "count"
    */
   public String visit(SimpleAggregations n, String argu) {
      return n.f0.accept(this, argu);
   }

   /**
    * f0 -> Atom()
    * f1 -> ( ComplexCondition() )?
    */
   public String visit(Condition n, String argu) {
      String _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> ( "or" | "and" )
    * f1 -> Atom()
    */
   public String visit(ComplexCondition n, String argu) {
      String _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> <STRING_LITERAL>
    */
   public String visit(Atom n, String argu) {
      return n.f0.accept(this, argu);
   }
    
}
