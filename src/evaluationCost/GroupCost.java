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
public class GroupCost {
    private  int M; //num of buffers available for sorting
    private  int br;  //blocks containing tupples of relation r
    private int bb;//buffer blocks per run
    private int nr;//number of tupples in r
    private double tranferTime;
    private double penaltyTime;
    private double latency;
    private double cost=0.0;
    private ArrayList<String> annotation = new ArrayList<String>();
    private ArrayList<String> sortedAnnotation = new ArrayList<String>();
    private ArrayList<String> hasedAnnotation = new ArrayList<String>();
    private boolean sorted=false;

    public GroupCost(SystemInfo si, int nr, int sizeOfTupple){
        tranferTime = si.getTransferTime();
        penaltyTime = si.getTimeForWritingPages();
        latency = si.getLatency();
        M = si.getNumOfBuffers();
        bb= M/2;
        br = (nr*sizeOfTupple) / si.getSizeOfBuffer();
        this.nr=nr;
    }
    
    
    public boolean getSorted(){
        return sorted;
    }
    
    
    
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
    
    
     //no sort , no hash, provide dublicates
    public void computeCost(boolean sorted, boolean hashed){

            double costSort = useSort(sorted);
            double costHash = useHash(hashed);
        
            if(costHash> costSort){
                annotation = sortedAnnotation;
                cost = costSort;
                sorted=true;
            }
            else{
                annotation = hasedAnnotation;
                cost = costHash;
            }
            
            //if relation fits to memory, on the fly compytation of aggregate
            if(br<M-2){
                cost+= br*tranferTime + latency;
            
            }
            else{ //worst case
                cost+= 2*br*tranferTime + br*penaltyTime+ 2*(br/bb)*latency;
            
            }
            annotation.add("Perform aggragation on output");
            
        }
        

    
    
    private double useSort(boolean sorted){
        if(sorted)
        {
            sortedAnnotation.add("Use sorted relation on groupby attributes");
            return 0;
        }
        else{
            sortedAnnotation.add("Sort relation on groupby attributes");
            return SortCost.computeCost(br, M, latency, penaltyTime, tranferTime);
        }
    }
    
    private double useHash(boolean hashed){
        if(hashed)
        {
            hasedAnnotation.add("Use hashed relation on groupby attributes");
            return 0;
        }
        else{
            hasedAnnotation.add("Hash relation on groupby attributes");
            int blocksTranfered=0;
            int blockWritten=0;
            //reading for partitioning
            blocksTranfered = br;
            //writting after partitioning
            blockWritten = br;
            int diskSeeks = 2*(br/bb);
            return (diskSeeks*latency + blockWritten*penaltyTime + blocksTranfered*tranferTime); 
        }
    
    }
    
}
