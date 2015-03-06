/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package catalog;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jimakos
 */
public class Catalog {
    String dbname = null;
    Map<String,TableInfo> catalog = null;
    String dbFile = null;
    String sysFile = null;
    SystemInfo systemInfo = null;
    
    
    
    public Catalog(String dbFile, String sysFile ){
        this.dbFile = dbFile;
        this.sysFile = sysFile;
        catalog = new HashMap<String,TableInfo>();
        systemInfo = new SystemInfo();
    }
    
    
    public void processingSystemInfoFile() throws FileNotFoundException, IOException{
         BufferedReader br = new BufferedReader(new FileReader(sysFile));
         String line = null;
         String del = ":";
         String []temp = null;
         
         if ((line = br.readLine()) != null) {
             temp = line.split(del);
             systemInfo.setNumOfBuffers(Integer.parseInt(temp[1]));
         }
         
         if ((line = br.readLine()) != null) {
             temp = line.split(del);
             systemInfo.setSizeOfBuffer(Integer.parseInt(temp[1]));
         }
         
         if ((line = br.readLine()) != null) {
             temp = line.split(del);
             systemInfo.setLatency(Double.parseDouble(temp[1]));
         }
         
         if ((line = br.readLine()) != null) {
             temp = line.split(del);
             systemInfo.setTransferTime(Double.parseDouble(temp[1]));
         }
         
         if ((line = br.readLine()) != null) {
             temp = line.split(del);
             systemInfo.setTimeForWritingPages(Double.parseDouble(temp[1]));
         }
    }
    
    
    public void processingDataBaseFile() throws FileNotFoundException, IOException{
        String del = ":";
        String del2 = ",|:| ";
        String del3 = ":| ";
        String del4 = "\\(|\\)|,";
        String del5 = ",";
        String del6 = "\\(|\\)";
        String del7 = ":|,";
        String []temp = null;
        String []temp2 = null;
        String []temp3 = null;
        String line;
        String tableName = null;
        TableInfo tabInfo = null;
        Map<String,IndexInfo> secondaryIndex = null;
        Map<String,ForeignIndexInfo> foreignIndex = null;
        String indexNameStr = null;
        Set <String> key = null;
        Set<String>indexName = null;
        Set<String> foreignName = null;
        Set<String> foreignOutAttr = null;
        
        
        BufferedReader br = new BufferedReader(new FileReader(dbFile));
        //take dbname
        if ((line = br.readLine()) != null){
            temp = line.split(del);
            dbname = temp[1];
        }
        
        while ((line = br.readLine()) != null) {
            
            //keni grammi
            line = br.readLine();
            //take all info for tables
            //take name of table
            if (line.contains("table")){
                temp = line.split(del);
                tableName = temp[1];
                tabInfo = new TableInfo();
            }
           
            //take attributes
            if ((line = br.readLine()) != null){
                temp = line.split(del2);
                for (int i = 1; i < temp.length ; i = i + 5){
                    Attributes attr = new Attributes();
                    String attrName = temp[i];
                    attr.setType(temp[i+1]);
                    attr.setMin(temp[i+2]);
                    attr.setMax(temp[i+3]);
                    attr.setDistinctValues(Integer.parseInt(temp[i+4]));
                    
                    tabInfo.attributes.put(attrName, attr);
                }
            }
            
            //take primary key
            if ((line = br.readLine()) != null){
                key = new HashSet <String>();
                if (line.contains(",")){
                    temp = line.split(del7);
                    for ( int i = 1 ; i < temp.length ; i ++ ){
                        key.add(temp[i]);
                    }
                }
                else{
                    temp = line.split(del);
                    key.add(temp[1]);
                }
                tabInfo.setKey(key);
            }
            //take primary index
            if ((line = br.readLine()) != null){
                temp = line.split(del3);
                IndexInfo indexInfo = new IndexInfo();
                indexName = new HashSet<String>();
                if (temp[1].contains(",")){
                    temp3 = temp[1].split(del5);
                    for ( int i = 0 ; i < temp3.length ; i ++){
                        indexName.add(temp3[i]);
                    }
                    indexInfo.setIndexName(indexName);
                    
                }
                else{
                    indexName.add(temp[1]);
                    indexInfo.setIndexName(indexName);
                }
                indexInfo.setStructure(temp[2]);
                indexInfo.setNumOfDistinctValues(Integer.parseInt(temp[3]));
                indexInfo.setCostFactor(Integer.parseInt(temp[4]));
                
                tabInfo.setPrimaryIndex(indexInfo);
            }
            
            //take secondary index
            if ((line = br.readLine()) != null){
                temp = line.split(del);
                if (temp.length > 1){
                    temp2 = temp[1].split(" ");
                    secondaryIndex = new HashMap<String,IndexInfo>();
                    IndexInfo indexInfo = new IndexInfo();
                    indexName = new HashSet<String>();
                    if (temp2[0].contains(",")){
                        temp3 = temp2[0].split(del5);
                        for ( int i = 0 ; i < temp3.length ; i ++){
                            indexName.add(temp3[i]);
                        }
                        indexInfo.setIndexName(indexName);

                    }
                    else{
                        indexName.add(temp2[0]);
                        indexInfo.setIndexName(indexName);
                    }
                    //indexInfo.setIndexName(temp2[0]);
                    indexInfo.setStructure(temp2[1]);
                    indexInfo.setNumOfDistinctValues(Integer.parseInt(temp2[2]));
                    indexInfo.setCostFactor(Integer.parseInt(temp2[3]));
                    
                    secondaryIndex.put(temp2[0], indexInfo);
                }
            }
            while ((line = br.readLine()) != null){
                if ( line.contains("foreignIndex")){
                    break;
                }
                else{
                    temp = line.split(" ");
                    IndexInfo indexInfo = new IndexInfo();
                    indexName = new HashSet<String>();
                    if (temp[0].contains(",")){
                        temp3 = temp[0].split(del5);
                        for ( int i = 0 ; i < temp3.length ; i ++){
                            indexName.add(temp3[i]);
                        }
                        indexInfo.setIndexName(indexName);

                    }
                    else{
                        indexName.add(temp[0]);
                        indexInfo.setIndexName(indexName);
                    }
                    //indexInfo.setIndexName(temp[0]);
                    indexInfo.setStructure(temp[1]);
                    indexInfo.setNumOfDistinctValues(Integer.parseInt(temp[2]));
                    indexInfo.setCostFactor(Integer.parseInt(temp[3]));
                    
                    secondaryIndex.put(temp[0], indexInfo);
                }
            }
            tabInfo.setSecondaryIndex(secondaryIndex);
            
            //take foreign Index
            if ( line.contains("foreignIndex")){
                temp = line.split(del);
                if (temp.length > 1){
                    temp2 = temp[1].split(" ");
                    foreignIndex = new HashMap<String,ForeignIndexInfo>();
                    ForeignIndexInfo foreignIndexInfo = new ForeignIndexInfo();
                    foreignName = new HashSet<String>();
                    
                    if (temp2[0].contains(",")){
                        temp3 = temp2[0].split(del5);
                        for (int i = 0 ; i < temp3.length ; i  ++){
                            foreignName.add(temp3[i]);
                        }
                        foreignIndexInfo.setIndexName(foreignName);
                    }
                    else{
                        foreignName.add(temp2[0]);
                        foreignIndexInfo.setIndexName(foreignName);
                    }
                    
                    
                    //foreignIndexInfo.setIndexName(temp2[0]);
                    //foreignIndexInfo.setOutTable(temp2[1]);
                    foreignOutAttr = new HashSet<String>();
                    if (temp2[1].contains(",")){
                        temp3 = temp2[1].split(del4);
                        for (int i = 1 ; i < temp3.length ; i  ++){
                            foreignOutAttr.add(temp3[i]);
                        }
                        foreignIndexInfo.setOutAttr(foreignOutAttr);
                        foreignIndexInfo.setOutTable(temp3[0]);
                    }
                    else{
                        temp3 = temp2[1].split(del6);
                        for (int i = 1 ; i < temp3.length ; i  ++){
                            foreignOutAttr.add(temp3[i]);
                        }
                        foreignIndexInfo.setOutAttr(foreignOutAttr);
                        foreignIndexInfo.setOutTable(temp3[0]);
                    }
                    
                    //foreignIndexInfo.setOutAttr(temp2[2]);
                    
                    foreignIndexInfo.setStructure(temp2[2]);
                    foreignIndexInfo.setNumOfDistinctValues(Integer.parseInt(temp2[3]));
                    foreignIndexInfo.setCostFactor(Integer.parseInt(temp2[4]));
                    
                    foreignIndex.put(temp2[0], foreignIndexInfo);
                }
            }
            tabInfo.setForeignIndex(foreignIndex);
            
            
            while ((line = br.readLine()) != null){
                //System.out.println("lineeeeeeeeeeeeeeeeeeeeeeeeeeeeeee = " +line);
                if ( line.contains("cardinality")){
                    break;
                }
                else{
                    temp = line.split(" ");
                    ForeignIndexInfo foreignIndexInfo = new ForeignIndexInfo();
                    
                    foreignName = new HashSet<String>();
                    if (temp[0].contains(",")){
                        temp2 = temp[0].split(del5);
                        for (int i = 0 ; i < temp2.length ; i  ++){
                            foreignName.add(temp2[i]);
                        }
                        foreignIndexInfo.setIndexName(foreignName);
                    }
                    else{
                        foreignName.add(temp[0]);
                        foreignIndexInfo.setIndexName(foreignName);
                    }
                    
                    //foreignIndexInfo.setIndexName(temp[0]);
                    //foreignIndexInfo.setOutTable(temp[1]);
                    
                    
                    //foreignIndexInfo.setIndexName(temp2[0]);
                    foreignOutAttr = new HashSet<String>();
                    //foreignIndexInfo.setOutTable(temp2[1]);
                    if (temp[1].contains(",")){
                        temp2 = temp[1].split(del4);
                        for (int i = 1 ; i < temp2.length ; i  ++){
                            foreignOutAttr.add(temp2[i]);
                        }
                        foreignIndexInfo.setOutAttr(foreignOutAttr);
                        foreignIndexInfo.setOutTable(temp2[0]);
                    }
                    else{
                        temp2 = temp[1].split(del6);
                        for (int i = 1 ; i < temp2.length ; i  ++){
                            foreignOutAttr.add(temp2[i]);
                        }
                        foreignIndexInfo.setOutAttr(foreignOutAttr);
                        foreignIndexInfo.setOutTable(temp2[0]);
                    }
                    //foreignIndexInfo.setOutAttr(temp[2]);
                    foreignIndexInfo.setStructure(temp[2]);
                    foreignIndexInfo.setNumOfDistinctValues(Integer.parseInt(temp[3]));
                    foreignIndexInfo.setCostFactor(Integer.parseInt(temp[4]));
                    
                    foreignIndex.put(temp[0], foreignIndexInfo);
                }
            }
            
            //take cardinality
            temp = line.split(del);
            tabInfo.setCarinality(Integer.parseInt(temp[1]));
            
            //take size of tuple
            if ((line = br.readLine()) != null){
                temp = line.split(del);
                tabInfo.setSizeOfTuple(Integer.parseInt(temp[1]));
            }
            
            //take size of tuple
            if ((line = br.readLine()) != null){
                temp = line.split(del);
                tabInfo.setNumberOfTuples(Integer.parseInt(temp[1]));
            }
            
            catalog.put(tableName, tabInfo);
            
        }
        
        
        br.close();
    }
    
    
    public Map<String,TableInfo> getCatalog(){
        return catalog;
    }
    
    
    public void setCatalog(Map<String,TableInfo> catalog){
        this.catalog = catalog;
    }

    public String getDbname() {
        return dbname;
    }

    public String getInputFile() {
        return dbFile;
    }

    
    public SystemInfo getSystemInfo() {
        return systemInfo;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public void setInputFile(String dbFile) {
        this.dbFile = dbFile;
    }

    public void setSystemInfo(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }
    
    
    
    
}
