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

    public static void main (String[] arg) throws IOException, ParseException, HttpException, InterruptedException {
        System.out.println("Start");
        nodeService = new NodeService();
        Thread thread = new Thread(() -> {
            try {
                nodeService.startMonitoring();
            } catch (IOException | HttpException | InterruptedException ignored) {
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
        nodeService.yAxis.setLabel("%");

        nodeService.uptimeSeries.setName("uptime in %");
        nodeService.downtimeSeries.setName("downtime in %");

        nodeService.bc.getData().addAll(nodeService.uptimeSeries, nodeService.downtimeSeries);

        VBox layout = new VBox(nodeService.endpointListView, nodeService.bc);

        StackPane root = new StackPane();
        root.getChildren().add(layout);
        primaryStage.setScene(new Scene(root, 1400, 800));
        primaryStage.show();
    }
}
