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
public class SelectCost {
    double cost = -1;
    ArrayList<String> selectFields = null;
    TableInfo tabinfo = null;
    boolean symbol = false;
    double tS = -1;
    double tT = -1;
    int costFactor = -1;
    int b = -1;
    int br = -1;
    

    public SelectCost(ArrayList<String> selectFields, TableInfo tabInfo, boolean symbol){
        this.selectFields = selectFields;
        this.tabinfo = tabInfo;
        this.symbol = symbol;
    }
    
    
    public void calculateVariables(){
        
    }
    
    
    public double linearSearch(){
        
        cost =  tS + (br *tT);
        
        return cost;
    }
    
    public double linearSearchWithKey(){
        
        cost = tS + ( ( br/2 )  * tS );
        
        return cost;
    }
    
    public double treePrimaryEqualWithKey(){
        
        cost = ( h + 1 ) * ( tS + tT );
        
        return cost;
    }
    
    public double treePrimaryEqualNonKey(){
    
        cost = h * ( tT + tS ) + b *tT;
        
        return cost;
    }
    
    public double hashingPrimaryEqualWithKey(){
    
        return cost;
    }
    
    public double hashingPrimaryEqualNonKey(){
        
        return cost;
    }
    
    public double treeSecondaryEqualWithKey(){
        
        cost = ( h + 1 ) * ( tS + tT );
        
        return cost;
    }
    
    public double treeSecondaryEqualNonKey(){
    
        cost = ( h + n ) * ( tS + tT );
        
        return cost;
    }
    
    public double hashingSecondaryEqualWithKey(){
    
        return cost;
    }
    
    public double hashingSecondaryEqualNonKey(){
    
        return cost;
    }
    
    public double treePrimaryCompare(){
    
        cost = h * ( tS + tT) + b * tT;
        
        return cost;
    }
    
    public double treeSecondaryCompare(){
    
        cost = ( h + n ) * ( tT + tS );
        
        return cost;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
    
}
