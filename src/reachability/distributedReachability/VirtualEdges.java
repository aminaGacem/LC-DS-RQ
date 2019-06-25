/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reachability.distributedReachability;
import graphs.DirectedEdge;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashSet;
import graphs.Node;

/**
 *
 * @author amina
 */
public class VirtualEdges {
    private ArrayList<DirectedEdge> virtual=new ArrayList<DirectedEdge>();
    
    public void addVirtualEdge(DirectedEdge e){
        this.virtual.add(e);
    }
    
    public ArrayList<String> getTargets(String source){
        ArrayList<String> tgt=new ArrayList<String>();
        Iterator it;
        it=this.virtual.iterator();
        DirectedEdge e;
        while(it.hasNext()){
            e=(DirectedEdge)it.next();
            if(e.getSource().getId().compareTo(source)==0)tgt.add(e.getTarget().getId());
        }
        return tgt;
    }
    
    public ArrayList<String> getSources(String target){
        ArrayList<String> src=new ArrayList<String>();
        Iterator it;
        it=this.virtual.iterator();
        DirectedEdge e;
        while(it.hasNext()){
            e=(DirectedEdge)it.next();
            if(e.getTarget().getId().compareTo(target)==0)src.add(e.getSource().getId());
        }
        return src;
    }
    
    public void println(){
        DirectedEdge e;
        Iterator it;
        it=this.virtual.iterator();
        System.out.println("[Virtual Edges "+this.virtual.size()+")");
        while(it.hasNext()){
            e=(DirectedEdge)it.next();
            //System.out.print("("+e.getSource().getId()+" "+e.getTarget().getId()+" "+e.getLabel()+")");
        }
    }
    
    /**
     * constructs a path made up of dqr2 following dqr1 whenever it is possible
     * @param dqr1 (s,v1)
     * @param dqr2 (v2, t)
     * if v1==v2 we obtain the path (s, t)
     * @return 
     */
    
    public HashSet<DistributedQueryResult> construct(HashSet<DistributedQueryResult> dqr1, HashSet<DistributedQueryResult> dqr2){
        DistributedQueryResult element1,element2;
        
        HashSet<DistributedQueryResult> dqr=new HashSet<DistributedQueryResult>();
        Iterator it1,it2;
        //in dqr1 we have the couples s v1
        //in dqr 2 we the couples v2 t
        Node s,v1,v2,t;
        if(dqr1.isEmpty()||dqr2.isEmpty())return dqr;
        dqr1=this.mergeWithSources(dqr1);
        it1=dqr1.iterator();
        while(it1.hasNext()){
            element1=(DistributedQueryResult) it1.next();
            s=element1.getSource();
            v1=element1.getTarget();
            it2=dqr2.iterator();
            while(it2.hasNext()){
                element2=(DistributedQueryResult)it2.next();
                v2=element2.getSource();
                t=element2.getTarget();
                if(v1.equals(v2))
                    dqr.add(new DistributedQueryResult(s, t));
            }
        }
        
        return dqr;
    }
    /**
     * merges the path of the parameters with the sources of virtual edges
     * @param dqr
     * @return 
     */
    private HashSet<DistributedQueryResult> mergeWithSources(HashSet<DistributedQueryResult> dqr){
        HashSet<DistributedQueryResult> res=new HashSet<DistributedQueryResult>();
        HashSet<String> temp;
        Iterator it;
        it=dqr.iterator();
        DistributedQueryResult q;
        while(it.hasNext()){
            q=(DistributedQueryResult)it.next();
            //q contains (s, v1)
            temp=new HashSet<String>();
            String tgt;
            //all the nodes v2 connected to v1 
            temp.addAll(this.getTargets(q.getTarget().getId()));
            Iterator it_temp=temp.iterator();
            while(it_temp.hasNext()){
                tgt=(String)it_temp.next();
                res.add(new DistributedQueryResult(q.getSource(),new Node(tgt)));
            }
        }        
        return res;
    }
    /**
     * deletes all the virtual edges that have src as a source
     * @param src 
     */
    public void deleteSource(Node src){
        Iterator it;
        DirectedEdge de;
        it=this.virtual.iterator();
        while(it.hasNext()){
            de=(DirectedEdge)it.next();
            if(de.getSource().equals(src))this.virtual.remove(de);
        }
    }
    /**
     * deletes all the virtual edges that have tgt as a target
     * @param tgt 
     */
    public void deleteTarget(Node tgt){
        Iterator it;
        DirectedEdge de;
        it=this.virtual.iterator();
        while(it.hasNext()){
            de=(DirectedEdge)it.next();
            if(de.getTarget().equals(tgt))this.virtual.remove(de);
        }        
    }
}
