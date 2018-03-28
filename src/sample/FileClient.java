package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class FileClient extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        List<String> params = this.getParameters().getRaw();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        try {
            String host = params.get(0);
            String path = params.get(1);
            if (new File(path).isDirectory()) {
                controller.setParams(host, path);
                controller.showClientFldr();
                primaryStage.setTitle("File Sharer");
                primaryStage.setScene(new Scene(root, 600, 800));
                primaryStage.show();
            } else {
                System.err.println("ERROR: No such directory exists!");
                System.exit(-1);
            }
        } catch (Exception e) {
            System.err.println("ERROR: parameters not correct");
        }
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