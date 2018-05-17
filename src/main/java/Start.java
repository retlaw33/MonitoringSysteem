import Service.NodeService;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.http.HttpException;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Start extends Application {
    private static NodeService nodeService;

    public static void main (String[] arg) throws IOException, ParseException, HttpException, InterruptedException {
        System.out.println("Start");
        nodeService = new NodeService();
        Thread thread = new Thread(() -> {
            try {
                nodeService.startMonitoring();
            } catch (IOException | InterruptedException ignored) {
            }
        });
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

        nodeService.xAxis.setLabel("Endpoints");
        nodeService.yAxis.setLabel("#calls");
        nodeService.uptimeSeries.setName("uptime");
        nodeService.downtimeSeries.setName("downtime");

        nodeService.barChart.getData().addAll(nodeService.uptimeSeries, nodeService.downtimeSeries);

        nodeService.lcxAxis.setAutoRanging(true);
        nodeService.lcxAxis.setLowerBound(0);
        nodeService.lcxAxis.setUpperBound(24);

        nodeService.lcyAxis.setAutoRanging(false);
        nodeService.lcyAxis.setLowerBound(0);
        nodeService.lcyAxis.setUpperBound(1);

        nodeService.datePicker.setValue(LocalDate.now());
        nodeService.datePicker.setShowWeekNumbers(true);

        nodeService.lineChart.getData().add(nodeService.lcSeries);

        HBox toolbar = new HBox(nodeService.ddNodes, nodeService.datePicker);

        VBox layout = new VBox(nodeService.endpointListView, toolbar , nodeService.barChart, nodeService.lineChart);

        StackPane root = new StackPane();
        root.getChildren().add(layout);
        primaryStage.setScene(new Scene(root, 1400, 800));
        primaryStage.show();
    }
}
