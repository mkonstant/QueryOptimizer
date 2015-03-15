/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluationCost;

import catalog.SystemInfo;
import catalog.TableInfo;
import java.util.ArrayList;
import java.util.Set;

/**
 *
 * @author michalis
 */
public class SetOperationCost {
    
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
    private ArrayList<String> annotation = new ArrayList<String>();
    private ArrayList<String> sortedAnnotation ;
    private ArrayList<String> hasedAnnotation ;
    private boolean sorted=false;
    private Set<String> sortKey=null;
    private TableInfo tInfo1;
    private TableInfo tInfo2;
    
    public SetOperationCost(SystemInfo si, TableInfo tInfo1 , TableInfo tInfo2) {
        tranferTime = si.getTransferTime();
        penaltyTime = si.getTimeForWritingPages();
        latency = si.getLatency();
        M = si.getNumOfBuffers();
        
        bb= M-2;
        
        this.nr= tInfo1.getNumberOfTuples();
        br = (nr*tInfo1.getSizeOfTuple()) / si.getSizeOfBuffer();
        this.ns= tInfo2.getNumberOfTuples();
        bs = (ns*tInfo2.getSizeOfTuple()) / si.getSizeOfBuffer();
        
        this.tInfo1 = tInfo1;
        this.tInfo2 = tInfo2;
        
        
        
    }
    
    public Set<String> getSortKey(){
        return sortKey;
    }
    
    
   
    public double computeCost(){
        
        
        boolean sorted1=false;
        boolean sorted2=false;
        boolean hashed1=false;
        boolean hashed2=false;
        String primaryStructure1 ;
        String primaryStructure2 ;
        
        boolean fromOperator1 = tInfo1.getOperator();
        boolean fromOperator2 = tInfo2.getOperator();
        
        ArrayList<String> temp_annotation = new ArrayList<String>();
        ArrayList<String> temp_sortedAnnotation = new ArrayList<String>();
        ArrayList<String> temp_hasedAnnotation = new ArrayList<String>();
        Set<String> sortKey1 ;
        Set<String> sortKey2 ;
        double cost=0;
        
        double costSort=0 ;
        double costHash=0 ;
        
        if(fromOperator1 || fromOperator2){ //one of the two is output of operation
            if(fromOperator1 && fromOperator2){
                sorted1 = tInfo1.getSorted();
                sorted2 = tInfo2.getSorted();
                hashed1=false;
                hashed2=false;
            }
            else if(fromOperator1){
                sorted1 = tInfo1.getSorted();
                sorted2 = tInfo2.isSorted(); 
                hashed2 = tInfo2.hashIndexExist();
            }
            else{
                sorted2 = tInfo2.getSorted();
                sorted1 = tInfo1.isSorted(); 
                hashed1 = tInfo1.hashIndexExist();
            }
            
            sortKey1 = tInfo1.getSortKet();
            sortKey2 = tInfo2.getSortKet();
            
            if(sorted1 && sorted2){
                if(checkSameKey(sortKey1,sortKey2)){
                    sortKey = sortKey1;
                }
                else{//different sorting....compute both
                    double costSort1 = sortedSets(false,true);
                    double costSort2 = sortedSets(true,false);
                    if(costSort1< costSort2){
                        sorted1=false;
                        sorted2=true;
                        sortKey=sortKey2;
                    }
                    else{
                        sorted2=false;
                        sorted1=true;
                        sortKey=sortKey1;
                    }
                }

            }else if(sorted1){
                sortKey = sortKey1;
            }
            else if(sorted2){
                sortKey = sortKey2;
            }
            else{
                sortKey=null;
            }
            costSort = sortedSets(sorted1,sorted2);
            costHash = hashedSets(hashed1,hashed2);
        }
        else{
            if(tInfo1.sortedSameKey(tInfo2)){  //sort on same key
                //System.out.println("Both sorted");
                costSort = sortedSets(true,true);// both sorted on same key, minumun sortcost
                sortKey = tInfo1.getPrimaryIndex().getIndexName();
                if(tInfo1.hashIndexSameKey(tInfo2)){  //hashed same index
                    //System.out.println("Both sorted-> both hashed");
                    costHash = hashedSets(true,true);  //both hased same key, minimum hash cost
                }
                else{
                    hashed1 = tInfo1.hashIndexExist();
                    hashed2 = tInfo2.hashIndexExist();
                    System.out.println(hashed1+"   "+hashed2);
                    if(hashed1 && hashed2){ //both hashed on another key ...keep the minimum
                        double costHash1 = hashedSets(false,true);
                        temp_hasedAnnotation = hasedAnnotation;
                        costHash = hashedSets(true,false);
                        if(costHash1 < costHash){
                            costHash = costHash1;
                            hasedAnnotation = temp_annotation;
                        }
                    }
                    else
                        costHash = hashedSets(hashed1,hashed2);
                } 
            }
            else if(tInfo1.hashIndexSameKey(tInfo2)){ //hash index same key
                costHash = hashedSets(true,true);  //both hased same key, minimum hash cost
               // System.out.println("Both hashed");
                sorted1 = tInfo1.isSorted();
                sorted2 = tInfo2.isSorted();
                sortKey1 = tInfo1.getSortKet();
                sortKey2 = tInfo2.getSortKet();
                
                if(sorted1 && sorted2){
                    double costSort1 = sortedSets(true,false);
                    temp_sortedAnnotation = sortedAnnotation;
                    costSort = sortedSets(false,true);
                    sortKey = sortKey2;
                    if(costSort1 < costSort){
                        sortKey = sortKey1;
                        costSort = costSort1;
                        sortedAnnotation = temp_sortedAnnotation;
                    }
                }
                else{
                    costSort = sortedSets(sorted1,sorted2);
                    if(sorted1)
                        sortKey = sortKey1;
                    else if(sorted2)
                        sortKey = sortKey2;
                    else
                       sortKey = null;    
                }
            }
            else{ //no commont sort/hash
                sorted1 = tInfo1.isSorted();
                sorted2 = tInfo2.isSorted();
                hashed1 = tInfo1.hashIndexExist();
                hashed2 = tInfo2.hashIndexExist();
                sortKey1 = tInfo1.getSortKet();
                sortKey2 = tInfo2.getSortKet();
                /*
                                    System.out.println(sorted1);
                    System.out.println(sorted2);
                    System.out.println(hashed1);
                    System.out.println(hashed2);
                */
                if(sorted1 && sorted2){
                    double costSort1 = sortedSets(true,false);
                    temp_sortedAnnotation = sortedAnnotation;
                    costSort = sortedSets(false,true);
                    sortKey = sortKey2;
                    if(costSort1 < costSort){
                        sortKey = sortKey1;
                        costSort = costSort1;
                        sortedAnnotation = temp_sortedAnnotation;
                    }
                }
                else{
                    costSort = sortedSets(sorted1,sorted2);
                    if(sorted1)
                        sortKey = sortKey1;
                    else if(sorted2)
                        sortKey = sortKey2;
                    else
                       sortKey = null;    
                }
                
                if(hashed1 && hashed2){ //both hashed on another key ...keep the minimum
                    double costHash1 = hashedSets(false,true);
                    temp_hasedAnnotation = hasedAnnotation;
                    costHash = hashedSets(true,false);
                    if(costHash1 < costHash){
                        costHash = costHash1;
                        hasedAnnotation = temp_annotation;
                    }
                }
                else
                     costHash = hashedSets(hashed1,hashed2);
                    

            }
        }
        //System.out.println("sort:"+costSort);
        //System.out.println("hash:"+costHash);
        if(costHash>= costSort){
            annotation = sortedAnnotation;
            cost = costSort;
            sorted=true;
        }
        else{
            annotation = hasedAnnotation;
            cost = costHash;
        }
        
        return cost;
        
    }
    
    
    public String getAnnotation(){
        String temp="";
        
        for(int i=0;i<annotation.size();i++)
        {
            if(i>0)
                temp+=" -> ";
            temp += annotation.get(i);
            
        }
        return temp;
    }
    
    
    /*public void computeCost(boolean s1, boolean s2, boolean h1, boolean h2){
        double costSort = sortedSets(s1,s2);
        double costHash = hashedSets(h1,h2);
        
        if(costHash> costSort){
            annotation = sortedAnnotation;
            cost = costSort;
            sorted=true;
        }
        else{
            annotation = hasedAnnotation;
            cost = costHash;
        }
    }*/
    
    public boolean getSorted(){
        return sorted;
    }
    
    public double sortedSets(boolean sorted1 , boolean sorted2){
        sortedAnnotation = new ArrayList<String>();
        
        //performing sorting
        double sortTime=0.0;
        if(!sorted1){
            sortTime= SortCost.computeCost(br, M, latency, penaltyTime,  tranferTime);
            sortedAnnotation.add("Sort rel1");
        }
        else{
            sortedAnnotation.add("Use sorted rel1");
        }
        if(!sorted2){
            sortTime+= SortCost.computeCost(bs,M, latency, penaltyTime,  tranferTime);
            sortedAnnotation.add("Sort rel2");
        }
        else{
            sortedAnnotation.add("Use sorted rel2");
        }
        sortedAnnotation.add("Perform operation on sortred relations");
        //performing set operation
        int blocksTranfered = br+bs;
        int diskSeeks = (br/bb) + (bs/bb);
         
        //writting back output   how much???? calculate distinct values ktl
        int blockWritten = br+bs;
       
        return (diskSeeks*latency + blocksTranfered*tranferTime + blockWritten*penaltyTime + sortTime);  
    }
    
    
    public double hashedSets(boolean partitioned1,boolean partitioned2){
        hasedAnnotation = new ArrayList<String>();
        int blocksTranfered=0;
        int blockWritten=0;
        
        //partitioning relations
        if(!partitioned1){
            hasedAnnotation.add("Partition rel1");
            //reading for partitioning
            blocksTranfered = br;
            //writting after partitioning
            blockWritten = br;
        }
        else{
            hasedAnnotation.add("Use hashed rel1");
        }
        
        if(!partitioned2){
            hasedAnnotation.add("Partition rel2");
            //reading for partitioning
            blocksTranfered += bs;
            //writting after partitioning
            blockWritten += bs;
        }
        else{
            hasedAnnotation.add("Use hashed rel2");
        }
        
        //performing set operation....for each partition of r build index kai check correspondi partition of S
        //so we have br+bs tranfers and br+bs seeks worst case
        
        //reading for join
        blocksTranfered+= br+bs;
        //2nh factor is too small coresponding to br+bs,it can be ignored
        int diskSeeks = 2*((br/bb) + (bs/bb));
        
        //writting back output   how much???? calculate distinct values ktl
        blockWritten += br+bs;
        hasedAnnotation.add("Perform operation on hashed relations"); 
        
        return (diskSeeks*latency + blockWritten*penaltyTime + blocksTranfered*tranferTime);       
   }
    
   private boolean checkSameKey(Set<String> s1, Set<String> s2){
       if(s1.size()!=s2.size())
           return false;
       for(String i: s1){
           if(!s2.contains(i))
               return false;
       }
       return true;
   } 
        
    
        
}
