/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reachability.distributedReachability.slaveThreads;

import database.DBConnect;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import reachability.distributedReachability.ConnectingPaths;
import reachability.distributedReachability.DistributedQueryResult;
import reachability.distributedReachability.SendReceiv;
import reachability.distributedReachability.VirtualEdges;
import graphs.Node;
import java.util.ArrayList;

/**
 * computes the contraint reachability between virtual nodes & targets
 * @author amina
 */
public class VirtualTargetsComputation extends Thread{
    private int numPartition;
    private ConnectingPaths path;
    private DBConnect db=new DBConnect();
    private Duration duration;
    private HashSet<String> constraints=new HashSet<String>();
    private LocalReachability loc=new LocalReachability();
    private int numberNodes=0;
    private int pathLength=0;
    /**
     * the key integer is the number of virtual node
     * the hashset of integer are the sources
     */
    //private HashMap<Integer, HashSet<Integer>> interm_result;
    private VirtualEdges virtualEdges;
    private ArrayList<DistributedQueryResult> final_result=new ArrayList<DistributedQueryResult>();
    
    public VirtualTargetsComputation(int numPartition,DBConnect db, ConnectingPaths path, HashSet<String> constraints/*, VirtualEdges virtualEdges, HashMap<Integer, HashSet<Integer>> interm_result*/){
        this.numPartition=numPartition;
        this.db=db;
        this.path=path;
        this.constraints=constraints;
        //this.interm_result=interm_result;
    }

    @Override
    public void run() {
        try{;
            Instant start=Instant.now();
            String tgt;
            Iterator itt,it;
            String node;
            HashSet<String> virtual=new HashSet<String>();
            SendReceiv send;
            Iterator itsend=this.path.getSend().iterator();
            int in=0;
            db.connect("/data/delab/amina/NEO4J/NEO4J_"+this.numPartition+"/data/graph.db");
            while(itsend.hasNext()){
                send=(SendReceiv)itsend.next();
                virtual=send.getVirtualNodes();
                in=in+send.getVirtualNodes().size();
            //loops on virtual nodes v
            /*for(int v:this.interm_result.keySet()){
                //for each source, loops on its sources
                virtual=this.interm_result.get(v);*/                
                it=virtual.iterator();
                while(it.hasNext()){
                    //node is a source
                    node=(String)it.next();
                    itt=this.path.getTgts().iterator();
                    while(itt.hasNext()){
                        tgt=(String)itt.next();
                        if(tgt==node)this.final_result.add(new DistributedQueryResult(new Node(node),new Node(tgt)));
                        else if(this.loc.explore(node, tgt, db, constraints)){
                            this.final_result.add(new DistributedQueryResult(new Node(node),new Node(tgt)));
                        }
                        //System.out.println("Virtual Target "+node+" "+tgt);
                        //System.out.println("#Virtual Target Number of Nodes# "+loc.getNumberNodes()+" for the couple "+node+" "+tgt);
                        //System.out.println("#Virtual Target Length of Path# "+loc.getPathLength()+" for the couple "+node+" "+tgt);
                        //System.out.println("#Virtual Target Explored Nodes# "+loc.getExploredNodes().size()+" for the couple "+node+" "+tgt);
                        /*this.numberNodes=this.numberNodes+loc.getNumberNodes();
                        if(loc.getPathLength()>this.pathLength)this.pathLength=loc.getPathLength();*/
                    }
                }
            }
            Instant end=Instant.now();
            duration=Duration.between(start, end);
            System.out.println("**** Virtual Target Computation "+this.numPartition+" "+this.final_result.size());
            db.disconnect();
            System.out.println("$$$$ Virtual Target "+this.numPartition+" "+in+"*"+this.path.getTgts().size());
        }
        catch(Exception e){
            System.out.println("reachability.distributedReachability.slaveThreads.SourcesVirtualComputation"+e.getMessage());
        }
    }

    public Duration getDuration() {
        return duration;
    }

    public ArrayList<DistributedQueryResult> getFinal_result() {
        ArrayList<DistributedQueryResult> res=new ArrayList<DistributedQueryResult>();
        Iterator it=this.final_result.iterator();
        while(it.hasNext())res.add((DistributedQueryResult)it.next());
        return res;
    }

    public int getNumberNodes() {
        return numberNodes;
    }

    public int getPathLength() {
        return pathLength;
    }
      
}
