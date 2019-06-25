/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reachability.distributedReachability;

import database.DBConnect;
import graphs.DirectedEdge;
import graphs.Node;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import reachability.distributedReachability.slaveThreads.LocalComputation;
import reachability.distributedReachability.slaveThreads.SourcesVirtualComputation;
import reachability.distributedReachability.slaveThreads.VirtualTargetsComputation;
import reachability.distributedReachability.slaveThreads.VirtualVirtualComputation;

/**
 *is able to orchestrate the processing for multiple SCCs by server
 * @author amina
 */
public class MasterGeneric extends Thread{
    
    private DBConnect db;
    private ArrayList<DBConnect> dbs=new ArrayList<DBConnect>();
    private int numberSlaves;
    private DistributedQuery dq;
    private ArrayList<ConnectingPaths> paths=new ArrayList<ConnectingPaths>();
    private PathSerie set_path=new PathSerie();
    /**
     * it contains the sources
     */
    private ArrayList<Boolean> source_partitions=new ArrayList<Boolean>();
    /**
     * it contains the targets
     */
    private ArrayList<Boolean> target_partitions=new ArrayList<Boolean>();
    private ArrayList<Boolean> active_partitions=new ArrayList<Boolean>();
    private HashSet<DistributedQueryResult> result=new HashSet<DistributedQueryResult>();
    private ArrayList<HashSet<DistributedQueryResult>> temp_result=new ArrayList<HashSet<DistributedQueryResult>>();
    private VirtualEdges virtualEdges=new VirtualEdges();
    private ArrayList<ArrayList<DistributedQueryResult>> src_virt_result=new ArrayList<ArrayList<DistributedQueryResult>>();
    private ArrayList<ArrayList<DistributedQueryResult>>virt_virt_result=new ArrayList<ArrayList<DistributedQueryResult>>();
    private ArrayList<ArrayList<DistributedQueryResult>>virt_tgt_result=new ArrayList<ArrayList<DistributedQueryResult>>();
    
    private ArrayList<Duration> parallel_durations=new ArrayList<Duration>();
    
    private Duration total_duration;
    private ArrayList<Integer> numberNodes=new ArrayList<Integer>();
    private ArrayList<Integer> pathLength=new ArrayList<Integer>();
    
    public MasterGeneric(DistributedQuery dq,int numberSlaves) {
        this.dq = dq;
        this.numberSlaves=numberSlaves;
        for(int i=0;i<this.numberSlaves;i++){
            this.source_partitions.add(Boolean.FALSE);
            this.target_partitions.add(Boolean.FALSE);
            this.active_partitions.add(Boolean.FALSE);
        }                
    }
    
    /*public void init(){
        try{
            System.out.println("Begins");
            db=new DBConnect() ;
            int i=db.connect("/data/delab/amina/NEO4J/NEO4J_HOME/data/graph.db");
            //gets the virtual edges
            i=db.query(this.generateBoundQuery(dq.getUnivConstraints()));
            ArrayList<String> result=new ArrayList<String>();
            result=db.getResult();          
            db.disconnect();
            System.out.println("Number of virtual edges being retrieved "+result.size());
            //preprocesses the results
            this.preprocess(result);
            System.out.println("Results were preprocessed");
            //this.virtualEdges.println();
            //cures the paths
            this.cure();
            System.out.println("Results were cured");
        }
        catch (Exception e){
            System.out.println("Erreur MasterParallel.init "+e.getMessage());
        }        
    }*/
    /**
     * writes the query which retrieves the virtual edges that connects between the slave under constraints
     * @param constraints edges labels of the constraints
     * @return 
     */
    private String generateBoundQuery(HashSet constraints){
        String query="match p=(n)-[r]-> (m) where ";
        Iterator it;
        it=constraints.iterator();
        if(it.hasNext()){
            query=query+"TYPE(r)=\""+(String)it.next()+"\"";
        }
        while(it.hasNext()){
            query=query+" or "+ "TYPE(r)=\""+(String)it.next()+"\"";
        }
        query=query+" return n.id, n.slave, m.id, m.slave, type(r)";
        System.out.println(query);
        return query;
    }
    /**
     * builds the connecting paths between the slaves, what are the senders and the
     * receivers and also the virtual nodes involved
     * @param result 
     */
    /*private void preprocess(ArrayList<String> result){
        Iterator it;        
        String id_src,slave_src, id_tgt, slave_tgt;
        ConnectingPaths record;
        String r;
        HashSet<DistributedQueryResult> dqr=new HashSet<DistributedQueryResult>();
        
        for(int i=0;i<this.numberSlaves;i++){
            this.paths.add(new ConnectingPaths());
        }
        
        //populate the sources
        it=dq.getSources().iterator();
        Node n;
        while(it.hasNext()){
            n=(Node)it.next();
            record=(ConnectingPaths)this.paths.get((n.getSlave()));
            record.addSource(n.getId());            
            this.paths.set((n.getId()/50000), record);            
            this.source_partitions.set((n.getId()/50000), true);            
        }        
        //populates the targets
        it=dq.getTargets().iterator();
        while(it.hasNext()){
            n=(Node)it.next();
            record=(ConnectingPaths)this.paths.get((n.getId()/50000));
            record.addTarget(n.getId());
            this.paths.set((n.getId()/50000), record);
            this.target_partitions.set((n.getId()/50000), Boolean.TRUE);
        }        
        
        //populates the senders & receivers
        it=result.iterator();
        while(it.hasNext()){
            id_src=(String)it.next();
            slave_src=(String)it.next();
            id_tgt=(String)it.next();
            slave_tgt=(String)it.next();
            r=(String)it.next();
            dqr.add(new DistributedQueryResult(new Node(Integer.parseInt(slave_src)),new Node(Integer.parseInt(slave_tgt))));
            record=(ConnectingPaths)this.paths.get(Integer.parseInt(slave_src)-1);
            record.addReceiver(new SendReceiv(Integer.parseInt(slave_tgt)));
            record.addVirtualToReceiver(Integer.parseInt(slave_tgt), Integer.parseInt(id_src));
            record=(ConnectingPaths)this.paths.get(Integer.parseInt(slave_tgt)-1);
            record.addSender(new SendReceiv(Integer.parseInt(slave_src)));
            record.addVirtualToSender(Integer.parseInt(slave_src), Integer.parseInt(id_tgt));
            this.virtualEdges.addVirtualEdge(new DirectedEdge(new Node(Integer.parseInt(id_src)), new Node(Integer.parseInt(id_tgt)),r));
        }        
        this.set_path.checkVirtualEdgesPresence(dqr);        
    }*/
    /**
     * checks that redundant partitions will be removed
     * it keeps only the active partition
     * a partition is active if
     * it contains both sources and targets, or
     * contains sources and can reach at least one partition where targets are stored, or
     * contains targets and is reachable from at least one partition of sources, or
     * it can reach at least one target partition and is reachable from at least one source partition
     * at the end, if a partition is not active, it has to be removed from any list of senders or receivers
     */
    private void cure(){
        int j;
        for(int i=0;i<this.numberSlaves;i++){
            //if partition is contains sources & targets, it is active
            if(this.source_partitions.get(i)&&this.target_partitions.get(i)){
                this.active_partitions.set(i, Boolean.TRUE);
            }
            //case only sources are there
            if(this.active_partitions.get(i)==false&&this.source_partitions.get(i)){
                //look for a target partition reachable from i
                for(j=i+1;j<this.numberSlaves&&this.set_path.isReachable(i+1, j+1)==false;j++);
                if(j==this.numberSlaves)this.source_partitions.set(i, Boolean.FALSE);
                else this.active_partitions.set(i, Boolean.TRUE);
            }
            //case only targets are here
            if(this.active_partitions.get(i)==false&&this.target_partitions.get(i)==true){
                //look for a source partition that reaches i
                for(j=0;j<i&&this.set_path.isReachable(j+1, i+1)==false;j++);
                if(j==i)this.target_partitions.set(i, Boolean.FALSE);
                else this.active_partitions.set(i, Boolean.TRUE);
            }
            //case no source no target 
            if(this.active_partitions.get(i)==false) {
                //must find at least a source partition that reaches i
                for(j=0;j<i&&this.set_path.isReachable(j+1, i+1)==false;j++);                
                if(j<i) {
                    for(j=i+1;j<this.numberSlaves&&this.set_path.isReachable(i+1, j+1)==false;j++);
                    if(j<this.numberSlaves)this.active_partitions.set(i, Boolean.TRUE);
                }                
            }           
        }
        //ensure here that non-active partition are removed from senders/receivers
        //this code will be helpful in case we have cycles
        ConnectingPaths record;
        for(int i=0;i<this.numberSlaves;i++){
            if(this.active_partitions.get(i)==false){
                //remove from senders --> check on partitions < i
                //remove from receivers --> check on partitions > i
                for(j=0;j<this.numberSlaves;j++){
                    record=(ConnectingPaths)this.paths.get(j);
                    record.removeSender(new SendReceiv(i+1));
                    record.removeReceiver(new SendReceiv(i+1));
                    this.paths.set(j, record);
                }
            }
        }
    }
    
    @Override
    public void run(){
            //this.initDb();        
            for(int i=0;i<this.numberSlaves;i++){
                this.numberNodes.add(0);
                this.pathLength.add(0);
                this.parallel_durations.add(Duration.ZERO);
            }
            //starts threads on slaves
            //starts the local computation
            //ArrayList<LocalComputation> threads=new ArrayList<LocalComputation>();
            LocalComputation[] threads=new LocalComputation[50];
            VirtualTargetsComputation[] threads2=new VirtualTargetsComputation[50];
            VirtualVirtualComputation[] threads3=new VirtualVirtualComputation[50];
                       
            try{
                for(int i=0;i<this.numberSlaves;i++){
                    if(this.active_partitions.get(i)&&this.source_partitions.get(i)&&this.target_partitions.get(i)){
                        threads[i]=new LocalComputation(i+1,this.dbs.get(i),this.paths.get(i),this.dq.getUnivConstraints());
                        //threads.add(new LocalComputation(i+1,this.paths.get(i),this.dq.getUnivConstraints()));
                        //threads.set(i,new LocalComputation(i+1,this.paths.get(i),this.dq.getUnivConstraints()));                        
                        //threads.get(i).start();
                        threads[i].start();
                        threads[i].join();                        
                       // threads.get(i).join();                          
                        //this.result.addAll(threads.get(i).getFinal_result());
                        this.result.addAll(threads[i].getFinal_result());
                        //this.parallel_durations.set(i,threads.get(i).getDuration());
                        this.parallel_durations.set(i, threads[i].getDuration());
                        //this.numberNodes.set(i, this.numberNodes.get(i)+threads.get(i).getNumberNodes());
                        this.numberNodes.set(i, threads[i].getNumberNodes());
                        this.pathLength.set(i, threads[i].getNumberNodes());
                        //if(threads.get(i).getPathLength()>this.pathLength.get(i))this.pathLength.set(i, threads.get(i).getPathLength());                         
                    }                    
                    System.out.println("Local Computation "+(i+1));
                }
                /*Iterator it=this.result.iterator();
                DistributedQueryResult dqr;
                while(it.hasNext()){
                    dqr=(DistributedQueryResult)it.next();
                    dqr.println();
                }*/
            }
            catch(Exception e){
                
            }
            //starts the computations between the sources & the intermediate results            
            ArrayList<SourcesVirtualComputation> threads1=new ArrayList<SourcesVirtualComputation>();                        
            try{
                for(int i=0;i<this.numberSlaves;i++){
                    threads1.add(new SourcesVirtualComputation(i+1,this.dbs.get(i),this.paths.get(i),this.dq.getUnivConstraints()));
                    if(this.active_partitions.get(i)&&this.source_partitions.get(i)){
                        //threads1.add(new SourcesVirtualComputation(i+1,this.paths.get(i),this.dq.getUnivConstraints()));                     
                        threads1.get(i).start();
                        threads1.get(i).join();                        
                        this.parallel_durations.set(i, this.parallel_durations.get(i).plus(threads1.get(i).getDuration()));
                        this.numberNodes.set(i, this.numberNodes.get(i)+threads1.get(i).getNumberNodes());
                        
                        if(threads1.get(i).getPathLength()>this.pathLength.get(i))this.pathLength.set(i,threads1.get(i).getPathLength());
                        this.src_virt_result.add(threads1.get(i).getInterm_result());
                    }
                    else {
                        this.parallel_durations.set(i, this.parallel_durations.get(i).plus(Duration.ZERO));
                        this.src_virt_result.add(new ArrayList<DistributedQueryResult>());
                    }
                    System.out.println("Sources Virtuals Computation "+(i+1));
                }
                //computes the constraint reachability between virtual nodes & targets
                System.out.println("amina!!!!");
                for(int i=0;i<this.numberSlaves;i++){
                    if(this.active_partitions.get(i)&&this.target_partitions.get(i)){
                        threads2[i]=new VirtualTargetsComputation(i+1,this.dbs.get(i),this.paths.get(i),this.dq.getUnivConstraints());
                        //threads2.set(i, new VirtualTargetsComputation(i+1,this.paths.get(i),this.dq.getUnivConstraints()));                        
                        threads2[i].start();
                        //threads2.get(i).start();
                        threads2[i].join();
                        //threads2.get(i).join();
                        this.parallel_durations.set(i, this.parallel_durations.get(i).plus(threads2[i].getDuration()));
                        this.virt_tgt_result.add(threads2[i].getFinal_result());
                        
                        this.numberNodes.set(i, this.numberNodes.get(i)+threads2[i].getNumberNodes());
                        if(threads2[i].getPathLength()>this.pathLength.get(i))this.pathLength.set(i, threads2[i].getPathLength());
                    }
                    else {
                        this.parallel_durations.set(i, this.parallel_durations.get(i).plus(Duration.ZERO));
                        this.virt_tgt_result.add(new ArrayList<DistributedQueryResult>());
                    }
                    System.out.println("Virtuals Targets Computation "+(i+1));    
                        //this.durations[i]=this.durations[i].plus(threads2[i].getDuration());
                }                
                //computes the constraint reachability between virtual nodes & virtual nodes                
                for(int i=0;i<this.numberSlaves;i++){
                    if(this.active_partitions.get(i)){
                        threads3[i]=new VirtualVirtualComputation(i+1,this.dbs.get(i),this.paths.get(i),this.dq.getUnivConstraints());
                        //threads3.set(i, new VirtualVirtualComputation(i+1,this.paths.get(i),this.dq.getUnivConstraints()/*,this.virtualEdges*/));
                        threads3[i].start();;
                        //threads3.get(i).start();
                        threads3[i].join();
                        //threads3.get(i).join();
                        this.parallel_durations.set(i, this.parallel_durations.get(i).plus(threads3[i].getDuration()));
                        this.virt_virt_result.add(threads3[i].getDqr());
                        this.numberNodes.set(i, this.numberNodes.get(i)+threads3[i].getNumberNodes());
                        if(threads3[i].getPathLength()>this.pathLength.get(i))this.pathLength.set(i,threads3[i].getPathLength());
                    }
                    else {
                        this.parallel_durations.set(i, this.parallel_durations.get(i).plus(Duration.ZERO));
                        this.virt_virt_result.add(new ArrayList<DistributedQueryResult>());
                    }
                    System.out.println("Virtuals Virtuals Computation "+(i+1));
                }                
                long start=System.currentTimeMillis();
                this.filter();
                Long end=System.currentTimeMillis();
                this.total_duration=Duration.ofMillis(end-start);
                long max=this.parallel_durations.get(0).toMillis();
                for(int k=1;k<this.numberSlaves;k++){
                    if(this.parallel_durations.get(k).toMillis()>max)max=this.parallel_durations.get(k).toMillis();
                }
                this.total_duration=this.total_duration.plus(Duration.ofMillis(max));                
                Iterator it=this.result.iterator();
                DistributedQueryResult dqr;
                //prints the size of results
                System.out.println("% "+this.result.size());
                this.virtualEdges.println();
                while(it.hasNext()){
                    dqr=(DistributedQueryResult)it.next();
                    //dqr.println();
                    System.out.println("%%%"+dqr.getSource().getId()+"  "+dqr.getTarget().getId());
                }
                for(int i=0;i<this.numberSlaves;i++){
                    System.out.println("@ "+this.parallel_durations.get(i).toMillis());
                }
                System.out.println("!"+this.total_duration.toMillis());
                //prints the number of nodes
                System.out.print("#");
                for(int i=0;i<this.numberSlaves;i++)System.out.print(" "+this.numberNodes.get(i));
                System.out.println();
                //prints the length of paths
                System.out.print("?");
                for(int i=0;i<this.numberSlaves;i++)System.out.print(" "+this.pathLength.get(i));
                System.out.println();
            }
            catch(Exception e){
                System.out.println("reachability.distributedReachability.Master.run "+e.getMessage());
            }
            //this.disconnectDb();            
    }

    public HashSet<DistributedQueryResult> getResult() {
        return result;
    }

    public VirtualEdges getVirtualEdges() {
        return virtualEdges;
    }

    
    public HashMap<Integer, HashSet<Integer>> invertHashMap(HashMap<Integer, HashSet<Integer>> hashmap){
        //result contains the inverted HashMap
        HashMap<Integer, HashSet<Integer>> result=new HashMap<Integer, HashSet<Integer>>();
        //actually oldvalues are values of hashmap --> virtual nodes
        HashSet<Integer> oldvalue;
        //actually newvalues are values of result --> sources
        HashSet<Integer> newvalue;
        Iterator it;
        int n;
        for(int key: hashmap.keySet()){
            //key is the source
            oldvalue=hashmap.get(key);
            //oldvalue keeps the virtual nodes
            it=oldvalue.iterator();
            while(it.hasNext()){
                //n is a virtual node
                n=(Integer)it.next();
                //newvalue are the sources
                newvalue=result.get(n);
                if(newvalue==null)newvalue=new HashSet<Integer>();
                newvalue.add(key);
                result.put(n, newvalue);
            }
        }
        return result;
    }
    
    public void filter(){
       //explores first the active partitions with no senders and with sources
        Iterator it=this.paths.iterator();
        ConnectingPaths path;
        HashSet<DistributedQueryResult> dqr1=new HashSet<DistributedQueryResult>();
        HashSet<DistributedQueryResult> dqr2=new HashSet<DistributedQueryResult>();
        HashSet<DistributedQueryResult> temp=new HashSet<DistributedQueryResult>();
        
        SendReceiv receiv;

        for(int i=0;i<this.numberSlaves;i++)this.temp_result.add(new HashSet<DistributedQueryResult>());
        for(int i=0;i<this.numberSlaves;i++){
            if(this.active_partitions.get(i)){
                path=this.paths.get(i);
                if(path.getSend()!=null&&!path.getSend().isEmpty()){
                    //if it has predecessors
                    //gets back the cumulative results from previous nodes temp
                    temp=this.temp_result.get(i);
                    dqr1.clear();
                    dqr1.addAll(this.virt_virt_result.get(i));
                    //merges temp with virt-virt pairs
                    temp=this.virtualEdges.construct(temp, dqr1);
                    this.temp_result.get(i).addAll(temp);
                    dqr2.clear();
                    dqr2.addAll(this.virt_tgt_result.get(i));
                    //merges temp with virt-tgt
                    temp=this.virtualEdges.construct(temp, dqr2);
                    this.result.addAll(temp);
                }
                if(path.getReceiv()!=null&&!path.getReceiv().isEmpty()){
                    it=path.getReceiv().iterator();
                    this.temp_result.get(i).addAll(this.src_virt_result.get(i));
                    temp=this.temp_result.get(i);
                    //for each receiver
                    while(it.hasNext()){
                        receiv=(SendReceiv)it.next();
                        this.temp_result.get(receiv.getNumPartition()-1).addAll(this.temp_result.get(i));
                    }
                }
            }
        }      
    }
    
}
