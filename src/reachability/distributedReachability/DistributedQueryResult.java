/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reachability.distributedReachability;
import graphs.Node;
import java.util.Objects;

/**
 *
 * @author amina
 */
public class DistributedQueryResult {
    private Node source;
    private Node target;
    
    public DistributedQueryResult(Node source, Node target) {
        this.source = source;
        this.target = target;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public void setTarget(Node target) {
        this.target = target;
    }

    public Node getSource() {
        return source;
    }

    public Node getTarget() {
        return target;
    }
    
    public void println(){
        System.out.println("Distributed Query Result: Source "+this.source.getId()+" target: "+this.target.getId());
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
        final DistributedQueryResult other = (DistributedQueryResult) obj;
        if (!Objects.equals(this.source, other.source)) {
            return false;
        }
        if (!Objects.equals(this.target, other.target)) {
            return false;
        }
        return true;
    }
    
}
