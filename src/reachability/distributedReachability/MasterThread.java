/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reachability.distributedReachability;
import java.util.ArrayList;
import Network.*;
import java.util.Iterator;
import graphs.Node;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;
import database.DBConnect;
import java.util.HashMap;
import java.time.Duration;

/**
 *
 * @author amina
 */
public class MasterThread extends Thread{
    private DistributedQuery q;
    private ArrayList<DistributedQuery> partitionedDistributedQuery=new ArrayList<DistributedQuery>();
    private Cluster_Configuration cc;
    private ArrayList<DistributedQueryResult> result=new ArrayList<DistributedQueryResult>();
    private Index globalIndex=new Index();
    private ArrayList<DBConnect> dbs=new ArrayList<DBConnect>();
    private HashMap<Integer, ArrayList<DistributedQueryResult>> intermediateResult=new HashMap<Integer, ArrayList<DistributedQueryResult>>();
    private Duration duration;
    private long start;
    private long end;
    private Duration[] durations=new Duration[4];
    private long[] numberNodes=new long[4];
    
    
    public MasterThread(Cluster_Configuration cc,Index globalIndex){
        this.cc=cc;
        this.globalIndex=globalIndex;
    }
    
    public void addDBConnect(DBConnect db){
        this.dbs.add(db);
    }
    
//    public void run(){
//        this.partition();
//        Iterator it=this.cc.getSlaves().iterator();
//        Slave slave;
//        SlaveThread s;
//        int i=0;
//        ArrayList<DistributedQueryResult> remote_rset=new ArrayList<DistributedQueryResult>();
//        SlaveThread[] threads=new SlaveThread[4];
//        start=System.currentTimeMillis();
//        while(it.hasNext()){
//            slave=(Slave)it.next();
//            threads[i]=new SlaveThread(dbs.get(i),this.cc,this.partitionedDistributedQuery.get(i),slave.getIndex(),true);
//            i++;
//        }
//        for(i=0;i<4;i++){
//            threads[i].start();
//        }
//        try{
//            for(i=0;i<4;i++){
//                threads[i].join();
//                s=threads[i];
//                this.durations[i]=s.getDuration();
//                this.numberNodes[i]=s.getNumberNodes();
//                this.result.addAll(s.getResult());
//                for(Integer key: s.getVirtualNodes().keySet()){
//                    remote_rset=s.getVirtualNodes().get(key);
//                    if(this.intermediateResult.containsKey(key)){                        
//                        this.intermediateResult.get(key).addAll(remote_rset);
//                        //check if it is correctly updates
//                    }
//                    else this.intermediateResult.put(key, remote_rset);                   
//                }
//                //this.result.addAll(s.getResult());                
//            }
//            it=this.cc.getSlaves().iterator();
//            i=0;
//            while(it.hasNext()){
//                //create a thread here with flag== false + setVirtualMethod depending on the value of slave
//                //start every thread
//                slave=(Slave)it.next();
//                threads[i]=new SlaveThread(dbs.get(i),this.cc,this.partitionedDistributedQuery.get(i),slave.getIndex(),false);
//                threads[i].setVirtualNodes(intermediateResult);
//                //threads[i].start();
//                i++;
//            }
//            for(i=0;i<4;i++){
//                threads[i].start();
//            }
//            //this.result=new ArrayList<DistributedQueryResult>();
//            for(i=0;i<4;i++){
//                threads[i].join();
//                this.durations[i]=threads[i].getDuration().plus(this.durations[i]);
//                this.numberNodes[i]=threads[i].getNumberNodes()+this.numberNodes[i];
//                this.result.addAll(threads[i].getResult());
//            }
//            System.out.println("Final Results");
//            DistributedQueryResult dqr;
//            it=this.result.iterator();
//            while(it.hasNext()){
//                dqr=(DistributedQueryResult)it.next();
//                dqr.println();
//            }
//            /*for(Integer key: this.intermediateResult.keySet()){
//                System.out.println("Key "+key);
//                it=this.intermediateResult.get(key).iterator();
//                while(it.hasNext()){
//                    dqr=(DistributedQueryResult)it.next();
//                    dqr.println();
//                }
//            }*/
//            end=System.currentTimeMillis();           
//            this.duration=Duration.ofMillis(end-start);
//            //prints the line that will be output in a file
//            System.out.println("# "+start+" "+end+" "+this.duration.toMillis()
//            +" "+this.durations[0].toMillis()+","+this.durations[1].toMillis()+","+this.durations[2].toMillis()+","+this.durations[3].toMillis()
//            +" "+this.numberNodes[0]+","+this.numberNodes[1]+","+this.numberNodes[2]+","+this.numberNodes[3]
//            );
//            //gets the duration per slave
//            //gets the number of node per slave
//        }
//        catch(Exception e){
//            
//        }
//    }
    
//    private void partition(){
//        ArrayList<DistributedQuery> tab;
//        String line;
//        StringTokenizer st;
//        try{
//            /*BufferedReader buffer=new BufferedReader(new FileReader("/home/gacem/LC-DS-RQ/NEO4J_HOME/GlobalIndex.ind"));
//            line = buffer.readLine();            
//            while (line != null){
//                st=new StringTokenizer(line,"@");
//                this.globalIndex.addIndexRecord(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
//                line=buffer.readLine();
//            }
//            buffer.close();*/
//            int i;
//            Iterator it;
//            Node n;
//            DistributedQuery intermediate;
//            for(i=1;i<=this.cc.getSlaves().size();i++){
//                intermediate=new DistributedQuery();
//                it=this.q.getSources().iterator();
//                while(it.hasNext()){
//                    n=(Node)it.next();
//                    if(this.globalIndex.getIdSlave(n.getId())==i){
//                        intermediate.addSource(n);
//                    }
//                }
//                this.partitionedDistributedQuery.add(intermediate);
//            }
//            for(i=1;i<=this.cc.getSlaves().size();i++){
//                intermediate=(DistributedQuery)this.partitionedDistributedQuery.get(i-1);
//                it=this.q.getTargets().iterator();
//                while(it.hasNext()){
//                    n=(Node)it.next();
//                    if(this.globalIndex.getIdSlave(n.getId())==i){
//                        intermediate.addTarget(n);
//                    }
//                }
//                this.partitionedDistributedQuery.set(i-1, intermediate);
//            }
//            /*it=this.partitionedDistributedQuery.iterator();
//            DistributedQuery dq;
//            while(it.hasNext()){
//                dq=(DistributedQuery)it.next();
//                dq.println();
//            }*/
//        }        
//        catch(Exception en){
//            
//        }
//    }

    public void setQ(DistributedQuery q) {
        this.q = q;
    }
    
    
}
