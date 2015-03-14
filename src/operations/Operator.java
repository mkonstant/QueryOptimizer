/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operations;

import catalog.Catalog;
import catalog.TableInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    protected ArrayList<String> neededAttributes1=null;
    protected ArrayList<String> neededAttributes2=null;
    protected ArrayList<String> relAttributes1=null;
    protected ArrayList<String> relAttributes2=null;
    protected ArrayList<String> outputAttributes=null;
    
    protected int b1,n1;  
    protected int b2,n2;
    protected int bout, nout;
    
    protected TableInfo outTable;
    protected double cost;
    protected TableInfo tInfo1,tInfo2;

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
    public void computeAttributes(){}
    public void setAttributes(ArrayList<String> attrs){}
    public void updateRelOp(Operator _old, Operator _new){}
    public Operator fullCopy(Map<Operator,Operator> update){return null;}
    public void setCondition(ArrayList<Condition> con){}
    public void setTabInfo1(TableInfo t){
        tInfo1 = t;
    }
    public void setTabInfo2(TableInfo t){
        tInfo2 = t;
    }
    public double getCost(){
        return cost;
    }
    
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
    
    public TableInfo getOutTableInfo1(){
        return tInfo1;
    }
    
    public TableInfo getOutTableInfo2(){
        return tInfo2;
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
        if(annotation!=null)
            return annotation.length();
        return 0;
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
    
    public ArrayList<String> getNeededAttributes1(){
        return neededAttributes1;
    }
    
    public ArrayList<String> getNeededAttributes2(){
        return neededAttributes2;
    }
    
    public ArrayList<String> getRelAttributes1(){
        return relAttributes1;
    }
    
    public ArrayList<String> getRelAttributes2(){
        return relAttributes2;
    }
    
    public ArrayList<String> getOutputAttributes(){
        return outputAttributes;
    }
    
    
    public ArrayList<Condition> getConditionCopy(ArrayList<Condition> old){
        ArrayList<Condition> copy = new ArrayList<Condition>();
        
        for(int i=0;i<old.size();i++){
            copy.add(old.get(i).fullCopy());
        }
        return copy;
    }
    
    public ArrayList<String> getAttrsCopy(ArrayList<String> old){
        ArrayList<String> copy = new ArrayList<String>();
        
        for(int i=0;i<old.size();i++){
            copy.add(new String(old.get(i)));
        }
        return copy;
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
        if(annotation==null)
            annotation="";
        temp+=annotation;
        l = maxAn - annotation.length();        
        for(int i=0;i<l;i++){
                temp+=" ";
        }
        temp+="|";
   
        return temp;
    }
  
    
    
}
