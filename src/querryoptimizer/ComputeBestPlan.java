/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package querryoptimizer;

import catalog.Catalog;
import catalog.TableInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.naming.ldap.HasControls;
import operations.Condition;
import operations.Join;
import operations.Operator;
import operations.Projection;
import operations.Selection;
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
    private boolean updateBest=false;
    
    
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
    
   /*private synchronized void setCost(double cost, ArrayList<Operator> operations){
        
        if(cost< Bestcost){
            BestPlan = operations;
            Bestcost = cost;
            
        }
    }*/
    
    public void ApplyTranformations(){
        boolean t1=false,t2=false,t3=false,t4=false;   
        double cost;
        int i =0;
        /*MAKE THE POSSIBLE REAARANGEMENTS*/
        //after each tranformation do this!
        ArrayList<Operator> temp ;
        
        updateBest=true;
        
        while(updateBest){
            while(updateBest){
                updateBest=false; 
                i++;
                System.out.println("\n\n\nPass "+i);


                temp = getPlanCopy(BestPlan);
                
                t1 = eliminateMultipleProjections(temp);
                if(t1){
                    cost = processPlan(temp);
                    System.out.println("Query Projection Elmination:");
                    printPLan(temp, cost);
                    if(updateBest)
                        break;
                    temp = getPlanCopy(BestPlan);
                }
                
                t2 = pushProjections(temp);
                if(t2){
                    cost = processPlan(temp);
                    System.out.println("\n\n\nQuery Set Projection:");
                    printPLan(temp, cost);
                    if(updateBest){
                        break;
                    }
                    temp = getPlanCopy(BestPlan);
                }
                
                t3 = pushProjections2(temp);
                if(t3){
                    cost = processPlan(temp);
                    System.out.println("\n\n\nQuery JOin Projection:");
                    printPLan(temp, cost);
                    if(updateBest){
                        break;
                    }
                    temp = getPlanCopy(BestPlan);
                }
                
                t4 = rearangeSelections(temp);
                if(t4){
                    cost = processPlan(temp);
                    System.out.println("Selection rearange:");
                    printPLan(temp, cost);
                    if(updateBest)
                        break;
                    temp = getPlanCopy(BestPlan);
                }
                
                t4 = rearangeSets(temp);
                if(t4){
                    cost = processPlan(temp);
                    System.out.println("Selection rearange:");
                    printPLan(temp, cost);
                    if(updateBest)
                        break;
                    temp = getPlanCopy(BestPlan);
                }
                
                t4 = pushSelectionInSet1(temp);
                if(t4){
                    cost = processPlan(temp);
                    System.out.println("Selection rearange:");
                    printPLan(temp, cost);
                    if(updateBest)
                        break;
                    temp = getPlanCopy(BestPlan);
                }
                
                t4 = pushSelectionInSet2(temp);
                if(t4){
                    cost = processPlan(temp);
                    System.out.println("Selection rearange:");
                    printPLan(temp, cost);
                    if(updateBest)
                        break;
                    temp = getPlanCopy(BestPlan);
                }
            } 
        }
        
        System.out.println("\n\n\nBest Plan:");
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
    
    private boolean rearangeSelections(ArrayList<Operator> ops){
        boolean transformed = false;
        Operator temp1,temp2=null;
            for(int i=ops.size()-1; i>-1; i--){
                temp1 = ops.get(i); 
                if(temp1 instanceof Selection  ){
                    temp2 = temp1.getRelationOp1();
                    if(temp2 instanceof Selection){
                        Operator tempRelOp1Inside = temp2.getRelationOp1();
                        String tempRel1 = temp2.getRelation1();
                        TableInfo tempInfo = temp2.getOutTableInfo1();

                        temp2.setRelation1(null);
                        temp2.setRelationOp1(temp1);
                        temp2.setTabInfo1(temp1.getOutTableInfo1());
                                                                 
                        temp1.setRelation1(tempRel1);
                        temp1.setRelationOp1(tempRelOp1Inside);
                        temp1.setTabInfo1(tempInfo);
                        
                        ops.remove(temp2);
                        ops.add(i,temp2);
                        
                        transformed = true;
                        break;
                    }
                }
        }
        return transformed;
    }
    
        private boolean rearangeSets(ArrayList<Operator> ops){
        boolean transformed = false;
        Operator temp,temp1,temp2=null;
            for(int i=ops.size()-1; i>-1; i--){
                temp = ops.get(i); 
                if(temp instanceof SetOp  ){
                    if(!temp.getOperation().equals("diff")){
                        temp1 = temp.getRelationOp1();
                        temp2 = temp.getRelationOp2();
                        if(temp1!=null && temp2==null && (temp1 instanceof SetOp)){
                            if (temp1.getOperation().equals(temp.getOperation())){
                                if(temp1.getRelation1()!=null){
                                    String tempRel = temp.getRelation2();
                                    TableInfo tempInfo = temp.getOutTableInfo2();
                                
                                    temp.setRelation2(temp1.getRelation1());
                                    temp.setTabInfo2(temp1.getOutTableInfo1());
                                    
                                    temp1.setRelation1(tempRel);
                                    temp1.setTabInfo1(tempInfo);
                                    transformed = true;
                                    break;
                                }
                                else if(temp1.getRelation2()!=null){
                                    String tempRel = temp.getRelation2();
                                    TableInfo tempInfo = temp.getOutTableInfo2();
                                
                                    temp.setRelation2(temp1.getRelation2());
                                    temp.setTabInfo2(temp1.getOutTableInfo2());
                                    
                                    temp1.setRelation2(tempRel);
                                    temp1.setTabInfo2(tempInfo);
                                    transformed = true;
                                    break;
                                }
                            }
                        }
                        else if(temp2!=null && temp1==null && (temp2 instanceof SetOp)){
                            if (temp2.getOperation().equals(temp.getOperation())){
                                if(temp2.getRelation1()!=null){
                                    String tempRel = temp.getRelation1();
                                    TableInfo tempInfo = temp.getOutTableInfo1();
                                
                                    temp.setRelation1(temp2.getRelation1());
                                    temp.setTabInfo1(temp2.getOutTableInfo1());
                                    
                                    temp2.setRelation1(tempRel);
                                    temp2.setTabInfo1(tempInfo);
                                    transformed = true;
                                    break;
                                }
                                else if(temp2.getRelation2()!=null){
                                    String tempRel = temp.getRelation1();
                                    TableInfo tempInfo = temp.getOutTableInfo1();
                                
                                    temp.setRelation1(temp1.getRelation2());
                                    temp.setTabInfo1(temp1.getOutTableInfo2());
                                    
                                    temp2.setRelation2(tempRel);
                                    temp2.setTabInfo2(tempInfo);
                                    transformed = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
                    
                
        return transformed;
    }
            
    private boolean pushSelectionInSet1(ArrayList<Operator> ops){
        boolean tranformflag=true;
        boolean tranformed = false;
        Operator temp=null;     //the setoperation
        Operator temp1,temp2;  //the projections
        
        
        while(tranformflag){
            tranformflag=false;
            for(int i=ops.size()-1; i>-1; i--){
                temp1 = ops.get(i);
                if( temp1 instanceof Selection ){
                    temp = temp1.getRelationOp1();
                    if(temp!=null && (temp instanceof SetOp) && !temp.getOperation().equals("union")){   //to projection paizei panw se apotelesma set
                        
                        //selection 1
                        temp1.setRelation1(temp.getRelation1());
                        temp1.setRelationOp1(temp.getRelationOp1());
                        temp1.setTabInfo1(temp.getOutTableInfo1());

                        //new set
                        temp.setRelation1(null);
                        temp.setRelationOp1(temp1);
                        
                        //construct operation table
                        ops.remove(temp1);
                        ops.add(i-1, temp1);
                        
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
    
    private boolean pushSelectionInSet2(ArrayList<Operator> ops){
        boolean tranformflag=true;
        boolean tranformed = false;
        Operator temp=null;     //the setoperation
        Operator temp1,temp2;  //the projections
        
        
        while(tranformflag){
            tranformflag=false;
            for(int i=ops.size()-1; i>-1; i--){
                temp1 = ops.get(i);
                if( temp1 instanceof Selection ){
                    temp = temp1.getRelationOp1();
                    if(temp!=null && (temp instanceof SetOp)){   //to projection paizei panw se apotelesma set
                        
                        //selection 1
                        temp1.setRelation1(temp.getRelation1());
                        temp1.setRelationOp1(temp.getRelationOp1());
                        temp1.setTabInfo1(temp.getOutTableInfo1());

                        //selection 2
                        temp2 = temp1.fullCopy(null);
                        temp2.setRelation1(temp.getRelation2());
                        temp2.setRelationOp1(temp.getRelationOp2());
                        temp2.setTabInfo1(temp.getOutTableInfo2());
                            
                        //new set
                        temp.setRelation1(null);
                        temp.setRelationOp1(temp1);
                        temp.setRelation2(null);
                        temp.setRelationOp2(temp2);

                        //construct operation table
                        ops.remove(temp1);
                        ops.add(i-1, temp1);
                        ops.add(i-1, temp2);

                        
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
                            Operator startProj = temp1;  //delete thus
                            if(projAttrs1!=null){
                               // temp1 = new Projection(); //delete this
                                temp1.setRelation1(temp.getRelation1());
                                temp1.setRelationOp1(temp.getRelationOp1());
                                temp1.setAttributes(projAttrs1);
                                        temp.setRelation1(null);
                                        temp.setRelationOp1(temp1);
                                       ops.remove(temp1);        //restore this
                                        ops.add(i-1, temp1);
                                
                            }
                            if(projAttrs2!=null){    
                                        temp2 = new Projection();
                                        temp2.setRelation1(temp.getRelation2());
                                        temp2.setRelationOp1(temp.getRelationOp2());
                                        temp2.setAttributes(projAttrs2);
                                        temp.setRelation2(null);
                                        temp.setRelationOp2(temp2);
                                        ops.add(i-1, temp2);
                            }
                            
                            //delete this
                            /*
                            if(!extraNeededProjAttrs(projAttrs1, projAttrs2, projAttrs)){
                                ops.remove(startProj);                 
                            }*/
                            
                            
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
    
        
    public boolean extraNeededProjAttrs(ArrayList<String> proj1, ArrayList<String> proj2, ArrayList<String> proj){
        
        
        for(int i =0; i<proj1.size();i++){
            String temp = proj1.get(i);
            if(!proj.contains(temp)){
                return true;
            }
        }
        for(int i =0; i<proj2.size();i++){
            String temp = proj2.get(i);
            if(!proj.contains(temp)){
                return true;
            }
        }
        return false;
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
            updateBest=true;
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
    
     
    public ArrayList<Operator> getPlanCopy(ArrayList<Operator> old){
        ArrayList<Operator> copy = new ArrayList<Operator>();
        Map<Operator,Operator> update = new HashMap<Operator,Operator>();

        for(int i=0;i<old.size();i++){            
            copy.add(old.get(i).fullCopy(update));
        }
        return copy;
    }
    
    
}
