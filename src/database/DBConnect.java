/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.*;
import java.util.Map;
import java.util.ArrayList;
import org.neo4j.graphdb.Transaction;
import java.io.File;
import java.util.HashSet;
//import org.neo4j.graphdb.Direction;
//import org.neo4j.graphdb.Relationship;
//import org.neo4j.graphdb.Node;


//import org.neo4j.cypher.

/**
 *
 * @author amina
 */
public class DBConnect {/* maybe it should implement */
    private GraphDatabaseService graphDb ;
    private Result rs;
    private ArrayList<String> result=new ArrayList<String>();
    
    public int disconnect(){
        graphDb.shutdown();
        return 1;
    }
    /*public int query(String s) throws ExecutionException{
        rs=graphDb.execute(s);
        if(rs.hasNext()==false)return 0;
        while(rs.hasNext()){
            Map<String,Object> row=rs.next();
            for(String key: rs.columns()){
                this.result.add((String)row.get(key));
                System.out.println(row.get(key));//row.get(key);
                //System.out.println(row.values().toString());
            }
        }
        return 1;
    }*/
    
    /*public boolean queryReach(String s) throws ExecutionException{
        rs=graphDb.execute(s);
        boolean x=false;
        while(rs.hasNext()){
            x=true;
            Map<String,Object> row=rs.next();
            for(String key: rs.columns()){
                //rs.resultAsString();
                //System.out.println(row.get(key));//row.get(key);
                //System.out.println(row.values().toString());
            }
        }
        return x;
    }*/
    
    public int profile(String s){
        Result rs=graphDb.execute(s);        
        return 1;
    }
    public int result(String s){
        return 1;
    }
    
    public int connect(String dbpath)throws ConnectionException{
        File file=new File(dbpath);
        //graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(file);
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dbpath);// a better alternative exists
        if(graphDb==null)throw new ConnectionException();
        else return 1;
    }
    
    /**
     * Clear the results of a query
     */
    public void clearResult(){
        this.result=new ArrayList<String>();
    }

    public ArrayList<String> getResult() {
        return result;
    }
    
    public synchronized int executeTransation(String s){
        Transaction tx=graphDb.beginTx();
        try{this.clearResult();
        this.result=new ArrayList<String>();
            rs=graphDb.execute(s);     
            if(rs.hasNext()==false)return 0;            
            while(rs.hasNext()){
                Map<String,Object> row=rs.next();
                for(String key: rs.columns()){
                    this.result.add((String)row.get(key));
                    System.out.println("A node explored"+row.get(key));//row.get(key);
                System.out.println(" voir ici"+row.values().toString());
                }
            }
            tx.success();
        }
        catch(Exception e){
           System.out.println("database.DBConnect.executeTransation "+e.getMessage());
           return 0;
        }
        finally{
            tx.close();
            return 1;
        }
        
    }
    
    public synchronized boolean executeReachTransation(String s){
        Transaction tx=graphDb.beginTx();
        boolean x=false;
        try{
            rs=graphDb.execute(s);
            if(rs.hasNext())x=true;
            tx.success();
        }
        finally{
            tx.close();
        }
        return x;
    }
    
    public synchronized int query(String s) throws ExecutionException{
        this.clearResult();
        rs=graphDb.execute(s);
        if(rs.hasNext()==false)return 0;
        while(rs.hasNext()){ 
            Map<String,Object> row=rs.next();
            for(String key: rs.columns()){
                this.result.add((String)row.get(key));
                //System.out.print("  "+(String)row.get(key));//row.get(key);
                //System.out.println(row.values().toString());               
            }
            //System.out.println();
        }        
        return 1;
    }
    
    /*public void explorebreadthFirst(){
        TraversalDescription td=this.graphDb.traversalDescription();
        td=td.breadthFirst();
        td=td.relationships(Labels.valueOf("a"));
        td.traverse(null)
        
        td.breadthFirst();
        td.depthFirst();
        td.evaluator(null);
        td.evaluator(null);
        td.expand(null);
        td.relationships(null);
        td.relationships(null, Direction.BOTH);
        td.traverse(null);           
    }*/
    
  
    /*public void explore(int src, int tgt, DBConnect db, HashSet<String> constr){        
        for ( Path position : this.graphDb.traversalDescription()
            .depthFirst()
            .relationships( Rels.KNOWS )
            .relationships( Rels.LIKES, Direction.INCOMING )
            .evaluator( Evaluators.toDepth( 5 ) )
            .traverse( node ) )
        {
        output += position + "\n";
        }
    }*/
}
