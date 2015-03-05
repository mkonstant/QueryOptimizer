/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package catalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jimakos
 */
public class TableInfo {
    Map<String,Attributes> attributes = null;
    IndexInfo primaryIndex = null;
    Map<String,IndexInfo> secondaryIndex = null;
    Map<String,ForeignIndexInfo> foreignIndex = null;
    int carinality = -1;
    int numberOfTuples = -1;
    int sizeOfTuple = -1;
    ArrayList <String> key = null;
    
    boolean sorted=false; //only for output table f operations
    
    
    public TableInfo(){
        attributes = new HashMap<String,Attributes>();
        IndexInfo indexInfo = new IndexInfo();
    }

    public void setSorted(boolean s){
        sorted=s;
    }
    
    public boolean getSorted(){
        return sorted;
    }
    
    public ArrayList <String> getKey() {
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

    
    public int getCarinality() {
        return carinality;
    }

    
    public int getNumberOfTuples() {
        return numberOfTuples;
    }

    public void setKey(ArrayList <String> key) {
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
                if(type.equals("int") || type.equals("flaot"))
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

    
    public void setCarinality(int carinality) {
        this.carinality = carinality;
    }

    
    public void setNumberOfTuples(int numberOfTuples) {
        this.numberOfTuples = numberOfTuples;
    }

    public void setForeignIndex(Map<String, ForeignIndexInfo> foreignIndex) {
        this.foreignIndex = foreignIndex;
    }
    
}
