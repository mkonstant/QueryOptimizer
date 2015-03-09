/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operations;

import catalog.Catalog;
import catalog.TableInfo;
import java.util.ArrayList;

/**
 *
 * @author michalis
 */
public abstract class Operator {

    protected String operation; // union or inter or diff or sel or proj or join
    protected String operatorName="";
    protected String relationPrint1="";
    protected String relationPrint2="";
    protected String attributesPrint="";
    protected String conditionsPrint="";
    protected String annotation="";
    protected Catalog catalog;
    protected String complexCondtion=null;
    protected String aggregation="";
    protected String aggregationAttr="";
    
    protected int b1,n1;  
    protected int b2,n2;
    protected int bout, nout;
    
    protected TableInfo outTable;
    

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
    public boolean getHasHavingClause(){return false;};
    public void AddAttr(String attr){};
    public ArrayList<String> getAttrs(){return null;};
    public void AddCondition(String attr1, String attr2, String action){};
    public ArrayList<Condition> getConditions(){return null;};
    protected void prePrint(){};
    public void computeCost(){};    
    public void setAggregation(String agr){}
    public void setAggregationAttr(String agr){}   
    public String getAggregation(){return "";}
    public String getAggregationAttr(){return "";}
    
    
    
    public void setCatalog(Catalog c){
        catalog = c;
    }
    
    public String getComplexCondtion(){
        return complexCondtion;
    }
    
    public void setComplexCondition(String complexCondition){
        this.complexCondtion = complexCondition;
    }
    
    public TableInfo getOutTable(){
        return outTable;
    }
    
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
    
    public int getAnnotationLenght(){
        return annotation.length();
    }
    
    public int getAggragationLenght(){
        return aggregation.length()+aggregationAttr.length()+2;
    }
    
    public void setOpName(String name){
        prePrint();
        operatorName = name;
    }
    public String getOpName(){
        return operatorName;
    }
    
        public void setB1(int b1){
        this.b1=b1;
    }

    public void setB2(int b2){
        this.b2=b2;
    }
    
    public void setBout(int bout){
        this.bout=bout;
    }
    
    public void setN1(int n1){
        this.n1=n1;
    }

    public void setN2(int n2){
        this.n2=n2;
    }
    
    public void setNout(int nout){
        this.nout=nout;
    }
    
    public int getBout(){
        return bout;
    }
      
    public int getNout(){
        return nout;
    }
    
    
   
    public String toPrint(int maxR1,int maxR2, int maxC, int maxA,int maxAn , int maxAggr){        
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
        
        //add aggragation
        if(!aggregation.equals("")){
            temp+=aggregation+"("+aggregationAttr+")";
            l = maxAggr - aggregation.length() - aggregationAttr.length() -2;  
        }
        else
            l = maxAggr;  
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
