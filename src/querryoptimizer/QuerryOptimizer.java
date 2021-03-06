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
import myVisitor.CheckQueryVisitor;
import syntaxtree.Query;
import catalog.Attributes;
import catalog.Catalog;
import catalog.ForeignIndexInfo;
import catalog.IndexInfo;
import catalog.TableInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import myExceptions.ComplexConditionException;
import myExceptions.JoinAttributeException;
import myExceptions.JoinAttributeTypeException;
import myExceptions.ProjectionAttributeException;
import myExceptions.RelationAttributeException;
import myExceptions.RelationException;
import operations.Operator;

/**
 *
 * @author michalis
 */
public class QuerryOptimizer {

    static ArrayList<Operator> operations;
    static ArrayList<ArrayList<Operator>> alloperations;
    static Catalog catalog;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ParseException, IOException {
        
        
        // TODO code application logic here
        String dbFile = null;
        String sysFile = null;
        boolean printAll=false;
        
        dbFile = args[0];
        sysFile = args[1];
        
        if(args.length>3){
            if(args[3].equals("-all"))
            {
                printAll= Boolean.parseBoolean(args[4]);
            }
        }
        
        catalog = new Catalog(dbFile,sysFile);
        catalog.processingDataBaseFile();
        catalog.processingSystemInfoFile();
        
        //printCatalog(dbFile,sysFile);
        Map<String,TableInfo> table = catalog.getCatalog();
        
         
        
        
        FileInputStream fis = null;
        try {
            if(args.length == 0){
                System.out.println("No input files given!");
                System.exit(0);
            }   fis = new FileInputStream(args[2]);
            QueryParser parser = new QueryParser(fis);
            Query tree = parser.Query();
            CheckQueryVisitor tv = new CheckQueryVisitor();
            tree.accept(tv,null);
            alloperations = tv.getAllOperations();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(QuerryOptimizer.class.getName()).log(Level.SEVERE, null, ex);
        }catch(ComplexConditionException ex){
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        catch(RelationAttributeException ex){
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(QuerryOptimizer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try{
            
            for(int i=0; i<alloperations.size();i++){
                System.out.println("**********************************************Query "+ (i+1)+"*****************************************************");
                
                operations = alloperations.get(i);
                System.out.println("Query Materialization:");
                printPLan();

                double cost = processPlan();
                System.out.println("\n\n\nQuery Materialization Annotated:");
                printPLan();
                System.out.println(cost);
                ComputeBestPlan cb = new ComputeBestPlan(operations, catalog, cost);
                cb.ApplyTranformations(printAll);
                System.out.println("\n\n\n");
            }
            
            
        }
        catch(RelationException ex){
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        catch(ProjectionAttributeException ex){
            System.err.println(ex.getMessage());
            System.exit(1);
        }catch(JoinAttributeException ex){
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        catch(JoinAttributeTypeException ex){
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        catch(ComplexConditionException ex){
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        
        
        
    }
    
    
    public static double processPlan(){
       // ArrayList<Operator> op  =operations;
        Operator temp;
        double cost=0;
        for (int i = 0; i < operations.size(); i++)
        {
            
            temp = operations.get(i);
            temp.setCatalog(catalog);
            temp.computeCost();
            cost+= temp.getCost();
	}
        return cost;
    }
    
        
     public static void printPLan(){
        ArrayList<Operator> op  =operations;
        int tempR1,maxR1="Relation1".length();
        int tempR2,maxR2="Relation2".length();
        int tempC,maxC="Condition".length();
        int tempA,maxA="Attributes".length();
        int tempAn,maxAn="Annotation".length();
        int tempAggr, maxAggr="Aggregation".length();
        String splitLine="+----------+-------+";
        String headLine= "|Operation |  type |";
        int l;
        Operator temp;

        for (int i = 0; i < op.size(); i++)
        {
            
            temp = op.get(i);
            temp.setOpName("Operation"+i);
            tempR1=temp.getRelationPrint1Lenght();
            tempR2=temp.getRelationPrint2Lenght();
            tempC=temp.getConditionsPrintLenght();
            tempA=temp.getAttributesPrintLenght();
            tempAn=temp.getAnnotationLenght();
            tempAggr = temp.getAggragationLenght();
            
                    
            if(tempR1>maxR1)
                maxR1=tempR1;
            if(tempR2>maxR2)
                maxR2=tempR2;
            if(tempC>maxC)
                maxC=tempC;
            if(tempA>maxA)
                maxA=tempA;
            if(tempAn>maxAn)
                maxAn=tempAn;
            if(tempAggr>maxAggr)
                maxAggr=tempAggr;

	}
        
        //construct headline 
        headLine+="Condition";
        l = maxC - "Condition".length();        
        for(int i=0;i<l;i++){
                headLine+=" ";
        }
        headLine+="|";
        
        headLine+="Attributes";
        l = maxA - "Attributes".length();        
        for(int i=0;i<l;i++){
                headLine+=" ";
        }
        headLine+="|";
        
        headLine+="Relation1";
        l = maxR1 - "Relation1".length();        
        for(int i=0;i<l;i++){
                headLine+=" ";
        }
        headLine+="|";
        
        headLine+="Relation2";
        l = maxR2 - "Relation2".length();        
        for(int i=0;i<l;i++){
                headLine+=" ";
        }
        headLine+="|";
        
        headLine+="Aggregation";
        l = maxAggr - "Aggregation".length();        
        for(int i=0;i<l;i++){
                headLine+=" ";
        }
        headLine+="|";
        
        headLine+="Annotation";
        l = maxAn - "Annotation".length();        
        for(int i=0;i<l;i++){
                headLine+=" ";
        }
        headLine+="|";
        
        
        
        //construct splitline
        for(int i=0;i<maxC;i++){
                splitLine+="-";
        }
        splitLine+="+";
        for(int i=0;i<maxA;i++){
                splitLine+="-";
        }
        splitLine+="+";
        for(int i=0;i<maxR1;i++){
                splitLine+="-";
        }
        splitLine+="+";
        for(int i=0;i<maxR2;i++){
                splitLine+="-";
        }
        splitLine+="+";
        for(int i=0;i<maxAggr;i++){
                splitLine+="-";
        }
        splitLine+="+";
        
        
        for(int i=0;i<maxAn;i++){
                splitLine+="-";
        }
        splitLine+="+";
        

        System.out.println(splitLine);
        System.out.println(headLine);
        System.out.println(splitLine);
        
        for (int i = 0; i < op.size(); i++)
        {
            temp = op.get(i);
            System.out.println(temp.toPrint(maxR1, maxR2, maxC, maxA,maxAn,maxAggr));
	}
        System.out.println(splitLine);
   
    }
     
     public static void printCatalog(String dbFile, String sysFile) throws IOException{
         Catalog catalog = null;
         catalog = new Catalog(dbFile,sysFile);
         catalog.processingDataBaseFile();
         catalog.processingSystemInfoFile();
        
         Map<String,TableInfo> table = null;
         table = catalog.getCatalog();
         TableInfo tabInfo = null;
         for (String key : table.keySet()){
             System.out.println("key = " + key);
             tabInfo = table.get(key);
             System.out.println("Cardinality : " + tabInfo.getCardinality());
             System.out.println("Size of Tuple :" + tabInfo.getSizeOfTuple());
             System.out.println("Size Of Tuples : " + tabInfo.getNumberOfTuples());
             System.out.println("Key : " + tabInfo.getKey().toString());
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
             System.out.println(primaryIndex.getCostFactor());
             System.out.println(primaryIndex.getIndexName().toString());
             System.out.println(primaryIndex.getNumOfDistinctValues());
             System.out.println(primaryIndex.getStructure());
             System.out.println("Secondary Index : ");
             Map<String,IndexInfo> secondaryIndex = tabInfo.getSecondaryIndex();
             if ( secondaryIndex != null){
                for(String key1 : secondaryIndex.keySet()){
                    System.out.println("key : " + key1);

                    IndexInfo indexInfo = secondaryIndex.get(key1);
                    System.out.println(indexInfo.getCostFactor());
                    System.out.println(indexInfo.getIndexName().toString());
                    System.out.println(indexInfo.getNumOfDistinctValues());
                    System.out.println(indexInfo.getStructure());
                }
             }
             System.out.println("Foreign Index : ");
             Map<String,ForeignIndexInfo> foreignIndex = tabInfo.getForeignIndex();
             if (foreignIndex != null){
                for(String key1 : foreignIndex.keySet()){
                    System.out.println("key : " + key1);
                    ForeignIndexInfo index = foreignIndex.get(key1);
                    System.out.println(index.getCostFactor());
                    System.out.println(index.getIndexName().toString());
                    System.out.println(index.getNumOfDistinctValues());
                    System.out.println(index.getOutAttr().toString());
                    System.out.println(index.getOutTable());
                    System.out.println(index.getStructure());
                }
             }
            
             System.out.println("System Info");
             System.out.println("latency : " + catalog.getSystemInfo().getLatency());
             System.out.println("numberOfBuffers : " + catalog.getSystemInfo().getNumOfBuffers());
             System.out.println("sizeOfBuffers : " + catalog.getSystemInfo().getSizeOfBuffer());
             System.out.println("transferTime : " + catalog.getSystemInfo().getTransferTime());
             System.out.println("timeWaiting : " + catalog.getSystemInfo().getTimeForWritingPages());
         }
     }
    
    
}
