/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operations;

/**
 *
 * @author michalis
 */
public  class SetOp extends Operator{
    String operation; // union or inter or diff
    
    //only one of the following 2 will be null
    String relation1 = null;
    Operator relationOp1=null;
    
    //only one of the following 2 will be null
    String relation2= null;
    Operator relationOp2=null;
    
    
    @Override
    public void setOperation(String op){
        operation = op;
    }
    
    @Override
    public String getOperation(){
        return operation;
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
    public String toPrint(){
        String temp= "|"+operatorName+"| "+operation+" |";
        
         //add conditions 
        for(int i=0;i<40;i++)
                temp+=" ";
        temp+="|";
        
        
         //add relation1
        String rel1="";
        if(relationOp1==null)
            rel1+= relation1;
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
        String rel2="";
        if(relationOp2==null)
            rel2+= relation2;
        else
            rel2+= relationOp2.getOpName();
        
        if(rel2.length()< 18)
        {
            int j = 18 - rel2.length();
            for(int i=0;i<j;i++)
                rel2+=" ";
        }
        temp+=rel2+"|";
        
        return temp;
    }

}
   
    
    

