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
    
    
    
      public static int externalSortCost(int br,int M,int latency, int penaltyTime, int tranferTime){
        
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
