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
public class SortCost {
    
    
    
      public static double computeCost(int br,int M,double latency, double penaltyTime, double tranferTime){
          
          
        if(br< M-1){ //quicksort
            return br*tranferTime + latency + br*penaltyTime;
        
        }  
          
        //now external sort
        
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
    
}
