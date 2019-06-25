/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Network;
import graphs.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Iterator;
import graphs.Property;

/**
 *
 * @author amina
 */
public class Master {
    private Graph g;

    public void setG(Graph g) {
        this.g = g;
    }
    
        
    public void generateCommandBoundaryGraph(String file){
        Node n;
        DirectedEdge de;
        String line;
        Property p;
        try{
          BufferedWriter buffer=new BufferedWriter(new FileWriter(file));  
          Iterator it=this.g.getVertices().iterator();
          while(it.hasNext()){
              n=(Node)it.next();
              p=n.getProperties().get(0);
              line="create (a:vertex{id:'"+n.getId()+"',slave:'"+p.getValue()+"'}) return a";              
              buffer.write(line);
              buffer.newLine();
          }
          it=this.g.getEdges().iterator();
          while(it.hasNext()){
              de=(DirectedEdge)it.next();
              line="match (a),(b) where a.id='"+de.getSource().getId()+"' and b.id='"+
                       de.getTarget().getId()+"' create (a)-[r:isLinkedto]->(b) return r";
              buffer.write(line);
              buffer.newLine();
          }
          buffer.write("CREATE INDEX ON :vertex(slave)");
          buffer.newLine();
          buffer.close();
        }
        catch(Exception e){
            System.out.println("erreur dans la générationdu fichier");
            System.out.println(e.getMessage());
        }
    }
}
