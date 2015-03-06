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

/**
 *
 * @author jimakos
 */
public class Select {
    double cost = -1;
    ArrayList<String> selectFields = null;
    TableInfo tabinfo = null;
    boolean symbol = false;
    double tS = -1;
    double tT = -1;
    int costFactor = -1;
    int b = -1;
    int br = -1;
    

    public Select(ArrayList<String> selectFields, TableInfo tabInfo, boolean symbol){
        this.selectFields = selectFields;
        this.tabinfo = tabInfo;
        this.symbol = symbol;
    }
    
    
    public void calculate(){
    
    }
    
    
    public void linearSearch(){
    
    }
    
    public void linearSearchWithKey(){
    
    }
    
    public void treePrimaryEqualWithKey(){
    
    }
    
    public void treePrimaryEqualNonKey(){
    
    }
    
    public void hashingPrimaryEqualWithKey(){
    
    }
    
    public void hashingPrimaryEqualNonKey(){
    
    }
    
    public void treeSecondaryEqualWithKey(){
    
    }
    
    public void treeSecondaryEqualNonKey(){
    
    }
    
    public void hashingSecondaryEqualWithKey(){
    
    }
    
    public void hashingSecondaryEqualNonKey(){
    
    }
    
    public void treePrimaryCompare(){
    
    }
    
    public void treeSecondaryCompare(){
    
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
    
}
