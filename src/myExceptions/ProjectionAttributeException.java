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
public class ProjectionAttributeException extends RuntimeException {
    String message;
    
    public ProjectionAttributeException(String string) {
        
        message = "Relation does not contain attribute '"+string+"' to perform projection on.";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
