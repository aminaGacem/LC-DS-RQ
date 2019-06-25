/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Network;
import java.util.ArrayList;
import graphs.Node;
import graphs.Graph;
import java.util.Iterator;
/**
 * This class models the cluster configuration (slave, master and indexes
 * @author amina
 */
public class Cluster_Configuration {
    private ArrayList<Slave> slaves=new ArrayList<Slave>();//number of slaves of the cluster
    private Master master;
    /**
     * contains an index of boundary nodes
     */
    private Index masterIndex;
    /**
     * contains an index of all nodes
     */
    private Index globalIndex;
    private ArrayList<Index> slaveIndexes=new ArrayList<Index>();

    public Cluster_Configuration() {
    }
    
    /**
    * add a new slave to the cluster, i is the size of the new cluster.
    */
    public void add(Slave s){
       this.slaves.add(s);
    }
    
    public int getSize(){
        return this.slaves.size();
    }

    public ArrayList<Slave> getSlaves() {
        return slaves;
    }

    public Master getMaster() {
        return master;
    }

    public void setMaster(Master master) {
        this.master = master;
    }
    
    /**
     * returns the slave where the node n is stored, here we assume that a node is stored
     * at slave s, where s=n.id%4
     * @param n
     * @return 
     */
    
    public Graph graphAtSlave(Slave s){
        Iterator it=this.slaves.iterator();
        while(it.hasNext()){
            if(s.equals((Slave)it.next()))return s.getG();
        }
        return null;//throws an exception here
    }

    public void setSlaves(ArrayList<Slave> slaves) {
        this.slaves = slaves;
    }

    public Index getMasterIndex() {
        return masterIndex;
    }

    public void setMasterIndex(Index masterIndex) {
        this.masterIndex = masterIndex;
    }

    public ArrayList<Index> getSlaveIndexes() {
        return slaveIndexes;
    }
    
    public void addSlaveIndex(Index index){
        this.slaveIndexes.add(index);
    }    

    public Index getGlobalIndex() {
        return globalIndex;
    }

    public void setGlobalIndex(Index globalIndex) {
        this.globalIndex = globalIndex;
    }
    
    
    
}
