/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package querryoptimizer;

import catalog.Catalog;
import java.util.ArrayList;
import operations.Operator;
import static querryoptimizer.QuerryOptimizer.operations;

/**
 *
 * @author michalis
 */
public class ComputeBestPlan {
    static ArrayList<Operator> operations;
    static Catalog catalog;
    
    private double Bestcost =0;
    private ArrayList<Operator> BestPlan;
    
    
    
    private class processPlan extends Thread {

        ArrayList<Operator> plan;
        public processPlan(ArrayList<Operator> plan) {
            this.plan=plan;
        }
        
        public void run() {
            Operator temp;
            double cost=0;
            for (int i = 0; i < operations.size(); i++)
            {
                temp = operations.get(i);
                temp.setCatalog(catalog);
                temp.computeCost();
                cost+=temp.getCost();
            }
            
            setCost(cost, operations);
        }
    }


    
          
    public ComputeBestPlan(ArrayList<Operator> operations , Catalog catalog) {
        this.operations = operations;
        this.catalog = catalog;
    }
    
    private synchronized void setCost(double cost, ArrayList<Operator> operations){
        if(cost< Bestcost){
            BestPlan = operations;
            Bestcost = cost;
        }
    }
    
    public void ApplyTranformations(){
        
        /*MAKE THE POSSIBLE REAARANGEMENTS*/
        
        
        
        //after each tranformation do this!
        ArrayList<Operator> temp = (ArrayList<Operator>) operations.clone();
        (new processPlan(temp)).start();
    }
    
    public ArrayList<Operator> getBestPlan(){
        return BestPlan;
    }
    
    
    
    
    
    
}
