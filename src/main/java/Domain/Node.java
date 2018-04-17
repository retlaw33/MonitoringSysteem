package Domain;

import java.util.List;

public class Node {
    private String name;
    private List<String> endPoints;

    public Node(String name, List<String> endPoints) {
        this.name = name;
        this.endPoints = endPoints;
    }

    public List<String> getEndPoints() {
        return endPoints;
    }
}
