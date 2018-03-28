package sample;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.io.File;

public class Controller {
    @FXML private Button download;
    @FXML private Button upload;
    @FXML private ListView<String> localFiles;
    @FXML private ListView<String> serverFiles;
    private ObservableList<String> server, local;

    public static String hostName;
    public String path;
    public static int serverPort = 49000;

    public void setParams(String hostName, String path) {
        this.hostName = hostName;
        this.path = path;
    }
    @FXML public void initialize() {
        //TODO: populate server and client files from shared folders

    }
    @FXML public void getFile() {
        //TODO: Download button obtains file for client from server
    }
    @FXML public void sendFile() {
        //TODO: Upload button sends file from client to server
    }
}
