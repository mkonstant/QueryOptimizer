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
public class Selection extends Operator {
    
    //only one of the following 2 will be null
    String relation1 = null;
    Operator relationOp1=null;
    
    ArrayList<Condition> conditions = null;

    public Selection() {
        operation = "sel";
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
    public String getRelation1(){
        return relation1;
    }
    
    @Override
    public Operator getRelationOp1(){
        return relationOp1;
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

    @Override
    protected void prePrint(){
        
         //add conditions 
        conditionsPrint="";
        for (int i = 0; i < conditions.size(); i++)
        {
            conditionsPrint += conditions.get(i).toPrint();
	}
        
        //add relation1
     
        if(relationOp1==null)
            relationPrint1+= relation1;
        else
            relationPrint1+= relationOp1.getOpName();
    
    }
    
    @Override
    public void computeCost(){
    
    }
    
    public void checkVariables(){
    
    }
    
}
