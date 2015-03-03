/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operations;

import java.util.ArrayList;

/**
 *
 * @author michalis
 */
public  class Group extends Operator{
    
    ArrayList<String> attrs = null;
    ArrayList<Condition> conditions = null;
    
    public boolean hasHavingClause=false;
    //only one of the following 2 and only if hasHavingClause=true
    String relation1 = null;
    Operator relationOp1=null;

    public Group() {
        operation = "group";
    }
    
 
    
    
    @Override
    public boolean getHasHavingClause(){
        return hasHavingClause;
    }
    
    @Override
    public void setRelation1(String rel){
        hasHavingClause=true;
        relation1 = rel;
    }
    
    @Override
    public void setRelationOp1(Operator rel){
        hasHavingClause=true;;
        relationOp1 = rel;
    }
    
    
    
    @Override
    public String getRelation1(){
        return relation1;
    }
    
    @Override
    public Operator getRelationOp1(){
        return relationOp1;
    }
    
    
    
    @Override
    public void AddAttr(String attr){
        if(attrs==null)
            attrs = new ArrayList<String>();
        attrs.add(attr);
    }
    
    
    @Override
    public ArrayList<String> getAttrs(){
        return attrs;
    }
    
    @Override
    public void AddCondition(String attr1, String attr2, String action){
        if(conditions==null)
            conditions = new ArrayList<Condition>();
        conditions.add(new Condition(attr1, attr2, action));
    }
    
    
    @Override
    public ArrayList<Condition> getConditions(){
        return conditions;
    }
   
    
    
    
}
