/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

/**
 *
 * @author amina
 */
public class ExecutionException extends Exception {

    /**
     * Creates a new instance of <code>ExecutionException</code> without detail
     * message.
     */
    public ExecutionException() {
    }

    /**
     * Constructs an instance of <code>ExecutionException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ExecutionException(String msg) {
        super(msg);
    }
    
    public String getMessage(){
        return "Error in the query execution"+super.getMessage();
    }
}
