package Service;

import Domain.HttpMethod;
import Domain.Leaf;
import Domain.Node;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import org.apache.http.HttpException;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NodeService {
    static Logger log = Logger.getLogger(NodeService.class.getName());

    public List<Node> nodes;
    public ListProperty<String> endpointList = new SimpleListProperty<>();
    public ListView<String> endpointListView = new ListView<>();

    public NodeService() throws IOException, ParseException {
        nodes = new ArrayList<>();
        loadNodesFromJson();
    }

    public void startMonitoring() throws IOException, HttpException, InterruptedException {
        for(;;) {
            checkEndPoints();
            updateFrontEnd();
            TimeUnit.MINUTES.sleep(15);
        }
    }

    private void checkEndPoints() throws IOException, HttpException {
        HttpClient myClient = new HttpClient();
        for (Node node : nodes) {
            for (Leaf leaf : node.getLeaves()) {
                switch (leaf.getHttpMethod()){
                    case GET:
                        leaf = myClient.getRequest(leaf);
                        logLeaf(leaf);
                        break;
                    case POST:
                        leaf = myClient.postRequest(leaf);
                        logLeaf(leaf);
                        break;
                    default:
                        throw new HttpException("Http method not known in Monitoring system.");
                }
            }
        }
    }

    private void logLeaf(Leaf leaf){
        System.out.println("testing: " + leaf.getEndPoint());
        if (leaf.isFunctional()) {
            log.info(" |" + leaf.getEndPoint() + "| - Statuscode: " + String.valueOf(leaf.getStatuscode()) + " - Response: " + leaf.getResult());
        }
        else {
            log.warn(" |" + leaf.getEndPoint() + "| - Statuscode: " + String.valueOf(leaf.getStatuscode()));
        }
    }

    private void updateFrontEnd() {
        // endpointList.clear();
        List<String> endpoints = new ArrayList<String>();
        for (Node node : nodes) {
            for (Leaf leaf : node.getLeaves()) {
                endpoints.add(leaf.toString());
            }
        }
        Platform.runLater(
                () -> {
                    endpointList.set(FXCollections.observableArrayList(endpoints));
                }
        );
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

            List<Leaf> leaves = new ArrayList<Leaf>();
            JSONArray jsonRequests = (JSONArray) jsonNode.get("leaves");

            for (Object jsonReq : jsonRequests)
            {
                String endPoint = "http://" + ip + ":" + String.valueOf(port) + "/" + ((JSONObject)jsonReq).get("lpEndpoint");
                HttpMethod httpMethod = HttpMethod.valueOf((String)((JSONObject)jsonReq).get("httpMethod"));
                String body = (String) ((JSONObject)jsonReq).get("body");
                leaves.add(new Leaf(endPoint, httpMethod, body));
                System.out.println("endPoint added: " + endPoint);
            }

            nodes.add(new Node(name, leaves));
            System.out.println("Node added: " + name);
        }
    }
}
