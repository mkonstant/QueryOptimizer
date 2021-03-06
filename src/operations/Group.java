/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operations;

import catalog.Attributes;
import catalog.IndexInfo;
import catalog.TableInfo;
import evaluationCost.GroupCost;
import evaluationCost.ProjectionCost;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import myExceptions.GroupAttributeException;
import myExceptions.ProjectionAttributeException;
import myExceptions.RelationException;
import syntaxtree.HavingClause;

/**
 *
 * @author michalis
 */
public  class Group extends Operator{
    
    ArrayList<String> attrs = null;
    ArrayList<Condition> conditions = null;
    
    private boolean hasHavingClause=false;
    
    //only one of the following 2 
    String relation1 = null;
    Operator relationOp1=null;

    public Group() {
        operation = "group";
    }
    
    
    public Group(Group old,Map<Operator,Operator> update) { 
        operation = "group";
        this.relation1 = old.getRelation1();
        if(relation1==null){
            this.relationOp1 = update.get(old.getRelationOp1()) ;
        }

        this.attrs = old.getAttrsCopy(old.getAttrs());
        this.hasHavingClause = old.getHasHavingClause();
        if(hasHavingClause)
        
        this.conditions = old.getConditionCopy(old.getConditions());
        this.tInfo1 = old.getOutTableInfo1().fullCopy();
        this.aggregation = new String(old.getAggregation());
        this.aggregationAttr= new String(old.getAggregationAttr());
    }
     
   
    @Override
    public Operator fullCopy(Map<Operator,Operator> update){
       // System.out.println("projrction");
        Group temp =  new Group(this,update);
        update.put(this,temp);
        return temp;
    } 
    
 
    
    
    @Override
    public boolean getHasHavingClause(){
        return hasHavingClause;
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
    public void setAggregation(String agr){
        aggregation =agr;
    }
    
    @Override
    public void setAggregationAttr(String agr){
        aggregationAttr =agr;
    }
    
    @Override
    public String getAggregation(){
        return aggregation;
    }

    @Override
    public String getAggregationAttr(){
        return aggregationAttr;
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
    public void AddCondition(String attr1, String attr2, String action){
        if(conditions==null){
            hasHavingClause = true;
            conditions = new ArrayList<Condition>();
        }
        conditions.add(new Condition(attr1, attr2, action));
    }
    
    
    @Override
    public ArrayList<Condition> getConditions(){
        return conditions;
    }
    
    
    @Override
    public void updateRelOp(Operator _old, Operator _new){
        if(relationOp1!=null && relationOp1==_old)
            relationOp1 = _new;
    
    }
        
    @Override
    public void computeAttributes(){
        Map<String,Attributes> temp = tInfo1.getAttributes();
        neededAttributes1 = new ArrayList<String>();
        for(int i=0;i<attrs.size();i++){
            neededAttributes1.add(attrs.get(i));
        }
        if(!aggregationAttr.equals(""))
            neededAttributes1.add(aggregationAttr);
        if(conditions!=null){
            String tempattr;
            for(int i=0; i>conditions.size(); i++){   //needed???
                tempattr = conditions.get(i).getAttr1();
                if(!neededAttributes1.contains(tempattr))
                    neededAttributes1.add(tempattr);
            }
        }
        
        outputAttributes = new ArrayList<String>();
        for(String key1 : temp.keySet()){
            outputAttributes.add(key1);
        }        
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
        
       //add conditions 
        if(conditions!=null){
            conditionsPrint="";
            for (int i = 0; i < conditions.size(); i++)
            {
                if(i>0)
                    conditionsPrint+=" "+complexCondtion+" ";
                conditionsPrint += conditions.get(i).toPrint();
            }
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
        tInfo1=null;
        
        
        if(relation1!=null)//i have to deal with a database relation
        {
           if(table.containsKey(relation1))//error is not exists
               tInfo1 = table.get(relation1);
           else
                throw new RelationException("Relation '"+relation1+"' does not exist.");
        }
        else{//i have to deal with an operation's output
            tInfo1 = relationOp1.getOutTable();
        }

                       
        
        
        //check if groupBy,having and aggregate attributes all exist in relation
        Map<String,Attributes> attributes = tInfo1.getAttributes();
        
        for(String key1 : attributes.keySet()){
            if(!attrs.contains(key1)){
                if(!key1.equals(aggregationAttr))
                    throw new GroupAttributeException(key1,0);
            }
            else{
                 if(key1.equals(aggregationAttr))
                    throw new GroupAttributeException(key1,1);
            }
        }
        if(attributes.size()!=attrs.size()+1)
            throw new GroupAttributeException("",2);
        
        if(hasHavingClause){
            Condition c;
            for(int i=0; i<conditions.size();i++){
                c = conditions.get(i);
                if(!attrs.contains(c.getAttr1())){
                    if(!c.getAttr1().equals(aggregation+"("+aggregationAttr+")"))
                        throw new GroupAttributeException(c.getAttr1(),3);
                }
                else{
                     if(c.getAttr1().equals(aggregationAttr))
                        throw new GroupAttributeException(c.getAttr1(),4);
                }
            }
            //if(conditions.size()>attrs.size()+1)
            //     throw new GroupAttributeException("",5);    

        }
        
        
        GroupCost grCost = new GroupCost(catalog.getSystemInfo(), tInfo1);
        
        cost = grCost.computeCost(tInfo1.isSortedOnKey(attrs) , tInfo1.isHashedOnKey(attrs));
        annotation = grCost.getAnnotation();

        
        outTable.setAttributes(tInfo1.getAttributes());
        //same output , same tupple size
        outTable.setSizeOfTuple(tInfo1.getSizeOfTuple());
             
        outTable.setKey(tInfo1.getKey());
        //same numbert of attributes on output
        outTable.setCardinality(tInfo1.getCardinality());
        //what to do with it?????
        outTable.setNumberOfTuples(tInfo1.getNumberOfTuples());
        boolean sorted = grCost.getSorted();
        if(sorted){
            if(grCost.getSortKey() == null){
                Set<String> sortKey= new HashSet<String>();
                for(int i = 0;i<attrs.size();i++){
                    sortKey.add(attrs.get(i));
                }
                outTable.setSortKey(sortKey);
            }
            else
                outTable.setSortKey(grCost.getSortKey());
        }
        outTable.setOperator(true);
       
    
    }
    
    
    
    
    
}
