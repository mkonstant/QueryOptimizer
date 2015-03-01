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
import catalog.Attributes;
import catalog.Catalog;
import catalog.ForeignIndexInfo;
import catalog.IndexInfo;
import catalog.TableInfo;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author michalis
 */
public class QuerryOptimizer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ParseException, IOException {
        
        
        // TODO code application logic here
        String dbFile = null;
        String sysFile = null;
        Catalog catalog = null;
        
        dbFile = args[1];
        sysFile = args[0];
        catalog = new Catalog(dbFile,sysFile);
        catalog.processingDataBaseFile();
        catalog.processingSystemInfoFile();
        
        Map<String,TableInfo> table = null;
        table = catalog.getCatalog();
        TableInfo tabInfo = null;
        for (String key : table.keySet()){
            System.out.println("key = " + key);
            tabInfo = table.get(key);
            System.out.println("Cardinality : " + tabInfo.getCarinality());
            System.out.println("Size Of Tuples : " + tabInfo.getSizeOfTuples());
            System.out.println("Attributes :");
            Map<String,Attributes> attributes = tabInfo.getAttributes();
            for(String key1 : attributes.keySet()){
                Attributes attr = attributes.get(key1);
                System.out.println(key1);
                System.out.println(attr.getDistinctValues());
                System.out.println(attr.getMax());
                System.out.println(attr.getMin());
                System.out.println(attr.getType());
                
                
            }
            System.out.println("Primary Index : ");
            IndexInfo primaryIndex = tabInfo.getPrimaryIndex();
            System.out.println(primaryIndex.getHeight());
            System.out.println(primaryIndex.getIndexName());
            System.out.println(primaryIndex.getNumOfDistinctValues());
            System.out.println(primaryIndex.getStructure());
            System.out.println("Secondary Index : ");
            Map<String,IndexInfo> secondaryIndex = tabInfo.getSecondaryIndex();
            for(String key1 : secondaryIndex.keySet()){
                IndexInfo indexInfo = secondaryIndex.get(key1);
                System.out.println(indexInfo.getHeight());
                System.out.println(indexInfo.getIndexName());
                System.out.println(indexInfo.getNumOfDistinctValues());
                System.out.println(indexInfo.getStructure());
            }
            System.out.println("Foreign Index : ");
            Map<String,ForeignIndexInfo> foreignIndex = tabInfo.getForeignIndex();
            for(String key1 : foreignIndex.keySet()){
                ForeignIndexInfo index = foreignIndex.get(key1);
                System.out.println(index.getHeight());
                System.out.println(index.getIndexName());
                System.out.println(index.getNumOfDistinctValues());
                System.out.println(index.getOutAttr());
                System.out.println(index.getOutTable());
                System.out.println(index.getStructure());
            }
            
            System.out.println("System Info");
            System.out.println("latency : " + catalog.getSystemInfo().getLatency());
            System.out.println("numberOfBuffers : " + catalog.getSystemInfo().getNumOfBuffers());
            System.out.println("sizeOfBuffers : " + catalog.getSystemInfo().getSizeOfBuffer());
            System.out.println("transferTime : " + catalog.getSystemInfo().getTransferTime());
            System.out.println("timeWaiting : " + catalog.getSystemInfo().getTimeForWritingPages());
       }
        
        
        FileInputStream fis = null;
        try {
            if(args.length == 0){
                System.out.println("No input files given!");
                System.exit(0);
            }   fis = new FileInputStream(args[2]);
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
