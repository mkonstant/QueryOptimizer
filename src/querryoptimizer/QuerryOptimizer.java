/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package querryoptimizer;

import generated.ParseException;
import generated.QueryParser;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.logging.Level;
import java.util.logging.Logger;
import myVisitor.TestVisitor;
import syntaxtree.Query;

/**
 *
 * @author michalis
 */
public class QuerryOptimizer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ParseException {
        
        FileInputStream fis = null;
        try {
            if(args.length == 0){
                System.out.println("No input files given!");
                System.exit(0);
            }   fis = new FileInputStream(args[0]);
            QueryParser parser = new QueryParser(fis);
            Query tree = parser.Query();
            //TestVisitor tv = new TestVisitor();
            //tree.accept(tv,null);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(QuerryOptimizer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(QuerryOptimizer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
                    
        
        
    }
    
}
