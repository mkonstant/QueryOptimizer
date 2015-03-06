/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operations;

import catalog.Attributes;
import catalog.Catalog;
import catalog.TableInfo;
import evaluationCost.ProjectionCost;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import myExceptions.ProjectionAttributeException;
import myExceptions.RelationException;

/**
 *
 * @author michalis
 */
public  class Projection extends Operator{
    
    //only one of the following 2 will be null
    String relation1 = null;
    Operator relationOp1=null;
    ArrayList<String> attrs = null;
    ProjectionCost prCost ;

    public Projection() {
        operation = "proj";
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
   
    
    @Override
    public void computeCost(){
        outTable = new TableInfo();
        Map<String,TableInfo> table = catalog.getCatalog();
        TableInfo tInfo=null;
        boolean projOnkey=true;
       
        ArrayList<String> prKey;
       

               
        if(relation1!=null)//i have to deal with a database relation
        {
           if(table.containsKey(relation1))//error is not exists
           {
               tInfo = table.get(relation1);
               if(!tInfo.getPrimaryIndex().equalsKey(attrs))
                    projOnkey = false;       
               prCost = new ProjectionCost(catalog.getSystemInfo(),tInfo.getNumberOfTuples(),tInfo.getSizeOfTuple());
               
           }
           else{
                throw new RelationException("Relation '"+relation1+"' does not exist.");
           }
        }
        else{//i have to deal with an operation's output
            tInfo = relationOp1.getOutTable();
            prCost = new ProjectionCost(catalog.getSystemInfo(),tInfo.getNumberOfTuples(),tInfo.getSizeOfTuple());
            projOnkey = tInfo.getSorted();
        }
        
        //check if projection attribute exist in relation
        Map<String,Attributes> attributes = tInfo.getAttributes();
        Map<String,Attributes> outAttributes = new HashMap<String,Attributes>();
        
        for(int i=0; i<attrs.size();i++){
            if(!attributes.containsKey(attrs.get(i)))
                throw new ProjectionAttributeException(attrs.get(i));
            else
                outAttributes.put(attrs.get(i), attributes.get(attrs.get(i)));       
        }
        
        
        prCost.computeCost(projOnkey);
        annotation = prCost.getAnnotation();
        
        
        
        
        outTable.setAttributes(outAttributes);
        //compute new tupple size from the type of Atributes given 
        outTable.setSizeOfTuple();
             
        outTable.setKey(tInfo.getKey());
        //overestimation if duplicate elimination is performed
        outTable.setCarinality(attrs.size());
        outTable.setNumberOfTuples(tInfo.getSizeOfTuple());
        outTable.setSorted(prCost.getSorted());  //if output is sorted
       
       
        
       
    
    
    }
    
   
}
