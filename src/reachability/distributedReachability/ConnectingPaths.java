/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reachability.distributedReachability;
import java.util.HashSet;
import graphs.Node;
import java.util.Iterator;

/**
 * Represents the paths of the boundary graph to recollect the distributed results
 * @author amina
 */
public class ConnectingPaths {   
    private HashSet<String> srcs=new HashSet<String>();
    private HashSet<String> tgts=new HashSet<String>();
    private HashSet<SendReceiv> send=new HashSet<SendReceiv>();
    private HashSet<SendReceiv> receiv=new HashSet<SendReceiv>();
    
    public void addSource(String n){
        this.srcs.add(n);
    }
    
    public void addTarget(String n){
        this.tgts.add(n);
    }
    
    /**
     * create a sender if it does not already exist
     */
    
    public void addSender(SendReceiv send){
        this.send.add(send);
    }
    
    /**
     * create a receiver if it does not already exist
     */
    
    public void addReceiver(SendReceiv receiv){
        this.receiv.add(receiv);
    }
    
     /**
     * add a virtual node into the sender if it does not already exist
     */
    
    public void addVirtualToSender(int send, String id_node){
        Iterator it;
        SendReceiv record;
        boolean found=false;
        it=this.send.iterator();
        while(it.hasNext()&&found==false){
            record=(SendReceiv)it.next();
            if(record.getNumPartition()==send){
                record.addVirtualNode(id_node);
                found=true;
            }
        }
    }
    
     /**
     * add a virtual node into the receiver if it does not already exist
     */
    
    public void addVirtualToReceiver(int receiv, String id_node){
        Iterator it;
        SendReceiv record;
        boolean found=false;
        it=this.receiv.iterator();
        while(it.hasNext()&&found==false){
            record=(SendReceiv)it.next();
            if(record.getNumPartition()==receiv){
                record.addVirtualNode(id_node);
                found=true;
            }
        }
    }
    
    public void removeSender(SendReceiv send){
        this.send.remove(send);
    }
    
    public void removeReceiver(SendReceiv receiv){
        this.receiv.remove(receiv);
    }

    public HashSet<String> getSrcs() {
        return srcs;
    }

    public HashSet<String> getTgts() {
        return tgts;
    }

    public HashSet<SendReceiv> getSend() {
        return send;
    }

    public HashSet<SendReceiv> getReceiv() {
        return receiv;
    }
    
}
