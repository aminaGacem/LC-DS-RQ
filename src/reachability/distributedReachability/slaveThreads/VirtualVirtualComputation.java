/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reachability.distributedReachability.slaveThreads;

import database.ConnectionException;
import database.DBConnect;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import reachability.distributedReachability.ConnectingPaths;
import reachability.distributedReachability.DistributedQueryResult;
import reachability.distributedReachability.VirtualEdges;
import reachability.distributedReachability.SendReceiv;
import java.util.Iterator;
import java.util.ArrayList;
import graphs.Node;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *computes the contraint reachability between the virtual nodes of senders & receivers
 * @author amina
 */
public class VirtualVirtualComputation extends Thread{
    private int numPartition;
    private ConnectingPaths path;
    private DBConnect db=new DBConnect();
    private Duration duration;
    private HashSet<String> constraints=new HashSet<String>();
    private LocalReachability loc=new LocalReachability();
    private ArrayList<DistributedQueryResult> dqr=new ArrayList<DistributedQueryResult>();
    private int numberNodes=0;
    private int pathLength=0;
    
    /**
     * the key integer is the number of virtual node
     * the hashset of integer are the sources
     */    
    
    public VirtualVirtualComputation(int numPartition, DBConnect db,ConnectingPaths path, HashSet<String> constraints){
        this.numPartition=numPartition;
        this.db=db;
        this.path=path;
        this.constraints=constraints;
    }

    @Override
    public void run() {
        try {
            SendReceiv send, receiv;
            Iterator itsend,itreceiv;
            Iterator its,itt;
            itsend=this.path.getSend().iterator();
            HashSet<String> virtualSend,virtualReceiv;
            String n1,n2;
            int in=0,out=0;
            db.connect("/data/delab/amina/NEO4J/NEO4J_"+this.numPartition+"/data/graph.db");
            Instant start=Instant.now();
            //System.out.println("Amina!"+this.numPartition);
            //for each sender
            java.util.ArrayDeque d;
            while(itsend.hasNext()){
                send=(SendReceiv)itsend.next();
                //for each node of the sender
                in=in+send.getVirtualNodes().size();
                virtualSend=send.getVirtualNodes();
                its=virtualSend.iterator();
                while(its.hasNext()){
                    n1=(String)its.next();
                    //for each receiver
                    out=0;
                    itreceiv=this.path.getReceiv().iterator();
                    while(itreceiv.hasNext()){
                        receiv=(SendReceiv)itreceiv.next();
                        out=out+receiv.getVirtualNodes().size();
                        //for each node of the receiver
                        virtualReceiv=receiv.getVirtualNodes();
                        itt=virtualReceiv.iterator();
                        while(itt.hasNext()){
                            n2=(String)itt.next();
                            if(n1==n2)dqr.add(new DistributedQueryResult(new Node(n1), new Node(n2)));
                            else if(this.loc.explore(n1, n2, db, constraints)){
                                dqr.add(new DistributedQueryResult(new Node(n1), new Node(n2)));
                            }
                            //System.out.println("Virtual Virtual "+n1+" "+n2);
                            //System.out.println("#Virtual Virtual Number of Nodes# "+loc.getNumberNodes()+" for the couple "+n1+" "+n2);
                            //System.out.println("#Virtual Virtual Length of Path# "+loc.getPathLength()+" for the couple "+n1+" "+n2);
                            //System.out.println("#Virtual Virtual Explored Nodes# "+loc.getExploredNodes().size()+" for the couple "+n1+" "+n2);
                            /*this.numberNodes=this.numberNodes+loc.getNumberNodes();
                            if(loc.getPathLength()>this.pathLength)this.pathLength=loc.getPathLength();*/
                        }
                    }
                }
            } 
        Instant end=Instant.now();
        duration=Duration.between(start, end);
        System.out.println("**** Virtual Virtual Computation "+this.numPartition+" "+this.dqr.size());
        db.disconnect();
        System.out.println("$$$$ Virtual Virtual "+this.numPartition+" "+in+"*"+out);
        } catch (Exception ex) {
            System.out.println("Error in reachability.distributedReachability.slaveThreads.VirtualVirtualComputation "+ex.getMessage());
        }
    }

    public Duration getDuration() {
        return duration;
    }

    public ArrayList<DistributedQueryResult> getDqr() {
        return dqr;
    }

    public int getNumberNodes() {
        return numberNodes;
    }

    public int getPathLength() {
        return pathLength;
    }
 
}
