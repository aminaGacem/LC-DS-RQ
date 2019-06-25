/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Network;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Map;
/**
 *a structure that indexes each node in the slave where it is stored
 * @author amina
 */
public class Index {
    /**
     * the key is the node id and the value is the slave id
     */
    private HashMap<Integer,Integer> record=new HashMap<Integer,Integer>();
    
    /**
     * adds a new record (new node with the slave it is stored)
     * @param n the id of the node
     * @param s the id of the slave
     */
    public void addIndexRecord(int n,int s){
        record.put(n, s);
    }
    /**
     * returns the slave id given a node id
     * @param n given id node
     * @return the slave id where the the node is stored
     */
    public int getIdSlave(int n){
        return this.record.get(n);
    }
    
    /**
     * given an id slave, gives the nodes that are stored in it (virtual nodes)
     * @param s slave
     * @return list of id nodes
     */
    public ArrayList<Integer> getIdNodes(int s){
        ArrayList<Integer> result=new ArrayList<Integer>();
        Integer key;
        //first check that the value exists
        if(this.record.containsValue(s)){
            Set<Integer> set=this.record.keySet();
            Iterator it=set.iterator();
            while(it.hasNext()){
                key=(Integer)it.next();
                if(this.record.get(key)==s)result.add(s);
            }
        }
        return result;
    }
    /**
     * create the corresponding index
     * @param directoryPath 
     */
    public void createIndexes(String directoryPath){
        try{
           BufferedWriter buf=new BufferedWriter(new FileWriter(directoryPath)) ;
           Iterator it=this.record.entrySet().iterator();
           while(it.hasNext()){
               Map.Entry entry=(Map.Entry<Integer, Integer>)it.next();
               buf.write(entry.getKey()+"@"+entry.getValue());
               buf.newLine();
           }
           buf.close();
        }
        catch(Exception e){
            
        }
    }
    
}
