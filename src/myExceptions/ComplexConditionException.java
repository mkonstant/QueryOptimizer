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
public class ComplexConditionException extends RuntimeException {
    String message;
    
    public ComplexConditionException() {
        
        message = "Only conjuctions or disjunctions of condtions are supported, not both.";
    }
    
    public ComplexConditionException(String bla) {
        if(bla ==null)
        message = "Disjunctions of condtions on joins are not supported.";
    }

    @Override
    public String getMessage() {
        return message;
    }
}

