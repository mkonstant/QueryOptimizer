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
public class JoinAttributeException extends RuntimeException {
    String message;
    
    public JoinAttributeException(String string) {
        
        message = "Relation does not contain attribute '"+string+"' to perforn join on.";
    }

    @Override
    public String getMessage() {
        return message;
    }
}

