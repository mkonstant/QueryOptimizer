/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package querryoptimizer;

import catalog.Attributes;
import catalog.Catalog;
import catalog.TableInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import operations.Condition;
import operations.Join;
import operations.Operator;
import operations.Projection;
import operations.Selection;
import operations.SetOp;
import syntaxtree.Attribute;

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
      
    
    public void ApplyTranformations(boolean all){
        double cost;
        int i =0;
        ArrayList<Operator> temp ;
        
        updateBest=true;
        while(updateBest){
            while(updateBest){
                updateBest=false; 
                i++;
                if(all)
                    System.out.println("\n\n\nPass "+i);


                temp = getPlanCopy(BestPlan);
                if(eliminateMultipleProjections(temp)){                    
                    cost = processPlan(temp);
                    if(all){
                         System.out.println("\n\n\nQuery Projection Elmination:");
                        printPLan(temp, cost);
                    }
                    if(updateBest)
                        break;
                    temp = getPlanCopy(BestPlan);
                }
                if(pushProjectionsInSet(temp)){
                    cost = processPlan(temp);
                    if(all){
                        System.out.println("\n\n\nPush Projection in Set:");
                        printPLan(temp, cost);
                    }
                    if(updateBest){
                        break;
                    }
                    temp = getPlanCopy(BestPlan);
                }
                if(pushProjectionsInJoin(temp)){
                    cost = processPlan(temp);
                    if(all){
                        System.out.println("\n\n\nPush Projection in Join:");
                        printPLan(temp, cost);
                    }
                    if(updateBest){
                        break;
                    }
                    temp = getPlanCopy(BestPlan);
                }
                if(rearangeSelections(temp)){
                    cost = processPlan(temp);
                    if(all){
                        System.out.println("\n\n\nSelection rearange:");
                        printPLan(temp, cost);
                    }
                    if(updateBest)
                        break;
                    temp = getPlanCopy(BestPlan);
                }
                if(rearangeSets(temp)){
                    cost = processPlan(temp);
                    if(all){
                        System.out.println("\n\n\nSet rearange:");
                        printPLan(temp, cost);
                    }
                    if(updateBest)
                        break;
                    temp = getPlanCopy(BestPlan);
                }
                if(pushSelectionInSet1(temp)){
                    cost = processPlan(temp);
                    if(all){
                        System.out.println("\n\n\nPush Selection in Set:");
                        printPLan(temp, cost);
                    }
                    if(updateBest)
                        break;
                    temp = getPlanCopy(BestPlan);
                }
                if( pushSelectionInSet2(temp)){
                    cost = processPlan(temp);
                    if(all){
                        System.out.println("\n\n\nPush Selection in Set::");
                        printPLan(temp, cost);
                    }
                    if(updateBest)
                        break;
                    temp = getPlanCopy(BestPlan);
                }
                if(pushSelectionInJoin(temp)){
                    cost = processPlan(temp);
                    if(all){
                        System.out.println("\n\n\nPush Projection in Join:");
                        printPLan(temp, cost);
                    }
                    if(updateBest)
                        break;
                    temp = getPlanCopy(BestPlan);
                }
                if(rearangeJoins(temp)){
                    cost = processPlan(temp);
                    if(all){
                        System.out.println("\n\n\nRearrange Join:");
                        printPLan(temp, cost);
                    }
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
                        for(int k = ops.size()-1; k > i+1 ;k--)
                        {
                            ops.get(k).updateRelOp(temp2, temp1);
                        }
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
                        for(int k = ops.size()-1; k > i+1 ;k--)
                        {
                            ops.get(k).updateRelOp(temp2, temp1);
                        }
                        
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
                                    
                                    for(int k = ops.size()-1; k > i+1 ;k--)
                                    {
                                        ops.get(k).updateRelOp(temp1, temp);
                                    }
                                    
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
                                    
                                    for(int k = ops.size()-1; k > i+1 ;k--)
                                    {
                                        ops.get(k).updateRelOp(temp1, temp);
                                    }
                                    
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
                                    for(int k = ops.size()-1; k > i+1 ;k--)
                                    {
                                        ops.get(k).updateRelOp(temp2, temp);
                                    }
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
                                    for(int k = ops.size()-1; k > i+1 ;k--)
                                    {
                                        ops.get(k).updateRelOp(temp2, temp);
                                    }
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
        
        //while(tranformflag){
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
       // }
    
        return tranformed;
    }
    
    private boolean pushSelectionInSet2(ArrayList<Operator> ops){
        boolean tranformflag=true;
        boolean tranformed = false;
        Operator temp=null;     //the setoperation
        Operator temp1,temp2;  //the projections
        
        
       // while(tranformflag){
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
       // }
        return tranformed;
    }
        
    private boolean pushProjectionsInSet(ArrayList<Operator> ops){
        boolean tranformflag=true;
        boolean tranformed = false;
        Operator temp=null;     //the setoperation
        Operator temp1,temp2;  //the projections
        
        
       // while(tranformflag){
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
       // }
    
        return tranformed;
    }
    
     private boolean pushProjectionsInJoin(ArrayList<Operator> ops){
        boolean tranformed = false;
        Operator temp=null;     //the setoperation
        Operator temp1,temp2;  //the projections
        

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
                                temp1 = new Projection(); //delete this
                                temp1.setRelation1(temp.getRelation1());
                                temp1.setRelationOp1(temp.getRelationOp1());
                                temp1.setAttributes(projAttrs1);
                                        temp.setRelation1(null);
                                        temp.setRelationOp1(temp1);
                                       // ops.remove(temp1);        //restore this
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
                            
                            
                            if(checkAttrs(projAttrs2, projAttrs1) && checkAttrs( projAttrs1,projAttrs) ){
                                ops.remove(startProj);
                                for(int k = ops.size()-1; k > i+1 ;k--)
                                {
                                    ops.get(k).updateRelOp(startProj, temp);
                                }
                            }
                            
                            
                            
                            
                            tranformed=true;
                            break;
                        }
                    }                 
                }    
            }
        
    
        return tranformed;
    }
    
     
     
     private boolean pushSelectionInJoin(ArrayList<Operator> ops){
        boolean tranformed = false;
        Operator temp=null;     //the join
        Operator temp1,temp2;  //the selections
        
            for(int i=ops.size()-1; i>-1; i--){
                temp1 = ops.get(i);
                if( temp1 instanceof Selection ){
                    temp = temp1.getRelationOp1();
                    if(temp!=null && (temp instanceof Join)){   //to projection paizei panw se apotelesma join

                        ArrayList<Condition> con = temp1.getConditions();
                        //Map<String,Condition> selAttrs = new HashMap<String,Condition>();
                        ArrayList<String> selAttrs = new ArrayList<String>();
                        for(int k=0;k<con.size();k++)
                            selAttrs.add(con.get(k).getAttr1());
                        
                        temp.computeAttributes(); 
                      //  ArrayList<String> joinNeededAttrs1 = temp.getNeededAttributes1();
                      //  ArrayList<String> joinNeededAttrs2 = temp.getNeededAttributes2();
                       
                        ArrayList<String> relAttrs1 = temp.getRelAttributes1();
                        ArrayList<String> relAttrs2 = temp.getRelAttributes2();
                        
                        ArrayList<String> selAttrs1 = findSelAttrs(relAttrs1, selAttrs);
                        ArrayList<String> selAttrs2 = findSelAttrs(relAttrs2, selAttrs);
                        
                        if(allSelectedAttrs(selAttrs1, selAttrs2, selAttrs)){
                            Operator startProj = temp1;  //delete thus
                            if(selAttrs1!=null){
                                //temp1 = new Projection(); //delete this
                                
                                temp1.setRelation1(temp.getRelation1());
                                temp1.setRelationOp1(temp.getRelationOp1());
                                temp1.setAttributes(selAttrs1);
                                        
                                ArrayList<Condition> newCon = new ArrayList<Condition>();                                
                                for(int l=0; l < selAttrs1.size();l++){
                                    String selatr = selAttrs1.get(l);
                                    //System.out.println(selatr);
                                    
                                    for(int k=0;k<con.size();k++){
                                        Condition selc = con.get(k);
                                        if(selatr.equals(selc.getAttr1())){
                                            newCon.add(selc);
                                            break;
                                        }
                                    }
                                }
                                
                                temp1.setCondition(newCon);
                                
                                temp.setRelation1(null);
                                temp.setRelationOp1(temp1);
                                
                                ops.remove(temp1);        //restore this
                                ops.add(i-1, temp1);
                                
                                for(int k = ops.size()-1; k > i+1 ;k--)
                                {
                                    ops.get(k).updateRelOp(temp1, temp);
                                }
                                
                            }
                            if(selAttrs2!=null){
                                if(selAttrs1==null)
                                    temp2=temp1;
                                else    
                                    temp2 = temp1.fullCopy(null);//delete this
                                
                                temp2.setRelation1(temp.getRelation2());
                                temp2.setRelationOp1(temp.getRelationOp2());
                                temp2.setAttributes(selAttrs2);
                                        
                                ArrayList<Condition> newCon = new ArrayList<Condition>();                                
                                for(int l=0; l < selAttrs2.size();l++){
                                    String selatr = selAttrs2.get(l);
                                    System.out.println(selatr);
                                    
                                    for(int k=0;k<con.size();k++){
                                        Condition selc = con.get(k);
                                        if(selatr.equals(selc.getAttr1())){
                                            newCon.add(selc);
                                            break;
                                        }
                                    }
                                }
                                temp2.setCondition(newCon);
                                
                                temp.setRelation2(null);
                                temp.setRelationOp2(temp2);
                                
                                if(selAttrs1==null)
                                    ops.remove(temp2);        //restore this
                                ops.add(i-1, temp2);
                                
                                for(int k = ops.size()-1; k > i+1 ;k--)
                                {
                                    ops.get(k).updateRelOp(temp2, temp);
                                }
                                
                            }
                            tranformed=true;
                            break;
                        }
                    }                 
                }    
            }
        return tranformed;
    }
    
     
    private boolean rearangeJoins(ArrayList<Operator> ops){
        
        Operator temp=null;     //the join
        Operator temp1,temp2;  //the selections
        Boolean transformed=false;
       
            for(int i=ops.size()-1; i>-1; i--){
                temp = ops.get(i);
                if( temp instanceof Join ){
                    temp1 = temp.getRelationOp1();
                    temp2 = temp.getRelationOp2();
                    
                    ArrayList<Condition> tempConditions= temp.getConditions();      
                    if(tempConditions.size()>1 && temp.getComplexCondtion().equals("and")){
                        if(temp1!=null && (temp1 instanceof Join)){  
                           Map<String, Attributes> attrs1_1 = temp1.getOutTableInfo1().getAttributes();
                           Map<String, Attributes> attrs1_2 = temp1.getOutTableInfo2().getAttributes();
                           ArrayList<Condition> tempConditions1= temp1.getConditions();

                           if(tempConditions1.size()==1){
                                ArrayList<Condition> newJoinCond= new ArrayList<Condition>();
                                transformed=true;
                                while(transformed){
                                    transformed=false;
                                    for(int k=0;k<tempConditions.size();k++){
                                        String conditionAttr = tempConditions.get(k).getAttr1();
                                        if(attrs1_1.containsKey(conditionAttr) && !attrs1_2.containsKey(conditionAttr)){
                                            
                                            newJoinCond.add(tempConditions.get(k));
                                            temp.RemoveCondition(tempConditions.get(k)); 
                                            transformed=true;
                                            break;
                                        }
                                    }
                                }
                                if(newJoinCond.size()>0){
                                    unionConds(tempConditions,tempConditions1 );
                                    if(temp.getConditions().size()>1)
                                        temp.setComplexCondition("and");
                                    else
                                        temp.setComplexCondition(null);
                                    temp1.setCondition(newJoinCond);
                                    if(temp1.getConditions().size()>1)
                                        temp1.setComplexCondition("and");
                                    else
                                        temp1.setComplexCondition(null);

                                    String toSwitch = temp1.getRelation2();
                                    Operator toSwitchOp = temp1.getRelationOp2();

                                    temp1.setRelation2(temp.getRelation2());
                                    temp1.setRelationOp2(temp.getRelationOp2());

                                    temp.setRelation2(toSwitch);
                                    temp.setRelationOp2(toSwitchOp);

                                    ops.remove(temp1);
                                    ops.add(i-1, temp1);
                                    
                                    for(int k = ops.size()-1; k > i+1 ;k--)
                                    {
                                        ops.get(k).updateRelOp(temp1, temp);
                                    }

                                    transformed=true;
                                    break;
                                }
                           }
                        }
                        else if(temp2!=null && (temp2 instanceof Join)){  
                           Map<String, Attributes> attrs2_1 = temp2.getOutTableInfo1().getAttributes();
                           Map<String, Attributes> attrs2_2 = temp2.getOutTableInfo2().getAttributes();
                           ArrayList<Condition> tempConditions2= temp2.getConditions();
                          

                           if(tempConditions2.size()==1){
                                ArrayList<Condition> newJoinCond= new ArrayList<Condition>();
                                transformed=true;
                                while(transformed){
                                    transformed=false;
                                    for(int k=0;k<tempConditions.size();k++){
                                        String conditionAttr = tempConditions.get(k).getAttr1();
                                        if(attrs2_2.containsKey(conditionAttr) && !attrs2_1.containsKey(conditionAttr)){
                                            
                                            newJoinCond.add(tempConditions.get(k));
                                            temp.RemoveCondition(tempConditions.get(k)); 
                                            transformed=true;
                                            break;
                                        }
                                    }
                                }
                                if(newJoinCond.size()>0){
                                    unionConds(tempConditions,tempConditions2 );
                                    if(temp.getConditions().size()>1)
                                        temp.setComplexCondition("and");
                                    else
                                        temp.setComplexCondition(null);
                                    temp2.setCondition(newJoinCond);
                                    if(temp2.getConditions().size()>1)
                                        temp2.setComplexCondition("and");
                                    else
                                        temp2.setComplexCondition(null);

                                    String toSwitch = temp2.getRelation1();
                                    Operator toSwitchOp = temp2.getRelationOp1();

                                    temp2.setRelation1(temp.getRelation1());
                                    temp2.setRelationOp1(temp.getRelationOp1());

                                    temp.setRelation1(toSwitch);
                                    temp.setRelationOp1(toSwitchOp);

                                    ops.remove(temp2);
                                    ops.add(i-1, temp2);
                                    
                                    for(int k = ops.size()-1; k > i+1 ;k--)
                                    {
                                        ops.get(k).updateRelOp(temp2, temp);
                                    }

                                    transformed=true;
                                    break;
                                }
                           }
                        }
                    }
                }
            }
                       
        return transformed;
    }
    
    
    
    
    /*****************************************************************************************************************/
    
    public boolean checkAttrs(ArrayList<String> join, ArrayList<String> proj){
        for(int i=0;i<join.size();i++){
            if(!proj.contains(join.get(i)))
                return false;
        }
        return true;
    }
    
    
    public void unionConds(ArrayList<Condition> first, ArrayList<Condition> second){
        Condition temp;
        for(int i =0; i<second.size();i++){
            temp = second.get(i);
            if(!first.contains(temp))
                first.add(temp);
        }
        //return proj;
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
    
    public ArrayList<String> findSelAttrs(ArrayList<String> rel, ArrayList<String> sel){
        ArrayList<String> temp=null;
        //compute intresection of proj and rel
        for(int i =0; i<sel.size();i++){
            if(rel.contains(sel.get(i))){
                if(temp==null)
                    temp=new ArrayList<String>();
                temp.add(sel.get(i));
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
    
        public boolean allSelectedAttrs(ArrayList<String> sel1, ArrayList<String> sel2, ArrayList<String> sel){
        
        for(int i =0; i<sel.size();i++){
            String temp = sel.get(i);
            if(sel1 !=null){
                if(!sel1.contains(temp)){
                    if(sel2!=null && !sel2.contains(temp))
                        return false;
                }
            }
            else if(sel2!=null && !sel2.contains(temp))
                return false;
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
