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
public  class Projection extends Operator{
    
    //only one of the following 2 will be null
    String relation1 = null;
    Operator relationOp1=null;
    ArrayList<String> attrs = null;
    
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
    protected void prePrint(){
        operation = "proj";
         //add conditions 
        attributesPrint="";
        for (int i = 0; i < attrs.size(); i++)
        {
            if(i>0)
                attributesPrint+=",";
            attributesPrint += attrs.get(i);
	}
       
        //add relation1
     
        if(relationOp1==null)
            relationPrint1+=relation1;
        else
            relationPrint1+= relationOp1.getOpName();
        
        
    
    }
   
   
}
