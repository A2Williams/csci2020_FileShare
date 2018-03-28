package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.awt.event.ActionEvent;
import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

public class Controller {
    boolean DEBUG = true;

    @FXML private ListView<String> localFiles;
    @FXML private ListView<String> serverFiles;
    private ObservableList<String> server, local;

    private Socket socket = null;
    private PrintWriter toServer;
    private BufferedReader fromServer;
    private static String hostName;
    private String path;
    private static int serverPort = 49000;

    public void setParams(String hostName, String path) {
        this.hostName = hostName;
        this.path = path;
    }

    @FXML public void initialize() {
        //TODO: populate server and client files from shared folders
        // open a socket to get server file names
        try {
            socket = new Socket(hostName, serverPort);
        } catch (IOException e) {
            System.err.println("ERROR: initializing connection to server");
        }
        if (null != socket) {
            try {
                 toServer = new PrintWriter(
                        socket.getOutputStream(),true);
                 fromServer = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                System.err.println("ERROR: opening output stream");
            }
            if (null != toServer) {
                if (DEBUG) System.out.println("Sending DIR command");
                toServer.println("DIR");
                try {
                    showServerFldr(fromServer.readLine());
                } catch (IOException e) {
                    System.err.println("ERROR: no input to parse!");
                }
            }
            //showClientFldr();
            try {
                toServer.close();
                fromServer.close();
                socket.close();
            } catch (IOException e) {
                System.err.println("ERROR: cannot close connection");
            }
        }
    }
    @FXML public void getFile(ActionEvent e) {
        //TODO: Download button obtains file for client from server
    }
    @FXML public void sendFile(ActionEvent e) {
        //TODO: Upload button sends file from client to server
    }

    private void showServerFldr(String fileToken) {
        server = FXCollections.observableArrayList();
        StringTokenizer tokenizer = new StringTokenizer(fileToken);

        // add file names to an observable list
        while (tokenizer.hasMoreTokens()) {
            server.add(tokenizer.nextToken());
        }
        // show in listView
        serverFiles.setItems(server);
    }
}
