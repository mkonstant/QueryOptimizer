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
public class ProjectionCost {
    
    int M; //num of buffers available for sorting
    int br;  //blocks containing tupples of relation r
    int bb;//buffer blocks per run
    int nr;//number of tupples in r
    int tranferTime;
    int penaltyTime;
    int latency;
    
    //no sort , no hash, provide dublicates
    public int projectionCost(){
        return br*tranferTime + (br/bb)*latency;
    }
    
    
    //cost of dublicate elimiation with sort is equal to sorting one relation
    public int dublicateSort(){
            return SortCost.externalSortCost(br, M, latency, penaltyTime,  tranferTime);
    }
    
    public int dublicateHash(boolean partitioned){
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
