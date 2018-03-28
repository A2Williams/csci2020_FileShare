package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

public class Controller {
    private boolean DEBUG = true;

    @FXML private ListView<String> localFiles;
    @FXML private ListView<String> serverFiles;
    private ObservableList<String> server, local;

    private Socket socket = null;
    private PrintWriter toServer;
    private BufferedReader fromServer;
    private String hostName;
    private String path;
    private static int serverPort = 49000;

    public void setParams(String hostName, String path) {
        this.hostName = hostName;
        this.path = path;
        if (DEBUG) System.out.printf("host: %s\t path: %s", hostName,path);
    }

    @FXML public void initialize() {
        // show local folder
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
            try {
                toServer.close();
                fromServer.close();
                socket.close();
            } catch (IOException e) {
                System.err.println("ERROR: cannot close connection");
            }
        }
    }
    public void getFile(ActionEvent e) {
        //TODO: Download button obtains file for client from server
    }
    public void sendFile(ActionEvent e) {
        //TODO: Upload button sends file from client to server
    }

    private void showServerFldr(String fileToken) {
        server = FXCollections.observableArrayList();
        StringTokenizer tokenizer = new StringTokenizer(fileToken);

        // add file names to server observable list
        while (tokenizer.hasMoreTokens()) {
            server.add(tokenizer.nextToken());
        }
        // show in listView
        serverFiles.setItems(server);
    }

    public void showClientFldr() {
        local = FXCollections.observableArrayList();
        // navigate to path folder
        File[] files = new File(path).listFiles();

        // add file names to observable list
        for (File file : files) {
            local.add(file.getName());
        }
        localFiles.setItems(local);
    }
}
