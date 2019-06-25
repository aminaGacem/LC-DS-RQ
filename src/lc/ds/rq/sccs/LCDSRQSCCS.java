/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lc.ds.rq.sccs;
import reachability.distributedReachability.*;
import graphs.Node;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.*;

/**
 * replace the directories of connection
 * @author amina
 */
public class LCDSRQSCCS {

    /**
     * @param args the command line arguments
     * args[0] is the number of sources
     * args[1] is the number of targets
     * args[2] is the number of labels
     * args[3] is the number of slaves
     */
    //private static int currentSlave;//to keep the latest number of slave when looking for the id
    private static ArrayList<ArrayList<String>> init()throws FileNotFoundException,IOException{
        ArrayList<ArrayList<String>> result=new ArrayList<ArrayList<String>>();
        ArrayList<String> localNodes;
        BufferedReader buffer[]=new BufferedReader[12];
        String line;
        //for each buffer/partition/slave
        for(int i=0;i<12;i++){            
            localNodes=new ArrayList<String>();
            //open the corresponding file
            buffer[i]=new BufferedReader(new FileReader("/home/gacem/commands/nodeDataset"+(i+1)));
            line=buffer[i].readLine();
            while(line!=null){
                //gets the nodes
                localNodes.add(line);
                line=buffer[i].readLine();
            }
            //put the local nodes in the result
            result.add(localNodes);
        }
        return result;
    }
    
    /*private static String transform(int x,ArrayList<HashSet<String>> nodes){
       String result="";
       int i=0;
       int sum=nodes.get(i).size();
       while(x<sum&&i<12){
           i++;
           sum=sum+nodes.get(i).size();
       }
       if(x<sum){
           System.out.println("FATAL ERROR");System.exit(1);
       }
       else {
           HashSet<String> localNodes=nodes.get(i);
           currentSlave=i;
           int indice=sum-x;
           Iterator it=localNodes.iterator();
           while(indice>0&&it.hasNext()){
               it.next();
               indice--;
           }
           result=(String)it.next();
       }
       
       return result;
    }*/
    
    
    public static void main(String[] args) {
        // TODO code application logic here
        DistributedQuery dq=new DistributedQuery();
        int number_source=Integer.parseInt(args[0]);
        int number_target=Integer.parseInt(args[1]);
        int label=Integer.parseInt(args[2]);//number of labels
        int numberSlave=Integer.parseInt(args[3]);//number of slaves

        try{
            ArrayList<ArrayList<String>> allNodes=init();//gets all the nodes
            int sizes[]=new int[12];
            for(int i=0;i<12;i++){
                sizes[i]=allNodes.get(i).size();
            }
            Random rand=new Random();
            while(number_source>0){
                int randSlave=rand.nextInt(12);
                dq.addSource(new Node(allNodes.get(randSlave).get(rand.nextInt(sizes[randSlave])),randSlave+1));
                //dq.addSource(new Node(transform(rand.nextInt(1527025),allNodes),currentSlave));
                number_source--;
            }
            while(number_target>0){
                int randSlave=rand.nextInt(12);
                dq.addTarget(new Node(allNodes.get(randSlave).get(rand.nextInt(sizes[randSlave])),randSlave+1));
                number_target--;
            }
            
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        System.out.print("The query is ");
        dq.println();

        if(label>0)dq.addUnivConstraint("sameNode");
        if(label>1)dq.addUnivConstraint("actedInSet");//60443
        if(label>2)dq.addUnivConstraint("bornInSet");//79337
        if(label>3)dq.addUnivConstraint("graduateSet");//86349                                       
        if(label>4)dq.addUnivConstraint("worksAtSet");//88128
                
        
        MasterParallel masterP;
        masterP=new MasterParallel(dq,numberSlave);
        masterP.init();
        masterP.start();
        try{
            masterP.join();
          
        }
        catch(Exception e){
            
        }
    }
    
}
