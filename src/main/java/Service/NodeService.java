package Service;

import Domain.Node;
import org.apache.http.client.HttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NodeService {
    List<Node> nodes;
    HttpClient httpClient;

    public NodeService() throws IOException, ParseException {
        nodes = new ArrayList<Node>();
        loadNodesFromJson();
    }

    private void loadNodesFromJson() throws IOException, ParseException {
        //Read json array
        JSONParser parser = new JSONParser();
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("Nodes.json").getFile());
        JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(file));
        System.out.println("reading: " + file.getAbsolutePath());

        //Add node to list of nodes for each object in the json array
        for (Object jsonObj : jsonArray)
        {
            JSONObject jsonNode = (JSONObject) jsonObj;

            String name = (String) jsonNode.get("name");
            System.out.println("name = " + name);

            String ip = (String) jsonNode.get("ip");
            System.out.println("ip = " + ip);

            String port = (String) jsonNode.get("port");
            System.out.println("port = " + port);

            List<String> endPoints = new ArrayList<String>();
            JSONArray lastPartEndPoints = (JSONArray) jsonNode.get("lastPartEndPoints");

            for (Object ep : lastPartEndPoints)
            {
                String endPoint = "http://" + ip + ":" + String.valueOf(port) + "/" + ep.toString();
                endPoints.add(endPoint);
                System.out.println("endPoint added: " + endPoint);
            }

            nodes.add(new Node(name, endPoints));
            System.out.println("Node added: " + name);
        }
    }
}
