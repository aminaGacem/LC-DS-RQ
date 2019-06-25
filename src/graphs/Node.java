/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphs;
import java.util.ArrayList;

/**
 * models a node
 * @author amina
 */
public class Node {
    private String id;
    private String label;
    private ArrayList<Property> properties=new ArrayList <Property>();
    private int slave;

    public Node(String id, String label) {
        this.id = id;
        this.label = label;
    }
    
    public Node(String id) {
        this.id = id;
    }
    
    public Node(String id, int slave) {
        this.id = id;
        this.slave=slave;
    }

    public int getSlave() {
        return slave;
    }
    

    public void setProperties(ArrayList<Property> properties) {
        this.properties = properties;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public void add(Property p){
        this.properties.add(p);
    }

    public ArrayList<Property> getProperties() {
        return properties;
    }

    public String getLabel() {
        return label;
    }

    public String getId() {
        return id;
    }
    
    public void addProperty(Property p){
        this.properties.add(p);
    }
    
    public void println(){
        System.out.println(" Node has: slave"+this.slave+" id "+this.id);
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
        final Node other = (Node) obj;
        return this.id.equals(other.id);
    }
    
}
