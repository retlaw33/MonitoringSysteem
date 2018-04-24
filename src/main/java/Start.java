import Service.NodeService;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.http.HttpException;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

        StackPane root = new StackPane();
        root.getChildren().add(nodeService.endpointListView);
        primaryStage.setScene(new Scene(root, 700, 400));
        primaryStage.show();
    }
}
