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
public class Attributes {
    String type = null;
    String min = null;
    String max = null;
    int distinctValues = -1;

    
    public Attributes(){
    }
    
    public Attributes(Attributes old){
        type = new String(old.getType());
        min = new String(old.getMin());
        max = new String(old.getMax());
        distinctValues = old.getDistinctValues();
    }
    
    public void setType(String type) {
        this.type = type;
    }

    
    public void setMin(String min) {
        this.min = min;
    }

    
    public void setMax(String max) {
        this.max = max;
    }

    
    public void setDistinctValues(int distinctValues) {
        this.distinctValues = distinctValues;
    }

    
    public String getType() {
        return type;
    }

    
    public String getMin() {
        return min;
    }

    
    public String getMax() {
        return max;
    }

    
    public int getDistinctValues() {
        return distinctValues;
    }

    public Attributes fullCopy(){
        return new Attributes(this);
    }

}
