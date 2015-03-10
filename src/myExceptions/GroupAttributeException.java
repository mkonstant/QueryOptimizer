/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myExceptions;

/**
 *
 * @author michalis
 */
public class GroupAttributeException extends RuntimeException {
    String message;
    


    public GroupAttributeException(String string,int k) {
        if(k==0)
            message = "Attribute '"+string+"' not contained on groupby attributes.";
        else if(k==1)
            message = "Attribute '"+string+"' is contained both on groupby part and aggregation function.";
        else if(k==2)
            message = "Not all attributes of groupBy and aggregate function contained in relation.";
        else if(k==3)
            message = "Attribute '"+string+"' on having clause condition not contained on groupby attributes.";
        else if(k==4)
            message = "Attribute '"+string+"' on having clause condition is contained both on groupby part and aggregation function.";
        else if(k==5)
            message = "Not all attributes of having clause condition contained in either groupBy or aggregate function.";
    }
    
    @Override
    public String getMessage() {
        return message;
    }
}

