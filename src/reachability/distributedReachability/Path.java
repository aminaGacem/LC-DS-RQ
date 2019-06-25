/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reachability.distributedReachability;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.StringTokenizer;
import java.util.List;
import java.util.Iterator;
import graphs.Node;
import java.util.HashSet;

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
        if(this.path.size()==1)return false;
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
    
    /**
     * checks if the virtual connections between supernodes are preserved
     * if a sequence (i,i+1) does not exist in the retrieved virtual edges, it means that there
     * is no virtual edge that satisfy the constraints and connects between the supernodes
     * @param dqr list of couples (S,T) with S T being the source & target slave resp
     * @return a list of elements with index i so that (i,i+1) is not relevant anymore
     */
    public HashSet<Integer> checkVirtualEdgesPresence(HashSet<DistributedQueryResult> dqr){
        DistributedQueryResult elet;
        HashSet<Integer> result=new HashSet<Integer>();
        
        for(int i=0;i<this.path.size()-1;i++){
            elet=new DistributedQueryResult(new Node(""+this.path.get(i)),new Node(""+this.path.get(i+1)));
            if(dqr.contains(elet)==false){
                result.add(this.path.get(i));
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 7;
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
        final Path other = (Path) obj;
        if(this.path.size()!=other.path.size())return false;
        Iterator it,itother;
        it=this.path.iterator();
        itother=other.path.iterator();
        while(it.hasNext()&&itother.hasNext()){
            if((Integer)it.next()!=(Integer)itother.next())return false;
        }
        return true;
    }
    
    public int getSuccessor(int n){
        int rankn;
        rankn=this.path.indexOf(n);
        //ranks in the index position of the element n
        //rankn==-1 means n does not exist
        //rankn==this.path.size()-1 means it's the last element
        if(rankn==-1 ||rankn==this.path.size()-1)return -1;
        //rankn+1 is the position of the successor of n
        return this.path.get(rankn+1);
    }
    
    public int getPredecessor(int n){
        int rankn;
        rankn=this.path.indexOf(n);
        //ranks in the index position of the element n
        //rankn==-1 means n does not exist
        //rankn==0 means it's the first element
        if(rankn==-1 ||rankn==0)return -1;
        //rankn+1 is the position of the predecessor of n
        return this.path.get(rankn-1);
    }
    
    /**
     * divides the path $path$ into two paths and return them
     * @param elet the index in which we split
     * @return two paths one from the start until the last element before elt
     * and the other from the next element of elt to the end
     */
    private ArrayList<ArrayList<Integer>> splitPath(int elt, ArrayList<Integer> path){
        /*ArrayList<Integer> result=new ArrayList<Integer>();
        int index=path.indexOf(elt);
        result.addAll(index+1, path);
        path.removeAll(result);
        return result;*/
        ArrayList<ArrayList<Integer>> result=new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> before=new ArrayList<Integer>();
        ArrayList<Integer> after=new ArrayList<Integer>();
        //if $path$ does not contain the elt
        if(!path.contains(elt)){
            result.add(path);
        }
        //below path contains elt
        //path is a singleton
        if(path.size()==1)result.add(path);;
        //below path contains other elements in addition to elt        
        int i;
        for(i=0;i<path.size()&&path.get(i)!=elt;i++){
            before.add(path.get(i));
        }
        if(i<path.size())before.add(elt);
        result.add(before);
        i++;
        for(;i<path.size();i++){
            after.add(path.get(i));
        }
        result.add(after);
        return result;
    }
    
    public ArrayList<ArrayList<Integer>> split(HashSet<Integer> splitElt){
        ArrayList<ArrayList<Integer>> result=new ArrayList<ArrayList<Integer>>();
        Iterator itSplit=splitElt.iterator();
        int elt;
        ArrayList<Integer> partPath;        
        result.add(this.path);
        int i;
        boolean found;
        ArrayList<ArrayList<Integer>> temp=new ArrayList<ArrayList<Integer>>();
        
        //for each index to split
        while(itSplit.hasNext()){
            //we split on elt
            elt=(Integer)itSplit.next();
            found=false;
            //we explore the partial paths of result
            for(i=0;i<result.size()&&found==false;i++){
               partPath=result.get(i);
               if(partPath.contains(elt)){
                   found =true;
                   temp=this.splitPath(elt, partPath);
                   if(!temp.isEmpty()){
                       result.remove(i);
                       result.addAll(temp);
                       found=true;
                   }
               }
            }
        }
        return result;
    }

    public void setPath(ArrayList<Integer> path) {
        this.path = path;
    }
    
    public void println(){
        Iterator it=this.path.iterator();
        int elt;
        while(it.hasNext()){
            elt=(Integer)it.next();
            System.out.print(" "+elt);
        }
        System.out.println();
    }
    
    public boolean equals(Path other){
        if(this.path.size()!=other.getPath().size())return false;
        Iterator it1,it2;
        it1=this.path.iterator();
        it2=other.getPath().iterator();
        while(it1.hasNext()&&it2.hasNext()){
            if((Integer)it1.next()!=(Integer)it2.next())return false;
        }
        return true;
    }

    public ArrayList<Integer> getPath() {
        return path;
    }
    /**
     * checks if other is a subpath of the active object
     * @param other the other path
     * @return true is active object includes the other
     */
    public boolean sub(Path other){
        if(other.getPath().isEmpty())return true;
        if(other.getPath().size()>=this.path.size())return false;
        if(this.path.isEmpty())return false;
        int elt=other.getPath().get(0);
        boolean found=false;
        int i;
        for(i=0;i<this.path.size()&&found==false;i++){
            if(this.path.get(i)==elt)found=true;
        }
        if(found==false)return false;
        for(int j=0;i<this.path.size()&&j<other.getPath().size();i++,j++){
            if(this.path.get(i)!=other.getPath().get(j))return false;
        }
        return true;
    }
    /**
     * returns the collection comprised between elements src and dest
     * @param src element (inclusive)
     * @param dest element (inclusive)
     * @return 
     */
    public ArrayList<Integer> extract(int src, int dest){
        ArrayList<Integer> result=new ArrayList<Integer>();
        if(!this.path.contains(src+1)||!this.path.contains(dest-1))return result;
        //src and dest exists
        src=this.path.indexOf(src+1);
        //src++;
        dest=this.path.indexOf(dest-1);
        if(src>dest)return result;
        result.addAll(this.path.subList(src, dest));
        result.add(this.path.get(dest));
        return result;
    }
    /**
     * returns a new path that contains the integers from src to dest (inclusive)
     * @param src 
     * @param dest
     * @return 
     */
    /*public Path extract(int src, int dest){
        ArrayList<Integer> result=new ArrayList<Integer>();
        Path newpath=new Path();
        if(!this.path.contains(src)||!this.path.contains(dest))return newpath;
        //src and dest exists
        src=this.path.indexOf(src);
        src++;
        dest=this.path.indexOf(dest);
        if(src>dest)return newpath;    
        result.addAll(this.path.subList(src, dest));
        newpath.setPath(result);
        newpath.addElement(dest);
        return newpath;
    }*/
    
}
