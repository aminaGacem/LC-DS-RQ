/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reachability.distributedReachability.slaveThreads;

import database.*;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Iterator;

/**
 *
 * @author amina
 */
public class LocalReachability {

    private int numberNodes = 0;
    private int pathLength = 0;
    private ArrayDeque<Integer> stack = new ArrayDeque<Integer>();
    private boolean[] exploredNodes = new boolean[50000];
    //private HashSet<Integer> paths=new HashSet<Integer>();

    /*public boolean evaluateLocalReachability(int src, int tgt,DBConnect db, HashSet<String> constr){
     int x;
     ArrayList<String> result_src=new ArrayList<String>();
     ArrayList<String> result_tgt=new ArrayList<String>();
     HashSet<String> paths=new HashSet<String>();
     ArrayList<HashSet<String>> allpaths=new ArrayList<HashSet<String>>();
     try{
     db.clearResult();
     x=db.query("match (n: vertex{id:\""+src+"\"})-[R]-> (m: vertex{id:\""+tgt+"\"}) where R=\"a\" or R=\"b\" return R");
     if(!db.getResult().isEmpty())return true;
     else{
     x=db.query("match (n: vertex)-[R]-> (m: vertex) where Type(R)=\"a\" or Type(R)=\"e\" or Type(R)=\"b\" or Type(R)=\"d\" or Type(R)=\"f\" return n.id");
     result_src=db.getResult();
     db.clearResult();
     x=db.query("match (n: vertex)-[R]-> (m: vertex) where Type(R)=\"a\" or Type(R)=\"e\" or Type(R)=\"b\" or Type(R)=\"d\" or Type(R)=\"f\" return m.id");
     result_tgt=db.getResult();
     if(!result_src.contains(src)&&!result_tgt.contains(tgt))return false;
                                
     }
     }
     catch(Exception e){
     System.out.println("Error "+e.getMessage());
     }
     return true;
     }*/
    /**
     * explores the graph to check the constraint-reachability (universal constraint 
     * reachability)
     *
     * @param src source of the query
     * @param tgt target of the query
     * @param db the database connector
     * @param constr the set of keys
     * @return
     */
    public boolean exploreFalse(int src, int tgt, DBConnect db, HashSet<String> constr) {
        int start_node = src;
        boolean found = false;
        this.stack = new ArrayDeque<Integer>();
        this.numberNodes = 0;
        this.pathLength = 0;
        for (int i = 0; i < 50000; i++) {
            this.exploredNodes[i] = false;
        }
        boolean marked;
        int m;
        boolean emptyStack = false;
        //this.paths=new HashSet<Integer>();

        String predicate = this.generateConstraints(constr);
        Iterator it;
        ArrayList<String> interm = new ArrayList<String>();
        try {
            db.clearResult();
            do {
                //this.paths.add(start_node);
                //System.out.println("elements of the stack"+this.stack.toString());
                //we first check if there is a connection between the nodes
                db.clearResult();
                db.query("match (n: vertex{id:\"" + start_node + "\"})-[R]-> (m: vertex{id:\"" + tgt + "\"})" + predicate + " return n.id");
                //System.out.println("!!Query!!!!"+"match (n: vertex{id:\""+start_node+"\"})-[R]-> (m: vertex{id:\""+tgt+"\"})"+ predicate +" return R");
                this.exploredNodes[start_node % 50000] = true;
                //System.out.println("START "+start_node);
                if (db.getResult().isEmpty() == false) {
                    //there is a connection between the start and the target
                    this.numberNodes++;
                    this.pathLength++;
                    found = true;
                    //this.paths.add(tgt);
                } else {
                    //there is no connection between the start and the target
                    //search for the successors of the starting node
                    db.clearResult();
                    db.query("match (n: vertex{id:\"" + start_node + "\"})-[R]-> (m: vertex)" + predicate + " return m.id");
                    //System.out.println("!!!!Query version2!!!!!"+"match (n: vertex{id:\""+start_node+"\"})-[R]-> (m: vertex)"+ predicate +" return m.id");
                    //interm contains the successors of the starting node
                    interm = db.getResult();
                    this.numberNodes = this.numberNodes + interm.size();
                    it = interm.iterator();
                    //pushes elements into the stack
                    while (it.hasNext()) {
                        //System.out.println("???"+Integer.parseInt((String)it.next()));
                        m = Integer.parseInt((String) it.next());
                        marked = this.exploredNodes[m % 50000];
                        if (marked == false) {
                            this.stack.push(m);
                            //System.out.println("STACK "+m);
                            this.exploredNodes[m % 50000] = true;
                        }
                        /*if(marked==true){
                         this.stack.push(m);
                         //System.out.println("STACK "+m);
                         }*/
                    }
                    if (interm.isEmpty()) {
                        //System.out.println("NOT START "+start_node);
                        //this.paths.remove(start_node);
                    } else {
                        this.pathLength++;
                    }
                    if (this.stack.isEmpty()) {
                        emptyStack = true;
                    } else {
                        start_node = this.stack.pop();
                    }
                }
            } while (found == false && emptyStack == false);
        } catch (Exception e) {
            System.out.println("Error in reachability.distributedReachability.slaveThreads.LocalReachability.explore " + e.getMessage());
        }
        this.stack.clear();
        return found;
    }

    private String generateConstraintsFalse(HashSet<String> constr) {
        String temp = " where";
        Iterator it = constr.iterator();
        if (it.hasNext()) {
            temp = temp + " TYPE(R)=\"" + (String) it.next() + "\"";
        }
        while (it.hasNext()) {
            temp = temp + " or TYPE(R)=\"" + (String) it.next() + "\"";
        }
        return temp;
    }
    
    private String generateConstraints(HashSet<String> constr) {
        String temp = " [:";
        Iterator it = constr.iterator();
        if (it.hasNext()) {
            temp = temp + (String) it.next();
        }
        while(it.hasNext()){
            temp = temp + "|"+ (String) it.next() ;
        }
        temp=temp+"*]";
        return temp;
    }

    public int getNumberNodes() {
        return numberNodes;
    }

    public int getPathLength() {
        return pathLength;
    }
    
    
    public boolean explore(int src, int tgt, DBConnect db, HashSet<String> constr1, HashSet<String> constr2) {
        int start_node = src;
        boolean found = false;
        this.stack = new ArrayDeque<Integer>();
        this.numberNodes = 0;
        this.pathLength = 0;
        for (int i = 0; i < 50000; i++) {
            this.exploredNodes[i] = false;
        }
        boolean marked;
        int m;
        boolean emptyStack = false;
        //this.paths=new HashSet<Integer>();

        String predicate = this.generateConstraints(constr1);
        Iterator it;
        ArrayList<String> interm = new ArrayList<String>();
        try {
            db.clearResult();
            do {
                //this.paths.add(start_node);
                //System.out.println("elements of the stack"+this.stack.toString());
                //we first check if there is a connection between the nodes
                db.clearResult();
                db.query("match (n: vertex{id:\"" + start_node + "\"})-[R]-> (m: vertex{id:\"" + tgt + "\"})" + predicate + " return n.id");
                //System.out.println("!!Query!!!!"+"match (n: vertex{id:\""+start_node+"\"})-[R]-> (m: vertex{id:\""+tgt+"\"})"+ predicate +" return R");
                this.exploredNodes[start_node % 50000] = true;
                //System.out.println("START "+start_node);
                if (db.getResult().isEmpty() == false) {
                    //there is a connection between the start and the target
                    this.numberNodes++;
                    this.pathLength++;
                    found = true;
                    //this.paths.add(tgt);
                } else {
                    //there is no connection between the start and the target
                    //search for the successors of the starting node
                    db.clearResult();
                    db.query("match (n: vertex{id:\"" + start_node + "\"})-[R]-> (m: vertex)" + predicate + " return m.id");
                    //System.out.println("!!!!Query version2!!!!!"+"match (n: vertex{id:\""+start_node+"\"})-[R]-> (m: vertex)"+ predicate +" return m.id");
                    //interm contains the successors of the starting node
                    interm = db.getResult();
                    this.numberNodes = this.numberNodes + interm.size();
                    it = interm.iterator();
                    //pushes elements into the stack
                    while (it.hasNext()) {
                        //System.out.println("???"+Integer.parseInt((String)it.next()));
                        m = Integer.parseInt((String) it.next());
                        marked = this.exploredNodes[m % 50000];
                        if (marked == false) {
                            this.stack.push(m);
                            //System.out.println("STACK "+m);
                            this.exploredNodes[m % 50000] = true;
                        }
                        /*if(marked==true){
                         this.stack.push(m);
                         //System.out.println("STACK "+m);
                         }*/
                    }
                    if (interm.isEmpty()) {
                        //System.out.println("NOT START "+start_node);
                        //this.paths.remove(start_node);
                    } else {
                        this.pathLength++;
                    }
                    if (this.stack.isEmpty()) {
                        emptyStack = true;
                    } else {
                        start_node = this.stack.pop();
                    }
                }
            } while (found == false && emptyStack == false);
        } catch (Exception e) {
            System.out.println("Error in reachability.distributedReachability.slaveThreads.LocalReachability.explore " + e.getMessage());
        }
        this.stack.clear();
        return found;
    }
    /**
     * checks the presence of a constraint in the graph, each time a constraint is found,
     * it is removed from the HashSet<String> constr
     * @param src
     * @param tgt
     * @param db
     * @param constr
     * @return 
     */
    public HashSet<String> checkPresence(int src, int tgt, DBConnect db, HashSet<String> constr){
        Iterator it=constr.iterator();
        String label;
        try {
            db.clearResult();
            while (it.hasNext()){
                db.clearResult();
                label=(String)it.next();
                db.query("match ()-[r]-() where type(r)=\"" + label + "\"return type(r)");
                //the label is present
                if (db.getResult().isEmpty() == false) {
                    constr.remove(label);
                }
            }
        } catch (Exception e) {
            System.out.println("Error in reachability.distributedReachability.slaveThreads.LocalReachability.explore " + e.getMessage());
        }
        return constr;
    }
    
    public boolean explore(String src, String tgt, DBConnect db, HashSet<String> constr) {        
       boolean found = false;
        String predicate = this.generateConstraints(constr);        
        try {
            db.clearResult();
            db.query("match (n: vertex{id:\"" + src + "\"})-"+predicate+"-> (m: vertex{id:\"" + tgt + "\"}) return n.id");
            if (db.getResult().isEmpty() == false) 
                    found = true;
            
        } catch (Exception e) {
            System.out.println("Error in reachability.distributedReachability.slaveThreads.LocalReachability.explore " + e.getMessage());
        }
        return found;
        
    }
    /**
     * find a random target distant from a given source node
     * @param src a given node
     * @param db helps to connect the database
     * @param constr constraints of query
     * @param lengthPath the given distance
     * @return 
     */
    public String findTarget(String src, DBConnect db, HashSet<String> constr, int lengthPath) {        
        boolean found = true;
        String predicate = this.generateConstraints(constr);
        String save=src;
        try {
            for(int x=0;x<lengthPath&&found==true;x++){
                db.clearResult();
                db.query("match (n: vertex{id:\"" + src + "\"})-"+predicate+"-> (m) return m.id");
                if (db.getResult().isEmpty()) 
                    found = false;
                else {
                    src=db.getResult().get(0);
                }
            }
            if(found==false)src=save;
            
        } catch (Exception e) {
            System.out.println("Error in reachability.distributedReachability.slaveThreads.LocalReachability.explore " + e.getMessage());
        }
        return src;
    }

}
