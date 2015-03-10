/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package catalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jimakos
 */
public class TableInfo {
    Map<String,Attributes> attributes = null;
    IndexInfo primaryIndex = null;
    Map<String,IndexInfo> secondaryIndex = null;
    Map<String,ForeignIndexInfo> foreignIndex = null;
    int cardinality = -1;
    int numberOfTuples = -1;
    int sizeOfTuple = -1;
    Set <String> key = null;
    
    boolean sorted=false; //only for output table f operations
    boolean operator=false;
    
    
    public TableInfo(){
        attributes = new HashMap<String,Attributes>();
        IndexInfo indexInfo = new IndexInfo();
    }

    public void setSorted(boolean s){
        sorted=s;
    }
    
    public void setOperator(boolean o){
        operator=o;
    } 
    
    public boolean getOperator(){
        return operator;
    }
    
    public boolean getSorted(){
        return sorted;
    }
    
    public Set <String> getKey() {
        return key;
    }

    
    
    public int getSizeOfTuple() {
        return sizeOfTuple;
    }
    
    public Map<String, ForeignIndexInfo> getForeignIndex() {
        return foreignIndex;
    }
    
    public Map<String, Attributes> getAttributes() {
        return attributes;
    }

    
    public IndexInfo getPrimaryIndex() {
        return primaryIndex;
    }

    
    public Map<String, IndexInfo> getSecondaryIndex() {
        return secondaryIndex;
    }

    
    public int getCardinality() {
        return cardinality;
    }

    
    public int getNumberOfTuples() {
        return numberOfTuples;
    }

    public void setKey(Set <String> key) {
        this.key = key;
    }

    
    
    public void setSizeOfTuple(int sizeOfTuple) {
        this.sizeOfTuple = sizeOfTuple;
    }
    
    //use for output operation tableInfo to compute new tuppleSIze
    public void setSizeOfTuple() {
        int temp=0;
        String type;
        for(String key1 : attributes.keySet()){
                type = attributes.get(key1).getType();
                if(type.equals("int") || type.equals("float"))
                    temp+= 4;
                else if(type.endsWith("double"))
                    temp+=8;
                else if(type.startsWith("varchar")){
                    temp+= Integer.parseInt(type.split("\\(")[1].split("\\)")[0]);
                }
        }
        this.sizeOfTuple = temp;
    }
    

    public void setAttributes(Map<String, Attributes> attributes) {
        this.attributes = attributes;
    }

    
    public void setPrimaryIndex(IndexInfo primaryIndex) {
        this.primaryIndex = primaryIndex;
    }

    
    public void setSecondaryIndex(Map<String, IndexInfo> secondaryIndex) {
        this.secondaryIndex = secondaryIndex;
    }

    
    public void setCardinality(int carinality) {
        this.cardinality = carinality;
    }

    
    public void setNumberOfTuples(int numberOfTuples) {
        this.numberOfTuples = numberOfTuples;
    }

    public void setForeignIndex(Map<String, ForeignIndexInfo> foreignIndex) {
        this.foreignIndex = foreignIndex;
    }
    
    public boolean equalsKey( String tocheck) {
        if(key.size() !=1)
            return false;
        if(!key.contains(tocheck))
            return false;
        return true;
    }
    
    
    public IndexInfo findBestIndex(ArrayList<String> attr){
        boolean FLAG = false;
        
        //primaryIndex
        if ( attr.size() == primaryIndex.indexName.size()){
            for ( int i = 0 ; i < attr.size() ; i ++ ){
                if ( !primaryIndex.indexName.contains(attr.get(i))){
                    FLAG = true;
                    break;
                }
            }
            if ( FLAG == false ){
                return primaryIndex;
            }
        }
    
        
        //secondaryIndex
        for ( String secondary : secondaryIndex.keySet() ){
            
            FLAG = false ;
            IndexInfo index = secondaryIndex.get(secondary);
            if ( attr.size() == index.indexName.size()){
                for ( int i = 0 ; i < attr.size() ; i ++ ){
                    if ( !index.indexName.contains(attr.get(i))){
                        FLAG = true;
                        break;
                    }
                }
                if ( FLAG == false ){
                    return index;
                }
            }
        }
        
        return null;
    }
    
     public IndexInfo findBestIndex(String attr){
        
        //primaryIndex
        if ( primaryIndex.indexName.size() == 1){
            if ( !primaryIndex.indexName.contains(attr)){
                 return primaryIndex;
            }
        }
    
        
        //secondaryIndex
        for ( String secondary : secondaryIndex.keySet() ){
            IndexInfo index = secondaryIndex.get(secondary);
            if ( index.indexName.size() == 1){
                if ( !index.indexName.contains(attr)){
                    return index;
                }
            }
        }
        
        return null;
    }
     
     public IndexInfo findBestIndexForInequality(String attr){
        
        //primaryIndex
        if ( primaryIndex.indexName.size() == 1){
            if (primaryIndex.structure.contains("B+tree")){
                if ( !primaryIndex.indexName.contains(attr)){
                     return primaryIndex;
                }
            }
        }
    
        
        //secondaryIndex
        for ( String secondary : secondaryIndex.keySet() ){
            IndexInfo index = secondaryIndex.get(secondary);
            if ( index.indexName.size() == 1){
                if ( index.structure.contains("B+Tree")){
                    if ( !index.indexName.contains(attr)){
                        return index;
                    }
                }
            }
        }
        
        return null;
    }
    
    
    public boolean equalPrimaryKey( ArrayList<String> attr){
        
        if ( attr.size() == key.size() ){
            for( int i = 0 ; i < attr.size() ; i++ ){
                if ( !key.contains(attr.get(i))){
                    return false;
                }
            }
            return true;
        }
        else{
            return false;
        }
        
    }
    
    

    public boolean hashIndexSameKey(TableInfo tInfo){
        if(tInfo.hashIndexExist() && hashIndexExist() ){
            IndexInfo temp = primaryIndex;
            IndexInfo tempTocheck = tInfo.getPrimaryIndex();
            Map<String, IndexInfo> tempsecondaryIndex = tInfo.getSecondaryIndex();
            
            if(!temp.getStructure().equals("B+tree")){  //check this.primary index with every index f tinfo
                if(!tempTocheck.getStructure().equals("B+tree"))
                {
                    if(temp.equalsKey(tempTocheck.getIndexName()))
                        return true;
                }
                if(tempsecondaryIndex!=null){
                    for(String key1 : tempsecondaryIndex.keySet()){
                        tempTocheck = tempsecondaryIndex.get(key1);
                        if(!tempTocheck.getStructure().equals("B+tree")){
                            if(temp.equalsKey(tempTocheck.getIndexName()))
                                return true;
                        }
                    }
                }
            }
            else{ //check all this.secondary with every index of tinfo
                if(secondaryIndex!=null){
                    for(String key1 : secondaryIndex.keySet()){
                        temp = secondaryIndex.get(key1);
                        if(!temp.getStructure().equals("B+tree")){  //check this.primary index with every index f tinfo
                            tempTocheck = tInfo.getPrimaryIndex();
                            if(!tempTocheck.getStructure().equals("B+tree"))
                            {
                                if(temp.equalsKey(tempTocheck.getIndexName()))
                                    return true;
                            }
                            if(tempsecondaryIndex!=null){
                                for(String key2 : tempsecondaryIndex.keySet()){
                                    tempTocheck = tempsecondaryIndex.get(key2);
                                    if(!tempTocheck.getStructure().equals("B+tree")){
                                        if(temp.equalsKey(tempTocheck.getIndexName()))
                                            return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
        }
        
        return false;
    }
    
    public boolean sortedSameKey(TableInfo tInfo){   
        if(tInfo.isSorted() && this.isSorted()){
            if(tInfo.getPrimaryIndex().equalsKey(primaryIndex.getIndexName()))
                return true;
        }
        return false;
    }
    
    public boolean hashIndexExist(){
        IndexInfo temp;
        if(primaryIndex!=null && !primaryIndex.getStructure().equals("B+tree"))
            return true;
        if(secondaryIndex!= null){
            for(String key1 : secondaryIndex.keySet()){
                temp = secondaryIndex.get(key1);
                if(!temp.getStructure().equals("B+tree")){
                    //System.out.println("csddvf df       "+key1);
                    return true;
                }
           }
        }
        return false;
    }
    
    public boolean isSorted(){
        if(primaryIndex!=null  && primaryIndex.getStructure().equals("B+tree"))
            return true;
        return false;
    }
    
    public boolean isSortedOnKey(ArrayList<String> tocheck){
        if(isSorted()){
            if(primaryIndex.equalsKey(tocheck))
                return true;
        
        }
        return false;
    }
    
        public boolean isHashedOnKey(ArrayList<String> tocheck){
        if(hashIndexExist()){
            if(!primaryIndex.getStructure().equals("B+tree") && primaryIndex.equalsKey(tocheck))
                    return true;
            else{
                if(secondaryIndex!= null){
                    IndexInfo temp;
                    for(String key1 : secondaryIndex.keySet()){
                        temp = secondaryIndex.get(key1);
                        if(!temp.getStructure().equals("B+tree") && temp.equalsKey(tocheck)){
                            return true;
                        }
                   }
                }
            }
        
        }
        return false;
    }
    
    
    
    
    
}
