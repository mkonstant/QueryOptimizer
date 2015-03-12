/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operations;

import catalog.Attributes;
import catalog.TableInfo;
import evaluationCost.SetOperationCost;
import java.util.ArrayList;
import java.util.Map;
import myExceptions.RelationException;

/**
 *
 * @author michalis
 */
public  class SetOp extends Operator{
    
    
    //only one of the following 2 will be null
    String relation1 = null;
    Operator relationOp1=null;
    
    //only one of the following 2 will be null
    String relation2= null;
    Operator relationOp2=null;
    
    boolean s1=true,s2=true,h1=false,h2=false;
    
    SetOperationCost setCost;
    TableInfo tInfo1,tInfo2;
    
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
    public void computeAttributes(){
        Map<String,Attributes> temp = tInfo1.getAttributes();
        neededAttributes1 = new ArrayList<String>();
        for(String key1 : temp.keySet()){
            neededAttributes1.add(key1);
        }
        
        temp = tInfo2.getAttributes();
        neededAttributes2 = new ArrayList<String>();
        for(String key1 : temp.keySet()){
            neededAttributes2.add(key1);
        }
        
        outputAttributes = neededAttributes1; 
    }
    
    @Override
    protected void prePrint(){
       
        //add relation1
     
        if(relationOp1==null)
            relationPrint1=relation1;
        else
            relationPrint1= relationOp1.getOpName();
        
          //add relation2
        if(relationOp2==null)
            relationPrint2= relation2;
        else
            relationPrint2= relationOp2.getOpName();
    
    }
    
    @Override
    public void computeCost(){
        outTable = new TableInfo();
        unionCompatible();
        
        Map<String,TableInfo> table = catalog.getCatalog();
        
        setCost = new SetOperationCost(catalog.getSystemInfo(), tInfo1,tInfo2);
        cost = setCost.computeCost();
        outTable.setSorted(setCost.getSorted());  //if output is sorted
        annotation = setCost.getAnnotation();
    }
    
    
    public void createOutput(){
        
    }
    
    
    public void unionCompatible(){
        Map<String,TableInfo> table = catalog.getCatalog();
        
        int n1=0,n2=0;
        ArrayList<String> prKey;
       
        
       
        if(relation1!=null)//i have to deal with a database relation
        {
           if(table.containsKey(relation1))//error is not exists
               tInfo1 = table.get(relation1);
           else
               throw new RelationException(relation1);
        }
        else   //i have to deal with an operation's output
            tInfo1 = relationOp1.getOutTable();
        
        if(relation2!=null)//i have to deal with a database relation
        {
           if(table.containsKey(relation2))//error is not exists
               tInfo2 = table.get(relation2);   
           else
               throw new RelationException(relation2);
        }
        else//i have to deal with an operation's output
            tInfo2 = relationOp2.getOutTable();
        
        
        outTable.setAttributes(tInfo1.getAttributes());
        outTable.setCardinality(tInfo1.getCardinality());
        outTable.setSizeOfTuple(tInfo1.getSizeOfTuple());
        outTable.setOperator(true);
        outTable.setKey(tInfo1.getKey());
        n1 = tInfo1.getNumberOfTuples();
        n2 = tInfo2.getNumberOfTuples();       
        
        //change it for each operation
        outTable.setNumberOfTuples(n1+n2);
        
        //in order to perform setoperation relations should have the same attributes   
        Map<String,Attributes> attributes1 = tInfo1.getAttributes();
        Map<String,Attributes> attributes2 = tInfo2.getAttributes();
        if(attributes1.size()!=attributes2.size())
            throw new RelationException("Relations have not the same attributes to perform '"+operation+"' operation");
        
        for(String key1 : attributes1.keySet()){
            if(!attributes2.containsKey(key1))
                throw new RelationException("Relations have not the same attributes to perform '"+operation+"' operation");
            else if(!attributes1.get(key1).getType().equals(attributes2.get(key1).getType()))
                throw new RelationException("Relations have not the same attributes to perform '"+operation+"' operation");              
        }
        
        
    }

    
    
}
   
    
    

