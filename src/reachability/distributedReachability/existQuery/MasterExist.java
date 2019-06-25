/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reachability.distributedReachability.existQuery;
import database.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import graphs.Node;
import reachability.distributedReachability.slaveThreads.*;
import graphs.DirectedEdge;
import java.util.HashMap;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import reachability.distributedReachability.ConnectingPaths;
import reachability.distributedReachability.DistributedQuery;
import reachability.distributedReachability.DistributedQueryResult;
import reachability.distributedReachability.VirtualEdges;
/**
 *The master supervises the distributed computation among parallel slaves for existential queries
 * @author amina
 */
public class MasterExist extends Thread{
    /*
    match match p=(n: vertex)-[r]-> (m: vertex) where TYPE(r)="d" or TYPE(r)="a" or TYPE(r)="b" return  n.id, n.slave, m.id, m.slave

build the record send receiv src target

correct the record with the constraint i > i
    */
    private int numberSlaves;
    private DistributedQuery dq;
    private ArrayList<ConnectingPaths> paths=new ArrayList<ConnectingPaths>();
    private ArrayList<DistributedQueryResult> compressedQuery=new ArrayList<DistributedQueryResult>();
    private ArrayList<VirtualEdgeExist> vee=new ArrayList<VirtualEdgeExist>();
    private PathSerie interm_path;
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
    private VirtualEdges virtualEdges=new VirtualEdges();
    
    private ArrayList<Duration> parallel_durations=new ArrayList<Duration>();
    private ArrayList<Boolean> presence_constraints=new ArrayList<Boolean>();

    public MasterExist(DistributedQuery dq) {
        this.dq = dq;
        for(int i=0;i<this.numberSlaves;i++){
            this.source_partitions.add(Boolean.FALSE);
            this.target_partitions.add(Boolean.FALSE);
            this.active_partitions.add(Boolean.FALSE);
        }
    }

    /**
     * distributes the sources and the targets across the connecting paths
     * identifies the source partitions, target partition, the active partitions
     * compresses the query
     * builds the path serie
     * @param 
     */
//    private void preprocess(){
//        Iterator it;
//        ConnectingPaths record;
//        for(int i=0;i<this.numberSlaves;i++){
//            this.paths.add(new ConnectingPaths());
//        }
//        //populate the sources
//        it=dq.getSources().iterator();
//        Node n;
//        while(it.hasNext()){
//            n=(Node)it.next();
//            this.source_partitions.set(n.getId()/50000, Boolean.TRUE);
//            record=this.paths.get((n.getId()/50000));
//            record.addSource(n.getId());
//            this.paths.set((n.getId()/50000), record);
//        }  
//        //populates the targets
//        it=dq.getTargets().iterator();
//        while(it.hasNext()){
//            n=(Node)it.next();
//            this.target_partitions.set(n.getId()/50000, Boolean.TRUE);
//            record=this.paths.get((n.getId()/50000));
//            record.addTarget(n.getId());;
//            this.paths.set((n.getId()/50000), record);            
//        }
//        this.interm_path=new PathSerie();
//        //create the compressed query
//        int i,j;
//        for(i=0;i<this.numberSlaves;i++){
//            if(this.source_partitions.get(i)){
//                for(j=0;j<this.numberSlaves;j++){
//                    if(this.target_partitions.get(j)){
//                        if(this.interm_path.isReachable(i+1, j+1))
//                        this.compressedQuery.add(new DistributedQueryResult(new Node(i+1),new Node(j+1)));
//                    }
//                }
//            }
//        }
//    }

    
//    @Override
//    public void run(){
//        this.preprocess();
//        //we explore the slaves first to find active partitions, an active partition contains at least one label of the presence constraint
//        ArrayList<SlaveExist> threads=new ArrayList<SlaveExist>();
//        for(int i=0;i<this.numberSlaves;i++){
//            //create the thread for each slave
//            threads.set(i, new SlaveExist(i+1,dq.getExistConstraints()));
//            //keeps trace of active partitions
//            this.active_partitions.set(i, threads.get(i).isActive());
//        }
//        try{
//            for(int i=0;i<this.numberSlaves;i++)threads.get(i).join();
//            //generates results for local reachability
//            for(int i=0;i<this.numberSlaves;i++){
//                if(this.fullySatisfied(threads.get(i).getPresence_constraints()))this.generateResult(i+1, i+1);
//            }
//            //run on virtual edges
//            Iterator it=this.compressedQuery.iterator();
//            VirtualEdgeExist v;
//            DistributedQueryResult dqr;
//            while(it.hasNext()){
//                dqr=(DistributedQueryResult)it.next();
//                v=new VirtualEdgeExist(dqr.getSource().getId(),dqr.getTarget().getId(),this.dq.getExistConstraints());
//                v.checkConstraint();
//                this.vee.add(v);                
//            }
//            it=this.compressedQuery.iterator();
//            ArrayList<Boolean> op1,op2;
//            int src, tgt;//ranges from 1 to numberSlaves
//            ArrayList<List<Integer>> set_paths;
//            List<Integer> one_path;
//            Iterator itpath;
//            Iterator itlist;
//            int supernode;
//            boolean found;
//            while(it.hasNext()){
//                dqr=(DistributedQueryResult)it.next();
//                //of course they are reachable
//                src=dqr.getSource().getId();
//                tgt=dqr.getTarget().getId();
//                op1=threads.get(src-1).getPresence_constraints();
//                op2=threads.get(tgt-1).getPresence_constraints();
//                if(this.fullySatisfied(this.orOperator(op1, op2)))this.generateResult(src-1, tgt-1);
//                else {
//                    //retrieve the intermediate paths
//                    set_paths=this.interm_path.retrievePaths(src, tgt);
//                    itpath=set_paths.iterator();
//                    while(itpath.hasNext()){
//                        one_path=(List<Integer>)itpath.next();
//                        op1=this.orOperator(op1, op2);
//                        //checks the connecting slaves
//                        itlist=one_path.iterator();
//                        found=false;
//                        while(itlist.hasNext()&&found==false){
//                            supernode=(Integer)itlist.next();
//                             op2=threads.get(supernode-1).getPresence_constraints();
//                             if(this.fullySatisfied(this.orOperator(op1, op2)))found=true;
//                        }
//                        if(found==false){
//                            //check on virtual edges
//                            
//                        }
//                        else {
//                            //we find
//                            this.generateResult(src-1, tgt-1);
//                        }
//                    }
//                    //check on slaves
//                    //check on virtual edges                    
//                }
//            }
//            //we cure the partitions, an active partition which is not reachable from the source or the target is not active anymore
//            
//            
//            //we check if there is any further exploration of virtual edges          
//            //we search for the remaining constraints in the virtual edges
//            //we select the paths where the lenth >= the number of remaining constraints
//        }
//        catch(Exception e){
//            
//        }          
//    }

    public HashSet<DistributedQueryResult> getResult() {
        return result;
    }

    public VirtualEdges getVirtualEdges() {
        return virtualEdges;
    }   
    
    private boolean fullySatisfied(ArrayList<Boolean> constr){
        Iterator it;
        boolean elt;
        it=constr.iterator();
        while(it.hasNext()){
            elt=(Boolean)it.next();
            if(elt==false)return false;
        }
        return true;
    }
    
    private ArrayList<Boolean> orOperator(ArrayList<Boolean> constr1,ArrayList<Boolean> constr2){
        ArrayList<Boolean> result=new ArrayList<Boolean>();
        boolean elt1,elt2;
        if(constr1.size()!=constr2.size())return null;
        Iterator it1=constr1.iterator();
        Iterator it2=constr1.iterator();
        while(it1.hasNext()&&it2.hasNext()){
            elt1=(boolean)it1.next();
            elt2=(boolean)it2.next();
            result.add(elt2||elt1);
        }
        return result;
    }
    /**
     * builds the pairs of results from two partitions
     * @param src the source partition 0-->5
     * @param tgt the target partition 0-->5
     */
//    private void generateResult(int src, int tgt){
//        ConnectingPaths record1,record2;
//        record1=this.paths.get(src);
//        record2=this.paths.get(tgt);
//        Iterator its,itt;
//        its=record1.getSrcs().iterator();
//        int n1,n2;
//        while(its.hasNext()){
//            n1=(int)its.next();
//            itt=record2.getTgts().iterator();
//            while(itt.hasNext()){
//                n2=(int)itt.next();
//                this.result.add(new DistributedQueryResult(new Node(n1),new Node(n2)));
//            }
//        }
//    }

}
