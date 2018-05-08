import Domain.Leaf;
import Domain.Node;
import Service.NodeService;
import javafx.scene.layout.VBox;
import org.apache.http.HttpException;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class Start extends Application {
    private static NodeService nodeService;
    private static Node currentNode;

    public static void main (String[] arg) throws IOException, ParseException, HttpException, InterruptedException {
        System.out.println("Start");
        nodeService = new NodeService();
        Thread thread = new Thread(() -> {
            try {
                nodeService.startMonitoring();
            } catch (IOException | HttpException | InterruptedException ignored) {
            }
        });
        currentNode = nodeService.nodes.get(0);
        thread.start();
        launch(arg);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Monitoring");

        List<String> StillLoading = new ArrayList<>();
        StillLoading.add("Loading");
        nodeService.endpointList.set(FXCollections.observableArrayList(StillLoading));
        nodeService.endpointListView.itemsProperty().bind(nodeService.endpointList);

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> bc =
                new BarChart<String,Number>(xAxis,yAxis);
        xAxis.setLabel("Endpoints");
        yAxis.setLabel("%");

        XYChart.Series uptimeSeries = new XYChart.Series();
        uptimeSeries.setName("uptime in %");
        //for (Leaf leaf : currentNode.getLeaves()){
        //    uptimeSeries.getData().add(new XYChart.Data(leaf., 25601.34));
        //}

        XYChart.Series downtimeSeries = new XYChart.Series();
        downtimeSeries.setName("downtime in %");
        //downtimeSeries.getData().add(new XYChart.Data(austria, 57401.85));

        VBox layout = new VBox(nodeService.endpointListView, bc);

        StackPane root = new StackPane();
        root.getChildren().add(layout);
        primaryStage.setScene(new Scene(root, 700, 400));
        primaryStage.show();
    }
}
