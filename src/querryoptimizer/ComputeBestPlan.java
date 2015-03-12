/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package querryoptimizer;

import catalog.Catalog;
import java.util.ArrayList;
import operations.Operator;
import operations.Projection;
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
        System.out.println("\n\n\nQuery Projection Elmination:");
        eliminateMultipleProjections(temp);
        printPLan(temp);
        //(new processPlan(temp)).start();
    }
    
    public ArrayList<Operator> getBestPlan(){
        return BestPlan;
    }
    
    /*********************** transformations***********************/
    
    private void eliminateMultipleProjections(ArrayList<Operator> ops){
        boolean project=false;
        Operator temp=null;
        for(int i=ops.size()-1; i>-1; i--){
            if(ops.get(i) instanceof Projection ){
                if(project){   //previous operation was projection, eliminate this
                    
                    temp.setRelation1(ops.get(i).getRelation1());
                    temp.setRelationOp1(ops.get(i).getRelationOp1());
                    ops.remove(i);
                }
                else{
                    temp = ops.get(i);
                    project = true;
                }
            }
            else
                project=false;
        }
    
    }
    
    
    
        public static void processPlan(){
       // ArrayList<Operator> op  =operations;
        Operator temp;
        for (int i = 0; i < operations.size(); i++)
        {
            
            temp = operations.get(i);
            temp.setCatalog(catalog);
            temp.computeCost();
	}
    
    }
    
        
     public static void printPLan(ArrayList<Operator> op){
        //ArrayList<Operator> op  =operations;
        int tempR1,maxR1="Relation1".length();
        int tempR2,maxR2="Relation2".length();
        int tempC,maxC="Condition".length();
        int tempA,maxA="Attributes".length();
        int tempAn,maxAn="Annotation".length();
        int tempAggr, maxAggr="Aggregation".length();
        String splitLine="+----------+-------+";
        String headLine= "|Operation |  type |";
        int l;
        Operator temp;

        for (int i = 0; i < op.size(); i++)
        {
            
            temp = op.get(i);
            temp.setOpName("Operation"+i);
            tempR1=temp.getRelationPrint1Lenght();
            tempR2=temp.getRelationPrint2Lenght();
            tempC=temp.getConditionsPrintLenght();
            tempA=temp.getAttributesPrintLenght();
            tempAn=temp.getAnnotationLenght();
            tempAggr = temp.getAggragationLenght();
            
                    
            if(tempR1>maxR1)
                maxR1=tempR1;
            if(tempR2>maxR2)
                maxR2=tempR2;
            if(tempC>maxC)
                maxC=tempC;
            if(tempA>maxA)
                maxA=tempA;
            if(tempAn>maxAn)
                maxAn=tempAn;
            if(tempAggr>maxAggr)
                maxAggr=tempAggr;

	}
        
        //construct headline 
        headLine+="Condition";
        l = maxC - "Condition".length();        
        for(int i=0;i<l;i++){
                headLine+=" ";
        }
        headLine+="|";
        
        headLine+="Attributes";
        l = maxA - "Attributes".length();        
        for(int i=0;i<l;i++){
                headLine+=" ";
        }
        headLine+="|";
        
        headLine+="Relation1";
        l = maxR1 - "Relation1".length();        
        for(int i=0;i<l;i++){
                headLine+=" ";
        }
        headLine+="|";
        
        headLine+="Relation2";
        l = maxR2 - "Relation2".length();        
        for(int i=0;i<l;i++){
                headLine+=" ";
        }
        headLine+="|";
        
        headLine+="Aggregation";
        l = maxAggr - "Aggregation".length();        
        for(int i=0;i<l;i++){
                headLine+=" ";
        }
        headLine+="|";
        
        headLine+="Annotation";
        l = maxAn - "Annotation".length();        
        for(int i=0;i<l;i++){
                headLine+=" ";
        }
        headLine+="|";
        
        
        
        //construct splitline
        for(int i=0;i<maxC;i++){
                splitLine+="-";
        }
        splitLine+="+";
        for(int i=0;i<maxA;i++){
                splitLine+="-";
        }
        splitLine+="+";
        for(int i=0;i<maxR1;i++){
                splitLine+="-";
        }
        splitLine+="+";
        for(int i=0;i<maxR2;i++){
                splitLine+="-";
        }
        splitLine+="+";
        for(int i=0;i<maxAggr;i++){
                splitLine+="-";
        }
        splitLine+="+";
        
        
        for(int i=0;i<maxAn;i++){
                splitLine+="-";
        }
        splitLine+="+";
        

        System.out.println(splitLine);
        System.out.println(headLine);
        System.out.println(splitLine);
        
        for (int i = 0; i < op.size(); i++)
        {
            temp = op.get(i);
            System.out.println(temp.toPrint(maxR1, maxR2, maxC, maxA,maxAn,maxAggr));
	}
        System.out.println(splitLine);
   
    }
    
    
    
}
