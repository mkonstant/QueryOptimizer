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
public class JoinAttributeTypeException extends RuntimeException {
    String message;
    
    public JoinAttributeTypeException(String string1,String string2) {
        
        message = "Join attributes '"+string1+"' and '"+string2+"' have not the same type.";
    }

    @Override
    public String getMessage() {
        return message;
    }
}