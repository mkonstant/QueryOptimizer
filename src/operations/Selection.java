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
    public String toPrint(){
        String temp= "|"+operatorName+"|  sel  |";
        
        
        //add conditions 
        String cond="";
        for (int i = 0; i < conditions.size(); i++)
        {
            cond += conditions.get(i).toPrint();
	}
        
        if(cond.length()< 40)
        {
            int j = 40 - cond.length();
            for(int i=0;i<j;i++)
                cond+=" ";
        }
        
        temp+=cond+"|";
        
        
        //add relation1
        String rel1="";
        if(relationOp1==null)
            rel1+= "relation '"+relation1+"'";
        else
            rel1+= relationOp1.getOpName();
        
        if(rel1.length()< 18)
        {
            int j = 18 - rel1.length();
            for(int i=0;i<j;i++)
                rel1+=" ";
        }
        
        temp+=rel1+"|";
        
         //add relation2
         for(int i=0;i<18;i++)
                temp+=" ";
        temp+="|";
        
        
        
        return temp;
    }
   
    
    
}
