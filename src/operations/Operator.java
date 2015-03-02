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
public abstract class Operator {

    String operation; // union or inter or diff or sel or proj or join
    String operatorName="";
    String relationPrint1="";
    String relationPrint2="";
    String attributesPrint="";
    String conditionsPrint="";
    String annotation="";
    public int getRelationPrint1Lenght(){
        return relationPrint1.length();
    }
    public int getRelationPrint2Lenght(){
        return relationPrint2.length();
    }
    public int getAttributesPrintLenght(){
        return attributesPrint.length();
    }
    public int getConditionsPrintLenght(){
        return conditionsPrint.length();
    }
    
    
    public void setOpName(String name){
        prePrint();
        operatorName = name;
    }
    public String getOpName(){return operatorName;}
    
    
    public void setOperation(String op){};
    public String getOperation(){return null;};
    
    public void setRelation1(String rel){};
    public void setRelation2(String rel){};
    public void setRelationOp1(Operator rel){};
    public void setRelationOp2(Operator rel){};  
    public String getRelation1(){ return null;};
    public String getRelation2(){return null;};
    public Operator getRelationOp1(){return null;};
    public Operator getRelationOp2(){return null;};
    
    public void AddAttr(String attr){};
    public ArrayList<String> getAttrs(){return null;};
    public boolean getHasHavingClause(){return false;};
    public void AddCondition(String attr1, String attr2, String action){};
    public ArrayList<Condition> getConditions(){return null;};
    
    protected void prePrint(){};
    public String toPrint(int maxR1,int maxR2, int maxC, int maxA,int maxAn){
        
        String temp= "|"+operatorName+"| "+operation;
        int l = 6 - operation.length();
        for(int i=0;i<l;i++){
                temp+=" ";
        }
        temp+="|";
        
       
         //add conditions 
        temp+=conditionsPrint;
        l = maxC- conditionsPrint.length();
        
        for(int i=0;i<l;i++){
                temp+=" ";
        }
        temp+="|";
        
        //add attributes
        temp+=attributesPrint;
        l= maxA - attributesPrint.length();
        for(int i=0;i<l;i++){
                temp+=" ";
        }
        temp+="|";
        
         //add relation1
        temp+=relationPrint1;
        l = maxR1- relationPrint1.length();        
        for(int i=0;i<l;i++){
                temp+=" ";
        }
        temp+="|";
        
        //add relation2
        temp+=relationPrint2;
        l = maxR2 - relationPrint2.length();        
        for(int i=0;i<l;i++){
                temp+=" ";
        }
        temp+="|";
        
        //add annotation
        temp+=annotation;
        l = maxAn - annotation.length();        
        for(int i=0;i<l;i++){
                temp+=" ";
        }
        temp+="|";
   
        return temp;
    
    }
  
    
    
}
