/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reachability.distributedReachability.existQuery;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.List;

/**
 *keeps all the distinct paths in a DAG
 * @author amina
 */
public class PathSerie {
    private ArrayList<Path> paths=new ArrayList<Path>();

    public PathSerie() {
        try{
            BufferedReader buffer=new BufferedReader(new FileReader("index"));
            String line;
            line=buffer.readLine();
            StringTokenizer st;
            Path p;
            while(line!=null){
                st=new StringTokenizer(line);
                p=new Path();
                while(st.hasMoreTokens()){
                    p.addElement(Integer.parseInt(st.nextToken()));
                }
                this.paths.add(p);
                line=buffer.readLine();
            }
        }
        catch(Exception e){
            
        }
    }
    
    /**
     * checks if a target is reachable from a source
     * search is made across several paths
     * @param src source
     * @param tgt partition
     * @return true if reachability is provided, false otherwise
     */
    public boolean isReachable(int src, int tgt){
        Iterator it=this.paths.iterator();
        Path p;
        while(it.hasNext()){
            p=(Path)it.next();
            if(p.isReachable(src, tgt))return true;
        }
        return false;
    }
    
    /**
     * outputs all the possible paths between the source slave and the target slave
     * isReachable should be invoked first before retrievePaths
     * @param src the source partition
     * @param tgt the target partition
     * @return the list of slaves
     */
    public ArrayList<List<Integer>> retrievePaths(int src, int tgt){
        ArrayList<List<Integer>> result=new ArrayList<List<Integer>>();
        Iterator it;
        it=this.paths.iterator();
        Path p;
        while(it.hasNext()){
            p=(Path)it.next();
            if(p.isReachable(src, tgt))result.add(p.retrievePaths(src, tgt));
            
        }
        return result;
    }

    public ArrayList<Path> getPaths() {
        return paths;
    }
}
