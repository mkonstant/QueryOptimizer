/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluationCost;

import catalog.SystemInfo;
import catalog.TableInfo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import syntaxtree.Attribute;

/**
 *
 * @author michalis
 */
public class ProjectionCost {
    
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
    private TableInfo tinfo;
    private Set<String> sortKey=null;

    public ProjectionCost(SystemInfo si, TableInfo tinfo){
        this.tinfo = tinfo;
        tranferTime = si.getTransferTime();
        penaltyTime = si.getTimeForWritingPages();
        latency = si.getLatency();
        M = si.getNumOfBuffers();
        bb= M-2;
        nr = tinfo.getNumberOfTuples();
        br = (nr*tinfo.getSizeOfTuple()) / si.getSizeOfBuffer();
        
    }
    
    
    public boolean getSorted(){
        return sorted;
    }
    
    public Set<String> getSortKey(){
        return sortKey;
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
    public double computeCost(boolean primary, boolean sorted, boolean hashed){
        if(primary){ //projection sto primary key, no duplicates
            if(tinfo.getPrimaryIndex().getStructure().equals("B+tree"))
            {
                this.sorted=true;
                sortKey = tinfo.getKey();
            }
            cost = br*tranferTime + (br/bb)*latency;
            annotation.add("Just projection,No duplicates");
        }
        else{
            
            double costSort = dublicateSort(sorted);
            double costHash = dublicateHash(hashed);
        
            if(costHash> costSort){
                annotation = sortedAnnotation;
                cost = costSort;
                this.sorted=true;
            }
            else{
                annotation = hasedAnnotation;
                cost = costHash;
            }
        }
        return cost;

        //return br*tranferTime + (br/bb)*latency;
    }
    
    
    //cost of dublicate elimiation with sort is equal to sorting one relation
    public double dublicateSort(boolean sorted){
            if(sorted){
                sortedAnnotation.add("Relation already sorted on projection attributes ");
                sortKey = tinfo.getSortKet();
                return 0;
            }
            sortedAnnotation.add("Sort to eliminate dublicates");
            return SortCost.computeCost(br, M, latency, penaltyTime,  tranferTime);
    }
    
    public double dublicateHash(boolean partitioned){
        
        
        int blocksTranfered=0;
        int blockWritten=0;
        //partitioning relations
        if(!partitioned){
            hasedAnnotation.add("Hash to eliminate dublicates");
            //reading for partitioning
            blocksTranfered = br;
            //writting after partitioning
            blockWritten = br;
        }
        else{
            hasedAnnotation.add("Relation already hashed on projection attributes");
        }
        
        //reading for dublicate elimination
        blocksTranfered+= br;
        //2nh factor is too small coresponding to br+bs,it can be ignored
        int diskSeeks = 2*(br/bb) ;
        
        //writting back output   how much???? calculate distinct values ktl
       // blockWritten += br;
         
        return (diskSeeks*latency + blockWritten*penaltyTime + blocksTranfered*tranferTime); 
    }
    
    
    
    
}
