/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphs;
import java.util.ArrayList;

/**
 * models an edge
 * @author amina
 */
public abstract class Edge {
    private String id;
    private String label;
    private ArrayList<Property> properties=new ArrayList<Property>();
    
    public Edge(String id, String label) {
        this.id = id;
        this.label = label;
    }
    
    public Edge(String id) {
        this.id = id;
    }

    public Edge() {
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
}
