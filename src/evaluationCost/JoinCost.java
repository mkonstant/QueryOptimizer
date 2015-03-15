/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluationCost;

import catalog.IndexInfo;
import catalog.SystemInfo;
import catalog.TableInfo;
import java.util.ArrayList;
import java.util.Set;
import operations.Condition;

/**
 *
 * @author michalis
 */
public class JoinCost {
    private int M; //num of buffers available for sorting
    private int br;  //blocks containing tupples of relation r
    private int bs;//blocks containing tupples of relation s
    private int bb;//buffer blocks per run
    private int nr;//number of tupples in r
    private int ns;//number of tupples in s
    private double tranferTime;
    private double penaltyTime;
    private double latency;
    private double cost;
    private String message;
    private ArrayList<String> annotation ;
    private ArrayList<String> hashAnnotation = new ArrayList<String>();
    private ArrayList<String> mergeAnnotation = new ArrayList<String>();
    private ArrayList<String> blockNestedAnnotation = new ArrayList<String>();
    private ArrayList<String> indexedNestedAnnotation = new ArrayList<String>();
    private boolean sorted=true;
    SystemInfo si = null;
    Condition condition = null;
    TableInfo tabInfo1 = null;
    TableInfo tabInfo2 = null;
    IndexInfo index1 = null;
    IndexInfo index2 = null;
    ArrayList<Double> allCosts = new ArrayList<Double>();
    ArrayList<String> allMessages = new ArrayList<String>();
    
    
    public JoinCost(SystemInfo si, Condition condition ,TableInfo tabInfo1, TableInfo tabInfo2, IndexInfo index1, IndexInfo index2 ) {
        this.si = si;
        this.condition = condition;
        this.tabInfo1 = tabInfo1;
        this.tabInfo2 = tabInfo2;
        this.index1 = index1;
        this.index2 = index2;
    }

    public double getCost() {
        return cost;
    }
    
    
    public String getMessage(){
        
        return message;
    }
    
    public void calculateVariables(){
        tranferTime = si.getTransferTime();
        penaltyTime = si.getTimeForWritingPages();
        latency = si.getLatency();
        M = si.getNumOfBuffers();
        bb= M-2;
        this.nr=tabInfo1.getNumberOfTuples();
        br = (nr*tabInfo1.getSizeOfTuple()) / si.getSizeOfBuffer();
        this.ns=tabInfo2.getNumberOfTuples();
        bs = (ns*tabInfo2.getSizeOfTuple()) / si.getSizeOfBuffer();
        
    }
    

    //public double computeCost(boolean s1,boolean s2, boolean h1, boolean h2, boolean i1,boolean i2){
    public void computeCost(){
        double costMerge = -1;
        double costHash = -1;
        double costBlockNested = -1;
        int minNumCost = -1;
        boolean sorted1 = false;
        boolean sorted2 = false;
        
        calculateVariables();
        
        if (index1 != null && index2 != null ){
            if ( index1.getStructure().equals(index2.getStructure()) ){//same structure
                if (index1.getStructure().equals("B+tree")){
                    if (sameIndex(index1,index2)){
                        costMerge = mergeJoin(true,true);
                        allCosts.add(costMerge);
                        
                    }
                    else{
                        costMerge = mergeJoin(true,false);
                        allCosts.add(costMerge);
                        
                        costMerge = mergeJoin(false,true);
                        allCosts.add(costMerge);
                                               
                    }
                    
                    costHash = hashJoin(false,false);
                    allCosts.add(costHash);
                        
                }
                else{
                    if (sameIndex(index1,index2)){
                                              
                        costHash = hashJoin(true,true);
                        allCosts.add(costHash);
                       
                    }
                    else{
                        
                        costHash = hashJoin(true,false);
                        allCosts.add(costHash);
                     
                        costHash = hashJoin(false,true);
                        allCosts.add(costHash);
                        
                    }
                    
                    costMerge = mergeJoin(false,false);
                    allCosts.add(costMerge);

                }
            }
            else{//different structure
                if (index1.getStructure().equals("B+tree")){
                    costMerge = mergeJoin(true,false);
                    allCosts.add(costMerge);
                        
                    costHash = hashJoin(false,true);
                    allCosts.add(costHash);
                        
                    
                }
                else{
                    costMerge = mergeJoin(false,true);
                    allCosts.add(costMerge);
                        
                    costHash = hashJoin(true,false);
                    allCosts.add(costHash);
                       
                }
                
            }
        
        }
        else{
            if ( index1 != null ){
                if ( index1.getStructure().equals("B+tree")){
                    if ( tabInfo2.getSorted() ){
                        Set <String> sortedKey = tabInfo2.getSortKet();
                        if ( this.sameSortedKey(index1.getIndexName(), sortedKey)){//both sorted same key
                            //sorted2 = true;
                            costMerge = mergeJoin(true,true);
                            allCosts.add(costMerge);
                            
                        }
                        else{//both sorted different key
                            costMerge = mergeJoin(true,false);
                            allCosts.add(costMerge);
                            
                            costMerge = mergeJoin(false,true);
                            allCosts.add(costMerge);

                        }
                        
                        costHash = hashJoin(false,false);
                        allCosts.add(costHash);
                        
                    }
                    else{//only relation1 sorted
                        costMerge = mergeJoin(true,false);
                        allCosts.add(costMerge);

                        costHash = hashJoin(false,false);
                        allCosts.add(costHash);
                    
                    }
                    
                }
                else{
                    costMerge = mergeJoin(false,tabInfo2.getSorted());
                    allCosts.add(costMerge);
                    
                    costHash = hashJoin(true,false);
                    allCosts.add(costHash);
                    
                }
            
            }
            else if ( index2 != null ){
                if ( index2.getStructure().equals("B+tree")){
                     if ( tabInfo1.getSorted() ){
                        Set <String> sortedKey = tabInfo1.getSortKet();
                        if ( this.sameSortedKey(index2.getIndexName(), sortedKey)){
                            costMerge = mergeJoin(true,true);
                            allCosts.add(costMerge);

                        }
                        else{
                            costMerge = mergeJoin(false,true);
                            allCosts.add(costMerge);
                            
                            costMerge = mergeJoin(true,false);
                            allCosts.add(costMerge);

                        }
                        
                        costHash = hashJoin(false,false);
                        allCosts.add(costHash);
                        
                     }
                     else{
                        costMerge = mergeJoin(false,true);
                        allCosts.add(costMerge);

                        costHash = hashJoin(false,false);
                        allCosts.add(costHash);

                     }
                          
                }
                else{
                    costMerge = mergeJoin(tabInfo1.getSorted(),false);
                    allCosts.add(costMerge);
                    
                    costHash = hashJoin(false,true);
                    allCosts.add(costHash);
                    
                }
            }
            else{
                if ( tabInfo1.getSorted()){
                    if ( tabInfo2.getSorted()){
                        if ( this.sameSortedKey(tabInfo1.getSortKet(), tabInfo2.getSortKet())){
                            costMerge = mergeJoin(true,true);
                            allCosts.add(costMerge);
                        }
                        else{
                            costMerge = mergeJoin(true,false);
                            allCosts.add(costMerge);
                            
                            costMerge = mergeJoin(false,true);
                            allCosts.add(costMerge);
                        }
                    }
                    else{
                        costMerge = mergeJoin(true,false);
                        allCosts.add(costMerge);
                    }
                }
                else{
                    if ( tabInfo2.getSorted()){
                        costMerge = mergeJoin(false,true);
                        allCosts.add(costMerge);
                    }
                    else{
                        costMerge = mergeJoin(false,false);
                        allCosts.add(costMerge);
                    }
                
                }
                
                costHash = hashJoin(false,false);
                allCosts.add(costHash);
                
            }
        }
        
        costBlockNested = blockNestedJoin();
        allCosts.add(costBlockNested);;
        
        minNumCost = this.getMinCost();
        cost = allCosts.get(minNumCost);
        message = allMessages.get(minNumCost);
        
        System.out.println("join Cost = " + cost);
        System.out.println("message = " + message);
        
        /*double costMerge = mergeJoin(s1,s2);
        double costHash = hashJoin(h1,h2);
        double costBlockNested  = blockNestedJoin();
        //double costIndexedBlockNested = indexedBlockNestedJoin(i1, i2);
        
        
        
        cost= costMerge;
        annotation = mergeAnnotation;
        
        if(costHash< cost){
            annotation = hashAnnotation;
            cost = costHash;
            sorted=false;
        }
       if(costBlockNested< cost){
            annotation = blockNestedAnnotation;
            cost = costBlockNested;
            sorted=false;
        }
       // if(costIndexedBlockNested< cost){
        //    annotation = indexedNestedAnnotation;
       //     cost = costIndexedBlockNested;
       //     sorted=false;
       // }
        //return br*tranferTime + (br/bb)*latency;
       */
       
    }
    
    public boolean getSorted(){
        return sorted;
    }
    
    public double blockNestedJoin(){
        int outerR;
        int innerR;
        int blocksTranfered ;
        int diskSeeks;
        
        if(bs<br){
            outerR = bs;
            innerR = br;
        }
        else{
            outerR = br;
            innerR = bs;
        }
          
        
        
        //relation feets in memory
        if(outerR <= bb)
        {
            blocksTranfered = outerR* innerR;
            diskSeeks=2;            
        }
        else{
            //hold 1 block for innerR and 1 for ouput, the others on outer
            blocksTranfered = (outerR/(M-2))* innerR + outerR;
            diskSeeks=2*(outerR/(M-2));
            
            //blocksTranfered = outerR* innerR + outerR;
            //diskSeeks = 2*outerR;
        }
        
        allMessages.add("block Nested loop join");
         
        return (diskSeeks*latency + blocksTranfered*tranferTime ); 
         
    }
    
    
      public double indexedBlockNestedJoin(boolean indexed1, boolean indexed2){
        int outerR, outerS;
        int innerR, innerS;
        int blocksTranfered ;
        int diskSeeks;
        
        //both relations indexed -> choose outer the one with less tupples
        if(indexed1 && indexed2){
            if(ns<nr){
                outerR = bs; outerS=ns;
                innerR = br; innerS = nr;
            }
            else{
                outerR = br; outerS=nr;
                innerR = bs; innerS = ns;
            }    
        }  
        //only relation1 is indexed so relation1 is inner
        else if(indexed1){
            outerR = bs; outerS=ns;
            innerR = br; innerS = nr; 
        }
        //only relation2 is indexed so relation2 is inner
        else if(indexed2)
        {
            outerR = br; outerS=nr;
            innerR = bs; innerS = ns;
        }
        else{
            //no indexed, return 0 to choose another join
            return 0;
        }
        
        //////////////////////////////////////CALLL THE SELECTION
        int selectionCost=0;
        
        //for each block of outer 1 seek and 1 block transfer + for each tupple selection on inner indexed
        double cost = outerR *(latency+tranferTime) + outerS *selectionCost;
        indexedNestedAnnotation.add("indexed block Nested loop join");
        
        return cost;
         
    }
    
    
    public double mergeJoin(boolean sorted1, boolean sorted2){ //relations have to be already sorted
        String message = "";
        
        //performing sorting
        double sortTime=0;
        if(!sorted1){
            message += ("sort relation1");
            sortTime= SortCost.computeCost(br, M, latency, penaltyTime,  tranferTime);
        }
        else
            message += ("Use sorted relation1");
        if(!sorted2){
            message += (" -> sort relation2"); 
            sortTime= SortCost.computeCost(bs, M, latency, penaltyTime,  tranferTime);

        }
        else
            message += (" -> Use sorted relation2");
        
        //performing join
         int blocksTranfered = br+bs;
         int diskSeeks = (br/bb) + (bs/bb);
         
         message += (" -> merge join");
         
         allMessages.add(message);
         
         return (diskSeeks*latency + blocksTranfered*tranferTime  + sortTime); 
         
    }
    
    public double hashJoin(boolean partitioned1, boolean partitioned2){ //sunolika 3(br+bs)+ nh to opoio parleipetai 
        int blocksTranfered=0;
        int blockWritten=0;
        String message = "";
        
        //partitioning relations
        if(!partitioned1){
            message += ("partition relation1");
            //reading for partitioning
            blocksTranfered = br;
            //writting after partitioning
            blockWritten = br;
        }
        else
            message += ("use hashIndex on relation1");
        if(!partitioned2){
            message += (" -> partition relation2");
            //reading for partitioning
            blocksTranfered += bs;
            //writting after partitioning
            blockWritten += bs;
        }
        else
            message += (" -> use hashIndex on relation2");
       
        
        //reading for join
        blocksTranfered+= br+bs;
        //2nh factor is too small coresponding to br+bs,it can be ignored
        int diskSeeks = 2*((br/bb) + (bs/bb));
        message += (" -> hash join");
        
        allMessages.add(message);
        
        return (diskSeeks*latency + blockWritten*penaltyTime + blocksTranfered*tranferTime); 
         
    }
    

    
    public int getMinCost(){
        double minCost = -1;
        int position = -1;
        
        if ( allCosts.size() > 1 ){
            minCost = allCosts.get(0);
            position = 0;
            for ( int i = 1 ; i < allCosts.size() ; i ++ ){
                if ( minCost > allCosts.get(i)){
                    minCost = allCosts.get(i);
                    position = i;
                }
            }
            return position;
        }
        else{
            return 0;
        }
    }
    
    public boolean sameIndex(IndexInfo index1, IndexInfo index2){
        if( index1.getIndexName().size() == index2.getIndexName().size()){
            for ( String indexName : index1.getIndexName()){
                if( !index2.getIndexName().contains(indexName)){
                    return false;
                }
            }
        }
        else{
            return false;
        }
        
        return true;
        
    }
    
    public boolean sameSortedKey(Set <String> set1, Set <String> set2){
        
        
        return false;
    }
    
   
}
