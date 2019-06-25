/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reachability;
import database.*;
import java.util.ArrayList;
/**
 * checks whether two nodes are reachable on the same slave
 * @author amina
 */
public class LocalReachability {
    private DBConnect db;
    private Query q;
    private ArrayList<String> result=new ArrayList<String>();

    public LocalReachability(DBConnect db,Query q) {
        this.q = q;
        this.db=db;
    }
    
    /**
     * checks whether reachable query is true in the database that has the following string connection path
     * @param path
     * @return 
     */
    
    public boolean evaluateLocalReachability(){
        boolean x;
        try{
           x=db.executeReachTransation("match p =(s {id :'"+q.getSource().getId()+"'}) -[*]-> (t {id :'"+q.getTarget().getId()+"'}) return p");
           this.result=db.getResult();
           return x;
        }
        catch(Exception e){
            System.out.println("Error in LocalReachability Execution Error "+e.getMessage());
            return false;
        }
    }

    public ArrayList<String> getResult() {
        return result;
    }
    
    
}
