package sample;

import javafx.application.Preloader;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

public class Controller {
    private boolean DEBUG = true;

    @FXML private ListView<String> localFiles;
    @FXML private ListView<String> serverFiles;
    private String clientFile = "";
    private String serverFile = "";

    private Socket socket = null;
    private PrintWriter toServer;
    private BufferedReader fromServer;
    private String hostName;
    private String path;
    private static int serverPort = 49000;

    public void setParams(String hostName, String path) {
        this.hostName = hostName;
        this.path = path;
        if (DEBUG) System.out.printf("host: %s\t path: %s\n",
                hostName,path);
    }
    @FXML public void initialize() {
        getServerFldr();
    }
    public void getFile(ActionEvent event) {
        // check if file  on server side is selected
        if (null == serverFile) {
            System.err.println("ERROR: No file selected.");
        } else {
            openSocket();
            if (null != socket) {
                if (null != toServer) {
                    // send download request
                    if (DEBUG) System.out.println("Sending DOWNLOAD command");
                    toServer.println("DOWNLOAD " + serverFile);
                    // get socket output into a file
                    try {
                        PrintWriter writer = new PrintWriter(path + serverFile);
                        String buffer = fromServer.readLine();
                        while (null != buffer) {
                            writer.println(buffer);
                            buffer = fromServer.readLine();
                        }
                        System.out.println("DOWNLOAD Complete");
                        writer.close();
                    } catch (IOException e) {
                        System.err.println("ERROR: cannot download file");
                    }
                }
            }
            close();
        }
        showClientFldr();
    }
    public void sendFile(ActionEvent event) {
        if (null == clientFile) {
            System.err.println("ERROR: No file selected.");
        } else {
            openSocket();
            if (null != socket) {
                if (null != toServer) {
                    // send download request
                    if (DEBUG) System.out.println("Sending UPLOAD command");
                    toServer.println("UPLOAD " + clientFile);
                    // get socket output into a file
                    try {
                        if (DEBUG) System.out.println(path + clientFile);
                        BufferedReader readFile = new BufferedReader(new FileReader(
                                new File(path + clientFile)));
                        String buffer = null;
                        // read from file and write to socket
                        try {
                            buffer = readFile.readLine();
                        } catch (IOException e) {
                            System.err.println("ERROR: " +
                                    "IOException trying to read file");
                        }
                        while (null != buffer) {
                            toServer.println(buffer);
                            try {
                                buffer = readFile.readLine();
                            } catch (IOException e) {
                                System.err.println("ERROR: " +
                                        "IOException trying to read file");
                            }
                        }
                    } catch (FileNotFoundException e) {
                        System.err.println("ERROR: " + clientFile+ "does not exist?");
                    }
                }
            }
            close();
            getServerFldr();
        }
    }
    private void openSocket() {
        // open a socket
        try {
            socket = new Socket(hostName, serverPort);
        } catch (IOException e) {
            System.err.println("ERROR: initializing connection to server");
        }
        if (null != socket) {
            try {
                toServer = new PrintWriter(
                        socket.getOutputStream(), true);
                fromServer = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                System.err.println("ERROR: opening output stream");
            }
        }
    }
    private void close() {
        try {
            toServer.close();
            fromServer.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("ERROR: cannot close connection");
        }
    }
    private void getServerFldr() {
        // show local folder
        openSocket();
        if (null != socket) {
            if (null != toServer) {
                if (DEBUG) System.out.println("Sending DIR command");
                toServer.println("DIR");
                try {
                    showServerFldr(fromServer.readLine());
                } catch (IOException e) {
                    System.err.println("ERROR: no input to parse!");
                }
            }
            close();
        }
    }
    private void showServerFldr(String fileToken) {
        ObservableList<String> server = FXCollections.observableArrayList();
        StringTokenizer tokenizer = new StringTokenizer(fileToken);

        // add file names to server observable list
        while (tokenizer.hasMoreTokens()) {
            server.add(tokenizer.nextToken());
        }
        // show in listView
        serverFiles.setItems(server);
        serverFiles.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (DEBUG) System.out.println("Selected: " + newValue);
                    serverFile = newValue;
                }
        );
    }
    public void showClientFldr() {
        ObservableList<String> local = FXCollections.observableArrayList();
        // navigate to path folder
        File[] files = new File(path).listFiles();

        // add file names to observable list
        for (File file : files) {
            local.add(file.getName());
        }
        localFiles.setItems(local);
        localFiles.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (DEBUG) System.out.println("Selected: " + newValue);
                    clientFile = newValue;
                }
        );
    }
}
