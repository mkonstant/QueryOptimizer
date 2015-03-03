/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluationCost;

/**
 *
 * @author michalis
 */
public class SetOperationCost {
    
    int M; //num of buffers available for sorting
    int br;  //blocks containing tupples of relation r
    int bs;//blocks containing tupples of relation s
    int bb;//buffer blocks per run
    int nr;//number of tupples in r
    int ns;//number of tupples in s
    int tranferTime;
    int penaltyTime;
    int latency;
    
    /*
        public int externalSortCost(int br){
        
        int mergePasses = (int) (Math.log10(br/M)/Math.log10(M-1)); 
        int blocksTranfered = br * (2*mergePasses+1);
        //writting back the result
        int blockWritten = br;
        
        //seeks for sorting
        int diskSeeks = 2*(br/M)+ br*(2*mergePasses-1) ;
        //seeks for writting back
        diskSeeks+=br;
        return (diskSeeks*latency + blockWritten*penaltyTime + blocksTranfered*tranferTime);
    }
    */
    
    public int sortedSet(boolean sorted1 , boolean sorted2){
        //performing sorting
        int sortTime=0;
        if(!sorted1)
            sortTime= SortCost.externalSortCost(br, M, latency, penaltyTime,  tranferTime);
        if(!sorted2)
            sortTime+= SortCost.externalSortCost(bs,M, latency, penaltyTime,  tranferTime);
            
        //performing set operation
        int blocksTranfered = br+bs;
        int diskSeeks = (br/bb) + (bs/bb);
         
        //writting back output   how much???? calculate distinct values ktl
        int blockWritten = br+bs;
       
        return (diskSeeks*latency + blocksTranfered*tranferTime + blockWritten*penaltyTime + sortTime); 
        
    }
    
    
    public int hashedSets(boolean partitioned1,boolean partitioned2){
        int blocksTranfered=0;
        int blockWritten=0;
        
        //partitioning relations
        if(!partitioned1){
            //reading for partitioning
            blocksTranfered = br;
            //writting after partitioning
            blockWritten = br;
        }
        if(!partitioned1){
            //reading for partitioning
            blocksTranfered += bs;
            //writting after partitioning
            blockWritten += bs;
        }
        
        //performing set operation....for each partition of r build index kai check correspondi partition of S
        //so we have br+bs tranfers and br+bs seeks worst case
        
        //reading for join
        blocksTranfered+= br+bs;
        //2nh factor is too small coresponding to br+bs,it can be ignored
        int diskSeeks = 2*((br/bb) + (bs/bb));
        
        //writting back output   how much???? calculate distinct values ktl
        blockWritten += br+bs;
         
         
        return (diskSeeks*latency + blockWritten*penaltyTime + blocksTranfered*tranferTime); 
        
   }
    
    
        
        
}
