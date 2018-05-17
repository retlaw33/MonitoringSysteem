package Service;

import Domain.HttpMethod;
import Domain.Leaf;
import Domain.Node;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.*;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import org.apache.http.HttpException;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class NodeService {
    static Logger log = Logger.getLogger(NodeService.class.getName());

    public List<Node> nodes;
    public Node currentNode;

    public ListProperty<String> endpointList = new SimpleListProperty<>();
    public ListView<String> endpointListView = new ListView<>();

    public XYChart.Series uptimeSeries = new XYChart.Series();
    public XYChart.Series downtimeSeries = new XYChart.Series();
    public CategoryAxis xAxis = new CategoryAxis();
    public NumberAxis yAxis = new NumberAxis();
    public BarChart<String,Number> barChart = new BarChart<String,Number>(xAxis,yAxis);

    public MenuButton ddNodes = new MenuButton("Choose a node:");
    public DatePicker datePicker = new DatePicker();
    private LocalDate currentDateTime = LocalDate.now();

    public NumberAxis lcxAxis = new NumberAxis();
    public NumberAxis lcyAxis = new NumberAxis();
    public LineChart<Number, Number> lineChart = new LineChart<>(lcxAxis, lcyAxis);
    public XYChart.Series lcSeries = new XYChart.Series();

    public NodeService() throws IOException, ParseException {
        nodes = new ArrayList<>();
        loadNodesFromJson();
        datePicker.valueProperty().addListener((ov, oldValue, newValue) -> {
            currentDateTime = newValue;
            updateFrontEnd();
        });
        currentNode = nodes.get(0);
    }

    public void startMonitoring() throws IOException, InterruptedException {
        for(;;) {
            Thread t = new Thread() {
                public void run() {
                    try {
                        checkEndPoints();
                        countUptimeFromLog();
                        updateFrontEnd();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (HttpException e) {
                        e.printStackTrace();
                    }
                }
            };

            t.start();
            countUptimeFromLog();
            updateFrontEnd();
            TimeUnit.MINUTES.sleep(15);
        }
    }

    private void loadNodesFromJson() throws IOException, ParseException {
        //Read json array
        JSONParser parser = new JSONParser();
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("Nodes.json").getFile());
        JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(file));
        System.out.println("reading: " + file.getAbsolutePath());

        //Add node to list of nodes for each object in the json array
        for (Object jsonObj : jsonArray) {
            JSONObject jsonNode = (JSONObject) jsonObj;

            String name = (String) jsonNode.get("name");
            System.out.println("name = " + name);

            String ip = (String) jsonNode.get("ip");
            System.out.println("ip = " + ip);

            String port = (String) jsonNode.get("port");
            System.out.println("port = " + port);

            List<Leaf> leaves = new ArrayList<>();
            JSONArray jsonRequests = (JSONArray) jsonNode.get("leaves");

            for (Object jsonReq : jsonRequests) {
                String leafName = (String) ((JSONObject) jsonReq).get("leafName");
                String endPoint = "http://" + ip + ":" + String.valueOf(port) + "/" + ((JSONObject) jsonReq).get("lpEndpoint");
                HttpMethod httpMethod = HttpMethod.valueOf((String) ((JSONObject) jsonReq).get("httpMethod"));
                String body = (String) ((JSONObject) jsonReq).get("body");
                leaves.add(new Leaf(leafName, endPoint, httpMethod, body));
                System.out.println("endPoint added: " + endPoint);
            }

            nodes.add(new Node(name, leaves));

            Node node = null;
            for (Node n : nodes) {
                if (n.getName().equals(name)) {
                    node = n;
                }
            }

            MenuItem mi = new MenuItem(name);
            Node finalNode = node;
            mi.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    currentNode = finalNode;
                    updateFrontEnd();
                    ddNodes.setText(name);
                }
            });
            ddNodes.getItems().add(mi);

            System.out.println("Node added: " + name);
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
            log.info(" ◤" + leaf.getName() + "◥" + " ◅" + leaf.getHttpMethod().toString() + "▻ Ω" + leaf.getEndPoint() + "℧ - Statuscode: " + String.valueOf(leaf.getStatuscode()) + " - Response: " + leaf.getResult());
        }
        else {
            log.warn(" ◤" + leaf.getName() + "◥" + " ◅" + leaf.getHttpMethod().toString() + "▻ Ω" + leaf.getEndPoint() + "℧ - Statuscode: " + String.valueOf(leaf.getStatuscode()));
        }
    }

    private void countUptimeFromLog() throws FileNotFoundException {
        //Read json array
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("europaMonitoring.log").getFile());
        System.out.println("reading: " + file.getAbsolutePath());

        Scanner s = new Scanner(file);
        while (s.hasNextLine()){
            String line = s.nextLine();

            String leafName = line.substring(line.indexOf("◤") + 1, line.indexOf("◥"));
            String httpMethod = line.substring(line.indexOf("◅") + 1, line.indexOf("▻"));
            String endPoint = line.substring(line.indexOf("Ω") + 1, line.indexOf("℧"));
            boolean up = false;
            if (line.substring(0,4).equals("INFO")) {
                up = true;
            }
            System.out.println(httpMethod + " -> " + endPoint + " -> " + String.valueOf(up));

            for(Node node : nodes){
                for (Leaf leaf : node.getLeaves()){
                    if (leaf.getEndPoint().equals(endPoint) && leaf.getHttpMethod().toString().equals(httpMethod)){
                        if (up){
                            leaf.addToUpCount();
                        }
                        else{
                            leaf.addToDownCount();
                        }
                    }
                }
            }
        }
        s.close();
    }

    private void updateFrontEnd() {
        // endpointList.clear();
        List<String> historingLogginsAsText = new ArrayList<>();
        List<Leaf> historicLogging = getHistoricDataFromLogging();

        for (Leaf l : historicLogging){
            if (l.isFunctional()){
                historingLogginsAsText.add("[" + l.getDateTime() + "] " + l.getName() + ": UP");
            }
            else {
                historingLogginsAsText.add("[" + l.getDateTime() + "] " + l.getName() + ": DOWN");
            }
        }

        Platform.runLater(
                () -> {
                    endpointList.set(FXCollections.observableArrayList(historingLogginsAsText));
                    uptimeSeries.getData().clear();
                    downtimeSeries.getData().clear();
                    lcSeries.getData().clear();

                    int totalUpTime = 0;
                    for (Leaf l : currentNode.getLeaves()){
                        uptimeSeries.getData().add(new XYChart.Data(l.getName(), l.getUpCount()));
                        totalUpTime += l.getUpCount();
                    }

                    int totalDownTime = 0;
                    for (Leaf l : currentNode.getLeaves()){
                        downtimeSeries.getData().add(new XYChart.Data(l.getName(), l.getDownCount()));
                        totalDownTime += l.getDownCount();
                    }

                    List<Integer> hours = new ArrayList<>();
                    for (int i = 0; i < 24; i++) {
                        hours.add(i);
                    }

                    for (int hour : hours) {
                        for (Leaf l : historicLogging){
                            if (hour == l.getDateTime().getHour()){
                                if (l.isFunctional() && l.getDateTime().getYear() == currentDateTime.getYear() && l.getDateTime().getMonth() == currentDateTime.getMonth() &&  l.getDateTime().getDayOfMonth() == currentDateTime.getDayOfMonth() ){
                                    lcSeries.getData().add(new XYChart.Data(l.getDateTime().getHour(), 1));
                                }
                                else if (l.getDateTime().getYear() == currentDateTime.getYear() && l.getDateTime().getMonth() == currentDateTime.getMonth() && l.getDateTime().getDayOfMonth() == currentDateTime.getDayOfMonth()) {
                                    lcSeries.getData().add(new XYChart.Data(l.getDateTime().getHour(), 0));
                                }
                            }
                        }
                    }
                }
        );
    }

    private List<Leaf> getHistoricDataFromLogging() {
        List<Leaf> historicLogging = new ArrayList<>();

        //Read json array
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("europaMonitoring.log").getFile());
        System.out.println("reading: " + file.getAbsolutePath());

        Scanner s = null;
        try{
            s = new Scanner(file);
        }
        catch (FileNotFoundException ex){
            System.out.println(ex.toString());
        }
        while (s.hasNextLine()){
            String line = s.nextLine();
            String leafName = line.substring(line.indexOf("◤") + 1, line.indexOf("◥"));
            boolean up = false;
            if (line.substring(0,4).equals("INFO")) {
                up = true;
            }
            String dateTime = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS");
            LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
            historicLogging.add(new Leaf(leafName, up, localDateTime));
        }
        s.close();

        return historicLogging;
    }
}
