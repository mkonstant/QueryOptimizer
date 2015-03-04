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
public class RelationException extends RuntimeException{

    String message;
    
    public RelationException(String string) {
        message = "Relation '"+string+"' does not exist.";
    }

    @Override
    public String getMessage() {
        return message;
    }
    
    
    
}
