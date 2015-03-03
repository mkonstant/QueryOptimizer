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
    public void computeCost(){
        double costSort = dublicateSort();
        double costHash = dublicateHash(true);
        
        if(costHash> costSort){
            annotation = sortedAnnotation;
            cost = costSort;
        }
        else{
            annotation = hasedAnnotation;
            cost = costHash;
        }
        //return br*tranferTime + (br/bb)*latency;
    }
    
    
    //cost of dublicate elimiation with sort is equal to sorting one relation
    public double dublicateSort(){
            sortedAnnotation.add("Sort to eliminate dublicates");
            return SortCost.externalSortCost(br, M, latency, penaltyTime,  tranferTime);
    }
    
    public double dublicateHash(boolean partitioned){
        hasedAnnotation.add("Hash to eliminate dublicates");
        int blocksTranfered=0;
        int blockWritten=0;
        //partitioning relations
        if(!partitioned){
            //reading for partitioning
            blocksTranfered = br;
            //writting after partitioning
            blockWritten = br;
        }
        
        //reading for dublicate elimination
        blocksTranfered+= br;
        //2nh factor is too small coresponding to br+bs,it can be ignored
        int diskSeeks = 2*(br/bb) ;
        
        //writting back output   how much???? calculate distinct values ktl
        blockWritten += br;
         
        return (diskSeeks*latency + blockWritten*penaltyTime + blocksTranfered*tranferTime); 
    }
    
}
