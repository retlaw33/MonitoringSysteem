package Service;

import Domain.Node;

import java.util.ArrayList;
import java.util.List;

public class NodeService {
    List<Node> nodes;

    public NodeService() {
    }

    private void loadNodesFromJson(){
        List<String> endPoints = new ArrayList<String>();

        /*for (String lpep : lastPartEndPoints) {
            String endPoint = "http://" + ip + ":" + String.valueOf(port) + "/" + lpep;
            endPoints.add(endPoint);
            System.out.println("endPoint added: " + endPoint);
        }*/

        //nodes.add(new Node())
    }
}
