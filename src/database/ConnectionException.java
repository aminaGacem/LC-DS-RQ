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
public class ConnectionException extends Exception{
    public String getMessage(){
        return "Error in Connection"+super.getMessage();
    }
}
