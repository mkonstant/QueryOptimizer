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
public class JoinActionException extends RuntimeException {
    String message;
    
    public JoinActionException() {
        
        message = "Relation contains inequality";
    }

    @Override
    public String getMessage() {
        return message;
    }
    
}
