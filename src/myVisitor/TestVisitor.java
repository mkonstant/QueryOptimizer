/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myVisitor;

import java.util.ArrayList;
import myExceptions.ComplexConditionException;
import myExceptions.RelationAttributeException;
import operations.Group;
import operations.Join;
import operations.Operator;
import operations.Projection;
import operations.Selection;
import operations.SetOp;
import syntaxtree.AlphaNumIdent;
import syntaxtree.AtRel;
import syntaxtree.Atom;
import syntaxtree.AtomAttr;
import syntaxtree.AtomPart;
import syntaxtree.Attribute;
import syntaxtree.ComplexAtomPart;
import syntaxtree.ComplexCondition;
import syntaxtree.Condition;
import syntaxtree.FloatLiteral;
import syntaxtree.GroupingOp;
import syntaxtree.HavingClause;
import syntaxtree.IntegerLiteral;
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
import syntaxtree.StringLiteral;
import syntaxtree.UDF;

/**
 *
 * @author michalis
 */


public class TestVisitor extends visitor.GJDepthFirst<String,String> {

    
   ArrayList<Operator> operations = new ArrayList<Operator>();
   public ArrayList<Operator> getOperations(){
       return operations;
   } 
   
   String complexCondition;
   Boolean joinRel2=false;
   
   int count=0;
   
  Operator temp;
    
  
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
      n.f0.accept(this, argu);
      return "op";
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
      String relation = n.f5.accept(this, argu);
      Operator tempResult= temp;
      temp=new Selection();
      if(relation=="op"){   
        /*the relation is another operation result
          constructed to the variable temp
          pass the content of temp as relation
        */
          temp.setRelationOp1(tempResult);
      }
      else{               
        temp.setRelation1(relation);
      }
      n.f2.accept(this, argu);
      
      operations.add(temp);
      return null;
      
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
      String relation = n.f6.accept(this, argu);
      Operator tempResult= temp;
      temp=new Projection();
      if(relation=="op"){   
        /*the relation is another operation result
          constructed to the variable temp
          pass the content of temp as relation
        */
          temp.setRelationOp1(tempResult);
      }
      else{
          temp.setRelation1(relation);
      }       
       
      n.f2.accept(this, "addAttr");
      n.f3.accept(this, "addAttr");
      operations.add(temp);
      return null;
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
      String relation1 = n.f5.accept(this, argu);
      Operator tempResult1= temp;
      
      String relation2 = n.f8.accept(this, argu);
      Operator tempResult2= temp;
      
      temp=new Join();
      if(relation1=="op"){   
        /*the relation is another operation result
          constructed to the variable temp
          pass the content of temp as relation
        */
          temp.setRelationOp1(tempResult1);
      }
      else{               
        temp.setRelation1(relation1);
      }
      
      if(relation2=="op"){   
        /*the relation is another operation result
          constructed to the variable temp
          pass the content of temp as relation
        */
          temp.setRelationOp2(tempResult2);
      }
      else{               
        temp.setRelation2(relation2);
      }
      
      
      joinRel2 =true;
      n.f2.accept(this, argu);
      joinRel2 =false;
      operations.add(temp);
      return null;
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
   public String visit(GroupingOp n, String argu) {
      String relation1 = n.f8.accept(this, argu);
      Operator tempResult= temp;
      
      temp= new Group();
      if(relation1=="op"){   
        /*the relation is another operation result
          constructed to the variable temp
          pass the content of temp as relation
        */
          temp.setRelationOp1(tempResult);
      }
      else{               
        temp.setRelation1(relation1);
      }
       
      n.f2.accept(this, "addAttr");
      n.f3.accept(this, "addAttr"); 
      
      n.f5.accept(this, "group");
      
      n.f6.accept(this, argu);
      operations.add(temp);
     
     
      return null;
   }

   /**
    * f0 -> "having"
    * f1 -> "["
    * f2 -> Condition()
    * f3 -> "]"
    */
   public String visit(HavingClause n, String argu) {
      n.f2.accept(this, argu);
      return null;
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
       String op = n.f0.accept(this, argu);
       
       
      String relation1 = n.f2.accept(this, argu);
      Operator tempResult1= temp;
      
      String relation2 = n.f5.accept(this, argu);
      Operator tempResult2= temp;
      
      temp= new SetOp();
      if(relation1=="op"){   
        /*the relation is another operation result
          constructed to the variable temp
          pass the content of temp as relation
        */
          temp.setRelationOp1(tempResult1);
      }
      else{               
        temp.setRelation1(relation1);
      }
      
      if(relation2=="op"){   
        /*the relation is another operation result
          constructed to the variable temp
          pass the content of temp as relation
        */
          temp.setRelationOp2(tempResult2);
      }
      else{               
        temp.setRelation2(relation2);
      }
      temp.setOperation(op);
      operations.add(temp);
      return null;
   }

   /**
    * f0 -> "("
    * f1 -> Operators()
    * f2 -> ")"
    */
   public String visit(ParOp n, String argu) {
      return n.f1.accept(this, argu);
   }

   /**
    * f0 -> "inter"
    *       | "union"
    *       | "diff"
    */
   public String visit(Ops n, String argu) {
      return n.f0.choice.toString();
   }

    /**
    * f0 -> AlphaNumIdent()
    *       | Operators()
    */
   public String visit(Relation n, String argu) {
      return n.f0.accept(this, argu);
   }

   /**
    * f0 -> AtomAttr()
    */
   public String visit(Attribute n, String argu) {
      String attr =  n.f0.accept(this, argu);
      if(argu.equals("addAttr")){
          temp.AddAttr(attr);
      }
      return attr;
   }

/**
    * f0 -> SimpleAggregations()
    * f1 -> "("
    * f2 -> AtomAttr()
    * f3 -> ")"
    */
   public String visit(UDF n, String argu) {
       String aggregation = n.f0.accept(this, argu);
       String attr = n.f2.accept(this, argu);
       
       if(argu!=null && argu.equals("group")){
           temp.setAggregation(aggregation);
           temp.setAggregationAttr(attr);
       }
       else{
           return aggregation+"("+attr+")";
       }
       return "";
   }

   /**
    * f0 -> "min"
    *       | "max"
    *       | "sum"
    *       | "avg"
    *       | "count"
    */
   public String visit(SimpleAggregations n, String argu) {
      return n.f0.choice.toString();
   }

     /**
    * f0 -> Atom()
    * f1 -> ( ComplexCondition() )?
    */
   public String visit(Condition n, String argu) {
      String cc = complexCondition;
       
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      
      temp.setComplexCondition(complexCondition);
      complexCondition = cc;
      return null;
   }

   /**
    * f0 -> ( "or" | "and" )
    * f1 -> Atom()
    */
   public String visit(ComplexCondition n, String argu) {
      if(complexCondition==null){
          complexCondition = n.f0.choice.toString();
      }
      else{
          if(!complexCondition.equals(n.f0.choice.toString())){
              throw new ComplexConditionException();
          }
      }
      n.f1.accept(this, argu);
      return null;
   }

   /**
    * f0 -> AtomPart()
    * f1 -> ( "=" | ">" | "<" | "<=" | ">=" )
    * f2 -> AtomPart()
    */
   public String visit(Atom n, String argu) {
      
      
       
      String attr1 = n.f0.accept(this, argu);
      String action= n.f1.choice.toString();
      
      String attr2 = n.f2.accept(this, "attr2");
      temp.AddCondition(attr1, attr2, action);
      
      return null;
   }

   /**
    * f0 -> ( AtomAttr() | IntegerLiteral() | FloatLiteral() | StringLiteral() | UDF() )
    * f1 -> ( ComplexAtomPart() )?
    */
   public String visit(AtomPart n, String argu) {

      n.f1.accept(this, argu);
      return  n.f0.accept(this, argu);
   }

   /**
    * f0 -> ( "+" | "-" )
    * f1 -> ( IntegerLiteral() | FloatLiteral() )
    */
   public String visit(ComplexAtomPart n, String argu) {
      String _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> AlphaNumIdent()
    * f1 -> ( AtRel() )?
    */
   public String visit(AtomAttr n, String argu) {
      String head= n.f0.accept(this, argu);
      String tail = n.f1.accept(this, argu);
      if(argu!=null && argu.equals("attr2")){
          if(tail!=null){
            if(temp.getRelation2()==null){
                throw new RelationAttributeException( head+"."+tail,null);
            }
            else{
                if(!temp.getRelation2().equals(head))
                    throw new RelationAttributeException( head+"."+tail, temp.getRelation2());
            }

        }
      }
      else{
        if(tail!=null){
            if(temp.getRelation1()==null){
                throw new RelationAttributeException( head+"."+tail,null);
            }
            else{
                if(!temp.getRelation1().equals(head))
                    throw new RelationAttributeException( head+"."+tail, temp.getRelation1());
            }

        }
      }
      
      if(tail==null)
          return head;
      else
        return tail;
   }

   /**
    * f0 -> "."
    * f1 -> AlphaNumIdent()
    */
   public String visit(AtRel n, String argu) {
      return n.f1.accept(this, argu);
   }

   /**
    * f0 -> <ALPHA_NUM_IDENT>
    */
   public String visit(AlphaNumIdent n, String argu) {
      return n.f0.toString();
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public String visit(IntegerLiteral n, String argu) {
      return n.f0.toString();
   }

   /**
    * f0 -> <FLOATING_POINT_LITERAL>
    */
   public String visit(FloatLiteral n, String argu) {
      return n.f0.toString();
   }

   /**
    * f0 -> <STRING_LITERAL>
    */
   public String visit(StringLiteral n, String argu) {
      return n.f0.toString();
   }

    
}
