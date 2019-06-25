/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reachability.distributedReachability;
import java.util.ArrayList;
import java.util.Iterator;
import graphs.Node;
import reachability.Query;
import reachability.LocalReachability;
import Network.Slave;
import Network.Cluster_Configuration;
import database.DBConnect;
import java.util.HashMap;
import java.time.Duration;

/**
 *
 * @author amina
 */
public class SlaveThread extends Thread{
    /**
     * msg are the partial results that will be sent to other slaves
     */
    private ArrayList<DistributedQueryResult> result=new ArrayList<DistributedQueryResult>();
    /**
     * q stores the sources and targets submitted to the slave
     */
    private DistributedQuery q;
    /**
     * index of the slave
     */
    private int slaveid;

    private Cluster_Configuration cc;
    /**
     * the intermediate results to exchange
     */
    private HashMap<Integer, ArrayList<DistributedQueryResult>> virtualNodes=new HashMap<Integer, ArrayList<DistributedQueryResult>>();
    
    private DBConnect db=new DBConnect();
    
    /**
     * a boolean value that determines whether this thread run to send or to receive
     */
    
    private boolean flag;
    
    private long start;
    private long end;
    private Duration duration;
    
    private long numberNodes;
    
    
    public SlaveThread(DBConnect db,Cluster_Configuration cc,DistributedQuery q, int slaveid, boolean flag){
        this.cc=cc;
        this.q=q;
        this.slaveid=slaveid;
        this.db=db;
        this.flag=flag;
    }
    
//    public void run(){
//        ArrayList<Node> local_rset=new ArrayList<Node>();
//        ArrayList<DistributedQueryResult> remote_rset=new ArrayList<DistributedQueryResult>();
//        Node n1,n2;
//        String nodeid;
//        Query q;
//        LocalReachability localreach;
//        Iterator ittarget;
//        boolean reachable;
//        if(this.flag==true){
//            this.numberNodes=0;
//            start=System.currentTimeMillis();
//            Iterator itsource=this.q.getSources().iterator();        
//            //step1 checks the local reachability between the ss and the ts
//            while(itsource.hasNext()){
//                n1=(Node)itsource.next();
//                ittarget=this.q.getTargets().iterator();
//                while(ittarget.hasNext()){
//                    n2=(Node)ittarget.next();
//                    localreach=new LocalReachability(this.db,new Query(n1,n2, new java.util.HashSet()));
//                    this.numberNodes=this.numberNodes+localreach.getResult().size();
//                    reachable=localreach.evaluateLocalReachability(/*"/home/gacem/LC-DS-RQ/neo4j-" + this.slaveid + "/data/graph.db"*/);
//                    if(reachable==true){
//                        local_rset.add(n2);
//                        result.add(new DistributedQueryResult(n1,n2));
//                    }
//                }
//            }
//        /*DistributedQueryResult dq;
//        Iterator iter=this.result.iterator();
//        System.out.println("checking "+this.slaveid);
//        while(iter.hasNext()){
//            dq=(DistributedQueryResult)iter.next();
//            dq.println();
//        }*/
//        //step1 checks the reachable nodes from ss
//        //at first collect every pair (source, reachable intermediate virtual node)
//        //put them into remote_rset
//        //divide the previous collections depending on the slave where is the reachable intermediate virtual node stored
//        //For that purpose we will use the following structure HashMap<Slave, ArrayList<DQR>>
//     
//        int i;
//        for(i=1;i<=this.cc.getSize();i++){
//            remote_rset=new ArrayList<DistributedQueryResult>();
//            if(i!=this.slaveid){
//                itsource=this.q.getSources().iterator();
//                while(itsource.hasNext()){
//                    n1=(Node)itsource.next();
//                    db.executeTransation("match (n{id:'"+n1.getId()+"'})-[*]->(m{slave:'"+i+"'}) return m.id");
//                    this.numberNodes=db.getResult().size()+this.numberNodes;
//                    Iterator itresult=db.getResult().iterator();
//                    while(itresult.hasNext()){
//                        nodeid=(String)itresult.next();
//                        //System.out.println("@@@@"+this.slaveid+" to "+i+" nodes "+n1.getId()+" "+nodeid);                           
//                        remote_rset.add(new DistributedQueryResult(n1,new Node(Integer.parseInt(nodeid))));                           
//                    }
//                }
//            }                
//            this.virtualNodes.put(i, remote_rset);
//        }
//            /*remote_rset=new ArrayList<DistributedQueryResult>();
//            Iterator iter;
//            DistributedQueryResult r;
//            for(Integer key:this.virtualNodes.keySet()){
//                remote_rset=this.virtualNodes.get(key);
//                iter=remote_rset.iterator();
//                while(iter.hasNext()){
//                    r=(DistributedQueryResult)iter.next();
//                    System.out.println("Slave "+this.slaveid+"produces virtual nodes: key "+key+" value "+r.getSource().getId()+" "+r.getTarget().getId());
//                    //r.println();
//                }
//            }*/
//        //step2
//        //send to each slave the corresponding pairs(source, reachable intermediate virtual node)    
//        end=System.currentTimeMillis();
//        this.duration=Duration.ofMillis(end-start);
//        }
//        else {
//            this.numberNodes=0;
//            //waits here ......            
//            //receives from each slave the pairs (source, reachable intermediate virtual node)
//            //builds a list of pairs(reachable intermediate virtual node, <List of sources>)
//            start=System.currentTimeMillis();
//            remote_rset=this.virtualNodes.get(this.slaveid);
//            //remote_set contains reachable nodes source intermediate result
//            Iterator it=remote_rset.iterator();
//            DistributedQueryResult dqr;
//            //it=remote_rset.iterator();            
//            local_rset=new ArrayList<Node>();
//            while(it.hasNext()){
//                dqr=(DistributedQueryResult)it.next();
//                if(!local_rset.contains(dqr.getTarget())){
//                    local_rset.add(dqr.getTarget());
//                    //System.out.println(this.slaveid+" AMINA!!!!!"+dqr.getTarget().getId());
//                }
//            }
//            //localrset contains the intermediate
//            //checks the reachability (intermediate virtual node, target)
//            it=local_rset.iterator();            
//            ArrayList<DistributedQueryResult> intermediate=new ArrayList<DistributedQueryResult>();
//            while(it.hasNext()){
//                n1=(Node)it.next();
//                ittarget=this.q.getTargets().iterator();
//                while(ittarget.hasNext()){
//                    n2=(Node)ittarget.next();                    
//                    localreach=new LocalReachability(this.db,new Query(n1,n2,new java.util.HashSet<String>()));
//                    if(n1.getId()==n2.getId())reachable=true;
//                    else reachable=localreach.evaluateLocalReachability();
//                    this.numberNodes=this.numberNodes+localreach.getResult().size();
//                    if(reachable==true){
//                        intermediate.add(new DistributedQueryResult(n1,n2));
//                        System.out.println(this.slaveid+" Reachability between intermediate and target "+n1.getId()+" "+n2.getId());
//                    }
//                }
//            }
//            //intermediate contains the reachable nodes (intermediate node, target)
//            ittarget=intermediate.iterator();            
//            DistributedQueryResult dqr1;
//            //for each reachability (intermediate virtual node, target), if it is true add to the result
//            while(ittarget.hasNext()){
//                dqr=(DistributedQueryResult)ittarget.next();
//                it=remote_rset.iterator();
//                while(it.hasNext()){
//                    dqr1=(DistributedQueryResult)it.next();
//                    if(dqr1.getTarget()==dqr.getSource())
//                        this.result.add(new DistributedQueryResult(dqr1.getSource(),dqr.getTarget()));                    
//                }
//            }
//            end=System.currentTimeMillis();
//            this.duration=Duration.ofMillis(end-start);
//        }
//        
//        
//            /*for(Integer key:this.virtualNodes.keySet()){                
//                ArrayList<DistributedQueryResult> value=this.virtualNodes.get(i);
//                DistributedQueryResult dqr;
//                it=value.iterator();
//                while(it.hasNext()){
//                    System.out.println("amina");
//                    dqr=(DistributedQueryResult)it.next();
//                    System.out.println("un résultat "+dqr.getSource()+" "+dqr.getTarget());
//                }
//            }*/
//            
//            
//            
//            /*HashMap<Node,ArrayList<Node>> intermediate=new HashMap<Node,ArrayList<Node>>();
//            //step3            
//            //checks the reachability (intermediate virtual node, target)
//            //if it is true, add the corresponding slaves with the specific target
//            itsource=local_rset.iterator();
//            while(itsource.hasNext()){
//                n1=(Node)itsource.next();
//                ittarget=this.q.getTargets().iterator();
//                while(ittarget.hasNext()){
//                    n2=(Node)ittarget.next();
//                    localreach=new LocalReachability(db,new Query(n1,n2));
//                    if(localreach.evaluateLocalReachability()){
//                        //the intermediate node is connected to the target
//                        //get the list of sources and add them to the result
//                        intermediate.get(n1);
//                    }
//                }
//            }*/       
//    }

    public ArrayList<DistributedQueryResult> getResult() {
        return result;
    }

    public HashMap<Integer, ArrayList<DistributedQueryResult>> getVirtualNodes() {
        return virtualNodes;
    }

    public void setVirtualNodes(HashMap<Integer, ArrayList<DistributedQueryResult>> virtualNodes) {
        this.virtualNodes = virtualNodes;
    }    

    public Duration getDuration() {
        return duration;
    }

    public long getNumberNodes() {
        return numberNodes;
    }
 
}
