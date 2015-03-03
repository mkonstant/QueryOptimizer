/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operations;

import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author michalis
 */
public  class Join extends Operator{
    
     //only one of the following 2 will be null
    String relation1 = null;
    Operator relationOp1=null;
    
    //only one of the following 2 will be null
    String relation2= null;
    Operator relationOp2=null;
    
   ArrayList<Condition> conditions = null;

    public Join() {
        operation = "join";
    }
   
   
    
    @Override
    public void setRelation1(String rel){
        relation1 = rel;
    }
    
    @Override
    public void setRelation2(String rel){
        relation2 = rel;
    }
    
    @Override
    public void setRelationOp1(Operator rel){
        relationOp1 = rel;
    }
    
    @Override
    public void setRelationOp2(Operator rel){
        relationOp2 = rel;
    }
    
    @Override
    public String getRelation1(){
        return relation1;
    }
    
    @Override
    public String getRelation2(){
        return relation2;
    }
    
    @Override
    public Operator getRelationOp1(){
        return relationOp1;
    }
    
    @Override
    public Operator getRelationOp2(){
        return relationOp2;
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
            relationPrint1+=relation1;
        else
            relationPrint1+= relationOp1.getOpName();
        
          //add relation2
        if(relationOp2==null)
            relationPrint2+= relation2;
        else
            relationPrint2+= relationOp2.getOpName();
    
    }
    
     
   
}
