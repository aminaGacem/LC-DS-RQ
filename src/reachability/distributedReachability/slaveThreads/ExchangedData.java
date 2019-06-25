/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reachability.distributedReachability.slaveThreads;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Iterator;
/**
 *Structure to store the data that should be sent to a receiver
 * The 
 * @author amina
 */
public class ExchangedData {
    /**
     * First Integer is the number of receiver
     * Second one is the source 
     * the hashset are the virtual nodes of the receiver that are connected to the source
     */
    private HashMap<Integer, HashMap<Integer, HashSet<Integer>>> result=new HashMap<Integer, HashMap<Integer, HashSet<Integer>>>();
    
    public void addElement(int receiv, int src, int vj){
        HashSet<Integer> virtuals=new HashSet<Integer>();
        HashMap<Integer, HashSet<Integer>> couples=new HashMap<Integer, HashSet<Integer>>();
        
        couples=this.result.get(receiv);
        if(couples==null){
            couples=new HashMap<Integer, HashSet<Integer>>();
            virtuals.add(vj);
            couples.put(src, virtuals);
            this.result.put(receiv, couples);
        }
        else {
            virtuals=couples.get(src);
            if(virtuals==null)virtuals=new HashSet<Integer>();
            virtuals.add(vj);
            couples.put(src, virtuals);        
            this.result.put(receiv, couples);
        }        
    }
    /**
     * it returns a string that has the following form <key (source, $v v v$)(souce...)><key....
     * @return 
     */
    /*@Override
    public String toString() {
        String str="";
        Iterator it;
        for(Map.Entry<Integer, HashMap<Integer, HashSet<Integer>>> entry: this.result.entrySet()){
            Integer key=entry.getKey();
            str=str+" <"+key;
            HashMap<Integer, HashSet<Integer>> value=entry.getValue();
            for(Map.Entry<Integer, HashSet<Integer>> entry1: value.entrySet()){
                Integer key1=entry1.getKey();
                str=str+"("+key1;
                HashSet<Integer> value1=entry1.getValue();
                it=value1.iterator();
                str=str+" $";
                while(it.hasNext()){
                    str=str+" "+(Integer)it.next();
                }
                
                str=str+"$)";
            }
            
            str=str+">";
        }
        return "ExchangedData{" +str+ '}';
    }*/
   
    
    public HashMap<Integer, HashSet<Integer>> getCouples(int receiver){        
        return this.result.get(receiver);
    }

    public HashMap<Integer, HashMap<Integer, HashSet<Integer>>> getResult() {
        return result;
    }
    
    /*public void merge(ExchangedData exda){
         HashMap<Integer, HashMap<Integer, HashSet<Integer>>> part=exda.getResult();
         HashMap<Integer, HashSet<Integer>> couples;
         for(int key: part.keySet()){
            couples=part.get(key);
            if(couples==null)couples=new HashMap<Integer, HashSet<Integer>>();
            couples
         }
    }*/
}
