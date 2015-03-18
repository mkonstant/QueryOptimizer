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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Vector;
import java.util.concurrent.CompletionException;
import myExceptions.ComplexConditionException;
import myExceptions.JoinActionException;
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
   
   
   boolean s1=false,s2=false,h1=false,h2=false,i1=false,i2=false;
    public Join() {
        operation = "join";
    }
   
     public Join(Join old,Map<Operator,Operator> update) { 
        operation = "join";
        this.relation1 = old.getRelation1();
        if(relation1==null){
            this.relationOp1 = update.get(old.getRelationOp1()) ;
        }
        this.relation2 = old.getRelation2();
        if(relation2==null){
            this.relationOp2 = update.get(old.getRelationOp2()) ;
        }
        this.tInfo1 = old.getOutTableInfo1().fullCopy();
        this.tInfo2 = old.getOutTableInfo2().fullCopy();
        
        this.conditions = old.getConditionCopy(old.getConditions());
        if(old.getComplexCondtion()!=null)
            this.complexCondtion = new String(old.getComplexCondtion());
        
    }
     
   
    @Override
    public Operator fullCopy(Map<Operator,Operator> update){
        //System.out.println("join");
        Join temp =  new Join(this,update);
        update.put(this,temp);
        return temp;
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
    public void AddCondition(Condition c){
        conditions.add(c);
        if(conditions.size()==2)
            complexCondtion="and";
    }
    
    @Override
    public void RemoveCondition(Condition c){
        conditions.remove(c);
        if(conditions.size()==1)
            complexCondtion=null;
    }
    
    @Override
    public ArrayList<Condition> getConditions(){
        return conditions;
    }
    
    public void setCondition(ArrayList<Condition> con){
        conditions=con;
    }
    
    
    @Override
    public void updateRelOp(Operator _old, Operator _new){
        if(relationOp1!=null && relationOp1==_old)
            relationOp1 = _new;
    
        if(relationOp2!=null && relationOp2==_old)
            relationOp2 = _new;
    }
    
    @Override
    public void computeAttributes(){
        neededAttributes1 = new ArrayList<String>();
        neededAttributes2 = new ArrayList<String>();
        for(int i=0; i<conditions.size(); i++){
            neededAttributes1.add(conditions.get(i).getAttr1());
            neededAttributes2.add(conditions.get(i).getAttr2()); 
        }
        relAttributes1 = new ArrayList<String>();
        relAttributes2 = new ArrayList<String>();
        
        Map<String,Attributes> temp1 = tInfo1.getAttributes();
        Map<String,Attributes> temp2 = tInfo2.getAttributes();
        for(String key1 : temp1.keySet()){
            relAttributes1.add(key1);
        }
        for(String key2 : temp2.keySet()){
            relAttributes2.add(key2);
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
        ArrayList<Double> allCosts = new ArrayList<Double>();
        ArrayList<String> messages = new ArrayList<String>();
        ArrayList<Boolean> allSorted = new ArrayList<Boolean>();
        ArrayList<Set<String>> allSortedKey = new ArrayList<Set<String>> ();
        Set <String> allAttr1 = new HashSet<String>();
        Set <String> allAttr2 = new HashSet<String>();
        ArrayList <IndexInfo> indexes1 = null;
        ArrayList <IndexInfo> indexes2 = null;
        outTable = new TableInfo();
        joinCompatible();
        int minNumCost = -1;
        Map<String,String> attributes = new HashMap<String,String>();
        
        if ( this.complexCondtion == null ){
            allAttr1.add(conditions.get(0).getAttr1());
            allAttr2.add(conditions.get(0).getAttr2());
            attributes.put(conditions.get(0).getAttr1(), conditions.get(0).getAttr2());
            
            indexes1 = tInfo1.findAllIndexes(allAttr1);
            indexes2 = tInfo2.findAllIndexes(allAttr2);
            
            if ( indexes1 != null && indexes2 != null){
                for ( int j = 0 ; j < indexes1.size() ; j ++ ){
                    for ( int k = 0 ; k < indexes2.size() ; k ++ ){
                        jCost = new JoinCost(catalog.getSystemInfo(), conditions.get(0), tInfo1,tInfo2,indexes1.get(j),indexes2.get(k),attributes);
                        jCost.computeCost();
                        allCosts.add(jCost.getCost());
                        messages.add(jCost.getMessage());
                        allSorted.add(jCost.getSorted());
                        allSortedKey.add(jCost.getSortKey());
                    }
                }
            }
            else{
                if ( indexes1 != null ){
                    for ( int j = 0 ; j < indexes1.size() ; j ++ ){
                        jCost = new JoinCost(catalog.getSystemInfo(), conditions.get(0), tInfo1,tInfo2,indexes1.get(j),null,attributes);
                        jCost.computeCost();
                        allCosts.add(jCost.getCost());
                        messages.add(jCost.getMessage());
                        allSorted.add(jCost.getSorted());
                        allSortedKey.add(jCost.getSortKey());
                    }
                }
                else if ( indexes2 != null ){
                    for ( int j = 0 ; j < indexes2.size() ; j ++ ){
                        jCost = new JoinCost(catalog.getSystemInfo(), conditions.get(0), tInfo1,tInfo2,null,indexes2.get(j),attributes);
                        jCost.computeCost();
                        allCosts.add(jCost.getCost());
                        messages.add(jCost.getMessage());
                        allSorted.add(jCost.getSorted());
                        allSortedKey.add(jCost.getSortKey());
                        
                    }
                }
                else{
                    jCost = new JoinCost(catalog.getSystemInfo(), conditions.get(0), tInfo1,tInfo2,null,null,attributes);
                    jCost.computeCost();
                    allCosts.add(jCost.getCost());
                    messages.add(jCost.getMessage());
                    allSorted.add(jCost.getSorted());
                    allSortedKey.add(jCost.getSortKey());
                }
            }
            
            minNumCost = this.getMinCost(allCosts);
            this.cost = allCosts.get(minNumCost);
            this.annotation = messages.get(minNumCost);
            boolean sorted = allSorted.get(minNumCost);
            if(sorted){
                    outTable.setSortKey(allSortedKey.get(minNumCost));
            }
            outTable.setSorted(sorted);
            
            //System.out.println(" cost = " + allCosts.get(minNumCost) );
            
        }
        else if ( this.complexCondtion.equals("and")){
            
            for( int i = 0 ; i < conditions.size() ; i ++ ){
                allAttr1.add(conditions.get(i).getAttr1());
                allAttr2.add(conditions.get(i).getAttr2());
                attributes.put(conditions.get(i).getAttr1(), conditions.get(i).getAttr2());
                
            }
            
            indexes1 = tInfo1.findAllIndexes(allAttr1);
            indexes2 = tInfo2.findAllIndexes(allAttr2);
            
            if ( indexes1 != null && indexes2 != null){
                for ( int j = 0 ; j < indexes1.size() ; j ++ ){
                    for ( int k = 0 ; k < indexes2.size() ; k ++ ){
                        jCost = new JoinCost(catalog.getSystemInfo(), conditions.get(0), tInfo1,tInfo2,indexes1.get(j),indexes2.get(k),attributes);
                        jCost.computeCost();
                        allCosts.add(jCost.getCost());
                        messages.add(jCost.getMessage());
                        allSorted.add(jCost.getSorted());
                        allSortedKey.add(jCost.getSortKey());
                    }
                }
            }
            else{
                if ( indexes1 != null ){
                    for ( int j = 0 ; j < indexes1.size() ; j ++ ){
                        jCost = new JoinCost(catalog.getSystemInfo(), conditions.get(0), tInfo1,tInfo2,indexes1.get(j),null,attributes);
                        jCost.computeCost();
                        allCosts.add(jCost.getCost());
                        messages.add(jCost.getMessage());
                        allSorted.add(jCost.getSorted());
                        allSortedKey.add(jCost.getSortKey());
                    }
                }
                else if ( indexes2 != null ){
                    for ( int j = 0 ; j < indexes2.size() ; j ++ ){
                        jCost = new JoinCost(catalog.getSystemInfo(), conditions.get(0), tInfo1,tInfo2,null,indexes2.get(j),attributes);
                        jCost.computeCost();
                        allCosts.add(jCost.getCost());
                        messages.add(jCost.getMessage());
                        allSorted.add(jCost.getSorted());
                        allSortedKey.add(jCost.getSortKey());
                    }
                }
                else{
                    jCost = new JoinCost(catalog.getSystemInfo(), conditions.get(0), tInfo1,tInfo2,null,null,attributes);
                    jCost.computeCost();
                    allCosts.add(jCost.getCost());
                    messages.add(jCost.getMessage());
                    allSorted.add(jCost.getSorted());
                    allSortedKey.add(jCost.getSortKey());
                }
            }
            
            minNumCost = this.getMinCost(allCosts);
            this.cost = allCosts.get(minNumCost);
            this.annotation = messages.get(minNumCost);
            boolean sorted = allSorted.get(minNumCost);
            if(sorted){
                    outTable.setSortKey(allSortedKey.get(minNumCost));
            }
            outTable.setSorted(sorted);
        
        }
        else{//or condition
            for ( int i = 0 ; i < conditions.size() ; i ++ ){
                allAttr1.add(conditions.get(i).getAttr1());
                allAttr2.add(conditions.get(i).getAttr2());

                indexes1 = tInfo1.findAllIndexes(allAttr1);
                indexes2 = tInfo2.findAllIndexes(allAttr2);

                if ( indexes1 != null && indexes2 != null){
                    for ( int j = 0 ; j < indexes1.size() ; j ++ ){
                        for ( int k = 0 ; k < indexes2.size() ; k ++ ){
                            jCost = new JoinCost(catalog.getSystemInfo(), conditions.get(0), tInfo1,tInfo2,indexes1.get(j),indexes2.get(k),attributes);
                            jCost.computeCost();
                            allCosts.add(jCost.getCost());
                            messages.add(jCost.getMessage());
                        }
                    }
                }
                else{
                    if ( indexes1 != null ){
                        for ( int j = 0 ; j < indexes1.size() ; j ++ ){
                            jCost = new JoinCost(catalog.getSystemInfo(), conditions.get(0), tInfo1,tInfo2,indexes1.get(j),null,attributes);
                            jCost.computeCost();
                            allCosts.add(jCost.getCost());
                            messages.add(jCost.getMessage());
                        }
                    }
                    else if ( indexes2 != null ){
                        for ( int j = 0 ; j < indexes2.size() ; j ++ ){
                            jCost = new JoinCost(catalog.getSystemInfo(), conditions.get(0), tInfo1,tInfo2,null,indexes2.get(j),attributes);
                            jCost.computeCost();
                            allCosts.add(jCost.getCost());
                            messages.add(jCost.getMessage());
                        }
                    }
                    else{
                        jCost = new JoinCost(catalog.getSystemInfo(), conditions.get(0), tInfo1,tInfo2,null,null,attributes);
                        jCost.computeCost();
                        allCosts.add(jCost.getCost());
                        messages.add(jCost.getMessage());
                    }
                }
                
                minNumCost = this.getMinCost(allCosts);
                this.cost += allCosts.get(minNumCost);
                this.annotation = this.annotation + "con " + i + ":" + messages.get(minNumCost) + "|";
                allCosts = null;
                messages = null;
                
                allCosts = new ArrayList<Double>();
                messages = new ArrayList<String>();
            }
            
        
        }
            
        
        /*Map<String,TableInfo> table = catalog.getCatalog();
        
        jCost = new JoinCost(catalog.getSystemInfo(), tInfo1.getNumberOfTuples(),tInfo1.getSizeOfTuple(),
                                        tInfo2.getNumberOfTuples(),tInfo2.getSizeOfTuple());
        findIndexed();
        
        //change s1,s2 etc
        cost = jCost.computeCost(s1,s2,h1,h2,i1,i2);
        outTable.setSorted(jCost.getSorted());  //if output is sorted
        annotation = jCost.getAnnotation();*/
    }
    
    /*public void findIndexed(){
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
            if(secondaryIndex!=null)
            {
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
        
        
        
        
    }*/
    

    
    public void joinCompatible(){
        Map<String,TableInfo> table = catalog.getCatalog();
        
        int n1=0,n2=0;
        ArrayList<String> prKey;
       
        
        //no join on disjunctions of conditons
        //if(complexCondtion!=null && complexCondtion.equals("or"))
          //  throw new ComplexConditionException(null);
        
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
        ArrayList<String> conAttr2 = new ArrayList<String>();
        
        for(int i=0;i<conditions.size();i++){
            String type1;
            String type2;
            Condition c = conditions.get(i);
            if (! c.action.equals("=")){
                throw new JoinActionException();
            }
            if(!attributes1.containsKey(c.getAttr1()))
                    throw new JoinAttributeException(c.getAttr1());
            else
                type1 = attributes1.get(c.getAttr1()).getType();
            
            conAttr2.add(c.getAttr2());
            if(!attributes2.containsKey(c.getAttr2()))
                    throw new JoinAttributeException(c.getAttr2());
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
            if(!conAttr2.contains(key1) )
                outAttributes.put(key1, attributes2.get(key1));
        }
        
        
        outTable.setAttributes(outAttributes);
        //join attributes will be common
        outTable.setCardinality(outAttributes.size());
        //compute new tupple size from the type of Atributes given 
        outTable.setSizeOfTuple();
        //outTable.setKey(tInfo1.getKey());
        n1 = tInfo1.getNumberOfTuples();
        n2 = tInfo2.getNumberOfTuples();       
        
        //overestimation...max number of tupples the max of the two
        if(n1>n2)
            outTable.setNumberOfTuples(n1);
        else
            outTable.setNumberOfTuples(n2);
        outTable.setOperator(true);
        
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
