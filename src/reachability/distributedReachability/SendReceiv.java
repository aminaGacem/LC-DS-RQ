/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reachability.distributedReachability;
import java.util.HashSet;

/**
 *
 * @author amina
 */
public class SendReceiv {
    private int numPartition;
    private HashSet virtualNodes=new HashSet<String>();

    public SendReceiv(int numPartition) {
        this.numPartition = numPartition;
    }
    
    public void addVirtualNode(String n){
        this.virtualNodes.add(n);
    }

    public void setVirtualNodes(HashSet<String> virtualNodes) {
        this.virtualNodes = virtualNodes;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + this.numPartition;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SendReceiv other = (SendReceiv) obj;
        if (this.numPartition != other.numPartition) {
            return false;
        }
        return true;
    }

    public int getNumPartition() {
        return numPartition;
    }  

    public HashSet<String> getVirtualNodes() {
        return virtualNodes;
    }
    /**
     * delete the virtual node n if it exists
     * @param n 
     */
    public void deleteNode(String n){
        this.virtualNodes.remove(n);
    }
  
}
