package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class FileClient extends Application {
    protected List<String> params = this.getParameters().getRaw();

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        try {
            String host = params.get(0);
            String path = params.get(1);
            controller.setParams(host, path);
        } catch (Exception e) {
            System.err.println("ERROR: parameters not correct");
        }
        primaryStage.setTitle("File Sharer");
        primaryStage.setScene(new Scene(root, 600, 800));
        primaryStage.show();
    }

    public static void main(String[] args) {
        if (2 == args.length) {
            launch(args);
        } else {
            System.err.println("ERROR: few arguments usage: " +
                    "javac FileClient.java <hostname> <shared folder>");
            System.exit(-1);
        }
    }
}