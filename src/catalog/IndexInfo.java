/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package catalog;

import java.util.ArrayList;

/**
 *
 * @author jimakos
 */
public class IndexInfo {
    ArrayList<String> indexName = null;
    String structure = null;
    int numOfDistinctValues = -1;
    int height = -1;

    
    public IndexInfo(){
    }


    public void setHeight(int height) {
        this.height = height;
    }
    
    
    public void setIndexName(ArrayList<String> indexName) {
        this.indexName = indexName;
    }

    
    public void setStructure(String structure) {
        this.structure = structure;
    }

    
    public void setNumOfDistinctValues(int numOfDistinctValues) {
        this.numOfDistinctValues = numOfDistinctValues;
    }

   
    public ArrayList<String> getIndexName() {
        return indexName;
    }

    
    public String getStructure() {
        return structure;
    }

    
    public int getNumOfDistinctValues() {
        return numOfDistinctValues;
    }
    
    
     public int getHeight() {
        return height;
    }
     
    public boolean equalsKey( ArrayList<String> tocheck) {
        if(tocheck.size()!=indexName.size())
            return false;
        for(int i=0; i<indexName.size(); i++){
            if(!tocheck.contains(indexName.get(i)))
                return false;
        }
        return true;
    }
    
    public boolean equalsKey( String tocheck) {
        if(indexName.size() !=1)
            return false;
        if(!indexName.get(0).equals(tocheck))
            return false;
        return true;
    }
    
     
}
