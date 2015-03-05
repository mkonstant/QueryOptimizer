/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluationCost;

import catalog.SystemInfo;
import java.util.ArrayList;

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
    private ArrayList<String> annotation ;
    private ArrayList<String> hashAnnotation = new ArrayList<String>();
    private ArrayList<String> mergeAnnotation = new ArrayList<String>();
    private ArrayList<String> blockNestedAnnotation = new ArrayList<String>();
    private ArrayList<String> indexedNestedAnnotation = new ArrayList<String>();
    private boolean sorted=true;
    
    public JoinCost(SystemInfo si, int nr, int nrSize, int ns, int nsSize) {
        tranferTime = si.getTransferTime();
        penaltyTime = si.getTimeForWritingPages();
        latency = si.getLatency();
        M = si.getNumOfBuffers();
        bb= M-2;
        br = (nr*nrSize) / si.getSizeOfBuffer();
        this.nr=nr;
        bs = (ns*nsSize) / si.getSizeOfBuffer();
        this.ns=ns;
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
    

    public void computeCost(boolean s1,boolean s2, boolean h1, boolean h2, boolean i1,boolean i2){
        double costMerge = mergeJoin(s1,s2);
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
       /* if(costIndexedBlockNested< cost){
            annotation = indexedNestedAnnotation;
            cost = costIndexedBlockNested;
            sorted=false;
        }*/
        //return br*tranferTime + (br/bb)*latency;
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
        
        blockNestedAnnotation.add("block Nested loop join");
         
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
      
        //performing sorting
        double sortTime=0;
        if(!sorted1){
            mergeAnnotation.add("sort relation1");
            sortTime= SortCost.externalSortCost(br, M, latency, penaltyTime,  tranferTime);
        }
        else
            mergeAnnotation.add("Use sorted relation1");
        if(!sorted2){
            mergeAnnotation.add("sort relation2"); 
            sortTime= SortCost.externalSortCost(bs, M, latency, penaltyTime,  tranferTime);

        }
        else
            mergeAnnotation.add("Use sorted relation2");
        
        //performing join
         int blocksTranfered = br+bs;
         int diskSeeks = (br/bb) + (bs/bb);
         
         mergeAnnotation.add("merge join");
         
         return (diskSeeks*latency + blocksTranfered*tranferTime  + sortTime); 
         
    }
    
    public double hashJoin(boolean partitioned1, boolean partitioned2){ //sunolika 3(br+bs)+ nh to opoio parleipetai 
        int blocksTranfered=0;
        int blockWritten=0;
        
        //partitioning relations
        if(!partitioned1){
            hashAnnotation.add("partition relation1");
            //reading for partitioning
            blocksTranfered = br;
            //writting after partitioning
            blockWritten = br;
        }
        else
            hashAnnotation.add("use hashIndex on relation1");
        if(!partitioned2){
            hashAnnotation.add("partition relation2");
            //reading for partitioning
            blocksTranfered += bs;
            //writting after partitioning
            blockWritten += bs;
        }
        else
            hashAnnotation.add("use hashIndex on relation2");
       
        
        //reading for join
        blocksTranfered+= br+bs;
        //2nh factor is too small coresponding to br+bs,it can be ignored
        int diskSeeks = 2*((br/bb) + (bs/bb));
        hashAnnotation.add("hash join");  
         
        return (diskSeeks*latency + blockWritten*penaltyTime + blocksTranfered*tranferTime); 
         
    }
    
    
    //tha xrhsimopoihthei??? 
    public void recursiveHashJoin(){
        int partitionPasses= (int) (Math.log10(br)/Math.log10(M-1) -1) ;
        int blocksTranfered = 2*(br+bs)* partitionPasses + bs+br;
        
        int diskSeeks = 2*((br/bb) + (bs/bb))* partitionPasses;
    
    }
    
   
}