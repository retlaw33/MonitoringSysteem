package Service;

import Domain.Node;
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

    public NodeService() throws IOException, ParseException {
        loadNodesFromJson();
    }

    private void loadNodesFromJson() throws IOException, ParseException {
        List<String> endPoints = new ArrayList<String>();

        JSONParser parser = new JSONParser();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("Nodes.json").getFile());

        JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(file));

        for (Object jsonObj : jsonArray)
        {
            JSONObject jsonNode = (JSONObject) jsonObj;

            String name = (String) jsonNode.get("name");
            System.out.println(name);

            String ip = (String) jsonNode.get("ip");
            System.out.println(ip);

            String port = (String) jsonNode.get("port");
            System.out.println(port);

            JSONArray lastPartEndPoints = (JSONArray) jsonNode.get("lastPartEndPoints");

            for (Object endPoint : lastPartEndPoints)
            {
                System.out.println(endPoint);
            }
        }

        /*for (String lpep : lastPartEndPoints) {
            String endPoint = "http://" + ip + ":" + String.valueOf(port) + "/" + lpep;
            endPoints.add(endPoint);
            System.out.println("endPoint added: " + endPoint);
        }*/

        //nodes.add(new Node())
    }
}
