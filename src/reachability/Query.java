/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reachability;
import graphs.Node;
import java.util.HashSet;

/**
 * represents a reachability query
 * @author amina
 */
public class Query {
    private Node source;
    private Node target;
    private boolean reachable;
    private HashSet univ_constraints=new HashSet<String>();

    public Query(Node source, Node target, HashSet univ_constraints) {
        this.source = source;
        this.target = target;
        this.univ_constraints.addAll(univ_constraints);
    }

    public Node getTarget() {
        return target;
    }

    public Node getSource() {
        return source;
    }

    public boolean isReachable() {
        return reachable;
    }

    public void setReachable(boolean reachable) {
        this.reachable = reachable;
    }
}
