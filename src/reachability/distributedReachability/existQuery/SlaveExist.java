/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reachability.distributedReachability.existQuery;

import database.DBConnect;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;

/**
 *
 * @author amina
 */
public class SlaveExist extends Thread{
    private boolean active=false;
    private int numPartition;
    private HashSet<String> constraints=new HashSet<String>();
    private ArrayList<Boolean> presence_constraints=new ArrayList<Boolean>();
    private Duration duration;
    private HashSet<String> remaining_constraints=new HashSet<String>();

    public SlaveExist(int numPartition, HashSet<String> constraints) {
        this.numPartition = numPartition;
        this.constraints=constraints;
    }
    
    

    public boolean isActive() {
        return active;
    }
    
    public void run(){
        DBConnect db=new DBConnect();
        String label;
        try{
            Instant start=Instant.now();
            db.connect("/data/delab/amina/NEO4J/NEO4J_"+this.numPartition+"/data/graph.db");
            Iterator it=constraints.iterator();            
            while(it.hasNext()){
                label=(String)it.next();
                db.query("match (n)-[r]-(m) where type(r)="+'"'+label+'"'+" return type(r)");
                if(db.getResult().isEmpty()){
                    this.presence_constraints.add(false);
                    this.remaining_constraints.add(label);
                }
                else {
                    this.presence_constraints.add(true);
                    this.active=true;
                }
            }
            db.disconnect();
            Instant end=Instant.now();
            duration=Duration.between(start, end);
        }
        catch(Exception e){  
        }   
    }

    public ArrayList<Boolean> getPresence_constraints() {
        return presence_constraints;
    }

    public Duration getDuration() {
        return duration;
    }

    public HashSet<String> getRemaining_constraints() {
        return remaining_constraints;
    }
    
}
