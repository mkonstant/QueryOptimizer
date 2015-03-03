/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluationCost;

import java.util.ArrayList;

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
    private ArrayList<String> sortedAnnotation = new ArrayList<String>();
    private ArrayList<String> hasedAnnotation = new ArrayList<String>();
    
    public String getAnnotation(){
        String temp="";
        
        for(int i=0;i<annotation.size();i++)
        {
            if(i>0)
                temp+="->";
            temp += annotation.get(i);
            
        }
        return temp;
    }
    
    
    public void computeCost(){
        double costSort = sortedSets(true, true);
        double costHash = hashedSets(true, true);
        
        if(costHash> costSort){
            annotation = sortedAnnotation;
            cost = costSort;
        }
        else{
            annotation = hasedAnnotation;
            cost = costHash;
        }
    }
    
    public double sortedSets(boolean sorted1 , boolean sorted2){
        //performing sorting
        double sortTime=0.0;
        if(!sorted1){
            sortTime= SortCost.externalSortCost(br, M, latency, penaltyTime,  tranferTime);
            sortedAnnotation.add("Sort rel1");
        }
        else{
            sortedAnnotation.add("Use sorted rel1");
        }
        if(!sorted2){
            sortTime+= SortCost.externalSortCost(bs,M, latency, penaltyTime,  tranferTime);
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
        
        if(!partitioned1){
            hasedAnnotation.add("Partition rel2");
            //reading for partitioning
            blocksTranfered += bs;
            //writting after partitioning
            blockWritten += bs;
        }
        else{
            hasedAnnotation.add("Use hashed rel1");
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
    
    
        
        
}
