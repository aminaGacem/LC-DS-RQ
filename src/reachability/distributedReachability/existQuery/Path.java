/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reachability.distributedReachability.existQuery;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.StringTokenizer;
import java.util.List;

/**
 *
 * @author amina
 */
public class Path {
    private ArrayList<Integer> path=new ArrayList<Integer>();  
    
    public void addElement(int x){
        this.path.add(x);
    }
    /**
     * checks if a target is reachable from a source
     * @param src source
     * @param tgt partition
     * @return true if reachability is provided, false otherwise
     */
    public boolean isReachable(int src, int tgt){
        int ranks,rankt;
        ranks=this.path.indexOf(src);
        rankt=this.path.indexOf(tgt);
        if(ranks==-1||rankt==-1)return false;
        if(ranks>rankt)return false;
        return true;
    }
    /**
     * outputs the number of slaves that connects between the source slave and the target slave
     * isReachable should be invoked first before retrievePaths
     * @param src the source partition
     * @param tgt the target partition
     * @return the list of slaves
     */
    public List<Integer> retrievePaths(int src, int tgt){
        int ranks,rankt;
        ranks=this.path.indexOf(src);
        rankt=this.path.indexOf(tgt);
        return this.path.subList(src, tgt);
    }
}
