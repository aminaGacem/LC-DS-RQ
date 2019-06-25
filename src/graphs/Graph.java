/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphs;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * models a graph
 * can store at the same time directed and non-directed edges
 * @author amina
 * *@deprecated does not check the consistency that edges of a particular label should be directed or undirected
 */
public class Graph {
    private ArrayList<Node> vertices=new ArrayList<Node>();
    private ArrayList<Edge> edges=new ArrayList<Edge>(420045);
    private boolean directed;
    private boolean cyclic;
    private boolean labeled;

    public Graph() {
    }

    public void setCyclic(boolean cyclic) {
        this.cyclic = cyclic;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public void setLabeled(boolean labeled) {
        this.labeled = labeled;
    }

    public ArrayList<Node> getVertices() {
        return vertices;
    }
    
    public void addNode(Node n){
        this.vertices.add(n);
    }
    
    public void addEdge(Edge e){
        this.edges.add(e);
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }
    
    public void merge(Graph g){
        Iterator it=g.vertices.iterator();
        Node n;
        while(it.hasNext()){
            n=(Node)it.next();
            if(!this.contains(n))this.addNode(n);
        }
        it=g.getEdges().iterator();
        DirectedEdge de;
        while(it.hasNext()){
            de=(DirectedEdge)it.next();
            if(this.edges.contains(de)==false)this.addEdge(de);
        }
    }
    
    public boolean contains(Node n){
        Iterator it=this.vertices.iterator();
        Node currentnode;
        while(it.hasNext()){
            currentnode=(Node)it.next();
            if(currentnode.equals(it))return true;
        }
        return false;
    }
    
}
