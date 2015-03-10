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
    Condition condition = null;
    ArrayList<String> allMessages = new ArrayList<String>();
    ArrayList<Double> allCosts = new ArrayList<Double>();
    String message;
    SystemInfo sysInfo = null;
    int numOfBlocks = -1;
    IndexInfo index = null;
    boolean equalPrimary = false;
    int numOfConditions = -1;
    

    public SelectCost(Condition condition, TableInfo tabInfo, SystemInfo sysInfo, IndexInfo index,boolean equalPrimary, int numOfConditions){
        this.condition = condition;
        this.tabInfo = tabInfo;
        this.sysInfo = sysInfo;
        this.index = index;
        this.equalPrimary = equalPrimary;
        this.numOfConditions = numOfConditions;
    }
    
    
    public void calculateVariables(){
        tS = sysInfo.getLatency();
        tT = sysInfo.getTransferTime();
        br = (tabInfo.getNumberOfTuples() * tabInfo.getCardinality())/sysInfo.getSizeOfBuffer();// paizei na kanpume overestimated
        //find b
        
    }
    
    public void calculateCost(){
        IndexInfo tempIndex = null;
        int minCostNum;
        
        //ena condition
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
        else{
            if ( condition.getAction().contentEquals("=")){    
                if ( index != null){//exei index
                    if ( index.getSecondary() == null ){//exei primary index
                       equalSelection(index);
                       //message = "Use primary Index in attribute " + index.getIndexName().toString();
                    }
                    else{//exei secondary index
                        equalSelection(index);
                        //allMessages.add("Use secondary index in attribute " + index.getIndexName().toString());
                        tempIndex = tabInfo.findBestIndex(condition.getAttr1());
                        if ( tempIndex.getSecondary() == null ){//tsekare mipws exei kapoion primary index kalutero
                            equalSelection(tempIndex);
                            //allMessages.add("Use secondary index in attribute " + tempIndex.getIndexName().toString()); 
                        }           
                    }
                }
                else{
                    
                    tempIndex = tabInfo.findBestIndex(condition.getAttr1());
                    equalSelection(tempIndex);
                }
            }
            else{
                if ( index != null ){
                    if ( index.getSecondary() == null && index.getStructure().contains("B+tree")){
                        inequalitySelection(index);
                    }
                    else if ( index.getSecondary() != null && index.getStructure().contains("B+tree") ){
                        inequalitySelection(index);
                    }
                    else{
                        tempIndex = tabInfo.findBestIndexForInequality(condition.getAttr1());
                        inequalitySelection(tempIndex);
                    }
                    
                }
                else{
                    tempIndex = tabInfo.findBestIndexForInequality(condition.getAttr1());
                    inequalitySelection(tempIndex);
                }
            }
        }
        minCostNum = getMinCost();
        cost = allCosts.get(minCostNum);
        message = allMessages.get(minCostNum);
    }
    
    public void equalSelection(IndexInfo tempIndex){
        double tempCost = -1;
        
        if ( tempIndex != null ){
            if( tempIndex.getStructure().contains("B+tree")){//tree
                if ( tempIndex.getSecondary() != null ){//primary tree index
                    if ( equalPrimary == true ){//primary tree with key
                        tempCost = treePrimaryEqualWithKey();
                        allCosts.add(tempCost);
                        allMessages.add("Use primary tree index in attribute(s) : " + tempIndex.toString());
                    }
                    else{//primary tree non key
                        tempCost = treePrimaryEqualNonKey();
                        allCosts.add(tempCost);
                        allMessages.add("Use primary tree index(non key) in attribute(s) : " + tempIndex.toString());
                    }
                }
                else{
                    if ( equalPrimary == true ){//secondary tree with key
                        tempCost = treeSecondaryEqualWithKey();
                        allCosts.add(tempCost);
                        allMessages.add("Use secondary tree index in attribute(s) : " + tempIndex.toString());
                    }
                    else{//secondary tree non key
                        tempCost = treeSecondaryEqualNonKey();
                        allCosts.add(tempCost);
                        allMessages.add("Use primary tree index(non key) in attribute(s) : " + tempIndex.toString());
                    }
                }
            }
            else{//hashing
                tempCost = hashingPrimary();
                allCosts.add(tempCost);
                allMessages.add("Use hashing index in attribute(s) : " + tempIndex.toString());
            }
        }
        else{//linear search
            if ( equalPrimary == true ){//linear with key
                tempCost = linearSearchWithKey();
                allCosts.add(tempCost);
                allMessages.add("Use linear Search");
            }
            else{//linear without key
                tempCost = linearSearch();
                allCosts.add(tempCost);
                allMessages.add("Use Linear Search (non key)");
            }    
        }
    }
    
    public void inequalitySelection(IndexInfo tempIndex){
        double tempCost = -1;
        
        if ( tempIndex != null ){
            if ( tempIndex.getStructure().contains("B+tree") ){
                if ( tempIndex.getSecondary()== null ){//primary tree index
                    tempCost = treePrimaryCompare();
                    allCosts.add(tempCost);
                    allMessages.add("Use primary tree index in attribute(s) : " + tempIndex.toString());
                }
                else{//secondary tree index
                    tempCost = treeSecondaryCompare();
                    allCosts.add(tempCost);
                    allMessages.add("Use secondary tree index in attribute(s) : " + tempIndex.toString());
                }
            }
            else{//hashing not in comparison, so we use linear
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
            }
        }
        else{//no index, linear search
            if (equalPrimary == true){//linear search with key
                tempCost = linearSearchWithKey();
                allCosts.add(tempCost);
                allMessages.add("Use Linear Search");
            }
            else{//linear search with non key
                tempCost = linearSearch();
                allCosts.add(tempCost);
                allMessages.add("Use Linear Search(non key)");
            }
        }
    }
    
    public double linearSearch(){
        
        cost =  tS + (br *tT);
        
        return cost;
    }
    
    public double linearSearchWithKey(){
        
        cost = tS + ( ( br/2 )  * tT );
        
        return cost;
    }
    
    public double treePrimaryEqualWithKey(){
        
        //cost = ( h + 1 ) * ( tS + tT );
        
        return cost;
    }
    
    public double treePrimaryEqualNonKey(){
    
        //cost = h * ( tT + tS ) + b *tT;
        
        return cost;
    }
    
    public double hashingPrimary(){
    
        return cost;
    }
    
    public double treeSecondaryEqualWithKey(){
        
        //cost = ( h + 1 ) * ( tS + tT );
        
        return cost;
    }
    
    public double treeSecondaryEqualNonKey(){
    
        //cost = ( h + n ) * ( tS + tT );
        
        return cost;
    }
    
    public double hashingSecondaryEqualWithKey(){
    
        return cost;
    }
    
    public double hashingSecondaryEqualNonKey(){
    
        return cost;
    }
    
    public double treePrimaryCompare(){
    
        //cost = h * ( tS + tT) + b * tT;
        
        return cost;
    }
    
    public double treeSecondaryCompare(){
    
        //cost = ( h + n ) * ( tT + tS );
        
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
