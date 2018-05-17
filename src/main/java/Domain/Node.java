package Domain;

import java.util.List;

public class Node {
    private String name;
    private List<Leaf> leaves;

    public Node(String name, List<Leaf> leaves) {
        this.name = name;
        this.leaves = leaves;
    }

    public String getName() {
        return name;
    }

    public List<Leaf> getLeaves() {
        return leaves;
    }
}
