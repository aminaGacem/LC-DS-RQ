/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reachability.distributedReachability;
import java.util.Iterator;
import graphs.Node;
import java.util.HashSet;

/**
 *
 * @author amina
 */
public class DistributedQuery {
    private HashSet<Node> sources=new HashSet<Node>();
    private HashSet<Node> targets=new HashSet<Node>();
    private HashSet<String> constraintsUniv=new HashSet<String>();
    private HashSet<String> constraintsExist=new HashSet<String>();

    public HashSet<Node> getSources() {
        return sources;
    }

    public void setSources(HashSet<Node> sources) {
        this.sources = sources;
    }

    public HashSet<Node> getTargets() {
        return targets;
    }

    public void setTargets(HashSet<Node> targets) {
        this.targets = targets;
    }    
    
    /**
     * merges the sources of q with the sources of invoking object, and the targets of
     * q with the targets of invoking objects
     * @param q 
     */
    
    public void merge(DistributedQuery q){
        HashSet<Node> nodes;
        Node node;
        Iterator it;
        it=q.getSources().iterator();
        while(it.hasNext()){
            node=(Node)it.next();
            this.addSource(node);
        }
        it=q.getTargets().iterator();
        while(it.hasNext()){
            node=(Node)it.next();
            this.addTarget(node);
        }
    }
    
    public void addSource(Node n){
        this.sources.add(n);
    }
    
    public void addTarget(Node n){
        this.targets.add(n);
    }
    
    public boolean addUnivConstraint(String cons){
        return constraintsUniv.add(cons);
    }
    
    public void addExistConstraint(String cons){
        this.constraintsExist.add(cons);
    }    
    
    public void println(){
        Iterator it;        
        Node n;
        it=this.sources.iterator();
        System.out.println(" Distributed Query Sources:");
        while(it.hasNext()){
            n=(Node)it.next();
            n.println();
        }
        System.out.println(" Distributed Query Targets:");
        it=this.targets.iterator();
        while(it.hasNext()){
            n=(Node)it.next();
            n.println();
        }
    }

    public HashSet<String> getUnivConstraints() {
        return constraintsUniv;
    }
    
    public HashSet<String> getExistConstraints() {
        return constraintsExist;
    }    

    public void setUnivConstraints(HashSet<String> constraints) {
        this.constraintsUniv = constraints;
    }
    
    public void setExistConstraints(HashSet<String> constraints) {
        this.constraintsExist = constraints;
    }    
}
