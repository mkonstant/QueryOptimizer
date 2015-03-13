/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operations;

/**
 *
 * @author michalis
 */
public class Condition {
    String attr1;
    String action;
    String attr2;

    public Condition(String attr1, String attr2, String action) {
        this.attr1=attr1;
        this.attr2=attr2;
        this.action=action;
    }
    
    public Condition(Condition c) {
        this.attr1=c.getAttr1();
        this.attr2=c.getAttr2();
        this.action=c.getAction();
    }
    
    
    
    public void setAttr1(String attr){
        attr1 = attr;
    }
    
    public void setAttr2(String attr){
         attr2 = attr;
    }
    
    public void setAction(String action){
         this.action = action;
    }
    
    public String getAttr1(){
        return attr1;
    }
    
    public String getAttr2(){
        return attr2;
    }
    
    public String getAction(){
        return action;
    }
    
    public String toPrint(){
        return attr1+action+attr2;
    }
    
    public Condition fullCopy(){
        return new Condition(this);
    }
    
    
}
