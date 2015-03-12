/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package querryoptimizer;

import catalog.Catalog;
import java.util.ArrayList;
import operations.Join;
import operations.Operator;
import operations.Projection;
import operations.SetOp;
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
    
    
    /*
    private class processPlan extends Thread {

        ArrayList<Operator> plan;
        public processPlan(ArrayList<Operator> plan) {
            this.plan=plan;
        }
        
        public void run() {
            Operator temp;
            double cost=0;
            for (int i = 0; i < plan.size(); i++)
            {
                temp = plan.get(i);
                temp.setCatalog(catalog);
                temp.computeCost();
                cost+=temp.getCost();
            }
            System.out.println(cost);
            setCost(cost, plan);
        }
    }*/


    
          
    public ComputeBestPlan(ArrayList<Operator> operations , Catalog catalog, double cost) {
        this.operations = operations;
        BestPlan = operations;
        Bestcost= cost;
        this.catalog = catalog;

    }
    
    private synchronized void setCost(double cost, ArrayList<Operator> operations){
        
        if(cost< Bestcost){
            BestPlan = operations;
            Bestcost = cost;
        }
    }
    
    public void ApplyTranformations(){
        boolean tranfrormed = true;
        boolean t1=false,t2=false,t3=false;   
        double cost;
        int i =0;
        /*MAKE THE POSSIBLE REAARANGEMENTS*/
        //after each tranformation do this!
        ArrayList<Operator> temp ;
        
        
        
        while(tranfrormed){
            while(tranfrormed){
                i++;
                System.out.println("\n\n\nPass "+i);

                temp = (ArrayList<Operator>) BestPlan.clone();

                System.out.println("Query Projection Elmination:");
                t1 = eliminateMultipleProjections(temp);
                cost =  processPlan(temp);
                printPLan(temp,cost);
                
                temp = (ArrayList<Operator>) BestPlan.clone();

                System.out.println("\n\n\nQuery Set Projection:");
                t2 = pushProjections(temp);
                cost =  processPlan(temp);
                printPLan(temp,cost);
                if(t2){
                    tranfrormed= true;
                    break;
                }
                temp = (ArrayList<Operator>) BestPlan.clone();

                System.out.println("\n\n\nQuery JOin Projection:");
                t3 = pushProjections2(temp);
                cost =  processPlan(temp);
                printPLan(temp,cost);
                if(t3){
                    tranfrormed= true;
                    break;
                }

                tranfrormed=t1 || t2 ||t3 ;
            }
            
        }
        
        
        printPLan(BestPlan,Bestcost);
        
    }
    
    public ArrayList<Operator> getBestPlan(){
        return BestPlan;
    }
    
    /*********************** transformations***********************/
    
    private boolean eliminateMultipleProjections(ArrayList<Operator> ops){
        boolean eliminate=true;
        boolean transformed = false;
        Operator temp1,temp2=null;
        while(eliminate){
            eliminate=false;
            for(int i=ops.size()-1; i>-1; i--){
                temp1 = ops.get(i); 
                if(temp1 instanceof Projection ){
                    temp2 = temp1.getRelationOp1();
                    if(temp2 instanceof Projection){
                        temp1.setRelation1(temp2.getRelation1());
                        temp1.setRelationOp1(temp2.getRelationOp1());
                        ops.remove(temp2);
                        eliminate = true;
                        transformed = true;
                        
                        break;
                    }
                }
            }
        }
        return transformed;
    }
    
        
    private boolean pushProjections(ArrayList<Operator> ops){
        boolean tranformflag=true;
        boolean tranformed = false;
        Operator temp=null;     //the setoperation
        Operator temp1,temp2;  //the projections
        
        
        while(tranformflag){
            tranformflag=false;
            for(int i=ops.size()-1; i>-1; i--){
                temp1 = ops.get(i);
                if( temp1 instanceof Projection ){
                    temp = temp1.getRelationOp1();
                    if(temp!=null && (temp instanceof SetOp)){   //to projection paizei panw se apotelesma set

                        //projection 1
                        temp1.setRelation1(temp.getRelation1());
                        temp1.setRelationOp1(temp.getRelationOp1());

                        //projection 2
                        temp2 = new Projection();
                        temp2.setRelation1(temp.getRelation2());
                        temp2.setRelationOp1(temp.getRelationOp2());
                        temp2.setAttributes(temp1.getAttrs());

                        //new set
                        temp.setRelation1(null);
                        temp.setRelation2(null);
                        temp.setRelationOp1(temp1);
                        temp.setRelationOp2(temp2);

                        //construct operation table
                        ops.remove(temp1);
                        ops.add(i-1, temp1);
                        ops.add(i, temp2);

                        
                        for(int k = ops.size()-1; k > i+1 ;k--)
                        {
                            ops.get(k).updateRelOp(temp1, temp);
                        }
                        
                        
                        tranformed=true;
                        tranformflag=true;
                        break;
                    }                 
                }    
            }
        }
    
        return tranformed;
    }
    
     private boolean pushProjections2(ArrayList<Operator> ops){
        boolean tranformflag=true;
        boolean tranformed = false;
        Operator temp=null;     //the setoperation
        Operator temp1,temp2;  //the projections
        
        while(tranformflag){
            tranformflag=false;
            for(int i=ops.size()-1; i>-1; i--){
                temp1 = ops.get(i);
                if( temp1 instanceof Projection ){
                    temp = temp1.getRelationOp1();
                    if(temp!=null && (temp instanceof Join)){   //to projection paizei panw se apotelesma join

                        ArrayList<String> projAttrs = temp1.getAttrs();
                        
                        temp.computeAttributes(); 
                        ArrayList<String> joinNeededAttrs1 = temp.getNeededAttributes1();
                        ArrayList<String> joinNeededAttrs2 = temp.getNeededAttributes2();
                       
                        ArrayList<String> relAttrs1 = temp.getRelAttributes1();
                        ArrayList<String> relAttrs2 = temp.getRelAttributes2();
                        
                        ArrayList<String> projAttrs1 = findProjAttrs(relAttrs1, projAttrs , joinNeededAttrs1);
                        ArrayList<String> projAttrs2 = findProjAttrs(relAttrs2, projAttrs, joinNeededAttrs2);
                        
                        if(allProjectedAttrs(projAttrs1, projAttrs2, projAttrs)){
                            if(projAttrs1!=null){
                                temp1.setRelation1(temp.getRelation1());
                                temp1.setRelationOp1(temp.getRelationOp1());
                                temp1.setAttributes(projAttrs1);
                                        temp.setRelation1(null);
                                        temp.setRelationOp1(temp1);
                                        ops.remove(temp1);
                                        ops.add(i-1, temp1);
                                
                            }
                            if(projAttrs2!=null){    
                                        temp2 = new Projection();
                                        temp2.setRelation1(temp.getRelation2());
                                        temp2.setRelationOp1(temp.getRelationOp2());
                                        temp2.setAttributes(projAttrs2);
                                        temp.setRelation2(null);
                                        temp.setRelationOp2(temp2);
                                        ops.add(i, temp2);
                            }
                            tranformed=true;
                            tranformflag=true;
                            break;
                            
                        }
                    }                 
                }    
            }
        }
    
        return tranformed;
    }
    
    
    
    
    
    
    /*****************************************************************************************************************/
    
    public boolean checkAttrs(ArrayList<String> join, ArrayList<String> proj){
        for(int i=0;i<join.size();i++){
            if(!proj.contains(join.get(i)))
                return false;
        }
        return true;
    }
    
    
    public ArrayList<String> unionAttrs(ArrayList<String> proj, ArrayList<String> needed){
        String temp;
        for(int i =0; i<needed.size();i++){
            temp = needed.get(i);
            if(!proj.contains(temp))
                proj.add(temp);
        }
        return proj;
    }
    
    public ArrayList<String> findProjAttrs(ArrayList<String> rel, ArrayList<String> proj, ArrayList<String> needed){
        
        
        ArrayList<String> temp=null;
        //compute intresection of proj and rel
        for(int i =0; i<proj.size();i++){
            if(rel.contains(proj.get(i))){
                if(temp==null)
                    temp=new ArrayList<String>();
                temp.add(proj.get(i));
            }
        }
        
        //add needed to output
        for(int i =0; i<needed.size();i++){
            if(!proj.contains(needed.get(i))){
                if(temp==null)
                    temp=new ArrayList<String>();
                temp.add(needed.get(i));
            }
        }
        return temp;
    }
    
    
    public boolean allProjectedAttrs(ArrayList<String> proj1, ArrayList<String> proj2, ArrayList<String> proj){
        
        for(int i =0; i<proj.size();i++){
            String temp = proj.get(i);
            if(!proj1.contains(temp)){
                if(!proj2.contains(temp))
                    return false;
            }
        }
        return true;
    }
    
     
    /**********************************************************************************************************/
     
    public double processPlan(ArrayList<Operator> plan){
       // ArrayList<Operator> op  =operations;
        Operator temp;
        double cost=0;
        for (int i = 0; i < plan.size(); i++)
        {
            
            temp = plan.get(i);
            temp.setCatalog(catalog);
            temp.computeCost();
            cost+=temp.getCost();
        }
        
        if(cost< Bestcost){
            BestPlan = plan;
            Bestcost = cost;
        }
        return cost;
        
    
    }
    
        
     public void printPLan(ArrayList<Operator> op, double cost){
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
        System.out.println("Total Cost:"+cost);
    }
    
    
    
}
