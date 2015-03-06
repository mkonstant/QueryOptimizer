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
public class ForeignIndexInfo {
    Set<String> indexName = null;
    String outTable = null;
    Set<String> outAttr = null;
    String structure = null;
    int numOfDistinctValues = -1;
    int costFactor = -1;

    
    public ForeignIndexInfo(){

    }
    
    public Set<String> getIndexName() {
        return indexName;
    }

    public String getOutTable() {
        return outTable;
    }

    public Set<String> getOutAttr() {
        return outAttr;
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
    

    public void setIndexName(Set<String> indexName) {
        this.indexName = indexName;
    }

    public void setOutTable(String outTable) {
        this.outTable = outTable;
    }

    public void setOutAttr(Set<String> outAttr) {
        this.outAttr = outAttr;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public void setNumOfDistinctValues(int numOfDistinctValues) {
        this.numOfDistinctValues = numOfDistinctValues;
    }

    public void setCostFactor(int costFactor) {
        this.costFactor = costFactor;
    }
    
    
}
