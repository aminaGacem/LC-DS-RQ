/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reachability.distributedReachability.slaveThreads;

import database.DBConnect;
import graphs.Node;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Iterator;
import reachability.distributedReachability.ConnectingPaths;
import reachability.distributedReachability.DistributedQueryResult;
import reachability.distributedReachability.SendReceiv;
import java.util.HashMap;
import java.util.ArrayList;
//import reachability.distributedReachability.VirtualEdges;

/**
 * computes the constraint reachability between the virtual nodes and the targets 
 * @author amina
 */
public class SourcesVirtualComputation extends Thread{
    private int numPartition;
    private ConnectingPaths path;
    private DBConnect db=new DBConnect();
    private Duration duration;
    private HashSet<String> constraints=new HashSet<String>();
    private LocalReachability loc=new LocalReachability();
    //private ExchangedData interm_result=new ExchangedData();
    private ArrayList<DistributedQueryResult> interm_result=new ArrayList<DistributedQueryResult>();
    //private VirtualEdges virtualEdges;
    private int numberNodes=0;
    private int pathLength=0;
    
    public SourcesVirtualComputation(int numPartition,DBConnect db, ConnectingPaths path, HashSet<String> constraints/*, /*VirtualEdges virtualEdges*/){
        this.numPartition=numPartition;
        this.db=db;
        this.path=path;
        this.constraints=constraints;
        //this.virtualEdges=virtualEdges;
    }
    
    
    @Override
    public void run() {
        try{
            SendReceiv receiv;
            Instant start=Instant.now();
            String src;
            Iterator its,itt,itv;
            String node;
            int out;
            
            out=0;
            //loops on the receivers fist
            itt=this.path.getReceiv().iterator();
            db.connect("/data/delab/amina/NEO4J/NEO4J_"+this.numPartition+"/data/graph.db");
            while(itt.hasNext()){
                receiv=(SendReceiv)itt.next();
                out=out+receiv.getVirtualNodes().size();
                //loops on the source second
                its=this.path.getSrcs().iterator();                
                while(its.hasNext()){
                    src=(String)its.next();
                    //System.out.println("Amina "+src);
                    //loops on virtual node of receivers
                    itv=receiv.getVirtualNodes().iterator();                    
                    while(itv.hasNext()){
                        node=(String)itv.next();                       
                        //virtual=this.virtualEdges.getSources(node);                        
                        //it=virtual.iterator();
                        //while(it.hasNext()){
                            //v=(Integer)it.next();
                            //System.out.println("!!!!"+src+"!!!!!"+node);                            
                            if(src==node)/*this.interm_result.addElement(receiv.getNumPartition(), src, node);*/ 
                                this.interm_result.add(new DistributedQueryResult(new Node(src),new Node(node)));
                            else {
                                if(this.loc.explore(src, node, db, constraints)){
                                //this.interm_result.addElement(partition, src, node);
                                this.interm_result.add(new DistributedQueryResult(new Node(src),new Node(node)));
                            }
                            /*this.numberNodes=this.numberNodes+loc.getNumberNodes();
                            if(loc.getPathLength()>this.pathLength)this.pathLength=loc.getPathLength();*/
                            //System.out.println("#Source Virtual Number of Nodes# "+loc.getNumberNodes()+" for the couple "+src+" "+node);
                            //System.out.println("#Source Virtual Length of Path# "+loc.getPathLength()+" for the couple "+src+" "+node);
                            //System.out.println("#Source Virtual Explored Nodes# "+loc.getExploredNodes().size()+" for the couple "+src+" "+node);
                            }
                        //}
                    }
                }
            }
            Instant end=Instant.now();
            duration=Duration.between(start, end);
            System.out.println("**** Source Virtual Computation "+this.numPartition+" "+this.interm_result.size());
            db.disconnect();
            System.out.println("$$$$ Source Virtual "+this.numPartition+" "+this.path.getSrcs().size()+"*"+out);
            
        }
        catch(Exception e){
            System.out.println("reachability.distributedReachability.slaveThreads.SourcesVirtualComputation"+e.getMessage());
        }
    }

    public Duration getDuration() {
        return duration;
    }

    public ArrayList<DistributedQueryResult> getInterm_result() {
        return interm_result;
    }

    public int getNumberNodes() {
        return numberNodes;
    }

    public int getPathLength() {
        return pathLength;
    }
       
}
