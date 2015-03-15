/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package evaluationCost;

import catalog.ForeignIndexInfo;
import catalog.IndexInfo;
import catalog.SystemInfo;
import catalog.TableInfo;
import java.util.ArrayList;
import java.util.Set;
import operations.Condition;

/**
 *
 * @author jimakos
 */
public class SelectCost {
    double cost = -1;
    TableInfo tabInfo = null;
    double tS = -1;
    double tT = -1;
    int costFactor = -1;
    int b = -1;
    int br = -1;
    int h = -1;
    int n = -1;
    int overflowBuckets = -1;
    Condition condition = null;
    ArrayList<String> allMessages = new ArrayList<String>();
    ArrayList<Double> allCosts = new ArrayList<Double>();
    ArrayList<Boolean> allSorted = new ArrayList<Boolean>();
    ArrayList<Set<String>> allSortKey =new ArrayList<Set<String>>(); 
    String message;
    SystemInfo sysInfo = null;
    int numOfBlocks = -1;
    IndexInfo index = null;
    boolean equalPrimary = false;
    int numOfConditions = -1;
    ArrayList<IndexInfo> indexes = null;
    boolean sorted=false;
    Set<String> sortKey=null;
    

    public SelectCost(Condition condition, TableInfo tabInfo, SystemInfo sysInfo, ArrayList<IndexInfo> indexes,boolean equalPrimary, int numOfConditions){
        this.condition = condition;
        this.tabInfo = tabInfo;
        this.sysInfo = sysInfo;
        this.indexes = indexes;
        this.equalPrimary = equalPrimary;
        this.numOfConditions = numOfConditions;
    }
    
        public boolean getSorted(){
        return sorted;
    }
    
    public Set<String> getSortKey(){
        return sortKey;
    }
    
    public void calculateVariables(IndexInfo index){
        tS = sysInfo.getLatency();
        tT = sysInfo.getTransferTime();
        br = (tabInfo.getNumberOfTuples() * tabInfo.getCardinality())/sysInfo.getSizeOfBuffer();// paizei na kanpume overestimated
        

        if ( index != null ){
            b = ((tabInfo.getNumberOfTuples()/index.getNumOfDistinctValues()) * tabInfo.getSizeOfTuple() ) / sysInfo.getSizeOfBuffer();
            if ( index.getStructure().contains("B+tree") ){
                h = index.getCostFactor();
                n = tabInfo.getNumberOfTuples()/index.getNumOfDistinctValues();
            }
            else{
                overflowBuckets = index.getCostFactor();
            }
        }
        
    }
    
    public void calculateCost(){
        IndexInfo tempIndex = null;
        int minCostNum;
        
        if ( condition.getAction().contentEquals("=")){
            if ( indexes != null ){
                for ( int i = 0 ; i < indexes.size() ; i ++ ){
                    equalSelection(indexes.get(i));
                }
            }
            equalSelection(null);
        }
        else{
            if ( indexes != null ){
                for ( int i = 0 ; i < indexes.size() ; i ++ ){
                    inequalitySelection(indexes.get(i));
                }
            }
            inequalitySelection(null);
        }
        
        /*//ena condition
        if ( numOfConditions == 1){
            if ( condition.getAction().contentEquals("=")){
                equalSelection(index);
            }
            else{
                if ( index.getStructure().contains("B+tree")){
                    inequalitySelection(index);
                }
                else{
                    tempIndex = tabInfo.findBestIndexForInequality(condition.getAttr1());
                    inequalitySelection(tempIndex);
                }
            }
        }
        else{//polla Condition
            if ( condition.getAction().contentEquals("=")){
                System.out.println("Conditionnnn");
                if ( index != null){//exei index
                    if ( index.getSecondary() == null ){//exei primary index
                       equalSelection(index);
                       //message = "Use primary Index in attribute " + index.getIndexName().toString();
                    }
                    else{//exei secondary index
                        System.out.println("Secondaryyyyyyyyyyyyyyyyy find new");
                        equalSelection(index);
                        //allMessages.add("Use secondary index in attribute " + index.getIndexName().toString());
                        tempIndex = tabInfo.findBestIndex(condition.getAttr1());
                        if (tempIndex != null ){
                            System.out.println("not null : " + tempIndex.getIndexName().toString());
                            if ( tempIndex.getSecondary() == null ){//tsekare mipws exei kapoion primary index kalutero
                                equalSelection(tempIndex);
                                //allMessages.add("Use secondary index in attribute " + tempIndex.getIndexName().toString()); 
                            }           
                        }
                        else{
                            System.out.println(" is null");
                        }
                    }
                }
                else{
                    
                    tempIndex = tabInfo.findBestIndex(condition.getAttr1());
                    System.out.println("Attr = " + condition.getAttr1());
                    if ( tempIndex != null){
                        System.out.println("temp Index = " + tempIndex.getStructure());
                        }
                        else{System.out.println("null");}
                    equalSelection(tempIndex);
                }
            }
            else{
                if ( index != null ){
                    System.out.println("xxexexee = " + index.getStructure());
                    if ( index.getSecondary() == null && index.getStructure().contains("B+tree")){
                        inequalitySelection(index);
                    }
                    else if ( index.getSecondary() != null && index.getStructure().contains("B+tree") ){
                        inequalitySelection(index);
                    }
                    else{
                        System.out.println("inequalityyyyyyyyyyyyyyyyyyyy");
                        tempIndex = tabInfo.findBestIndexForInequality(condition.getAttr1());
                        inequalitySelection(tempIndex);
                    }
                    
                }
                else{
                    
                    tempIndex = tabInfo.findBestIndexForInequality(condition.getAttr1());
                    inequalitySelection(tempIndex);
                }
            }
        }*/
        
        
        minCostNum = getMinCost();
        cost = allCosts.get(minCostNum);
        message = allMessages.get(minCostNum);
        sorted=allSorted.get(minCostNum);
        sortKey= allSortKey.get(minCostNum);
    }
    
    public void equalSelection(IndexInfo tempIndex){
        double tempCost = -1;
        
        
        if ( tempIndex != null ){
            
            calculateVariables(tempIndex);
            if( tempIndex.getStructure().contains("B+tree")){//tree
                if ( tempIndex.getSecondary() == null ){//primary tree index
                    if ( equalPrimary == true ){//primary tree with key
                        tempCost = treePrimaryEqualWithKey();
                        allCosts.add(tempCost);
                        allMessages.add("Use primary tree index in attribute(s) : " + tempIndex.getIndexName().toString());
                        allSorted.add(true);
                        allSortKey.add(tempIndex.getIndexName());
                    }
                    else{//primary tree non key
                        tempCost = treePrimaryEqualNonKey();
                        allCosts.add(tempCost);
                        allMessages.add("Use primary tree index(non key) in attribute(s) : " + tempIndex.getIndexName().toString());
                        allSorted.add(true);
                        allSortKey.add(tempIndex.getIndexName());
                    }
                }
                else{
                    if ( equalPrimary == true ){//secondary tree with key
                        tempCost = treeSecondaryEqualWithKey();
                        allCosts.add(tempCost);
                        allMessages.add("Use secondary tree index in attribute(s) : " + tempIndex.getIndexName().toString());
                        allSorted.add(true);
                        allSortKey.add(tempIndex.getIndexName());
                    }
                    else{//secondary tree non key
                        tempCost = treeSecondaryEqualNonKey();
                        allCosts.add(tempCost);
                        allMessages.add("Use secondary tree index(non key) in attribute(s) : " + tempIndex.getIndexName().toString());
                        allSorted.add(true);
                        allSortKey.add(tempIndex.getIndexName());   
                    }
                }
            }
            else{
                if ( tempIndex.getSecondary() == null ){//primary tree index
                    if ( equalPrimary == true ){//primary tree with key
                        tempCost = hashingPrimaryEqualWithKey();
                        allCosts.add(tempCost);
                        allMessages.add("Use primary hashing index in attribute(s) : " + tempIndex.getIndexName().toString());
                        allSorted.add(false);
                        allSortKey.add(null);
                    }
                    else{//primary tree non key
                        tempCost = hashingPrimaryEqualNonKey();
                        allCosts.add(tempCost);
                        allMessages.add("Use primary hashing index(non key) in attribute(s) : " + tempIndex.getIndexName().toString());
                        allSorted.add(false);
                        allSortKey.add(null);
                    }
                }
                else{
                    if ( equalPrimary == true ){//secondary tree with key
                        tempCost = hashingSecondaryEqualWithKey();
                        allCosts.add(tempCost);
                        allMessages.add("Use secondary hashing index in attribute(s) : " + tempIndex.getIndexName().toString());
                        allSorted.add(true);
                        allSortKey.add(null);
                    }
                    else{//secondary tree non key
                        tempCost = hashingSecondaryEqualNonKey();
                        allCosts.add(tempCost);
                        allMessages.add("Use secondary hashing index(non key) in attribute(s) : " + tempIndex.getIndexName().toString());
                        allSorted.add(false);
                        allSortKey.add(null);
                    }
                }
            }
        }
        else{//linear search
            calculateVariables(null);
            if ( equalPrimary == true ){//linear with key
                tempCost = linearSearchWithKey();
                allCosts.add(tempCost);
                allMessages.add("Use linear Search");   
                allSorted.add(true);
                allSortKey.add(tabInfo.getKey());
            }
            else{//linear without key
                tempCost = linearSearch();
                allCosts.add(tempCost);
                allMessages.add("Use Linear Search (non key)"); 
                allSorted.add(true);
                allSortKey.add(tabInfo.getKey());
            }    
            //linear scan->sorted tupples on key
            
        }
    }
    
    public void inequalitySelection(IndexInfo tempIndex){
        double tempCost = -1;
        
        if ( tempIndex != null ){
            calculateVariables(tempIndex);
            if ( tempIndex.getStructure().contains("B+tree") ){
                if ( tempIndex.getSecondary()== null ){//primary tree index
                    tempCost = treePrimaryCompare();
                    allCosts.add(tempCost);
                    allMessages.add("Use primary tree index in attribute(s) : " + tempIndex.getIndexName().toString());
                    allSorted.add(true);
                    allSortKey.add(tempIndex.getIndexName());
                }
                else{//secondary tree index
                    tempCost = treeSecondaryCompare();
                    allCosts.add(tempCost);
                    allMessages.add("Use secondary tree index in attribute(s) : " + tempIndex.getIndexName().toString());
                    allSorted.add(true);
                    allSortKey.add(tempIndex.getIndexName());
                }
            }
            /*else{//hashing not in comparison, so we use linear
                if (equalPrimary == true){//linear search with key
                    tempCost = linearSearchWithKey();
                    allCosts.add(tempCost);
                    allMessages.add("Use Linear Search");
                }
                else{//linear search with non key
                    tempCost = linearSearch();
                    allCosts.add(tempCost);
                    allMessages.add("Use Linear Search(non Key)");
                }
            }*/
        }
        else{//no index, linear search
            calculateVariables(null);
            if (equalPrimary == true){//linear search with key
                tempCost = linearSearchWithKey();
                allCosts.add(tempCost);
                allMessages.add("Use Linear Search");
                allSorted.add(true);
                allSortKey.add(tabInfo.getKey());
            }
            else{//linear search with non key
                tempCost = linearSearch();
                allCosts.add(tempCost);
                allMessages.add("Use Linear Search(non key)");
                allSorted.add(true);
                allSortKey.add(tabInfo.getKey());
            }
        }
    }
    
    public double linearSearch(){
        
        cost =  tS + (br * tT);
        
        return cost;
    }
    
    public double linearSearchWithKey(){
        
        cost = tS + ( ( br/2 )  * tT );
        
        return cost;
    }
    
    public double treePrimaryEqualWithKey(){
        
        cost = ( h + 1 ) * ( tS + tT );
        
        return cost;
    }
    
    public double treePrimaryEqualNonKey(){
    
        cost = h * ( tT + tS ) + b *tT + tS;
        
        return cost;
    }
    
    public double hashingPrimaryEqualWithKey(){
    
        cost = ( tS + tT ) * ( overflowBuckets + 1 );
        
        return cost;
    }
    
    public double hashingPrimaryEqualNonKey(){
    
        cost =  overflowBuckets * ( tT + tS ) + b * tT + tS;
                
        return cost;
    }
    
    public double treeSecondaryEqualWithKey(){
        
        cost = ( h + 1 ) * ( tS + tT );
        
        return cost;
    }
    
    public double treeSecondaryEqualNonKey(){
    
        cost = ( h + n ) * ( tS + tT );
        
        return cost;
    }
    
    public double hashingSecondaryEqualWithKey(){
    
        cost = ( tS + tT ) * ( overflowBuckets + 1 );
        
        return cost;
    }
    
    public double hashingSecondaryEqualNonKey(){
    
        cost = ( tS + tT ) * ( overflowBuckets + 1 + b );
        
        return cost;
    }
    
    public double treePrimaryCompare(){
    
        cost = h * ( tS + tT) + b * tT;
        
        return cost;
    }
    
    public double treeSecondaryCompare(){
    
        cost = ( h + n ) * ( tT + tS );
        
        return cost;
    }

    public String getMessage() {
        return message;
    }
    

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public int getMinCost(){
        double minCost = -1;
        int position = -1;
        
        if ( allCosts.size() > 1 ){
            minCost = allCosts.get(0);
            position = 0;
            for ( int i = 1 ; i < allCosts.size() ; i ++ ){
                if ( minCost > allCosts.get(i)){
                    minCost = allCosts.get(i);
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
