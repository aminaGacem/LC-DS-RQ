/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphs;

import java.util.Objects;

/**
 * models a directed edge
 * @author amina
 */
public class DirectedEdge extends Edge{
    private Node source;
    private Node target;
    private String label;

    public DirectedEdge(Node source, Node target, String label) {
        this.source = source;
        this.target = target;
        this.label=label;
    }

    public DirectedEdge() {
    }
    
    public DirectedEdge(String id, String label,Node source, Node target) {
        super(id, label);
        this.source = source;
        this.target = target;
    }

    public DirectedEdge(String id, String label) {
        super(id, label);
    }

    public void setTarget(Node target) {
        this.target = target;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public Node getTarget() {
        return target;
    }

    public Node getSource() {
        return source;
    }

    @Override
    public int hashCode() {
        int hash = 3;
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
        final DirectedEdge other = (DirectedEdge) obj;
        if (!Objects.equals(this.source.getId(), other.source.getId())) {
            return false;
        }
        if (!Objects.equals(this.target.getId(), other.target.getId())) {
            return false;
        }
        if (!Objects.equals(this.label, other.label)) {
            return false;
        }
        return true;
    }
   
}
