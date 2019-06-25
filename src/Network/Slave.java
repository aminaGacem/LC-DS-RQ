/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Network;

import graphs.DirectedEdge;
import graphs.Graph;
import graphs.Node;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.BufferedWriter;
import java.io.FileWriter;
import graphs.Property;
import java.io.IOException;
/**
 *
 * @author amina
 */
public class Slave {
    private int size;
    private int index;
    private Graph g;
    private ArrayList<Node> forwardlist=new ArrayList<Node>();
    private ArrayList<Node> backwardlist=new ArrayList<Node>();
    

    public Slave(int size,int index) {
        this.size = size;
        this.index=index;
    }

    public void setG(Graph g) {
        this.g = g;
    }

    public Graph getG() {
        return g;
    }
    
    /**
     * checks whether a node n is stored at the slave and returns true if so
     * it works for a cluster configuration of 4 slaves where nodes are partitioned randomly as follows
     * nodes with id=4k+n are stored at slave 
     * @param n
     * @return 
     */
    
    /*public boolean isStored(Node n){
        //if(Integer.parseInt(n.getId())>265214*this.index/4
                //&& Integer.parseInt(n.getId())<265214*(this.index+1)/4)return true;
        if(n.getId()==this.index%4)return true;
        return false;
    }*/
    
    public boolean equals(Object e){
        Slave s;
        s=(Slave)e;
        if(s.getIndex()==this.index)return true;
        else return false;
    }

    public int getIndex() {
        return index;
    }
    
    /**
     * returns all the forward list
     * @return 
     */

    public ArrayList<Node> getForwardlist() {
        return forwardlist;
    }
    
        
    /**
     * generates the forward list
     */
    
    /*public void generateForwardlist() {
        Iterator it=this.g.getVertices().iterator();
        Node n;
        while(it.hasNext()){
            n=(Node)it.next();
            if(n.getId()%4!=this.index)this.forwardlist.add(n);
        }
    }*/

    /**
     * returns forwardlist of nodes that are stored in another partition s
     * @param s a slave partition
     * @return subset of forwardlist
     */
    /*public ArrayList<Node> getForwardlist(Slave s) {
        //int i=s.getIndex();
        Iterator it=this.forwardlist.iterator();
        Node n;
        ArrayList<Node> fords=new ArrayList<Node>();
        while(it.hasNext()){
            n=(Node)it.next();
            if(n.getId()%4
                    ==s.getIndex())
                fords.add(n);
        }
        return fords;
    }*/    
    public void setForwardlist(ArrayList<Node> forwardlist) {
        this.forwardlist = forwardlist;
    }
    
    /**
     * returns backwardlist of nodes that are stored in another partition s
     * @param s a slave partition
     * @return subset of backwardlist
     */
    public ArrayList<Node> getBackwardlist() {
        return backwardlist;
    }
    
   /* public ArrayList<Node> getBackwardlist(Slave s) {
        int i=s.getIndex();
        Iterator it=this.backwardlist.iterator();
        Node n;
        ArrayList<Node> backs=new ArrayList<Node>();
        while(it.hasNext()){
            n=(Node)it.next();
            if(n.getId()%4==s.getIndex())backs.add(n);
        }
        return backwardlist;
    }*/

    public void setBackwardlist(ArrayList<Node> backwardlist) {
        this.backwardlist = backwardlist;
    }
    
    public void addForwardBoundary(Node n){
        this.forwardlist.add(n);
    }
    
    public void addBackwardBoundary(Node n){
        this.backwardlist.add(n);
    }
    
    public void implementSlave(String file){
        Iterator it;
        int i;
        Node n;
        Property p;
        DirectedEdge de;       
        try{
            BufferedWriter buffer=new BufferedWriter(new FileWriter(file));
            it=this.g.getVertices().iterator();
            while(it.hasNext()){
                n=(Node)it.next();
                p=n.getProperties().get(0);
                buffer.write("create (a:vertex{id:'"+n.getId()+"',slave:'"+p.getValue()+"'}) return a");                
                System.out.println("create (a{id:'"+n.getId()+"',slave:'"+p.getValue()+"'}) return a");
                buffer.newLine();
            }
            it=this.g.getEdges().iterator();
            System.out.println("!!!!!!!"+g.getEdges().size());
            while(it.hasNext()){
                de=(DirectedEdge)it.next();
                buffer.write("match (a),(b) where a.id='"+de.getSource().getId()+"' and b.id='"+
                       de.getTarget().getId()+"' create (a)-[r:isLinkedto]->(b) return r");
                System.out.println("match (a),(b) where a.id='"+de.getSource().getId()+"' and b.id='"+
                       de.getTarget().getId()+"' create (a)-[r:isLinkedto]->(b) return r");
                buffer.newLine();
                }
            buffer.write("CREATE INDEX ON :vertex(slave)");
            buffer.newLine();
            buffer.close();
           
        }
        catch(IOException e){
            System.out.println("erreur dans la générationdu fichier");
            System.out.println(e.getMessage());System.out.println();
        }        
    }
}
