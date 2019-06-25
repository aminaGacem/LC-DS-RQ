/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reachability.distributedReachability.existQuery;

import database.DBConnect;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author amina
 */
public class VirtualEdgeExist {
    private int src;
    private int tgt;
    private HashSet<String> constraints=new HashSet<String>();
    private ArrayList<Boolean> presence_constraints=new ArrayList<Boolean>();

    public VirtualEdgeExist(int src, int tgt,HashSet<String> constraints) {
        this.src = src;
        this.tgt = tgt;
        this.constraints=constraints;
    }
    
    public void checkConstraint(){
        DBConnect db=new DBConnect();
        String label;
        try{
            db.connect("/data/delab/amina/NEO4J/NEO4J_HOME/data/graph.db");
            Iterator it=constraints.iterator();
            while(it.hasNext()){
                label=(String)it.next();
                db.query("match (n)-[r]-(m) where n.slave="+'"'+src+'"'+"and m.slave="+'"'+tgt+'"'+"and type(r)="+'"'+label+'"'+" return n.id");
                if(db.getResult().isEmpty())this.presence_constraints.add(false);
                else this.presence_constraints.add(true);                
            }
            db.disconnect();
        }
        catch(Exception e){            
        }
    }

    public ArrayList<Boolean> getPresence_constraints() {
        return presence_constraints;
    }

}
