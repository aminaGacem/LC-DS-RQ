/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reachability.distributedReachability.slaveThreads;
import database.*;
import reachability.distributedReachability.ConnectingPaths;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.HashSet;
import java.util.ArrayList;
import reachability.distributedReachability.DistributedQueryResult;
import graphs.Node;

/**
 * computes the constraint reachability between sources & nodes
 * @author amina
 */
public class LocalComputation extends Thread{
    private int numPartition;
    private ConnectingPaths path;
    private DBConnect db;
    //private long start;
    //private long end;
    private Duration duration;
    private HashSet<String> constraints=new HashSet<String>();
    private LocalReachability loc=new LocalReachability();
    private ArrayList<DistributedQueryResult> final_result=new ArrayList<DistributedQueryResult>();
    private int numberNodes=0;
    private int pathLength=0;
    
    
    public LocalComputation(int numPartition, DBConnect db,ConnectingPaths path, HashSet<String> constraints){
        this.numPartition=numPartition;
        this.db=db;
        this.path=path;
        this.constraints=constraints;
    }

    @Override
    public void run() {
        try{
            //start=System.currentTimeMillis();
            Instant start;
            start=Instant.now();
            String src,tgt;
            Iterator its,itt;
            its=this.path.getSrcs().iterator();
            System.out.println("$$$$ Local Computation "+this.numPartition+" "+this.path.getSrcs().size()+"*"+this.path.getTgts().size());
            db.connect("/data/delab/amina/NEO4J/NEO4J_"+this.numPartition+"/data/graph.db");
            while(its.hasNext()){
                src=(String)its.next();
                itt=this.path.getTgts().iterator();
                while(itt.hasNext()){
                    tgt=(String)itt.next();
                    if(loc.explore(src, tgt, db, constraints)){
                        this.final_result.add(new DistributedQueryResult(new Node(src), new Node(tgt)));
                    }
                    //System.out.println("# Local Computation Number of Nodes# "+loc.getNumberNodes()+" for the couple "+src+" "+tgt);
                    /*this.numberNodes=this.numberNodes+loc.getNumberNodes();
                    if(loc.getPathLength()>this.pathLength)this.pathLength=loc.getPathLength();*/
                    //System.out.println("#Local Computation Length of Path# "+loc.getPathLength()+" for the couple "+src+" "+tgt);
                    //System.out.println("#Local Computation Explored Nodes# "+loc.getExploredNodes().size()+" for the couple "+src+" "+tgt);
                }
            }
            System.out.println("**** Local Computation "+this.numPartition+" "+this.final_result.size());
            db.disconnect();
            Instant end=Instant.now();
            duration=Duration.between(start, end);
        }
        catch(Exception e){
            System.out.println("Error in reachability.distributedReachability.slaveThreads.LocalComputation.run() "+e.getMessage());
        }        
    }

    public ArrayList<DistributedQueryResult> getFinal_result() {
        return final_result;
    }

    public Duration getDuration() {
        return duration;
    }

    public int getNumberNodes() {
        return numberNodes;
    }

    public int getPathLength() {
        return pathLength;
    }   
    
}
