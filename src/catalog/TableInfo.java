/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package catalog;

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
    int sizeOfTuples = -1;
    
    
    
    public TableInfo(){
        attributes = new HashMap<String,Attributes>();
        IndexInfo indexInfo = new IndexInfo();
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

    
    public int getSizeOfTuples() {
        return sizeOfTuples;
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

    
    public void setSizeOfTuples(int sizeOfTuples) {
        this.sizeOfTuples = sizeOfTuples;
    }

    public void setForeignIndex(Map<String, ForeignIndexInfo> foreignIndex) {
        this.foreignIndex = foreignIndex;
    }
    
}
