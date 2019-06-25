/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reachability.distributedReachability.slaveThreads;

import database.DBConnect;
import java.time.Duration;
import java.util.HashSet;
import reachability.distributedReachability.ConnectingPaths;
import reachability.distributedReachability.DistributedQueryResult;
import java.util.ArrayList;

/**
 *
 * @author amina
 */
public class GenericSlave extends Thread{
    private int numPartition;
    private DBConnect db;
    private ConnectingPaths path;
    private HashSet<String> constraints=new HashSet<String>();
    private boolean local_partition;
    private boolean source_partition;
    private boolean target_partition;
    private boolean intermediate_partition;
    
    private Duration local_duration;
    private Duration src_virt_duration;
    private Duration virt_tgt__duration;
    private Duration virt_virt_duration;
    private Duration total_duration;

    private ArrayList<DistributedQueryResult> result=new ArrayList<DistributedQueryResult>();
    private ArrayList<DistributedQueryResult> src_virt_result=new ArrayList<DistributedQueryResult>();
    private ArrayList<DistributedQueryResult> virt_tgt_result=new ArrayList<DistributedQueryResult>();
    private ArrayList<DistributedQueryResult> virt_virt_result=new ArrayList<DistributedQueryResult>();

    public GenericSlave(int numPartition, DBConnect db,ConnectingPaths path, HashSet<String> constraints) {
        this.numPartition=numPartition;
        this.db=db;
        this.path=path;
        this.constraints=constraints;
    }
    
    public void setFlags(boolean local_partition,boolean source_partition,boolean target_partition,boolean intermediate_partition){
        this.local_partition=local_partition;
        this.source_partition=source_partition;
        this.target_partition=target_partition;
        this.intermediate_partition=intermediate_partition;
    }

    @Override
    public void run() {
       LocalComputation loc=new LocalComputation(numPartition, db,path, constraints);
       SourcesVirtualComputation src_virt=new SourcesVirtualComputation(numPartition, db,path, constraints);
       VirtualVirtualComputation vir_vir=new VirtualVirtualComputation(numPartition, db,path, constraints);
       VirtualTargetsComputation vir_tgt=new VirtualTargetsComputation(numPartition, db,path, constraints);       
       
       this.total_duration=Duration.ZERO;
       try{
           System.out.println("Generic Slave "+this.numPartition);
           if(this.local_partition){
              loc.start();
              loc.join();
              this.result.addAll(loc.getFinal_result());
              this.local_duration=loc.getDuration();
              this.total_duration.plus(this.local_duration);
              System.out.print(this.local_duration.toMillis());
              System.out.println(" Local Computation "+this.numPartition+" Duration "+this.local_duration.toMillis());
           }
           if(this.source_partition){
                src_virt.start();
                src_virt.join();
                this.src_virt_duration=src_virt.getDuration();
                this.src_virt_result=src_virt.getInterm_result();
                this.total_duration.plus(this.src_virt_duration);
                System.out.print(this.src_virt_duration.toMillis());
                System.out.println(" Source Virtual Computation "+this.numPartition+" Duration "+this.src_virt_duration.toMillis());
           }
           if(this.target_partition){
               vir_tgt.start();
               vir_tgt.join();
               this.virt_tgt__duration=vir_tgt.getDuration();
               this.virt_tgt_result=vir_tgt.getFinal_result();
               this.total_duration.plus(this.virt_tgt__duration);
               System.out.print(this.virt_tgt__duration.toMillis());
               System.out.println(" Virtual Target Computation "+this.numPartition+" Duration "+this.virt_tgt__duration.toMillis());
           }
           if(this.intermediate_partition){
                vir_vir.start();
                vir_vir.join();
                this.virt_virt_duration=vir_vir.getDuration();
                this.virt_virt_result=vir_vir.getDqr();           
                this.total_duration.plus(this.virt_virt_duration);
                System.out.print(this.virt_virt_duration.toMillis());
                System.out.println(" Virtual Virtual Computation "+this.numPartition+" Duration "+this.virt_virt_duration.toMillis()); 
            }
       }
       catch(Exception e){
           System.out.println("Error in reachability.distributedReachability.slaveThreads.GenericSlave "+e.getMessage());
       }       
    }  

    public ArrayList<DistributedQueryResult> getResult() {
        return result;
    }

    public Duration getLocal_duration() {
        return local_duration;
    }

    public Duration getSrc_virt_duration() {
        return src_virt_duration;
    }

    public Duration getVirt_virt_duration() {
        return virt_virt_duration;
    }

    public Duration getTotal_duration() {
        return total_duration;
    }

    public ArrayList<DistributedQueryResult> getSrc_virt_result() {
        return src_virt_result;
    }

    public ArrayList<DistributedQueryResult> getVirt_tgt_result() {
        return virt_tgt_result;
    }

    public ArrayList<DistributedQueryResult> getVirt_virt_result() {
        return virt_virt_result;
    }
}
