/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operations;

import catalog.TableInfo;
import evaluationCost.SelectCost;
import java.util.ArrayList;
import java.util.Map;
import myExceptions.SelectAttributeException;

/**
 *
 * @author michalis
 */
public class Selection extends Operator {
    
    //only one of the following 2 will be null
    String relation1 = null;
    Operator relationOp1 = null;
    TableInfo tabInfo = null; 
    ArrayList<Double> allCosts = null;
    ArrayList<String> messages = null;
    Map<String,TableInfo> table = null;
    
    ArrayList<Condition> conditions = null;

    public Selection() {
        operation = "sel";
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
        allCosts = new ArrayList<Double>();
        messages = new ArrayList<String>();
        
        this.checkVariables();
        if ( this.complexCondtion == null ){
            if(relation1!=null) {
                tabInfo = table.get(relation1);
            }
            else{
                tabInfo = relationOp1.getOutTable();
            }
            
            selCost = new SelectCost(conditions.get(0),tabInfo,catalog.getSystemInfo());
            
        }
        else{
            for ( int i = 0 ; i < conditions.size() ; i ++ ){
                if(relation1!=null) {
                    tabInfo = table.get(relation1);
                }
                else{
                    tabInfo = relationOp1.getOutTable();
                }
                selCost = new SelectCost(conditions.get(i),tabInfo,catalog.getSystemInfo());
            }
                    
        }
        
    }
    
    public void checkVariables( ){
        table = catalog.getCatalog();
        tabInfo = null;
        String del = "//.";
        String []temp = null;
        
        
        if(relation1!=null) {
            tabInfo = table.get(relation1);
        }
        else{
            tabInfo = relationOp1.getOutTable();
        }
        
        if ( this.complexCondtion == null ){
            if ( conditions.get(0).getAttr1().contains(".")){
                temp = conditions.get(0).getAttr1().split(del);
                if ( !tabInfo.getAttributes().containsKey(temp[1])){
                    throw new SelectAttributeException(conditions.get(0).getAttr1());
                }
            }
            else{
                if ( !tabInfo.getAttributes().containsKey(conditions.get(0).getAttr1())){
                    throw new SelectAttributeException(conditions.get(0).getAttr1());
                }
            }
        }
        else{
            for ( int i = 0 ; i < conditions.size() ; i ++ ){
                if ( conditions.get(i).getAttr1().contains(".")){
                    temp = conditions.get(i).getAttr1().split(del);
                    if ( !tabInfo.getAttributes().containsKey(temp[1])){
                        throw new SelectAttributeException(conditions.get(i).getAttr1());
                    }
                }
                else{
                    if ( !tabInfo.getAttributes().containsKey(conditions.get(i).getAttr1())){
                        throw new SelectAttributeException(conditions.get(i).getAttr1());
                    }
                }
            }
        }
    }
    
}
