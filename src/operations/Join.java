/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operations;

import catalog.Attributes;
import catalog.IndexInfo;
import catalog.TableInfo;
import evaluationCost.JoinCost;
import evaluationCost.SetOperationCost;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Spliterator;
import java.util.Vector;
import myExceptions.JoinAttributeException;
import myExceptions.JoinAttributeTypeException;
import myExceptions.RelationException;

/**
 *
 * @author michalis
 */
public  class Join extends Operator{
    
     //only one of the following 2 will be null
    String relation1 = null;
    Operator relationOp1=null;
    
    //only one of the following 2 will be null
    String relation2= null;
    Operator relationOp2=null;
    
   ArrayList<Condition> conditions = null;
   JoinCost jCost;
   TableInfo tInfo1,tInfo2;
   
   boolean s1=false,s2=false,h1=false,h2=false,i1=false,i2=false;
    public Join() {
        operation = "join";
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
            conditionsPrint += conditions.get(i).toPrint();
	}
       
        //add relation1
     
        if(relationOp1==null)
            relationPrint1+=relation1;
        else
            relationPrint1+= relationOp1.getOpName();
        
          //add relation2
        if(relationOp2==null)
            relationPrint2+= relation2;
        else
            relationPrint2+= relationOp2.getOpName();
    
    }
    
    
    @Override
    public void computeCost(){
        outTable = new TableInfo();
        joinCompatible();
        
        Map<String,TableInfo> table = catalog.getCatalog();
        
        jCost = new JoinCost(catalog.getSystemInfo(), tInfo1.getNumberOfTuples(),tInfo1.getSizeOfTuple(),
                                        tInfo2.getNumberOfTuples(),tInfo2.getSizeOfTuple());
        findIndexed();
        jCost.computeCost(s1,s2,h1,h2,i1,i2);
        outTable.setSorted(jCost.getSorted());  //if output is sorted
        annotation = jCost.getAnnotation();
    }
    
    public void findIndexed(){
        //suppose only one condition
        Condition c = conditions.get(0);
        String att1,att2;
        ArrayList<String> key;
        IndexInfo iInfo;
        
        if(relation1==null)//i have operation output
            s1=tInfo1.getSorted();
        else{
            if(c.getAttr1().contains("."))
                att1 = (c.getAttr1().split("\\."))[1];
            else 
                att1=c.getAttr1();
            
            //the attribute has primary index
            iInfo= tInfo1.getPrimaryIndex();
            if(iInfo.equalsKey(att1)){
                i1=true;
                if(iInfo.getStructure().equals("B+tree"))
                    s1=true;
                else
                    h1=true;
            }
            Map<String,IndexInfo> secondaryIndex = tInfo1.getSecondaryIndex();
            for(String key1 : secondaryIndex.keySet()){
                IndexInfo indexInfo = secondaryIndex.get(key1);
                if(indexInfo.equalsKey(att1)){
                    i1=true;
                    if(!indexInfo.getStructure().equals("B+tree")){
                        h1=true;
                        break;
                    }
                }
            }
            
        }
        
        if(relation2==null)//i have operation output
            s2=tInfo2.getSorted();
        else{
            if(c.getAttr2().contains("."))
                att2 = (c.getAttr2().split("\\."))[1];
            else 
                att2=c.getAttr2();
            
            //the attribute has primary index
            iInfo= tInfo2.getPrimaryIndex();
            if(iInfo.equalsKey(att2)){
                i2=true;
                if(iInfo.getStructure().equals("B+tree"))
                    s2=true;
                else
                    h2=true;
            }
            Map<String,IndexInfo> secondaryIndex = tInfo2.getSecondaryIndex();
            for(String key1 : secondaryIndex.keySet()){
                IndexInfo indexInfo = secondaryIndex.get(key1);
                if(indexInfo.equalsKey(att2)){
                    i2=true;
                    if(!indexInfo.getStructure().equals("B+tree")){
                        h2=true;
                        break;
                    }
                }
            }
            
        }
        
        
        
        
    }
    

    
    public void joinCompatible(){
        Map<String,TableInfo> table = catalog.getCatalog();
        
        int n1=0,n2=0;
        ArrayList<String> prKey;
       
        
        //get the right tableInfo from relation or operation output
        if(relation1!=null)//i have to deal with a database relation
        {
           if(table.containsKey(relation1))//error if not exists
           {
               tInfo1 = table.get(relation1);
           }
           else{
               throw new RelationException(relation1);
           }
        }
        else{//i have to deal with an operation's output
            tInfo1 = relationOp1.getOutTable();
        }
        if(relation2!=null)//i have to deal with a database relation
        {
           if(table.containsKey(relation2))//error if not exists
           {
               tInfo2 = table.get(relation2);   
           }
           else{
               throw new RelationException(relation2);
           }
        }
        else{//i have to deal with an operation's output
            tInfo2 = relationOp2.getOutTable();
        }
                
        //in order to perform join,join attributes should be same type   
        Map<String,Attributes> attributes1 = tInfo1.getAttributes();
        Map<String,Attributes> attributes2 = tInfo2.getAttributes();
        
        for(int i=0;i<conditions.size();i++){
            String type1;
            String type2;
            Condition c = conditions.get(i);
            if(!attributes1.containsKey(c.getAttr1())){
                if(c.getAttr1().contains(".") && relation1 !=null){                  
                    String[] relAt= c.getAttr1().split("\\.");
                    if(!(relAt[0].equals(relation1) && attributes1.containsKey(relAt[1])))
                        throw new JoinAttributeException(c.getAttr1());  
                    type1 = attributes1.get(relAt[1]).getType();
                }
                else
                    throw new JoinAttributeException(c.getAttr1());
            }
            else
                type1 = attributes1.get(c.getAttr1()).getType();
            if(!attributes2.containsKey(c.getAttr2())){
                if(c.getAttr2().contains(".") && relation2 !=null){                  
                    String[] relAt= c.getAttr2().split("\\.");
                    if(!(relAt[0].equals(relation2) && attributes1.containsKey(relAt[1])))
                        throw new JoinAttributeException(c.getAttr2());  
                    type2 = attributes2.get(relAt[1]).getType();
                }
                else
                    throw new JoinAttributeException(c.getAttr2());
            }
            else
                type2 = attributes2.get(c.getAttr2()).getType();
            
            if(!type1.equals(type2))
                throw new JoinAttributeTypeException(c.getAttr1(),c.getAttr2());
        }
        
        //find the set of attributes of output operation
        Map<String,Attributes> outAttributes = new HashMap<String,Attributes>();
        for(String key1 : attributes1.keySet()){
            outAttributes.put(key1, attributes1.get(key1));
        }
        for(String key1 : attributes2.keySet()){
            String rel="";
            if(relation2!=null){
                rel = relation2;
            }
            if(!(conditions.contains(key1) || conditions.contains(rel+"."+key1)))
                outAttributes.put(key1, attributes2.get(key1));
        }
        
        
        outTable.setAttributes(outAttributes);
        //join attributes will be common
        outTable.setCarinality(outAttributes.size());
        //compute new tupple size from the type of Atributes given 
        outTable.setSizeOfTuple();
        outTable.setKey(tInfo1.getKey());
        n1 = tInfo1.getNumberOfTuples();
        n2 = tInfo2.getNumberOfTuples();       
        
        //overestimation...max number of tupples the max of the two
        if(n1>n2)
            outTable.setNumberOfTuples(n1);
        else
            outTable.setNumberOfTuples(n2);
        
    }
        

     
   
}
