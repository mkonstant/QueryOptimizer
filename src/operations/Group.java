/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operations;

import java.util.ArrayList;
import syntaxtree.HavingClause;

/**
 *
 * @author michalis
 */
public  class Group extends Operator{
    
    ArrayList<String> attrs = null;
    ArrayList<Condition> conditions = null;
    
    private boolean hasHavingClause=false;
    
    //only one of the following 2 
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
        relation1 = rel;
    }
    
    @Override
    public void setRelationOp1(Operator rel){
        relationOp1 = rel;
    }
    
    @Override
    public void setAggregation(String agr){
        aggregation =agr;
    }
    
    @Override
    public void setAggregationAttr(String agr){
        aggregationAttr =agr;
    }
    
    @Override
    public String getAggregation(){
        return aggregation;
    }

    @Override
    public String getAggregationAttr(){
        return aggregationAttr;
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
        if(conditions==null){
            hasHavingClause = true;
            conditions = new ArrayList<Condition>();
        }
        conditions.add(new Condition(attr1, attr2, action));
    }
    
    
    @Override
    public ArrayList<Condition> getConditions(){
        return conditions;
    }
    
     @Override
    protected void prePrint(){
         //add conditions 
        attributesPrint="";
        for (int i = 0; i < attrs.size(); i++)
        {
            if(i>0)
                attributesPrint+=",";
            attributesPrint += attrs.get(i);
	}
        
       //add conditions 
        if(conditions!=null){
            conditionsPrint="";
            for (int i = 0; i < conditions.size(); i++)
            {
                if(i>0)
                    conditionsPrint+=" "+complexCondtion+" ";
                conditionsPrint += conditions.get(i).toPrint();
            }
        }
        //add relation1
     
        if(relationOp1==null)
            relationPrint1+=relation1;
        else
            relationPrint1+= relationOp1.getOpName();
    }
   
    
    
    
}
