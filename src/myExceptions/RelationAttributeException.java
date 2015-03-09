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
public class RelationAttributeException extends RuntimeException {
    String message;
    
    public RelationAttributeException(String attribute, String relation) {
        if(relation==null)
            message = "No attribute head supported for '"+attribute+"' on the result of another operation.";
        else
            message = "Relation '"+relation+"' and attribute head '"+attribute+"' does not match.";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
