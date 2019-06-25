/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reachability.distributedReachability;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;

/**
 * keeps all the distinct paths in a DAG
 *
 * @author amina
 */
public class PathSerie {

    private ArrayList<Path> paths = new ArrayList<Path>();

    public PathSerie() {
        try {
            BufferedReader buffer = new BufferedReader(new FileReader("/home/gacem/INDEX/index"));
            String line;
            line = buffer.readLine();
            StringTokenizer st;
            Path p;
            while (line != null) {
                st = new StringTokenizer(line);
                p = new Path();
                while (st.hasMoreTokens()) {
                    p.addElement(Integer.parseInt(st.nextToken()));
                }
                this.paths.add(p);
                line = buffer.readLine();
            }
        } catch (Exception e) {
            System.out.println("Error in reachability.distributedReachability.PathSerie " + e.getMessage());
        }
    }

    /**
     * checks if a target is reachable from a source search is made across
     * several paths
     *
     * @param src source
     * @param tgt partition
     * @return true if reachability is provided, false otherwise
     */
    public boolean isReachable(int src, int tgt) {
        Iterator it = this.paths.iterator();
        Path p;
        while (it.hasNext()) {
            p = (Path) it.next();
            if (p.isReachable(src, tgt)) {
                return true;
            }
        }
        return false;
    }

    /**
     * outputs all the possible paths between the source slave and the target
     * slave isReachable should be invoked first before retrievePaths
     *
     * @param src the source partition
     * @param tgt the target partition
     * @return the list of slaves
     */
    public ArrayList<List<Integer>> retrievePaths(int src, int tgt) {
        ArrayList<List<Integer>> result = new ArrayList<List<Integer>>();
        Iterator it;
        it = this.paths.iterator();
        Path p;
        while (it.hasNext()) {
            p = (Path) it.next();
            if (p.isReachable(src, tgt)) {
                result.add(p.retrievePaths(src, tgt));
            }

        }
        return result;
    }

    public ArrayList<Path> getPaths() {
        return paths;
    }

    public void checkVirtualEdgesPresence(HashSet<DistributedQueryResult> dqr) {
        Path path;
        Iterator itpath = this.paths.iterator();
        HashSet<Integer> toSplit = new HashSet<Integer>();
        ArrayList<Path> result=new ArrayList<Path>();
        ArrayList<ArrayList<Integer>> finalelts;
        int i;
        Path newpath;
        
        while (itpath.hasNext()) {
            path = (Path) itpath.next();
            toSplit = path.checkVirtualEdgesPresence(dqr);//the indexes i that should be split
            if (!toSplit.isEmpty()) {
                //we need to split
                finalelts=path.split(toSplit);
                for(i=0;i<finalelts.size();i++){
                    newpath=new Path();
                    newpath.setPath(finalelts.get(i));
                    result.add(newpath);
                }
            }
            else {
                result.add(path);
            }
        }
        this.paths=result;
    }

    public HashSet<Integer> getSuccessors(int n) {
        HashSet<Integer> result = new HashSet<Integer>();
        Iterator it = this.paths.iterator();
        Path p;
        while (it.hasNext()) {
            p = (Path) it.next();
            result.add(p.getSuccessor(n));
        }
        return result;
    }

    public HashSet<Integer> getPredecessors(int n) {
        HashSet<Integer> result = new HashSet<Integer>();
        Iterator it = this.paths.iterator();
        Path p;
        while (it.hasNext()) {
            p = (Path) it.next();
            result.add(p.getPredecessor(n));
        }
        return result;
    }

    public void correctRedundancy() {
        int i,j;
        Path p1,p2;
        for(i=0;i<this.paths.size();i++){
            p1=this.paths.get(i);
            for(j=i+1;j<this.paths.size();j++){
                p2=this.paths.get(j);
                if(p1.equals(p2)||p1.sub(p2)){
                    this.paths.remove(j);
                    j=j-1;
                }
                else if(p2.sub(p1)){
                    this.paths.remove(i);
                    i=i-1;
                }
            }
        }
    }
    
    public void println(){
        Iterator it=this.paths.iterator();
        Path elt;
        while(it.hasNext()){
            elt=(Path)it.next();
            elt.println();            
        }
    }
    /**
     * 
     * @param src
     * @param dest
     * @return 
     */
    public ArrayList<ArrayList<Integer>> extract(int src,int dest){
        ArrayList<ArrayList<Integer>> result=new ArrayList<ArrayList<Integer>>();
        Iterator it;
        it=this.paths.iterator();
        Path p;
        while(it.hasNext()){
            p=(Path)it.next();
            result.add(p.extract(src, dest));
        }
        return result;
    }
    
    /*public ArrayList<Path> extract(int src,int dest){
        ArrayList<Path> result=new ArrayList<Path>();
        Iterator it;
        it=this.paths.iterator();
        Path p;
        while(it.hasNext()){
            p=(Path)it.next();
            result.add(p.extract(src, dest));
        }
        return result;
    }*/
}
