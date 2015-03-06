/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package myExceptions;

/**
 *
 * @author jimakos
 */
public class SelectAttributeException extends RuntimeException {
    String message = null;
    
    
    public SelectAttributeException(String string){
        message = "Relation does not contain attribute '"+string+"' to perform select on.";
    }
 
    @Override
    public String getMessage() {
        return message;
    }
}
