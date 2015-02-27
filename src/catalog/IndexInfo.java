/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package catalog;

/**
 *
 * @author jimakos
 */
public class IndexInfo {
    String indexName = null;
    String structure = null;
    int numOfDistinctValues = -1;
    int height = -1;

    
    public IndexInfo(){
    }


    public void setHeight(int height) {
        this.height = height;
    }
    
    
    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    
    public void setStructure(String structure) {
        this.structure = structure;
    }

    
    public void setNumOfDistinctValues(int numOfDistinctValues) {
        this.numOfDistinctValues = numOfDistinctValues;
    }

   
    public String getIndexName() {
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
    
     
}
