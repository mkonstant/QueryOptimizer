/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package catalog;

import java.util.ArrayList;
import java.util.Set;

/**
 *
 * @author jimakos
 */
public class IndexInfo {
    Set<String> indexName = null;
    String structure = null;
    int numOfDistinctValues = -1;
    int costFactor = -1;

    
    public IndexInfo(){
    }


    public void setCostFactor(int costFactor) {
        this.costFactor = costFactor;
    }
    
    
    public void setIndexName(Set<String> indexName) {
        this.indexName = indexName;
    }

    
    public void setStructure(String structure) {
        this.structure = structure;
    }

    
    public void setNumOfDistinctValues(int numOfDistinctValues) {
        this.numOfDistinctValues = numOfDistinctValues;
    }

   
    public Set<String> getIndexName() {
        return indexName;
    }

    
    public String getStructure() {
        return structure;
    }

    
    public int getNumOfDistinctValues() {
        return numOfDistinctValues;
    }
    
    
     public int getCostFactor() {
        return costFactor;
    }
     
    public boolean equalsKey( Set<String> tocheck) {
        if(tocheck.size()!=indexName.size())
            return false;
        for (String index : indexName) {
            if(!tocheck.contains(index))
                return false;
        }
        return true;
    }
    
    public boolean equalsKey( String tocheck) {
        if(indexName.size() !=1)
            return false;
        if(!indexName.contains(tocheck))
            return false;
        return true;
    }
    
    public IndexInfo returnIndex( ArrayList<String> tocheck){
        if(tocheck.size()!=indexName.size())
            return null;
        for (String index : indexName) {
            if(!tocheck.contains(index))
                return null;
        }
        return this;
    }
    
    public IndexInfo returnIndex( String tocheck){
        if(indexName.size() !=1)
            return null;
        if(!indexName.contains(tocheck))
            return null;
        return this;
    }
}
