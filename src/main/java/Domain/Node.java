package Domain;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private String name;
    private List<String> endPoints;

    public Node(String name, List<String> endPoints) {
        endPoints = new ArrayList<String>();
    }

    public List<String> getEndPoints() {
        return endPoints;
    }
}
