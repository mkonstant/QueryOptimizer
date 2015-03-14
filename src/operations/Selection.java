/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operations;

import catalog.Attributes;
import catalog.IndexInfo;
import catalog.TableInfo;
import evaluationCost.SelectCost;
import java.util.ArrayList;
import java.util.Map;
import myExceptions.GroupAttributeException;
import myExceptions.SelectAttributeException;

/**
 *
 * @author michalis
 */
public class Selection extends Operator {
    
    //only one of the following 2 will be null
    String relation1 = null;
    Operator relationOp1 = null;
    Map<String,TableInfo> table = null;
    
    ArrayList<Condition> conditions = null;

    public Selection() {
        operation = "sel";
    }
    
    public Selection(Selection old,Map<Operator,Operator> update) { 
        operation = "sel";
        this.relation1 = old.getRelation1();
        if(relation1==null){
            this.relationOp1 = update.get(old.getRelationOp1()) ;
        }

        this.conditions = old.getConditionCopy(old.getConditions());
        this.tInfo1 = old.getOutTableInfo1().fullCopy();
        
        
        //xreiazomai kati extra??????
        
    }
     
   
    @Override
    public Operator fullCopy(Map<Operator,Operator> update){
       // System.out.println("projrction");
        Selection temp =  new Selection(this,update);
        update.put(this,temp);
        return temp;
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
    public void computeAttributes(){
        neededAttributes1 = new ArrayList<String>();
        for(int i=0; i>conditions.size(); i++){
            neededAttributes1.add(conditions.get(i).getAttr1());
        }
        outputAttributes = new ArrayList<String>();
        Map<String,Attributes> temp = tInfo1.getAttributes();
        for(String key1 : temp.keySet()){
            outputAttributes.add(key1);
        }
    }

    @Override
    protected void prePrint(){
        
         //add conditions 
        conditionsPrint="";
        for (int i = 0; i < conditions.size(); i++)
        {
            if(i>0)
                conditionsPrint+=" "+complexCondtion+" ";
            conditionsPrint += conditions.get(i).toPrint();
	}
        
        //add relation1
     
        if(relationOp1==null)
            relationPrint1+= relation1;
        else
            relationPrint1+= relationOp1.getOpName();
    
    }
    
    @Override
    public void computeCost(){
        SelectCost selCost = null;
        ArrayList <String> allAttr = new ArrayList<String>();
        boolean equalPrimary = false;
        ArrayList<Double> costs = null;
        ArrayList<String> messages = null;
        ArrayList<IndexInfo> indexes = null;
        
        this.checkVariables();
        
        if ( this.complexCondtion == null ){
            allAttr.add(conditions.get(0).getAttr1());
            
            //index = tInfo1.findBestIndex(allAttr);
            indexes = tInfo1.findAllIndexes(allAttr.get(0));
            
            equalPrimary = tInfo1.equalPrimaryKey(allAttr);
            
            
            selCost = new SelectCost(conditions.get(0),tInfo1,catalog.getSystemInfo(),indexes,equalPrimary,allAttr.size());
            selCost.calculateCost();
            this.cost = selCost.getCost();
            this.annotation = selCost.getMessage();
        }
        else{
            costs = new ArrayList<Double>();
            messages = new ArrayList<String>();
            for ( int i = 0 ; i < conditions.size() ; i ++ ){
                allAttr.add(conditions.get(i).getAttr1());
            }
            
            
            
            for ( int i = 0 ; i < conditions.size() ; i ++ ){
                if(!tInfo1.getOperator())                
                    indexes = tInfo1.findAllIndexes(allAttr.get(i));
                else
                    indexes =null;
                
                selCost = new SelectCost(conditions.get(i),tInfo1,catalog.getSystemInfo(),indexes,equalPrimary,allAttr.size());
                selCost.calculateCost();
                costs.add(selCost.getCost());
                messages.add(selCost.getMessage());
            }
            
            if (complexCondtion.contains("and")){
                int minNumCost = this.getMinCost(costs);
                this.cost = costs.get(minNumCost);
                this.annotation = messages.get(minNumCost);
            }
            else{
                String str = null;
                boolean FLAG = false;
                
                for( int i = 0 ; i < costs.size() ; i ++ ){
                    this.cost = this.cost + costs.get(i);
                    if ( FLAG == false ){
                        FLAG = true;
                        str = messages.get(i);
                    }
                    else{
                        str = str + "->" + messages.get(i);
                    }
                }
                
                this.annotation = str;
            }
                    
        }
        outTable = new TableInfo();
        outTable.setAttributes(tInfo1.getAttributes());
        //same output , same tupple size
        outTable.setSizeOfTuple(tInfo1.getSizeOfTuple());
             
        outTable.setKey(tInfo1.getKey());
        //same numbert of attributes on output
        outTable.setCardinality(tInfo1.getCardinality());
        //what to do with it?????
        outTable.setNumberOfTuples(tInfo1.getNumberOfTuples());
        //outTable.setSorted(grCost.getSorted());  //if output is sorted
        outTable.setOperator(true);
        System.out.println("cost = " + this.cost + " message = " + this.annotation);   
        
    }
    
    public void checkVariables( ){
        table = catalog.getCatalog();
        tInfo1 = null;
        String del = "//.";
        String []temp = null;
        
        
        if(relation1!=null) {
            tInfo1 = table.get(relation1);
        }
        else{
            tInfo1 = relationOp1.getOutTable();
        }
        
        if ( this.complexCondtion == null ){
            if ( conditions.get(0).getAttr1().contains(".")){
                temp = conditions.get(0).getAttr1().split(del);
                if ( !tInfo1.getAttributes().containsKey(temp[1])){
                    throw new SelectAttributeException(conditions.get(0).getAttr1());
                }
            }
            else{
                if ( !tInfo1.getAttributes().containsKey(conditions.get(0).getAttr1())){
                    throw new SelectAttributeException(conditions.get(0).getAttr1());
                }
            }
        }
        else{
            for ( int i = 0 ; i < conditions.size() ; i ++ ){
                if ( conditions.get(i).getAttr1().contains(".")){
                    temp = conditions.get(i).getAttr1().split(del);
                    if ( !tInfo1.getAttributes().containsKey(temp[1])){
                        throw new SelectAttributeException(conditions.get(i).getAttr1());
                    }
                }
                else{
                    if ( !tInfo1.getAttributes().containsKey(conditions.get(i).getAttr1())){
                        throw new SelectAttributeException(conditions.get(i).getAttr1());
                    }
                }
            }
        }
    }
    
    public int getMinCost( ArrayList<Double> costs){
        double minCost = -1;
        int position = -1;
        
        if ( costs.size() > 1 ){
            minCost = costs.get(0);
            position = 0;
            for ( int i = 1 ; i < costs.size() ; i ++ ){
                if ( minCost > costs.get(i)){
                    minCost = costs.get(i);
                    position = i;
                }
            }
            return position;
        }
        else{
            return 0;
        }
    }
    
}
