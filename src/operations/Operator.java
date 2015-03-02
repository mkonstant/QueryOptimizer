/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operations;

import java.util.ArrayList;

/**
 *
 * @author michalis
 */
public abstract class Operator {

    String operatorName="";
    public void setOpName(String name){operatorName = name;}
    public String getOpName(){return operatorName;}
    
    
    public void setOperation(String op){};
    public String getOperation(){return null;};
    
    public void setRelation1(String rel){};
    public void setRelation2(String rel){};
    public void setRelationOp1(Operator rel){};
    public void setRelationOp2(Operator rel){};  
    public String getRelation1(){ return null;};
    public String getRelation2(){return null;};
    public Operator getRelationOp1(){return null;};
    public Operator getRelationOp2(){return null;};
    
    public void AddAttr(String attr){};
    public ArrayList<String> getAttrs(){return null;};
    public boolean getHasHavingClause(){return false;};
    public void AddCondition(String attr1, String attr2, String action){};
    public ArrayList<Condition> getConditions(){return null;};
    
    public String toPrint(){return "";};

    
    
}
