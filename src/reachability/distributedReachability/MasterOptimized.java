/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reachability.distributedReachability;
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
/**
 *The master supervises the distributed computation in a sequential manner among slaves for universal queries
 * @author amina
 */
public class MasterOptimized extends Thread{
    /*
    match match p=(n: vertex)-[r]-> (m: vertex) where TYPE(r)="d" or TYPE(r)="a" or TYPE(r)="b" return  n.id, n.slave, m.id, m.slave

build the record send receiv src target

correct the record with the constraint i > i
    */
    private DBConnect db;
    private DBConnect dbs[]=new DBConnect[6];
    private DistributedQuery dq;
    private ArrayList<ConnectingPaths> paths=new ArrayList<ConnectingPaths>();
    /**
     * it contains the sources
     */
    private boolean[] source_partitions=new boolean[6];
    /**
     * it contains the targets
     */
    private boolean[] target_partitions=new boolean[6];
    private boolean[] active_partitions=new boolean[6];
    private HashSet<DistributedQueryResult> result=new HashSet<DistributedQueryResult>();
    private VirtualEdges virtualEdges=new VirtualEdges();
    private ArrayList<ArrayList<DistributedQueryResult>> src_virt_result=new ArrayList<ArrayList<DistributedQueryResult>>();
    private ArrayList<ArrayList<DistributedQueryResult>>virt_virt_result=new ArrayList<ArrayList<DistributedQueryResult>>();
    private ArrayList<ArrayList<DistributedQueryResult>>virt_tgt_result=new ArrayList<ArrayList<DistributedQueryResult>>();

    private Duration[] optimzed_durations=new Duration[6];
    
    private Duration total_duration=Duration.ZERO;
    private int[] numberNodes=new int[6];
    private int[] pathLength=new int[6];
    private int[] slaveCost=new int[6];
    private boolean[] finished=new boolean[6];
    
    public MasterOptimized(DistributedQuery dq) {
        this.dq = dq;
        for(int i=0;i<6;i++){
            source_partitions[i]=false;
            target_partitions[i]=false;
            active_partitions[i]=false;
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
            System.out.println("Erreur Fatale!!!!!");
        }        
    }*/
    /**
     * writes the query which retrieves the virtual edges that connects between the slave under constraints
     * @param constraints edges labels of the constraints
     * @return 
     */
    private String generateBoundQuery(HashSet constraints){
        String query="match p=(n: vertex)-[r]-> (m: vertex) where ";
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
        
        for(int i=0;i<6;i++){
            this.paths.add(new ConnectingPaths());
        }
        //populate the sources
        it=dq.getSources().iterator();
        Node n;
        while(it.hasNext()){
            n=(Node)it.next();
            record=(ConnectingPaths)this.paths.get((n.getId()/5000000));
            record.addSource(n.getId());
            this.paths.set((n.getId()/5000000), record);
            this.source_partitions[(n.getId()/5000000)]=true;
        }  
        //populates the targets
        it=dq.getTargets().iterator();
        while(it.hasNext()){
            n=(Node)it.next();
            record=(ConnectingPaths)this.paths.get((n.getId()/5000000));
            record.addTarget(n.getId());
            this.paths.set((n.getId()/5000000), record);
            this.target_partitions[(n.getId()/5000000)]=true;
        }  
        //populates the senders & receivers
        it=result.iterator();
        while(it.hasNext()){
            id_src=(String)it.next();
            slave_src=(String)it.next();
            id_tgt=(String)it.next();
            slave_tgt=(String)it.next();
            r=(String)it.next();
            record=(ConnectingPaths)this.paths.get(Integer.parseInt(slave_src)-1);
            record.addReceiver(new SendReceiv(Integer.parseInt(slave_tgt)));
            record.addVirtualToReceiver(Integer.parseInt(slave_tgt), Integer.parseInt(id_src));
            record=(ConnectingPaths)this.paths.get(Integer.parseInt(slave_tgt)-1);
            record.addSender(new SendReceiv(Integer.parseInt(slave_src)));
            record.addVirtualToSender(Integer.parseInt(slave_src), Integer.parseInt(id_tgt));
            this.virtualEdges.addVirtualEdge(new DirectedEdge(new Node(Integer.parseInt(id_src)), new Node(Integer.parseInt(id_tgt)),r));
        }
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
        for(int i=0;i<6;i++){
            //if partition is contains sources & targets, it is active
            if(this.source_partitions[i]==true&&this.target_partitions[i]==true){
                this.active_partitions[i]=true;
            }
            //case only sources are there
            else if(this.source_partitions[i]==true&&this.target_partitions[i]==false){
                //look for a target partition reachable from i
                if(i==1)j=3;
                else j=i+1;
                for(;j<6&&this.target_partitions[j]==false;j++);
                if(j==6)this.source_partitions[i]=false;
                else this.active_partitions[i]=true;
            }
            //case only targets are here
            else if(this.source_partitions[i]==false&&this.target_partitions[i]==true){
                //look for a source partition that reaches i
                if(i==0)j=-1;
                else if(i<3)j=1;
                else j=i-1;
                for(;j>-1&&this.source_partitions[j]==false;j--);
                if(j==-1)this.target_partitions[i]=false;
                else this.active_partitions[i]=true;
            }
            //case no source no target 
            else {
                //must find at least a source partition that reaches i
                if(i==0)j=-1;
                else if(i<3)j=1;
                else j=i-1;
                for(;j>-1&&this.source_partitions[j]==false;j--);
                if(j>-1)this.active_partitions[i]=true;
                if(this.active_partitions[i]==true){
                    //must find at least a target partition reachable from i
                    if(i==1)j=3;
                    else j=i+1;
                    for(;j<6&&this.target_partitions[j]==false;j++);
                    if(j==6)this.active_partitions[i]=false;
                }                
            }           
        }
        //ensure here that non-active partition are removed from senders/receivers
        //this code will be helpful in case we have cycles
        ConnectingPaths record;
        for(int i=0;i<6;i++){
            if(this.active_partitions[i]==false){
                //remove from senders --> check on partitions < i
                //remove from receivers --> check on partitions > i
                for(j=0;j<6;j++){
                    record=(ConnectingPaths)this.paths.get(j);
                    record.removeSender(new SendReceiv(i+1));
                    record.removeReceiver(new SendReceiv(i+1));
                    this.paths.set(j, record);
                }
            }
        }
    }
//    @Override
//    public void run(){
//        for(int i=0;i<6;i++){
//            this.numberNodes[i]=0;
//            this.pathLength[i]=0;
//        }
//        //starts the local computation first
//        LocalComputation[] threads=new LocalComputation[6];
//            
//        try{
//            for(int i=0;i<6;i++){
//                if(this.active_partitions[i]==true){
//                    //threads[i]=new LocalComputation(i+1,this.paths.get(i),this.dq.getUnivConstraints(),this.dbs[i]);
//                    threads[i].start();
//                    threads[i].join();
//                    this.result.addAll(threads[i].getFinal_result());                        
//                    this.optimzed_durations[i]=threads[i].getDuration();
//                    this.numberNodes[i]=this.numberNodes[i]+threads[i].getNumberNodes();
//                     if(threads[i].getPathLength()>this.pathLength[i])this.pathLength[i]=threads[i].getPathLength();                         
//                }
//                else this.optimzed_durations[i]=Duration.ZERO;                    
//                System.out.println("Local Computation "+(i+1));
//             }
//        }
//        catch(Exception e){                
//        }
//        DistributedQueryResult dqr;
//        ArrayList<DistributedQueryResult> dqrs=new ArrayList<DistributedQueryResult>();
//        SourcesVirtualComputation[] threads1=new SourcesVirtualComputation[6];
//        VirtualTargetsComputation[] threads2=new VirtualTargetsComputation[6];
//        VirtualVirtualComputation[] threads3=new VirtualVirtualComputation[6];
//        int current=0;
//        HashSet<Integer> updateSend,updateReceiv=new HashSet<Integer>();
//        Iterator it;
//        SendReceiv sd;
//        for(int i=0;i<6;i++){
//            if(this.active_partitions[i]==true)this.finished[i]=false;
//            else this.finished[i]=true;
//            this.src_virt_result.add(new ArrayList<DistributedQueryResult>());
//            this.virt_tgt_result.add(new ArrayList<DistributedQueryResult>());
//            this.virt_virt_result.add(new ArrayList<DistributedQueryResult>());
//        }
//        
//        //while condition$
//        while(this.end()==false){
//            //compute the execution cost of each active partition (slave)
//            this.cost();
//            //chooses the slave that costs the less, let's say it's the slave "current"
//            current=50000;
//            for(int i=0;i<6;i++){
//                if(this.finished[i]==false&&this.slaveCost[i]<current)current=i;
//            }
//            //runs the threads of "current"
//            updateSend=new HashSet<Integer>();
//            updateReceiv=new HashSet<Integer>();
//            try{
//                //threads1[current]=new SourcesVirtualComputation(current+1,this.paths.get(current),this.dq.getUnivConstraints(),this.dbs[current]/*,this.virtualEdges*/);
//                threads1[current].start();
//                threads1[current].join();
//                this.optimzed_durations[current]=this.optimzed_durations[current].plus(threads1[current].getDuration());
//                this.numberNodes[current]=this.numberNodes[current]+threads1[current].getNumberNodes();
//                if(threads1[current].getPathLength()>this.pathLength[current])this.pathLength[current]=threads1[current].getPathLength();
//                this.src_virt_result.set(current, threads1[current].getInterm_result());
//                dqrs=threads1[current].getInterm_result();
//                it=dqrs.iterator();
//                while(it.hasNext()){
//                    dqr=(DistributedQueryResult)it.next();
//                    updateReceiv.add(dqr.getTarget().getId());
//                } 
//                System.out.println("Source Virtual COmputation "+(current+1));
//                
//                //threads2[current]=new VirtualTargetsComputation(current+1,this.paths.get(current),this.dq.getUnivConstraints(),this.dbs[current]/*,this.virtualEdges*/);
//                threads2[current].start();
//                threads2[current].join();
//                this.optimzed_durations[current]=this.optimzed_durations[current].plus(threads2[current].getDuration());
//                this.numberNodes[current]=this.numberNodes[current]+threads2[current].getNumberNodes();
//                if(threads2[current].getPathLength()>this.pathLength[current])this.pathLength[current]=threads2[current].getPathLength();
//                this.virt_tgt_result.set(current, threads2[current].getFinal_result());
//                dqrs=threads2[current].getFinal_result();
//                it=dqrs.iterator();
//                while(it.hasNext()){
//                    dqr=(DistributedQueryResult)it.next();
//                    updateSend.add(dqr.getSource().getId());
//                }                 
//                System.out.println("Virtual Target COmputation "+(current+1));
//                
//                //threads3[current]=new VirtualVirtualComputation(current+1,this.paths.get(current),this.dq.getUnivConstraints(),this.dbs[current]/*,this.virtualEdges*/);
//                threads3[current].start();
//                threads3[current].join();
//                this.optimzed_durations[current]=this.optimzed_durations[current].plus(threads3[current].getDuration());
//                this.virt_virt_result.set(current, threads3[current].getDqr());
//                this.numberNodes[current]=this.numberNodes[current]+threads3[current].getNumberNodes();
//                if(threads3[current].getPathLength()>this.pathLength[current])this.pathLength[current]=threads3[current].getPathLength();
//                dqrs=threads3[current].getDqr();
//                it=dqrs.iterator();
//                while(it.hasNext()){
//                    dqr=(DistributedQueryResult)it.next();
//                    updateReceiv.add(dqr.getTarget().getId());
//                }
//                it=dqrs.iterator();
//                while(it.hasNext()){
//                    dqr=(DistributedQueryResult)it.next();
//                    updateSend.add(dqr.getSource().getId());
//                }
//                System.out.println("Virtual Virtual COmputation "+(current+1));
//            }
//            catch(Exception e){                
//            }
//            //updateSend contains the nodes that are targets of virtual edges and that should be kept nodes belong to current
//            //updateReceiv contains the nodes that are sources of virtual edges and that should be kept nodes belong to current
//            //update the tuples
//            HashSet<Integer> x;
//            it=updateSend.iterator();
//            x=new HashSet<Integer>();
//            while(it.hasNext()){
//                x.addAll(this.virtualEdges.getSources((int)it.next()));
//            }
//            //access to the senders of current partition and update
//            //x contains the sources of virtual edges from predeccessors of current
//            //these sources participate in the fina results
//            it=this.paths.get(current).getSend().iterator();
//            while(it.hasNext()){
//                sd=(SendReceiv)it.next();                
//                this.deleteReceiv(sd.getNumPartition(), current+1, x);
//                //this.delete(sd.getNumPartition(), current+1, 0, x, null);                
//            }
//            
//            it=updateReceiv.iterator();
//            x=new HashSet<Integer>();
//            while(it.hasNext()){
//                x.addAll(this.virtualEdges.getTargets((int)it.next()));            
//            }
//            //access to the receivers of current partition and update
//            //x contains the targets of virtual edges from successors of current
//            //these targets participate in final results
//            it=this.paths.get(current).getReceiv().iterator();
//            while(it.hasNext()){
//                sd=(SendReceiv)it.next();
//                //this.delete(sd.getNumPartition(), 0, current+1, null, x);
//                this.deleteSend(sd.getNumPartition(), current+1, x);
//            }
//            this.finished[current]=true;
//            this.total_duration=this.total_duration.plus(this.optimzed_durations[current]);
//        }
//        this.filterParallel();
//        System.out.println("Everything is perfect!!!!");
//        //displays the result
//        it=this.result.iterator();
//        while(it.hasNext()){
//            dqr=(DistributedQueryResult)it.next();
//            //dqr.println();
//            System.out.println("%%%"+dqr.getSource().getId()+"  "+dqr.getTarget().getId());
//        }        
//        //displays the durations
//        System.out.println("% "+this.result.size());
//        this.virtualEdges.println();
//        for(int i=0;i<6;i++){
//            System.out.println("@ "+this.optimzed_durations[i].toMillis());
//        }
//        System.out.println("!"+this.total_duration.toMillis());
//        System.out.print("#");
//        for(int i=0;i<6;i++)System.out.print(" "+this.numberNodes[i]);
//        System.out.println();
//        System.out.print("?");
//        for(int i=0;i<6;i++)System.out.print(" "+this.pathLength[i]);
//    }
    
    public HashSet<DistributedQueryResult> getResult() {
        return result;
    }

    public VirtualEdges getVirtualEdges() {
        return virtualEdges;
    }
    public void initDb(){
        try{
           for (int i=1;i<7;i++){
               dbs[i-1]=new DBConnect();
               dbs[i-1].connect("/data/delab/amina/NEO4J/NEO4J_"+i+"/data/graph.db");
           } 
        }
        catch(Exception e){
            System.out.println("reachability.distributedReachability.slaveThreads.Master.initDb "+e.getMessage());
        }
    }
    
    public void disconnectDb(){
        for(int i=1;i<7;i++){
            dbs[i-1].disconnect();
        }
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
    
    public void filterParallel(){
        //the set A contains the couple (s,v)
        //the set B contains the couple (v , v)
        //the set C contains the couple (v , t)
        HashSet<DistributedQueryResult> dqr1=new HashSet<DistributedQueryResult>();
        HashSet<DistributedQueryResult> dqr2=new HashSet<DistributedQueryResult>();
        HashSet<DistributedQueryResult> temp=new HashSet<DistributedQueryResult>();
        ConnectingPaths path;

        for(int i=0;i<6;i++){
            if(this.active_partitions[i]==true){
                path=this.paths.get(i);
                //no sender here
                if(path.getSend()!=null&&!path.getSend().isEmpty()){
                    temp.clear();
                    temp.addAll(this.virt_tgt_result.get(i));
                    this.result.addAll(this.virtualEdges.construct(dqr1, temp));                    
                }
                if(path.getSend()!=null&&!path.getSend().isEmpty()&&
                        path.getReceiv()!=null&&!path.getReceiv().isEmpty()){
                    temp.clear();
                    temp.addAll(this.virt_virt_result.get(i));
                    if(i==1&&this.active_partitions[2]==true){
                        dqr2.clear();
                        dqr2.addAll(dqr1);
                        dqr1=this.virtualEdges.construct(dqr1,temp );
                    }
                    else if(i==2&&this.active_partitions[1]==true){
                        if(dqr1==null&&dqr1.isEmpty())dqr1=this.virtualEdges.construct(dqr2,temp );
                        else dqr1.addAll(this.virtualEdges.construct(dqr2,temp ));
                    }
                    else if(i==3&&this.active_partitions[5]==true){
                        dqr2.clear();
                        dqr2.addAll(dqr1);
                        dqr1=this.virtualEdges.construct(dqr1,temp );
                    }
                    else if(i==5&&this.active_partitions[5]==true){
                        if(dqr1==null&&dqr1.isEmpty())dqr1=this.virtualEdges.construct(dqr2,temp );
                        else dqr1.addAll(this.virtualEdges.construct(dqr2,temp ));
                    }
                    else if(i==4&&this.active_partitions[5]==true){
                        dqr2.clear();
                        dqr2.addAll(dqr1);
                        dqr1=this.virtualEdges.construct(dqr1,temp );
                    }
                    else if(i==5&&this.active_partitions[5]==true){
                        if(dqr1==null&&dqr1.isEmpty())dqr1=this.virtualEdges.construct(dqr2,temp );
                        else dqr1.addAll(this.virtualEdges.construct(dqr2,temp ));
                    }
                    else dqr1=this.virtualEdges.construct(dqr1,temp );
                }
                if(path.getReceiv()!=null&&!path.getReceiv().isEmpty()){
                    dqr1.addAll(this.src_virt_result.get(i));
                }
            }
        }
        
       /* HashMap<Integer, HashSet<Integer>> src_virt_res;
        //two sets of DistributedQueryResult because each slave has at max two nodes
        HashSet<DistributedQueryResult> temp_dqr1=new HashSet<DistributedQueryResult>();
        HashSet<DistributedQueryResult> temp_dqr2=new HashSet<DistributedQueryResult>();
        SendReceiv send,receiv;
        Iterator it;
        HashSet<SendReceiv> receivers;
        int r1,r2;
        ConnectingPaths path;
        
        for(int i=0;i<6;i++){
            if(this.active_partitions[i]==true){
                path=(ConnectingPaths)this.paths.get(i);
                if(path.getSend()==null
                        ||path.getSend().isEmpty()){
                    // no senders here
                    int j=i+1;
                    while(j<6 && (this.src_virt_result[i].getCouples(j)==null
                            ||this.src_virt_result[i].getCouples(j).isEmpty()))                        
                        j++;
                    if(j<6)temp_dqr1=this.src_virt_result[i].getCouples(j).
                    
                }
            }
        }*/
    }
    
    private void cost(){
        ConnectingPaths path;
        Iterator it=this.paths.iterator();
        int num_in, num_out;
        Iterator itSendReceiv;
        SendReceiv sr;
        int i=0;
        while(it.hasNext()){
            path=(ConnectingPaths)it.next();
            if(this.finished[i]==false){                
                num_in=path.getSrcs().size();
                num_out=path.getTgts().size();
                itSendReceiv=path.getSend().iterator();            
                while(itSendReceiv.hasNext()){
                    sr=(SendReceiv)itSendReceiv.next();
                    num_in=num_in+sr.getVirtualNodes().size();
                }
                itSendReceiv=path.getReceiv().iterator();
                while(itSendReceiv.hasNext()){
                    sr=(SendReceiv)itSendReceiv.next();
                    num_out=num_out+sr.getVirtualNodes().size();
                }
                if(path.getSend().isEmpty()&&path.getReceiv().isEmpty())this.slaveCost[i]=0;
                else this.slaveCost[i]=num_in*num_out;
            }
            i++;
        }
        //Arrays.sort(this.slaveCost, 0, 6);
    }
    
    /**
     * in the path with the partition number "index", change in the receiv partition 
     * keeps only the nodes belonging to srcs
     * @param i partition number to update
     * @param receiv receiver partition number to update in the partition above(index)
     * @param srcs nodes that should be kept in the senders
     */
//    private void deleteReceiv(int i,int receiv, HashSet<Integer> srcs){
//        ConnectingPaths path;
//        SendReceiv rv;
//        Iterator its;
//        HashSet<Integer> temp=new HashSet<Integer>();        
//       
//        if(receiv>0){
//            path=this.paths.get(i-1);
//            its=path.getReceiv().iterator();             
//            while(its.hasNext()){
//                rv=(SendReceiv)its.next();
//                if(rv.getNumPartition()==receiv){                    
//                    temp=rv.getVirtualNodes();
//                    temp.removeAll(srcs);                    
//                    //now temp contains the element that should not be kept
//                    if(i!=4&&i!=1&&i!=3/*||this.finished[0]==false&&this.finished[2]==false||this.finished[0]==false&&this.finished[3]==false||this.finished[2]==false&&this.finished[3]==false*/){                        
//                        HashSet<Integer> tempo=rv.getVirtualNodes();
//                        tempo.removeAll(temp);
//                        path.removeReceiver(rv);
//                        rv=new SendReceiv(receiv);
//                        rv.setVirtualNodes(tempo);                        
//                        path.addReceiver(rv);
//                        this.paths.set(i-1, path);                        
//                    }
//                }
//            }
//        }
//    }    
    
    /**
     * in the path with the partition number "index", change in the send partition (respect. 
     * receiv partition) keeps only the nodes belonging to srcs (respect tgts)
     * @param index partition number to update
     * @param send sender partition number to update in the partition above(index)
     * @param receiv receiver partition number to update in the partition above(index)
     * @param srcs nodes that should be kept in the senders
     * @param tgts nodes that should be kept in the receivers
     */
//    private void deleteSend(int j,int send, HashSet<Integer> tgts){
//        ConnectingPaths path;
//        SendReceiv sd;
//        Iterator its;
//        int x;
//        HashSet<Integer> temp=new HashSet<Integer>();
//        
//        if(send>0){
//            path=this.paths.get(j-1);
//            its=path.getSend().iterator();           
//            while(its.hasNext()){
//                sd=(SendReceiv)its.next();
//                if(sd.getNumPartition()==send){                    
//                    temp=sd.getVirtualNodes();
//                    temp.removeAll(tgts);
//                    //now temp contains the element that should not be kept
//                    if(j!=4&&j!=6/*||this.finished[3]==false&&this.finished[5]==false*/){                        
//                        HashSet<Integer> tempo=sd.getVirtualNodes();
//                        tempo.removeAll(temp);
//                        path.removeSender(sd);
//                        sd=new SendReceiv(send);
//                        sd.setVirtualNodes(tempo);                        
//                        path.addSender(sd);
//                        this.paths.set(j-1, path);                        
//                    }
//                }
//            }
//        }
//    }
    
    /**
     * if end=false, it means that there is still a processing on a slave
     * @return 
     */
    private boolean end(){
        for(int i=0;i<6;i++){
            if(this.finished[i]==false)return false;
        }
        return true;
    }
}
