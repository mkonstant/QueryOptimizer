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
public class JoinCost {
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
    
    public int blockNestedJoin(){
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
        
        //writting back output   how much???? calculate distinct values ktl
         int blockWritten = br+bs;
         
         return (diskSeeks*latency + blocksTranfered*tranferTime + blockWritten*penaltyTime ); 
         
    }
    
    
      public int indexedBlockNestedJoin(boolean indexed1, boolean indexed2){
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
        int cost = outerR *(latency+tranferTime) + outerS *selectionCost;
        
        
        //writting back output   how much???? calculate distinct values ktl
         int blockWritten = br+bs;
         
         return (cost+ blockWritten*penaltyTime ); 
         
    }
    
    
    public int mergeJoin(boolean sorted1, boolean sorted2){ //relations have to be already sorted
      
        //performing sorting
        int sortTime=0;
        if(!sorted1)
            sortTime= SortCost.externalSortCost(br, M, latency, penaltyTime,  tranferTime);
        if(!sorted2)
            sortTime= SortCost.externalSortCost(bs, M, latency, penaltyTime,  tranferTime);
        
        //performing join
         int blocksTranfered = br+bs;
         int diskSeeks = (br/bb) + (bs/bb);
         
         //writting back output   how much???? calculate distinct values ktl
         int blockWritten = br+bs;
         
         
         
         return (diskSeeks*latency + blocksTranfered*tranferTime + blockWritten*penaltyTime + sortTime); 
         
    }
    
    public int hashJoin(boolean partitioned1, boolean partinioned2){ //sunolika 3(br+bs)+ nh to opoio parleipetai 
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
        
        //reading for join
        blocksTranfered+= br+bs;
        //2nh factor is too small coresponding to br+bs,it can be ignored
        int diskSeeks = 2*((br/bb) + (bs/bb));
        
        //writting back output   how much???? calculate distinct values ktl
        blockWritten += br+bs;
         
         
        return (diskSeeks*latency + blockWritten*penaltyTime + blocksTranfered*tranferTime); 
         
    }
    
    
    
    
    
    //tha xrhsimopoihthei??? 
    public void recursiveHashJoin(){
        int partitionPasses= (int) (Math.log10(br)/Math.log10(M-1) -1) ;
        int blocksTranfered = 2*(br+bs)* partitionPasses + bs+br;
        
        int diskSeeks = 2*((br/bb) + (bs/bb))* partitionPasses;
    
    }
    
   
}
