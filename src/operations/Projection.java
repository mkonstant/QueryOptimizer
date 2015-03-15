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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
    
    public Projection(Projection old,Map<Operator,Operator> update) { 
        operation = "proj";
        this.relation1 = old.getRelation1();
        if(relation1==null){
            this.relationOp1 = update.get(old.getRelationOp1()) ;
        }
        
        this.tInfo1 = old.getOutTableInfo1().fullCopy();
        
        this.attrs = old.getAttrsCopy(old.getAttrs());
        
    }
     
   
    @Override
    public Operator fullCopy(Map<Operator,Operator> update){
       // System.out.println("projrction");
        Projection temp =  new Projection(this,update);
        update.put(this,temp);
        return temp;
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
    public void setAttributes(ArrayList<String> attrs){
        this.attrs = attrs;
    }
    
    @Override
    public void computeAttributes(){
        neededAttributes1  = attrs;
        outputAttributes = attrs;
    }
    
    @Override
    public void updateRelOp(Operator _old, Operator _new){
        if(relationOp1!=null && relationOp1==_old)
            relationOp1 = _new;
    
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
            relationPrint1=relation1;
        else
            relationPrint1= relationOp1.getOpName();
    }
   
    
    @Override
    public void computeCost(){
        outTable = new TableInfo();
        Map<String,TableInfo> table = catalog.getCatalog();
        boolean projOnkey=true;
       
        Set<String> prKey;
       

               
        if(relation1!=null)//i have to deal with a database relation
        {
           if(table.containsKey(relation1))//error is not exists
           {
               tInfo1 = table.get(relation1);
               prKey = tInfo1.getKey();
               for(int i =0;i<attrs.size();i++){
                   if(!prKey.contains(attrs.get(i))){
                       projOnkey=false;
                       break;
                   }
               }
               if(attrs.size()!= prKey.size())
                   projOnkey=false;
           }
           else{
                throw new RelationException("Relation '"+relation1+"' does not exist.");
           }
        }
        else{//i have to deal with an operation's output
            tInfo1 = relationOp1.getOutTable();
            projOnkey = false;
        }
         prCost = new ProjectionCost(catalog.getSystemInfo(),tInfo1);
        
        //check if projection attribute exist in relation
        Map<String,Attributes> attributes = tInfo1.getAttributes();
        Map<String,Attributes> outAttributes = new HashMap<String,Attributes>();
        
        for(int i=0; i<attrs.size();i++){
            if(!attributes.containsKey(attrs.get(i)))
                throw new ProjectionAttributeException(attrs.get(i));
            else
                outAttributes.put(attrs.get(i), attributes.get(attrs.get(i)));       
        }
        
        
        cost = prCost.computeCost(projOnkey,tInfo1.isSortedOnKey(attrs),tInfo1.isHashedOnKey(attrs));
        annotation = prCost.getAnnotation();

        
        outTable.setAttributes(outAttributes);
        //compute new tupple size from the type of Atributes given 
        outTable.setSizeOfTuple();
             
        outTable.setKey(tInfo1.getKey());
        //overestimation if duplicate elimination is performed
        outTable.setCardinality(attrs.size());
        outTable.setNumberOfTuples(tInfo1.getNumberOfTuples());
        boolean sorted = prCost.getSorted();
        if(sorted){
            if(prCost.getSortKey() == null){
                Set<String> sortKey= new HashSet<String>();
                for(int i = 0;i<attrs.size();i++){
                    sortKey.add(attrs.get(i));
                }
                outTable.setSortKey(sortKey);
            }
            else
                outTable.setSortKey(prCost.getSortKey());
        }
        outTable.setSorted(sorted);  //if output is sorted
        outTable.setOperator(true);
       
    
    }
    
   
}
