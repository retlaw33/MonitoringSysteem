package Domain;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private List<String> endPoints;

    public Node(String ip, int port, List<String> lastPartEndPoints) {
        endPoints = new ArrayList<String>();

        for (String lpep : lastPartEndPoints) {
            String endPoint = "http://" + ip + ":" + String.valueOf(port) + "/" + lpep;
            endPoints.add(endPoint);
            System.out.println("endPoint added: " + endPoint);
        }
    }

    public List<String> getEndPoints() {
        return endPoints;
    }
}
