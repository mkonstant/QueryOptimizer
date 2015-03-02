/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluation;

/**
 *
 * @author michalis
 */
public class Sorting {
    int M; //num of buffers available for sorting
    int br;  //blocks containing tupples of relation r
    int bs;//blocks containing tupples of relation s
    int bb;//buffer blocks per run
    
    int blocksTranfered;
    int diskSeeks;
    
    public void externalSortCost(){
        blocksTranfered = (int) (br*(2*(Math.log10(br/M)/Math.log10(M-1))+1));
        diskSeeks =  (int) (2*(br/M)+ (br/bb)*(2*(Math.log10(br/M)/Math.log10(M-1))+1));
    }
    
    public void mergeJoin(){
         blocksTranfered = br+bs;
         diskSeeks = (br/bb) + (bs/bb);
    }
    
   
}
