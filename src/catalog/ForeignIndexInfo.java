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
public class ForeignIndexInfo {
    String indexName = null;
    String outTable = null;
    String outAttr = null;
    String structure = null;
    int numOfDistinctValues = -1;
    int height = -1;

    
    public ForeignIndexInfo(){

    }
    
    public String getIndexName() {
        return indexName;
    }

    public String getOutTable() {
        return outTable;
    }

    public String getOutAttr() {
        return outAttr;
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
    

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public void setOutTable(String outTable) {
        this.outTable = outTable;
    }

    public void setOutAttr(String outAttr) {
        this.outAttr = outAttr;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public void setNumOfDistinctValues(int numOfDistinctValues) {
        this.numOfDistinctValues = numOfDistinctValues;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    
    
}
